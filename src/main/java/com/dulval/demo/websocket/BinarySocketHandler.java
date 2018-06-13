package com.dulval.demo.websocket;

import com.dulval.demo.util.FileUploadInFlight;
import com.dulval.demo.util.SaveToFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;

@Component
public class BinarySocketHandler extends BinaryWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    List<WebSocketSession> sessions = new CopyOnWriteArrayList();
    Map<WebSocketSession, FileUploadInFlight> sessionToFileMap = new WeakHashMap<>();

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        ByteBuffer payload = message.getPayload();

        FileUploadInFlight inflightUpload = sessionToFileMap.get(session);
        if (inflightUpload == null) {
            throw new IllegalStateException("This is not expected");
        }
        inflightUpload.append(payload);

        if (message.isLast()) {
            SaveToFileSystem.save(inflightUpload.getName(), "websocket", inflightUpload.convert());
            //session.sendMessage(new TextMessage("UPLOAD " + inflightUpload.getName()));
            //session.close();
            sessionToFileMap.remove(session);
            sessionToFileMap.put(session, new FileUploadInFlight(session));

            for (WebSocketSession webSocketSession : sessions) {
                webSocketSession.sendMessage(new BinaryMessage(inflightUpload.getBos().toByteArray()));
                //webSocketSession.sendMessage(new TextMessage("UPLOADED file "));
            }

            logger.info("Uploaded " + inflightUpload.getName());
        }

//        FileChannel channel =  new FileOutputStream(new File("file.png"), false).getChannel();
//        channel.write(payload);
//        channel.close();
//        String response = "Upload Received: size " + payload.array().length;
//        System.out.println(response);
//        for (WebSocketSession webSocketSession : sessions) {
//             webSocketSession.sendMessage(new BinaryMessage(response.getBytes()));
//             webSocketSession.sendMessage(new TextMessage("UPLOADED file "));
//        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        sessionToFileMap.put(session, new FileUploadInFlight(session));
        System.out.println(session.getId() + " socket success...");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("socket closes...");
        sessionToFileMap.remove(session);
        sessions.remove(session);
    }

}
