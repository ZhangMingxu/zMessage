package com.buren.zmessage.server;

/**
 * @author zhangmingxu ON 17:15 2019-05-10
 **/
public class ContextHolder {
    private static ServerContext serverContext;

    public static ServerContext getServerContext() {
        return serverContext;
    }

    static void setServerContext(ServerContext serverContext) {
        ContextHolder.serverContext = serverContext;
    }
}
