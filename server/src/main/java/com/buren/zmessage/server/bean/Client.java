package com.buren.zmessage.server.bean;

import com.buren.zmessage.server.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

import static com.buren.zmessage.server.constant.Constant.SERVER;

/**
 * @author zhangmingxu ON 11:06 2019-05-10
 **/
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private String id;
    private Socket clientSocket;
    private Thread listenThread;
    private PrintWriter writer;

    public void sendMessage(String from, String message) {
        String temp = from + ":" + message;
        writer.println(temp);
        writer.flush();
    }

    public void init() throws IOException {
        startListenResponse();
        this.writer = new PrintWriter(clientSocket.getOutputStream());
        sendHello();
        logger.info("初始化客户端id:{}", id);

    }

    public Client(String id, Socket clientSocket) {
        this.id = id;
        this.clientSocket = clientSocket;
    }

    private void sendHello() {
        sendMessage(SERVER, "欢迎");
        sendMessage(SERVER, "你的ID:" + id);
        sendMessage(SERVER, "已经注册的客户端有:");
        Map<String, Client> map = ContextHolder.getServerContext().getClientMap();
        map.forEach((k, v) -> sendMessage(SERVER, k));


    }

    private void process(String request) {
        String[] params = request.split(" ");
        if (params.length < 2) {
            sendMessage(SERVER, "命令非法");
            logger.info("id:{}发送非法请求request:{}", id, request);
            return;
        }
        String to = params[0];
        String message = params[1];
        logger.info("id:{}对id:{}说:{}", id, to, message);
        ContextHolder.getServerContext().sendForward(id, to, message);
    }

    private void startListenResponse() {
        listenThread = new Thread(() -> {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                while (true) {
                    String request = in.readLine();
                    if (request != null) {
                        process(request);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        listenThread.setName(id + ":listenResponseThread");
        listenThread.start();
    }
}
