package com.task.client.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
@RestController
public class RegisterByHandController {

    @Autowired
    private SecheduledTaskRegister secheduledTaskRegister;

    @PostMapping("/task_client/register_secheduled_task/by_hand")
    public void RegisterSecheduledTaskByHand() {
        secheduledTaskRegister.register();
    }
}
