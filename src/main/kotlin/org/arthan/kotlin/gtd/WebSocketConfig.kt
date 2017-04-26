package org.arthan.kotlin.gtd

import org.arthan.kotlin.gtd.web.MyWebSocketHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

/**
 * Created by arthan on 4/26/17 .
 */

@Configuration
@EnableWebSocket
@EnableScheduling
class WebSocketConfig : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry?) {
        registry?.addHandler(myWebSocketHandler(), "/handle")
    }

    @Bean
    fun myWebSocketHandler(): WebSocketHandler {
        return MyWebSocketHandler()
    }
}