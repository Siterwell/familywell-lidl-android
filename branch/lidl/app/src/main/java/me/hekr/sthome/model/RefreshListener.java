package me.hekr.sthome.model;

/**
 * Created by Administrator on 2017/6/30.
 */

/*
@class RefreshListener
@autor Administrator
@time 2017/6/30 13:50
@email xuejunju_4595@qq.com
刷新接口，调用接口做刷新主页面处理
*/
public interface RefreshListener {

    void refreshEqlist();

    void refreshSceneList();

    void refreshIndexMode();

    void getreplace(String eqid);

    void addEqSuccess();
}
