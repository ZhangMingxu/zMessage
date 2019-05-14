package com.buren.zmessage.server;

import java.util.concurrent.CountDownLatch;

/**
 * @author zhangmingxu ON 17:15 2019-05-10
 **/
public class Main {
    public static void main(String[] args) throws InterruptedException {
        ServerContext serverContext = new ServerContext(9090,10);
        ContextHolder.setServerContext(serverContext);
        serverContext.start();
        new CountDownLatch(1).await();
    }
}
