package com.task.client.register;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.task.client.config.ServerURL;
import com.task.client.exception.CronSequenceErrorException;
import com.task.client.exception.TaskClientException;
import com.task.client.SecheduledTask;
import com.task.client.support.SendServerRequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
public class SecheduledTaskRegister {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ApplicationInfoManager infoManager;
    private ApplicationContext context;
    private SendServerRequestHelper requestHelper;

    public void setInfoManager(ApplicationInfoManager infoManager) {
        this.infoManager = infoManager;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    public void setRequestHelper(SendServerRequestHelper requestHelper) {
        this.requestHelper = requestHelper;
    }

    public void register() {
        if(!requestHelper.getProperties().isRegister()) {
            logger.warn("服务配置定时任务不能注册");
            return;
        }
        if(StringUtils.isEmpty(requestHelper.getProperties().getTaskServerName())) {
            throw new TaskClientException("定时任务server的服务应用名称配置task.client.taskServerName不能为空");
        }
        SecheduledRegisterDTO registerDTO = this.getSecheduledRegisterDTO();
        if(!CollectionUtils.isEmpty(registerDTO.getSecheduledInfoList())) {
            requestHelper.sendRequest(ServerURL.TASK_SERVER_REGISTER_URL, registerDTO);
        } else {
            logger.warn("定时任务列表为空，系统不注册定时任务");
        }
    }

    private SecheduledRegisterDTO getSecheduledRegisterDTO() {
        SecheduledRegisterDTO registerDTO = new SecheduledRegisterDTO();
        InstanceInfo instanceInfo = infoManager.getInfo();
        registerDTO.setServiceName(instanceInfo.getAppName());
        registerDTO.setHostAndPort(instanceInfo.getIPAddr() + ":" + instanceInfo.getPort());
        List<SecheduledRegisterDTO.SecheduledInfo> secheduledInfoList = new ArrayList<>();
        Map<String, SecheduledTask> beanMap = context.getBeansOfType(SecheduledTask.class);
        if(!CollectionUtils.isEmpty(beanMap)) {
            beanMap.forEach((key, value) -> {
                //检验表达式是否配置正确
                try {
                    new CronSequenceGenerator(value.cron()).next(new Date());
                } catch (Exception e) {
                    logger.error("", e);
                    throw new CronSequenceErrorException(e.getMessage());
                }
                Class clazz = value.getClass();
                SecheduledRegisterDTO.SecheduledInfo secheduledInfo = new SecheduledRegisterDTO.SecheduledInfo();
                secheduledInfo.setClassName(clazz.getName());
                secheduledInfo.setCron(value.cron());
                secheduledInfoList.add(secheduledInfo);
            });
        }
        registerDTO.setSecheduledInfoList(secheduledInfoList);
        return registerDTO;
    }
}
