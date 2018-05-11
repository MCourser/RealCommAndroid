package com.machao.RealComm.contract;

import com.machao.RealComm.contract.view.adapter.PrincipalListRecyclerViewAdapter;
import com.machao.RealComm.contract.view.widget.WebrtcSurfaceView;
import com.machao.RealComm.model.Message4IceCandidate;
import com.machao.RealComm.model.Message4Call;
import com.machao.RealComm.model.Message4WebrtcConfiguration;
import com.machao.RealComm.model.Principal;
import com.machao.RealComm.model.RTCIceCandidate;
import com.machao.RealComm.model.RTCSessionDescription;

import org.webrtc.EglBase;

import java.util.List;

public interface WebrtcContract {
    interface View extends AbstractContract.View<WebrtcContract.Presenter>, PrincipalListRecyclerViewAdapter.OnPrincipalListItemClickedListener {
        WebrtcSurfaceView obtainLocalWebrtcSurfaceView();
        WebrtcSurfaceView obtainRemoteWebrtcSurfaceView();

        void initViewWebrtcSurfaceView();
        void initViewUserList();

        void updateUserList(List<Principal> users);
    }

    interface Presenter extends AbstractContract.Presenter<WebrtcContract.View> {
        EglBase obtainEglBase();

        void obtainPermissions();

        void setupPeerConnection();

        void sendWebrtcConfigurationRequest();
        void sendAnswerRequest(Principal principal, RTCSessionDescription sessionDescription);
        void sendIceCandidateRequest(Principal principal, RTCIceCandidate iceCandidate);

        void onReceiveWebrtcConfiguration(Message4WebrtcConfiguration message);
        void onReceiveOffer(Message4Call message);
        void onReceiveIceCandidate(Message4IceCandidate message4IceCandidate);
    }
}
