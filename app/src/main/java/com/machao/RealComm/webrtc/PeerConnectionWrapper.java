package com.machao.RealComm.webrtc;

import android.content.Context;
import android.util.Log;

import com.google.common.util.concurrent.SettableFuture;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class PeerConnectionWrapper {
    private static final String TAG = PeerConnectionWrapper.class.getSimpleName();

    private static final String VIDEO_TRACK_ID = "REAL_COMM_VIDEO_TRACK_ID";
    private static final String AUDIO_TRACK_ID = "REAL_COMM_AUDIO_TRACK_ID";
    private static final String LOCAL_MEDIA_STREAM_ID = "REAL_COMM_LOCAL_MEDIA_STREAM_ID";

    private final PeerConnection peerConnection;
    private final AudioTrack audioTrack;
    private final AudioSource audioSource;

    private final VideoCapturer videoCapturer;
    private final VideoSource videoSource;
    private final VideoTrack videoTrack;

    public PeerConnectionWrapper(Context context,
                                 PeerConnectionFactory factory,
                                 PeerConnection.Observer observer,
                                 VideoRenderer.Callbacks localRenderer,
                                 List<PeerConnection.IceServer> iceServers,
                                 boolean hideIp) {
        PeerConnection.RTCConfiguration configuration = new PeerConnection.RTCConfiguration(iceServers);

        configuration.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        configuration.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;

        if (hideIp) {
            configuration.iceTransportsType = PeerConnection.IceTransportsType.RELAY;
        }

        this.peerConnection = factory.createPeerConnection(configuration, obtainPeerConnectionMediaConstraints(), observer);
        this.peerConnection.setBitrate(2250000, 3500000, 6000000);

        MediaStream mediaStream = factory.createLocalMediaStream(LOCAL_MEDIA_STREAM_ID);
        this.audioSource = factory.createAudioSource(obtainAudioMediaConstraints());
        this.audioTrack = factory.createAudioTrack(AUDIO_TRACK_ID, audioSource);
        this.audioTrack.setEnabled(false);
        mediaStream.addTrack(audioTrack);

        this.videoCapturer = createVideoCapturer(context);
        if (videoCapturer != null) {
            this.videoSource = factory.createVideoSource(videoCapturer);
            this.videoTrack = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);

            this.videoTrack.addRenderer(new VideoRenderer(localRenderer));
            this.videoTrack.setEnabled(false);
            mediaStream.addTrack(videoTrack);
        } else {
            this.videoSource = null;
            this.videoTrack = null;
        }

        this.peerConnection.addStream(mediaStream);
    }

    public void setVideoEnabled(boolean enabled) {
        if (this.videoTrack != null) {
            this.videoTrack.setEnabled(enabled);
        }

        if (this.videoCapturer != null) {
            try {
                if (enabled) {
                    this.videoCapturer.startCapture(1280, 720, 30);
                } else {
                    this.videoCapturer.stopCapture();
                }
            } catch (InterruptedException e) {
                Log.w(TAG, e);
            }
        } else {
            Log.w(TAG, "videoCapturer is nul");
        }
    }

    public void setAudioEnabled(boolean enabled) {
        this.audioTrack.setEnabled(enabled);
    }

    public DataChannel createDataChannel(String name) {
        return this.peerConnection.createDataChannel(name, new DataChannel.Init());
    }

    public SessionDescription createOffer() throws PeerConnectionException {
        final SettableFuture<SessionDescription> future = SettableFuture.create();

        peerConnection.createOffer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sdp) {
                future.set(sdp);
            }

            @Override
            public void onCreateFailure(String error) {
                future.setException(new PeerConnectionException(error));
            }

            @Override
            public void onSetSuccess() {
                throw new AssertionError();
            }

            @Override
            public void onSetFailure(String error) {
                throw new AssertionError();
            }
        }, obtainPeerConnectionMediaConstraints());

        try {
            return future.get();
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        } catch (ExecutionException e) {
            throw new PeerConnectionException(e);
        }
    }

    public SessionDescription createAnswer() throws PeerConnectionException {
        final SettableFuture<SessionDescription> future = SettableFuture.create();

        this.peerConnection.createAnswer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sdp) {
                future.set(sdp);
            }

            @Override
            public void onCreateFailure(String error) {
                future.setException(new PeerConnectionException(error));
            }

            @Override
            public void onSetSuccess() {
                throw new AssertionError();
            }

            @Override
            public void onSetFailure(String error) {
                throw new AssertionError();
            }
        }, obtainPeerConnectionMediaConstraints());

        try {
            return future.get();
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        } catch (ExecutionException e) {
            throw new PeerConnectionException(e);
        }
    }

    public void setRemoteDescription(SessionDescription sdp) throws PeerConnectionException {
        final SettableFuture<Boolean> future = SettableFuture.create();

        peerConnection.setRemoteDescription(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sdp) {
            }

            @Override
            public void onCreateFailure(String error) {
            }

            @Override
            public void onSetSuccess() {
                future.set(true);
            }

            @Override
            public void onSetFailure(String error) {
                future.setException(new PeerConnectionException(error));
            }
        }, sdp);

        try {
            future.get();
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        } catch (ExecutionException e) {
            throw new PeerConnectionException(e);
        }
    }

    public void setLocalDescription(SessionDescription sdp) throws PeerConnectionException {
        final SettableFuture<Boolean> future = SettableFuture.create();

        peerConnection.setLocalDescription(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sdp) {
                throw new AssertionError();
            }

            @Override
            public void onCreateFailure(String error) {
                throw new AssertionError();
            }

            @Override
            public void onSetSuccess() {
                future.set(true);
            }

            @Override
            public void onSetFailure(String error) {
                future.setException(new PeerConnectionException(error));
            }
        }, sdp);

        try {
            future.get();
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        } catch (ExecutionException e) {
            throw new PeerConnectionException(e);
        }
    }

    public void dispose() {
        if (this.videoCapturer != null) {
            try {
                this.videoCapturer.stopCapture();
            } catch (InterruptedException e) {
                Log.w(TAG, e);
            }
            this.videoCapturer.dispose();
        }

        if (this.videoSource != null) {
            this.videoSource.dispose();
        }

        this.audioSource.dispose();
        this.peerConnection.close();
        this.peerConnection.dispose();
    }

    public boolean addIceCandidate(IceCandidate candidate) {
        return this.peerConnection.addIceCandidate(candidate);
    }

    public void removeIceCandidate(IceCandidate[] candidates) {
        this.peerConnection.removeIceCandidates(candidates);
    }

    private CameraVideoCapturer createVideoCapturer(Context context) {
        Log.w(TAG, "Camera2 enumerator supported: " + Camera2Enumerator.isSupported(context));
        CameraEnumerator enumerator = Camera2Enumerator.isSupported(context) ? new Camera2Enumerator(context) : new Camera1Enumerator(true);
        String[] deviceNames = enumerator.getDeviceNames();

        for (String deviceName : deviceNames) {
            if (enumerator.isBackFacing(deviceName)) {
                Log.w(TAG, "Creating back facing camera capturer.");
                final CameraVideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    Log.w(TAG, "Found back facing capturer: " + deviceName);
                    return videoCapturer;
                }
            }
        }

        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Log.w(TAG, "Creating other camera capturer.");
                final CameraVideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    Log.w(TAG, "Found other facing capturer: " + deviceName);
                    return videoCapturer;
                }
            }
        }

        Log.w(TAG, "Video capture not supported!");
        return null;
    }

    private MediaConstraints obtainPeerConnectionMediaConstraints() {
        MediaConstraints mediaConstraints = new MediaConstraints();
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        mediaConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        return mediaConstraints;
    }

    private MediaConstraints obtainAudioMediaConstraints() {
        MediaConstraints mediaConstraints = new MediaConstraints();
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googNoiseSuppression", "true"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googEchoCancellation", "true"));
        return mediaConstraints;
    }

//    private SessionDescription correctSessionDescription(SessionDescription sessionDescription) {
//        String updatedSdp = sessionDescription.description.replaceAll("(a=fmtp:111 ((?!cbr=).)*)\r?\n", "$1;cbr=1\r\n");
//        updatedSdp = updatedSdp.replaceAll(".+urn:ietf:params:rtp-hdrext:ssrc-audio-level.*\r?\n", "");
//        Log.d("WebrtcPresenter", "update sdp, " + updatedSdp);
//        return new SessionDescription(sessionDescription.type, updatedSdp);
//    }

    public static class PeerConnectionException extends Exception {
        public PeerConnectionException(String error) {
            super(error);
        }

        public PeerConnectionException(Throwable throwable) {
            super(throwable);
        }
    }
}
