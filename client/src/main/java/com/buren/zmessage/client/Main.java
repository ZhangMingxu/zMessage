package com.buren.zmessage.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhangmingxu ON 21:41 2019-05-14
 **/
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 2) {
            logger.error("启动命令非法");
            logger.error("需要输入服务端ip及端口");
            logger.error("例如;127.0.0.1 9090");
            return;
        }
        ClientContext clientContext = new ClientContext(args[0], Integer.parseInt(args[1]));
        clientContext.init();
        new CountDownLatch(1).await();
    }
}
