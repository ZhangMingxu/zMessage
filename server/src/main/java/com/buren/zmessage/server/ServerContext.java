package com.buren.zmessage.server;

import com.buren.zmessage.server.bean.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.buren.zmessage.server.constant.Constant.SERVER;

/**
 * @author zhangmingxu ON 11:01 2019-05-10
 **/
public class ServerContext {
    private static final Logger logger = LoggerFactory.getLogger(ServerContext.class);
    private Integer port;
    private Integer maxClientNumber;
    private AtomicInteger curClientNumber = new AtomicInteger(0);
    private ServerSocket serverSocket;
    private Map<String, Client> clientMap;
    private Thread listenThread;

    ServerContext(Integer port, Integer maxClientNumber) {
        this.port = port;
        this.maxClientNumber = maxClientNumber;
    }

    void start() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            logger.error("服务端监听[" + port + "]端口异常", e);
            return;
        }
        clientMap = new ConcurrentHashMap<>(maxClientNumber);
        logger.info("服务器在" + port + "端口启动");
        startListening();
    }

    public void sendForward(String fromId, String toId, String message) {
        if (!clientMap.containsKey(toId)) {
            clientMap.get(fromId).sendMessage(SERVER, toId + "没有链接服务");
        } else {
            clientMap.get(toId).sendMessage(fromId, message);
        }
    }

    private void startListening() {
        listenThread = new Thread(() -> {
            while (true) {
                try {
                    Socket client = serverSocket.accept();
                    if (client != null) {
                        addClient(client);
                    }
                } catch (IOException e) {
                    logger.error("添加");
                }
            }
        });
        listenThread.setName("listenThread");
        listenThread.start();
        logger.info("启动监听线程");
    }

    private void addClient(Socket socket) throws IOException {
        String id = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        if (curClientNumber.incrementAndGet() > maxClientNumber) {
            doRefuse(socket);
        }
        if (clientMap.containsKey(id)) {
            logger.info("id:{}的客户端已经添加过了", id);
            return;
        }
        Client client = new Client(id, socket);
        client.init();
        sendMessageToOther(id);
        clientMap.put(id, client);
    }

    private void sendMessageToOther(String id) {
        clientMap.forEach((k, v) -> v.sendMessage(SERVER, id + "登陆了"));
    }

    private void doRefuse(Socket socket) {
        //TODO 增加拒绝消息
        closeSocket(socket);
    }

    private void closeSocket(Socket socket) {
        if (socket == null || socket.isClosed()) {
            return;
        }
        try {
            socket.close();
        } catch (IOException e) {
            logger.error("关闭socket异常", e);
        }
    }

    public Map<String, Client> getClientMap() {
        return clientMap;
    }
}
