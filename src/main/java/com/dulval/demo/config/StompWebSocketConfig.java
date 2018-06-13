package com.dulval.demo.config;

import com.dulval.demo.websocket.BinarySocketHandler;
import com.dulval.demo.websocket.ProtocolBufferSocketHandler;
import com.dulval.demo.websocket.SocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class StompWebSocketConfig implements WebSocketConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(StompWebSocketConfig.class);

    private final String[] origin = {"*"};

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SocketHandler(), "/name").setAllowedOrigins(origin);
        registry.addHandler(new ProtocolBufferSocketHandler(), "/protobuf");
        registry.addHandler(new BinarySocketHandler(), "/binary").setAllowedOrigins(origin);
    }

}
