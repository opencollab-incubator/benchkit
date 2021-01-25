package io.github.lukeeey.skin2server.http;

import lombok.Data;

@Data
public class Session {
    private String sessionId;
    private boolean connected;
    private String newSkin;
}
