package com.machao.RealComm.model;

public class Message4Call {
    private String from;
    private Call content;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Call getContent() {
        return content;
    }

    public void setContent(Call content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message [from=" + from + ", content=" + content + "]";
    }
}
