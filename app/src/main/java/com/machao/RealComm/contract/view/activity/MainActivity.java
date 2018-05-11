package com.machao.RealComm.contract.view.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.machao.RealComm.R;
import com.machao.RealComm.contract.WebrtcContract;
import com.machao.RealComm.contract.presenter.WebrtcPresenter;
import com.machao.RealComm.contract.view.adapter.PrincipalListRecyclerViewAdapter;
import com.machao.RealComm.contract.view.widget.WebrtcSurfaceView;
import com.machao.RealComm.model.Principal;

import org.webrtc.RendererCommon;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.machao.RealComm.contract.WebrtcContract.Presenter;

public class MainActivity extends AbstractActivity<WebrtcContract.Presenter> implements WebrtcContract.View {
    private Presenter presenter;

    @BindView(R.id.main_local_surface_view)
    WebrtcSurfaceView localWebrtcSurfaceView;
    @BindView(R.id.main_remote_surface_view)
    WebrtcSurfaceView remoteWebrtcSurfaceView;

    @BindView(R.id.main_user_list_recycler_view)
    RecyclerView userListRecyclerView;
    private LinearLayoutManager userListRecyclerViewLinearLayoutManager;
    private PrincipalListRecyclerViewAdapter principalListRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        this.setPresenter();
        this.initView();
    }

    @Override
    public void setPresenter() {
        this.presenter = new WebrtcPresenter(this);
        this.presenter.create();
        this.presenter.setView(this);
    }

    @Override
    public Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void initView() {
        this.initViewWebrtcSurfaceView();
        this.initViewUserList();
    }

    @Override
    public WebrtcSurfaceView obtainLocalWebrtcSurfaceView() {
        return localWebrtcSurfaceView;
    }

    @Override
    public WebrtcSurfaceView obtainRemoteWebrtcSurfaceView() {
        return remoteWebrtcSurfaceView;
    }

    @Override
    public void initViewWebrtcSurfaceView() {
        this.localWebrtcSurfaceView.init(presenter.obtainEglBase().getEglBaseContext(), null);
        this.localWebrtcSurfaceView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        this.localWebrtcSurfaceView.setZOrderMediaOverlay(true);
        this.localWebrtcSurfaceView.setEnableHardwareScaler(true);

        this.remoteWebrtcSurfaceView.init(presenter.obtainEglBase().getEglBaseContext(), null);
        this.remoteWebrtcSurfaceView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        this.remoteWebrtcSurfaceView.setZOrderMediaOverlay(true);
        this.remoteWebrtcSurfaceView.setEnableHardwareScaler(true);
    }

    @Override
    public void initViewUserList() {
        this.userListRecyclerViewLinearLayoutManager = new LinearLayoutManager(this);
        this.userListRecyclerView.setLayoutManager(userListRecyclerViewLinearLayoutManager);
        this.principalListRecyclerViewAdapter = new PrincipalListRecyclerViewAdapter(this);
        this.principalListRecyclerViewAdapter.setOnPrincipalListItemClickedListener(this);
        this.userListRecyclerView.setAdapter(principalListRecyclerViewAdapter);
    }

    @Override
    public void updateUserList(List<Principal> users) {
    }

    @Override
    public void onCallClicked(Principal principal) {

    }

    @Override
    public void onEndCallClicked(Principal principal) {

    }
}
