package com.machao.RealComm.contract.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.PermissionUtils;
import com.machao.RealComm.contract.WebrtcContract;
import com.machao.RealComm.model.Message4IceCandidate;
import com.machao.RealComm.model.Message4Call;
import com.machao.RealComm.model.Message4WebrtcConfiguration;
import com.machao.RealComm.model.Principal;
import com.machao.RealComm.model.RTCIceCandidate;
import com.machao.RealComm.model.RTCSessionDescription;
import com.machao.RealComm.model.WebrtcConfiguration;
import com.machao.RealComm.net.StompClientWrapper;
import com.machao.RealComm.webrtc.PeerConnectionWrapper;

import org.webrtc.DataChannel;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoTrack;

import java.util.List;

public class WebrtcPresenter implements WebrtcContract.Presenter, StompClientWrapper.StompLifecycleListener, PeerConnection.Observer, PermissionUtils.FullCallback {
    private static final String TAG = WebrtcPresenter.class.getName();

    private EglBase rootElgBase =  EglBase.create();

    private final Activity context;

    private WebrtcContract.View view;
    private StompClientWrapper stompClient;

    private WebrtcConfiguration webrtcConfiguration;
    private PeerConnectionFactory peerConnectionFactory;
    private PeerConnectionWrapper peerConnection;

    private Principal remotePrincipal;

    public WebrtcPresenter(Activity context) {
        this.context = context;
    }

    @Override
    public void create() {
        this.obtainPermissions();

        try {
            this.stompClient = new StompClientWrapper();
            this.stompClient.setLifecycleListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        this.stompClient.connect(true);
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {
        this.stompClient.disconnect();
        if(peerConnection != null) {
            this.peerConnection.dispose();
        }
        this.rootElgBase.release();
    }

    @Override
    public void setView(WebrtcContract.View view) {
        this.view = view;
    }

    @Override
    public Context getContext() {
        return this.context;
    }

    @Override
    public void onOpen() {
        this.stompClient.topic("/user/sender/webrtc/config").subscribe(resp -> {
            WebrtcPresenter.this.onReceiveWebrtcConfiguration(JSONObject.parseObject(resp.getPayload(), Message4WebrtcConfiguration.class));
        });
        this.stompClient.topic("/user/sender/call/offer").subscribe(resp -> {
            WebrtcPresenter.this.onReceiveOffer(JSONObject.parseObject(resp.getPayload(), Message4Call.class));
        });
        this.stompClient.topic("/user/sender/call/candidate").subscribe(resp -> {
            WebrtcPresenter.this.onReceiveIceCandidate(JSONObject.parseObject(resp.getPayload(), Message4IceCandidate.class));
        });

        this.sendWebrtcConfigurationRequest();
    }

    @Override
    public void onClose() {

    }

    @Override
    public void onError() {

    }

    @Override
    public EglBase obtainEglBase() {
        return rootElgBase;
    }

    @Override
    public void obtainPermissions() {
        final String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
        };

        PermissionUtils.permission(permissions).callback(this).request();
    }

    @Override
    public void onGranted(List<String> permissionsGranted) {
    }

    @Override
    public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
        this.obtainPermissions();
    }

    @Override
    public void setupPeerConnection() {
        Log.d(TAG, "setup peer connection");
        try {
            if(peerConnection != null) {
                this.peerConnection.dispose();
            }

            PeerConnectionFactory.Options peerConnectionFactoryOptions = new PeerConnectionFactory.Options();
            this.peerConnectionFactory = new PeerConnectionFactory(peerConnectionFactoryOptions);
            this.peerConnectionFactory.setVideoHwAccelerationOptions(rootElgBase.getEglBaseContext(), rootElgBase.getEglBaseContext());

            List<PeerConnection.IceServer> iceServers = webrtcConfiguration.getIceServers();

            this.peerConnection = new PeerConnectionWrapper(context, peerConnectionFactory, this, view.obtainLocalWebrtcSurfaceView(), iceServers, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendWebrtcConfigurationRequest() {
        this.stompClient.send("/receiver/webrtc/config").subscribe();
    }

    @Override
    public void sendAnswerRequest(Principal principal, RTCSessionDescription sessionDescription) {
        this.stompClient.send("/receiver/call/answer/" + principal.getName(), sessionDescription).subscribe();
    }

    @Override
    public void sendIceCandidateRequest(Principal principal, RTCIceCandidate iceCandidate) {
        this.stompClient.send("/receiver/call/candidate/" + principal.getName(), iceCandidate).subscribe();
    }

    @Override
    public void onReceiveWebrtcConfiguration(Message4WebrtcConfiguration message) {
        Log.d(TAG, "receive webrtc configuration: " + message);
        this.webrtcConfiguration = message.getContent();
        this.setupPeerConnection();
    }

    @Override
    public void onReceiveOffer(Message4Call message) {
        try {
            Log.d(TAG, "receive offer: " + message);

            this.remotePrincipal = message.getContent().getCalling();

            this.peerConnection.setRemoteDescription(message.getContent().getCallingSessionDescription().toSessionDescription());
            SessionDescription localSessionDescription = peerConnection.createAnswer();
            this.peerConnection.setLocalDescription(localSessionDescription);

            this.peerConnection.setVideoEnabled(true);
            this.peerConnection.setAudioEnabled(true);

            this.sendAnswerRequest(message.getContent().getCalling(), RTCSessionDescription.toRTCSessionDescription(localSessionDescription));
        } catch (PeerConnectionWrapper.PeerConnectionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiveIceCandidate(Message4IceCandidate message4IceCandidate) {
        Log.d(TAG, "receive ice candidate: " + message4IceCandidate);
        this.peerConnection.addIceCandidate(message4IceCandidate.getContent().toIceCandidate());
    }

//    @Override
//    public void call(final User user) {
//        try {
//            SessionDescription localSessionDescription = peerConnection.createOffer();
//            this.peerConnection.setLocalDescription(localSessionDescription);
//
//            Sdp sdp = new Sdp();
//            sdp.setType(localSessionDescription.type.canonicalForm());
//            sdp.setSdp(localSessionDescription.description);
//
//            CallLegCreateRequestBody requestBody = new CallLegCreateRequestBody();
//            requestBody.setTo(user.getId());
//            requestBody.setSdp(sdp);
//            com.machao.RealComm.model.request.Request request = new com.machao.RealComm.model.request.Request("call-leg/create", requestBody);
//            WebrtcPresenter.this.sendMessage(request);
//
//            this.peerConnection.setVideoEnabled(true);
//            this.peerConnection.setAudioEnabled(true);
//        } catch (PeerConnectionWrapper.PeerConnectionException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void endCall(User user) {
//        this.peerConnection.setVideoEnabled(false);
//        this.peerConnection.setAudioEnabled(false);
//        this.peerConnection.dispose();
//    }

    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        Log.i(TAG, "onSignalingChange: " + signalingState);
    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        Log.i(TAG, "onIceConnectionChange: " + iceConnectionState);
    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {
        Log.i(TAG, "onIceConnectionReceivingChange: " + b);
    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        Log.i(TAG, "onIceGatheringChange: " + iceGatheringState);
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        Log.i(TAG, "onIceCandidate: " + iceCandidate);

        if(remotePrincipal == null) return;

        this.sendIceCandidateRequest(remotePrincipal, RTCIceCandidate.toRTCIceCandidate(iceCandidate));
    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
        this.peerConnection.removeIceCandidate(iceCandidates);
    }

    @Override
    public void onAddStream(final MediaStream mediaStream) {
        Log.i(TAG, "onAddStream: " + mediaStream);
        VideoTrack videoTrack = mediaStream.videoTracks.get(0);
        videoTrack.addRenderer(new VideoRenderer(view.obtainRemoteWebrtcSurfaceView()));
//        this.context.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                VideoTrack videoTrack = mediaStream.videoTracks.get(0);
//                videoTrack.addRenderer(remoteRenderer);
//                WebrtcPresenter.this.remoteMediaStream = mediaStream;
//            }
//        });
    }

    @Override
    public void onRemoveStream(final MediaStream mediaStream) {
        Log.i(TAG, "onRemoveStream: " + mediaStream);
//        this.context.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                VideoTrack videoTrack = mediaStream.videoTracks.get(0);
//                videoTrack.dispose();
//            }
//        });
    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {
        Log.i(TAG, "onDataChannel: " + dataChannel);
    }

    @Override
    public void onRenegotiationNeeded() {
        Log.i(TAG, "onRenegotiationNeeded");
    }

    @Override
    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {

    }

}
