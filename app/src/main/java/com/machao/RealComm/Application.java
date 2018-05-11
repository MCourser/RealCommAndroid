package com.machao.RealComm;

import android.support.multidex.MultiDexApplication;

import com.blankj.utilcode.util.Utils;

import org.webrtc.PeerConnectionFactory;

public class Application extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        Utils.init(this);
        PeerConnectionFactory.initialize(PeerConnectionFactory
                .InitializationOptions
                .builder(this)
                .createInitializationOptions());
    }

}
