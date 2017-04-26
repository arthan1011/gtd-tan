package org.arthan.kotlin.gtd.web

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

/**
 * Created by arthan on 4/26/17 .
 */
class MyWebSocketHandler : TextWebSocketHandler() {

    var session: WebSocketSession? = null
    var counter: Int = 0

    override fun handleTextMessage(session: WebSocketSession?, message: TextMessage?) {
        println("Message received")
        println(message.toString())
    }

    @Scheduled(fixedRate = 2000)
    fun periodicTask() {
        if (this.session != null) {
            println("Connected")
            this.session?.sendMessage(TextMessage("Message #${counter++}"))
        } else {
            println("Not yet connected")
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession?) {
        this.session = session
    }

    override fun afterConnectionClosed(session: WebSocketSession?, status: CloseStatus?) {
        session?.close()
        this.session = null
    }
}