package me.hekr.sthome.CarouselView;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.common.DeviceActivitys;
import me.hekr.sthome.main.MainActivity;
import me.hekr.sthome.model.modelbean.MonitorBean;
import me.hekr.sthome.tools.LOG;
import me.hekr.sthome.xmipc.ActivityGuideDeviceAdd;

/**
 * Created by ryanhsueh on 2018/12/24
 */
public class IpcViewPager extends RelativeLayout {
    private static final String TAG = IpcViewPager.class.getSimpleName();

    private static final String DEV_LIST = "device_list";

    private MainActivity activity;
    private ViewPager ipcViewPager;
    private TextView textIpcName;

    private LinearLayout indicatorLayout;
    private ImageView[] indicators;

    private List<FrameLayout> ipcPagers = new ArrayList<>();
    private List<MonitorBean> infos = new ArrayList<>();

    public IpcViewPager(Context context) {
        super(context);
    }

    public IpcViewPager(MainActivity activity, View rootView) {
        super(activity);
        this.activity = activity;
        initView(rootView);
    }

    private void initView(View rootView) {
        textIpcName =  rootView.findViewById(R.id.tv_ipc_name);
        indicatorLayout = rootView.findViewById(R.id.layout_viewpager_indicator);

        try {
            infos.clear();
            ipcPagers.clear();

            // Added device list page at last one
            MonitorBean monitorBean = new MonitorBean();
            monitorBean.setName(DEV_LIST);
            monitorBean.setDevid(DEV_LIST);
            infos.add(monitorBean);

            FrameLayout layout = ViewFactory.getDeviceListView(activity);
            ipcPagers.add(layout);
//            layout.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    onDeviceList();
//                }
//            });

            List<MonitorBean> list = CCPAppManager.getClientUser().getMonitorList();
            if(list.size()>0){
                infos.addAll(list);

                for (int i = 0; i < list.size(); i++) {
                    ipcPagers.add(ViewFactory.getImageView(getContext(),list.get(i).getDevid()));
                }

            }else{
                monitorBean = new MonitorBean();
                monitorBean.setName(getResources().getString(R.string.no_monitor_hint));
                monitorBean.setDevid("");
                infos.add(monitorBean);
                ipcPagers.add(ViewFactory.getImageView2(getContext()));
            }

            textIpcName.setText("");

            ipcViewPager = rootView.findViewById(R.id.viewpager_ipc);
            ipcViewPager.setAdapter(new IpcPagerAdapter());
            ipcViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    LOG.D(TAG, "[RYAN] onPageSelected > position: " + position);

                    final MonitorBean monitor = infos.get(position);
                    if (monitor.getDevid().equals(DEV_LIST)) {
                        textIpcName.setText("");
                    } else {
                        textIpcName.setText(monitor.getName());
                    }

                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            ipcViewPager.setCurrentItem(0);
            initIndicator();

        }catch (NullPointerException e){
            LOG.I(TAG,"tuichu");
            e.printStackTrace();
        }
    }

    private void initIndicator() {
        int size = ipcPagers.size();
        if(size==1){
            indicatorLayout.setVisibility(View.GONE);
        }
        // 设置指示器
        indicators = new ImageView[size];
        indicatorLayout.removeAllViews();
        for (int i = 0; i < indicators.length; i++) {
            View view = LayoutInflater.from(getContext()).inflate(
                    R.layout.view_cycle_viewpager_indicator, null);
            indicators[i] = view.findViewById(R.id.image_indicator);
            indicatorLayout.addView(view);
        }

        setIndicator(0);
    }

    private void setIndicator(int selectedPosition) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i]
                    .setBackgroundResource(R.mipmap.icon_point);
        }
        if (indicators.length > selectedPosition)
            indicators[selectedPosition]
                    .setBackgroundResource(R.mipmap.icon_point_pre);
    }

    public void onImageClick(MonitorBean info, int position, View imageView) {
        LOG.D(TAG, "[RYAN] onImageClick");
        DeviceActivitys.startDeviceActivity(getContext(),info.getDevid(),info.getName());
    }

    public void onNoContentAlert() {
        LOG.D(TAG, "[RYAN] onNoContentAlert");
        Intent intent = new Intent(getContext(), ActivityGuideDeviceAdd.class);
        getContext().startActivity(intent);
    }

    public void onDeviceList() {
        LOG.D(TAG, "[RYAN] onDeviceList");
//        activity.jumpToDevice();
    }

    private class IpcPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return ipcPagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final MonitorBean monitor = infos.get(position);
            FrameLayout v = ipcPagers.get(position);
            v.getChildAt(0).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(monitor.getDevid())) {
                        onNoContentAlert();
                    } else {
                        onImageClick(infos.get(position), position, v);
                    }
                }
            });

            container.addView(ipcPagers.get(position));
            return ipcPagers.get(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

}
