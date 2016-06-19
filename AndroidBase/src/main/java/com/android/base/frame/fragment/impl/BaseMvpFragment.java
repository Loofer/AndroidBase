package com.android.base.frame.fragment.impl;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.base.frame.presenter.impl.FragmentPresenter;
import com.android.base.frame.view.IBaseView;

/**
 * Created by Administrator on 2016/5/13.
 */
public abstract class BaseMvpFragment<P extends FragmentPresenter> extends SuperFragment{

    public P mPresenter;

    @NonNull
    protected abstract Class<P> getPresenterClass();

    @NonNull
    protected abstract <V extends IBaseView> V getMvpView();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter = getPresenter();
        mPresenter.setVM(this, getMvpView());
        initView(savedInstanceState);
        if(mPresenter !=null){
            mPresenter.onActivityCreated();
            mPresenter.start();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mPresenter !=null){
            mPresenter.onStart();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mPresenter !=null){
            mPresenter.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onBackPressedSupport() {
        if (mPresenter != null) {
            return mPresenter.onBackPressed();
        }
        return super.onBackPressedSupport();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mPresenter !=null){
            mPresenter.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mPresenter !=null){
            mPresenter.onPause();
        }
    }

    @Override
    public void onStop() {
        if(mPresenter !=null){
            mPresenter.onStop();
        }
        super.onStop();
    }


    @Override
    public void onDestroy() {
        if(mPresenter !=null){
            mPresenter.onDestroy();
            mPresenter = null;
        }
        super.onDestroy();
    }


    public P getPresenter(){
        try {
            return getPresenterClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("create IDelegate error");
        }
    }

}