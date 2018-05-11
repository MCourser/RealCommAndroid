package com.machao.RealComm.contract.view.activity;

import android.support.v7.app.AppCompatActivity;

import com.machao.RealComm.contract.AbstractContract;

public abstract class AbstractActivity<P extends AbstractContract.Presenter> extends AppCompatActivity implements AbstractContract.View<P>{

    @Override
    protected void onStart() {
        super.onStart();
        // Start Presenter
        this.getPresenter().start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume Presenter
        this.getPresenter().resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause Presenter
        this.getPresenter().pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop Presenter
        this.getPresenter().stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Destroy Presenter
        this.getPresenter().destroy();
    }
}
