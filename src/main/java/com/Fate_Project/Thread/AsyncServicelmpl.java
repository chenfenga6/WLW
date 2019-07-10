package com.Fate_Project.Thread;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.Fate_Project.FateProjectApplication.FLAG_EMAIL;

@Service
public class AsyncServicelmpl implements AsyncService {
    @Override
    @Async("asyncServiceExecutor")
    public void executeAsync() throws InterruptedException {
        Thread.sleep(20000);        //间隔20s
        System.out.println("冷却成功！");
        FLAG_EMAIL = true;
    }
}
