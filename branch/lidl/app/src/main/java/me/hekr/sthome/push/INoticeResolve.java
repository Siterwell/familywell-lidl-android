package me.hekr.sthome.push;

import java.util.List;

import me.hekr.sthome.http.HekrUserAction;


/**
 * Created by jishu0001 on 2017/3/2.
 */

public interface INoticeResolve {


    /**
     * 解析推送报警
     * @param code
     * @return
     */
    NoticeBean resolveNoticeCode(String code);

    /**
     * 解析情景报警
     * @param code
     * @return
     */
    void resolveScene(NoticeBean noticeBean, String code);

    /**
     * 解析设备报警
     * @param code
     * @return
     */
    void resolveEquipment(NoticeBean noticeBean, String code);


    /**
     *获取当前前一天的时间
     * @return
     */
    String dateGetOneDay();

    /**
     * 解析报警数据
     * @param string
     */
    List<NoticeBean> getJsonArrayResolve(String string);


    /**
     * 从云端获取报警信息数组
     * @return
     */
    String getAlarmInforFromHrke(int page, HekrUserAction.GetHekrDataListener getHekrDataListener);
}
