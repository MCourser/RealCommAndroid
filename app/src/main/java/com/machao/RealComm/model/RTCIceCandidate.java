package com.machao.RealComm.model;

import org.webrtc.IceCandidate;

public class RTCIceCandidate {
	private String candidate;
	private int sdpMLineIndex;
	private String sdpMid;

	public String getCandidate() {
		return candidate;
	}

	public void setCandidate(String candidate) {
		this.candidate = candidate;
	}

	public int getSdpMLineIndex() {
		return sdpMLineIndex;
	}

	public void setSdpMLineIndex(int sdpMLineIndex) {
		this.sdpMLineIndex = sdpMLineIndex;
	}

	public String getSdpMid() {
		return sdpMid;
	}

	public void setSdpMid(String sdpMid) {
		this.sdpMid = sdpMid;
	}

	public IceCandidate toIceCandidate() {
		return new IceCandidate(sdpMid,sdpMLineIndex, candidate);
	}

	public static RTCIceCandidate toRTCIceCandidate(IceCandidate iceCandidate) {
		RTCIceCandidate rtcIceCandidate = new RTCIceCandidate();
		rtcIceCandidate.setSdpMid(iceCandidate.sdpMid);
		rtcIceCandidate.setSdpMLineIndex(iceCandidate.sdpMLineIndex);
		rtcIceCandidate.setCandidate(iceCandidate.sdp);
		return rtcIceCandidate;
	}

	@Override
	public String toString() {
		return "RTCIceCandidate [candidate=" + candidate + ", sdpMLineIndex=" + sdpMLineIndex + ", sdpMid=" + sdpMid + "]";
	}

}
