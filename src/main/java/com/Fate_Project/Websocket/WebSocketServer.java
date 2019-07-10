package com.Fate_Project.Websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实现服务器客户端平等交流、达到服务器可以主动向客户端发消息
 */
@ServerEndpoint(value = "/websocket")
@Component
public class WebSocketServer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    //使用道格李的ConcurrentHashSet, 放的是WebSocketServer而不是session为了复用自己方法
    private static transient volatile Set<WebSocketServer> webSocketSet = ConcurrentHashMap.newKeySet();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);     //加入到SET webSocketSet中
        logger.info("WebSocket 客户端["+session.getId()+"]连接成功!");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        logger.error("WebSocket 断开连接：["+session.getId()+"]");
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
        sendMessage("HEARTBEAT");
    }


    /**
     * 发生错误时候回调函数
     * @param session   会话信息
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("WebSocket 发生错误:[" + error.getClass() + error.getMessage()+"]");
    }

    /**
     * 向某个客户端发送消息
     * @param message
     */
    public boolean sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
            return true;
        } catch (IOException error) {
            logger.error("webSocket-sendMessage发生错误:[" + error.getClass() + error.getMessage()+"]");
            return false;
        }
    }


    /**
     * 群发自定义消息
     * */
    public static void sendInfo(String message) {
        logger.trace("WebSocket 群发消息：[" + message+"]\n");
        for (WebSocketServer item : webSocketSet) {
            item.sendMessage(message);
        }
    }

    /**
     * 获取连接数
     * @return  数量
     */
    public static int getOnlineCount() {
        return webSocketSet.size();
    }

}
