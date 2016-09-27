package com.android.base.frame.model;

import com.android.base.common.rx.RxManager;

/**
 * 作者: lujianzhao
 * 创建时间: 2016/06/18 11:34
 * 描述: Model层,请把所有的rxJava的生命周期都交由 RxManager 管理,调用 add(Subscription) 方法
 */
public abstract class BaseModel{

    private RxManager mRxManager = new RxManager();

    /**
     * This method is called after model construction.
     *
     * This method is intended for overriding.
     *
     */
    protected void onCreate() {
    }

    /**
     * This method is being called when a user leaves view.
     *
     * This method is intended for overriding.
     */
    protected void onDestroy() {
    }

    public void create() {
        onCreate();
    }

    public void destroy() {
        if (mRxManager != null) {
            mRxManager.clear();
            mRxManager = null;
        }
        onDestroy();
    }

    protected RxManager getRxManager() {
        return mRxManager;
    }

}
