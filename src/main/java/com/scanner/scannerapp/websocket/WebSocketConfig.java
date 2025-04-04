package com.scanner.scannerapp.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final CanvasWebSocketHandler canvasHandler;

    public WebSocketConfig(CanvasWebSocketHandler canvasHandler) {
        this.canvasHandler = canvasHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(canvasHandler, "/ws/canvas").setAllowedOrigins("*");
    }

}
