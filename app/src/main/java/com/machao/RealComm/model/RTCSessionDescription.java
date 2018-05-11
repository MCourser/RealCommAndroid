package com.machao.RealComm.model;

import org.webrtc.SessionDescription;

public class RTCSessionDescription {
	private String type;
	private String sdp;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSdp() {
		return sdp;
	}

	public void setSdp(String sdp) {
		this.sdp = sdp;
	}

	public SessionDescription toSessionDescription() {
		return new SessionDescription(SessionDescription.Type.fromCanonicalForm(type), sdp);
	}

	public static RTCSessionDescription toRTCSessionDescription(SessionDescription sessionDescription) {
		RTCSessionDescription rtcSessionDescription = new RTCSessionDescription();
		rtcSessionDescription.setType(sessionDescription.type.canonicalForm());
		rtcSessionDescription.setSdp(sessionDescription.description);
		return rtcSessionDescription;
	}

	@Override
	public String toString() {
		return "RTCSessionDescription [type=" + type + ", sdp=" + sdp + "]";
	}

}
