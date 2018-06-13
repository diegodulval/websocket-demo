package com.dulval.demo.util;

import com.google.common.base.Splitter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;
import org.springframework.web.socket.WebSocketSession;

@Data
public class FileUploadInFlight {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private String name;
    private String uniqueUploadId;
    private ByteArrayOutputStream bos = new ByteArrayOutputStream();

    /**
     * Fragile constructor - beware not prod ready
     *
     * @param session
     */
    public FileUploadInFlight(WebSocketSession session) {
        String query = session.getUri().getQuery();
        String uploadSessionIdBase64 = query.split("=")[1];
        String uploadSessionId = new String(Base64Utils.decodeUrlSafe(uploadSessionIdBase64.getBytes()));

        List<String> sessionIdentifiers = Splitter.on("\\").splitToList(uploadSessionId);
        String uniqueUploadId = session.getRemoteAddress().toString() + sessionIdentifiers.get(0);
        String fileName = sessionIdentifiers.get(1);
        this.name = fileName;
        this.uniqueUploadId = uniqueUploadId;
        logger.info("Preparing upload for " + this.name + " uploadSessionId " + uploadSessionId);
    }

    public void append(ByteBuffer byteBuffer) throws IOException {
        bos.write(byteBuffer.array());
    }

    public ByteBuffer convert() {
        return ByteBuffer.wrap(this.getBos().toByteArray());
    }
}
