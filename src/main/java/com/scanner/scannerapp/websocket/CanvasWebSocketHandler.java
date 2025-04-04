package com.scanner.scannerapp.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scanner.scannerapp.components.CanvasUpdateEvent;
import com.scanner.scannerapp.components.EventSink;
import com.scanner.scannerapp.entities.KeyPressEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class CanvasWebSocketHandler extends TextWebSocketHandler {

    private final EventSink eventSink;
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    public CanvasWebSocketHandler(EventSink eventSink) {
        this.eventSink = eventSink;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // Allow only one session at a time
        for (WebSocketSession s : sessions) {
            try {
                s.close(CloseStatus.POLICY_VIOLATION.withReason("Only one WebSocket connection allowed at a time."));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sessions.clear();
        sessions.add(session);
        System.out.println("Client connected: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        System.out.println("Client disconnected: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload().trim().toUpperCase();
        try {
            KeyPressEvent.Direction direction = KeyPressEvent.Direction.valueOf(payload);
            KeyPressEvent event = new KeyPressEvent(direction);
            eventSink.enqueue(event);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid direction received via WebSocket: " + payload);
        }
    }

    public void broadcastUpdate(CanvasUpdateEvent event) {
        String json = serializeToJson(event);
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String serializeToJson(CanvasUpdateEvent event) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
