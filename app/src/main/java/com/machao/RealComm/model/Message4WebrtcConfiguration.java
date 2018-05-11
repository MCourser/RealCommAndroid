package com.machao.RealComm.model;

public class Message4WebrtcConfiguration {
	private String from;
	private WebrtcConfiguration content;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public WebrtcConfiguration getContent() {
		return content;
	}

	public void setContent(WebrtcConfiguration content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Message [from=" + from + ", content=" + content + "]";
	}
}
