package com.machao.RealComm.contract;

import android.content.Context;

public interface AbstractContract {
    interface View<P extends AbstractContract.Presenter> {
        void setPresenter();
        P getPresenter();
        void initView();
    }

    interface Presenter<V extends AbstractContract.View> {
        void create();
        void start();
        void resume();
        void pause();
        void stop();
        void destroy();
        void setView(V view);
        Context getContext();
    }
}
