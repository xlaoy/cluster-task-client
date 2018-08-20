package com.task.client.execute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
@RestController
public class TaskExecuteController {

    @Autowired
    private TaskExecuteService executeService;


    @PostMapping("/task_client/execute_secheduled_task")
    public void RegisterSecheduledTaskByHand(@RequestBody SecheduledRequestDTO requestDTO){
        executeService.executeSecheduled(requestDTO);
    }
}
