package com.codingtest.callservice.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class SignalMessage {
    private String type;
    private String from;
    private String to;
    private String sdp;
    private Long fromUserId;
    private Long roomId;
    private IceCandidate candidate;

    @Getter
    @AllArgsConstructor
    public static class IceCandidate {
        private String candidate;
        private String sdpMid;
        private int sdpMLineIndex;
    }
}