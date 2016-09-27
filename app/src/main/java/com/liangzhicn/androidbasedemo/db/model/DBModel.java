package com.liangzhicn.androidbasedemo.db.model;

import com.android.base.callback.ExecutorCallBack;
import com.android.base.db.BaseRxDao;
import com.liangzhicn.androidbasedemo.db.contract.DBContract;
import com.liangzhicn.androidbasedemo.db.model.domains.City;

import java.util.List;

/**
 * 作者: lujianzhao
 * 创建时间: 2016/06/18 14:51
 * 描述:
 */
public class DBModel extends DBContract.Model {

    private BaseRxDao<City> mCityDao;

    @Override
    protected void onCreate() {
        mCityDao = new BaseRxDao<>(City.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCityDao = null;
    }

    @Override
    public void insertSync(City city, ExecutorCallBack<Boolean> dbCallBack) {
        getRxManager().add(mCityDao.insertSync(city, dbCallBack));
    }

    @Override
    public void queryForAllSync(ExecutorCallBack<List<City>> dbCallBack) {
        getRxManager().add( mCityDao.queryForAllSync(dbCallBack));
    }

    @Override
    public void clearTableDataSync(ExecutorCallBack<Boolean> dbCallBack) {
        getRxManager().add(mCityDao.clearTableDataSync(dbCallBack));
    }
}
