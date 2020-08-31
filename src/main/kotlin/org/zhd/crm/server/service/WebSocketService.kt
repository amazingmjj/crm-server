package org.zhd.crm.server.service

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.concurrent.CopyOnWriteArraySet
import javax.websocket.*
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint



//@ServerEndpoint("/webSocket/{sid}")
//@Component
//@Profile("dev")
class WebSocketService {
    companion object {
        private val logger = LoggerFactory.getLogger(WebSocketService::class.java)
        private var onlineCount: Int = 0
        val webSocketSet = CopyOnWriteArraySet<WebSocketService>()
        @Synchronized fun addOnlineCount(): Int = onlineCount ++
        @Synchronized fun subOnlineCount(): Int = onlineCount --

        @Throws(IOException::class)
        fun sendInfo(message: String, @PathParam("sid") sid: String?) {
            logger.info("推送消息到窗口[$sid],推送内容:$message");
            for (item in webSocketSet) {
                try {
                    if(item.sid == sid){
                        item.sendMessage(message);
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private lateinit var session: Session
    private var sid: String = ""

    //连接建立成功调用的方法
    @OnOpen
    fun onOpen(session: Session, @PathParam("sid") sid: String) {
        this.session = session
        webSocketSet.add(this)
        addOnlineCount()
        logger.info("有新窗口[$sid]开始监听,当前在线人数为:$onlineCount");
        this.sid = sid
        try {
            sendMessage("连接成功")
        } catch (e: IOException) {
            logger.error("webSocket IO异常")
        }
    }

    //连接关闭调用的方法
    @OnClose
    fun onClose() {
        webSocketSet.remove(this)
        subOnlineCount()
        logger.info("有一连接关闭！当前在线人数为:$onlineCount")
    }

    //收到客户端消息后调用的方法
    @OnMessage
    fun onMessage(session: Session, message: String) {
        logger.info("收到来自窗口[$sid]的信息:$message")
        //群发消息
        for (item in webSocketSet) {
            try {
                item.sendMessage(message)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @OnError
    fun onError(session: Session, error: Throwable) {
        logger.error("发生错误")
        error.printStackTrace()
    }

    //实现服务器主动推送
    @Throws(IOException::class)
    fun sendMessage(message: String) {
        this.session.basicRemote.sendText(message)
    }
}