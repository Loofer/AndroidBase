package com.android.base.frame.presenter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.base.common.rx.RxManager;
import com.android.base.frame.model.BaseModel;
import com.android.base.frame.model.factory.ModelFactory;
import com.android.base.frame.model.factory.ReflectionModelFactory;
import com.android.base.frame.model.impl.ModelLifecycleDelegate;
import com.android.base.frame.view.IBaseView;
import com.android.base.frame.view.PresenterWithModel;
import com.android.base.netstate.NetWorkUtil;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Administrator on 2016/9/19.
 */
public class BasePresenter<M extends BaseModel, V extends IBaseView> implements PresenterWithModel {

    private V view;

    private CopyOnWriteArrayList<OnDestroyListener> onDestroyListeners = new CopyOnWriteArrayList<>();

    private ModelLifecycleDelegate<M> modelDelegate = new ModelLifecycleDelegate<>(ReflectionModelFactory.<M>fromViewClass(getClass()));

    protected RxManager rxManager = new RxManager();

    private Context mContext;


    /**
     * This method is called after presenter construction.
     * <p>
     * This method is intended for overriding.
     *
     * @param savedState If the presenter is being re-instantiated after a process restart then this Bundle
     *                   contains the data it supplied in {@link #onSave}.
     */
    protected void onCreate(@Nullable Bundle savedState) {
    }

    /**
     * Presenter开始工作
     * 在{@link com.android.base.frame.fragment.impl.BaseMvpFragment#initData()},
     *   {@link com.android.base.frame.activity.impl.BaseMvpActivity#initData()} 调用
     */
    public void start() {
    }

    /**
     * This method is being called when a user leaves view.
     * <p>
     * This method is intended for overriding.
     */
    protected void onDestroy() {
    }

    /**
     * A returned state is the state that will be passed to {@link #onCreate} for a new presenter instance after a process restart.
     * <p>
     * This method is intended for overriding.
     *
     * @param state a non-null bundle which should be used to put presenter's state into.
     */
    protected void onSave(Bundle state) {
    }

    /**
     * This method is being called when a view gets attached to it.
     * Normally this happens during {@link Activity#onResume()}, {@link android.app.Fragment#onResume()}
     * and {@link android.view.View#onAttachedToWindow()}.
     * <p>
     * This method is intended for overriding.
     *
     * @param view a view that should be taken
     */
    protected void onTakeView(V view) {
    }

    /**
     * This method is being called when a view gets detached from the presenter.
     * Normally this happens during {@link Activity#onPause()} ()}, {@link Fragment#onDestroyView()}
     * and {@link android.view.View#onDetachedFromWindow()}.
     * <p>
     * This method is intended for overriding.
     */
    protected void onDropView() {
    }

    /**
     * A callback to be invoked when a presenter is about to be destroyed.
     */
    public interface OnDestroyListener {
        /**
         * Called before {@link BasePresenter#onDestroy()}.
         */
        void onDestroy();
    }

    /**
     * Adds a listener observing {@link #onDestroy}.
     *
     * @param listener a listener to add.
     */
    public void addOnDestroyListener(OnDestroyListener listener) {
        onDestroyListeners.add(listener);
    }

    /**
     * Removed a listener observing {@link #onDestroy}.
     *
     * @param listener a listener to remove.
     */
    public void removeOnDestroyListener(OnDestroyListener listener) {
        onDestroyListeners.remove(listener);
    }


    public V getView() {
        return view;
    }

    /**
     * Initializes the presenter.
     */
    public void create(Bundle bundle) {
        onCreate(bundle);
    }

    /**
     * Destroys the presenter, calling all {@link BasePresenter.OnDestroyListener} callbacks.
     */
    public void destroy() {
        for (OnDestroyListener listener : onDestroyListeners) {
            listener.onDestroy();
        }

        modelDelegate.onDestroy();

        if (rxManager != null) {
            rxManager.clear();
            rxManager = null;
        }

        onDestroy();
    }

    /**
     * Saves the presenter.
     */
    public void save(Bundle state) {
        onSave(state);
    }

    /**
     * Attaches a view to the presenter.
     *
     * @param view a view to attach.
     */
    public void takeView(V view, Context context) {
        this.view = view;
        this.mContext = context;
        onTakeView(view);
    }

    /**
     * Detaches the presenter from a view.
     */
    public void dropView() {
        onDropView();
        this.view = null;
    }


    @Override
    public ModelFactory getModelFactory() {

        return modelDelegate.getmModelFactory();
    }

    @Override
    public void setModelFactory(ModelFactory modelFactory) {
        modelDelegate.setmModelFactory(modelFactory);
    }

    public M getModel() {
        return modelDelegate.getmModel();
    }


    public Context getContext() {
        return mContext;
    }





    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public boolean onBackPressed() {
        return false;
    }

    /**
     * 网络断开
     */
    public void onNetWorkDisConnect() {
    }

    /**
     * 网络连接上
     *
     * @param type 当前的网络状态:
     *             UnKnown(-1),没有网络
     *             Wifi(1),WIFI网络
     *             Net2G(2),2G网络
     *             Net3G(3),3G网络
     *             Net4G(4);4G网络
     */
    public void onNetWorkConnect(NetWorkUtil.NetWorkType type) {
    }

}
