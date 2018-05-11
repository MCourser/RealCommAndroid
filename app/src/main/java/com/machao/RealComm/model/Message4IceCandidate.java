package com.machao.RealComm.model;

public class Message4IceCandidate {
    private Principal from;
    private RTCIceCandidate content;

    public Principal getFrom() {
        return from;
    }

    public void setFrom(Principal from) {
        this.from = from;
    }

    public RTCIceCandidate getContent() {
        return content;
    }

    public void setContent(RTCIceCandidate content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message [from=" + from + ", content=" + content + "]";
    }
}
