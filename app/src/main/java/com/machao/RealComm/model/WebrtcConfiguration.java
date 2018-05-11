package com.machao.RealComm.model;

import org.webrtc.PeerConnection;

import java.util.ArrayList;
import java.util.List;

public class WebrtcConfiguration {
	private List<IceServer> iceServers;

	public List<PeerConnection.IceServer> getIceServers() {
		List<PeerConnection.IceServer> retList = new ArrayList<PeerConnection.IceServer>();
		if(iceServers == null) return  retList;

		for(WebrtcConfiguration.IceServer iceServerConfig : iceServers) {
			for(String url : iceServerConfig.getUrls()) {
				PeerConnection.IceServer iceServer = PeerConnection.IceServer
						.builder(iceServerConfig.getUrls())
						.setUsername(iceServerConfig.getUsername())
						.setPassword(iceServerConfig.getCredential())
						.createIceServer();
				retList.add(iceServer);
			}
		}
		return retList;
	}

	public void setIceServers(List<IceServer> iceServers) {
		this.iceServers = iceServers;
	}

	@Override
	public String toString() {
		return "WebrtcClientConfig [iceServers=" + iceServers + "]";
	}

	public static class IceServer {
		private List<String> urls;
		private String username = "";
		private String credential = "";

		public List<String> getUrls() {
			return urls;
		}

		public void setUrls(List<String> urls) {
			this.urls = urls;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getCredential() {
			return credential;
		}

		public void setCredential(String credential) {
			this.credential = credential;
		}

		@Override
		public String toString() {
			return "IceServer [urls=" + urls + ", username=" + username + ", credential=" + credential + "]";
		}
	}
}
