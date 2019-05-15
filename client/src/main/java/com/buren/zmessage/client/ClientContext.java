package com.buren.zmessage.client;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author zhangmingxu ON 11:02 2019-05-10
 **/
public class ClientContext {
    private static final Logger logger = LoggerFactory.getLogger(ClientContext.class);
    private String serverIp;
    private int serverPort;
    private Socket server;
    private PrintWriter writer;
    private BufferedReader reader;
    private Scanner scanner;
    private Thread systemListenThread;
    private Thread serverListenThread;

    public ClientContext(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public void init() throws IOException {
        this.server = new Socket(serverIp, serverPort);
        this.writer = new PrintWriter(server.getOutputStream());
        this.scanner = new Scanner(System.in);
        this.reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
        startSystemListenThread();
        startServerListenThread();
    }

    private void startSystemListenThread() {
        this.systemListenThread = new Thread(() -> {
            while (true) {
                if (scanner.hasNext()) {
                    String input = scanner.nextLine();
                    if (StringUtils.isNotBlank(input)) {
                        process(input);
                    }
                }
            }
        });
        systemListenThread.setName("systemListenThread");
        systemListenThread.start();
        logger.info("启动控制台输入监听线程");
    }

    private void startServerListenThread() {
        this.serverListenThread = new Thread(() -> {
            while (true) {
                try {
                    String response = reader.readLine();
                    if (StringUtils.isNotBlank(response)) {
                        logger.info(response);
                    }
                } catch (IOException e) {
                    logger.error("读取服务数据异常", e);
                }
            }
        });
        serverListenThread.setName("systemListenThread");
        serverListenThread.start();
        logger.info("启动服务端监听线程");
    }

    private void process(String input) {
        String[] params = input.split(" ");
        if (params.length < 2) {
            logger.info("命令非法");
            return;
        }
        writer.write(input);
        writer.flush();
    }
}
