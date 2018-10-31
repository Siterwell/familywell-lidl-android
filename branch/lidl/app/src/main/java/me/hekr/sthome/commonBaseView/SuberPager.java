package me.hekr.sthome.commonBaseView;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.model.modelbean.MonitorBean;

/**
 * Created by Administrator on 2017/8/19.
 */

public class SuberPager extends FrameLayout implements ViewPager.OnPageChangeListener {
    private final String TAG = "SuberPager";
    private ItemOnClickListener itemOnClickListener;
    private List<MonitorBean> monitorBeanList;
    private Context context;
    private List<MyviewAdapter> myviewAdapters;
    private List<GridView> gridViews;
    private CustomViewPager viewPager;
    private int show_index;
    private LinearLayout indicatorLayout; // 指示器
    private static final int PER_MONITOR_NUM = 5;
    private MyViewPagerAdapter adapter;
    private ImageView[] indicators;


    public SuberPager(Context context) {
        super(context);
        init(context);
    }

    public SuberPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SuberPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }

    private void init(Context context){
         this.context = context;
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_cycle_viewpager_contet_sub, null);

        viewPager = (CustomViewPager) view.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(1);
        indicatorLayout = (LinearLayout) view
                .findViewById(R.id.layout_viewpager_indicator);
        TextView textview = (TextView)view.findViewById(R.id.title);
        textview.setVisibility(View.GONE);
        addView(view);
    }

    /**
     * 初始化viewpager
     *
     *            要显示的views
     * @param showPosition
     *            默认显示位置
     */
    public void setData(List<MonitorBean> list,int showPosition) {
        show_index = showPosition;
        monitorBeanList = list;
        this.gridViews = new ArrayList<>();
        this.myviewAdapters = new ArrayList<>();
        this.gridViews.clear();



        List<MonitorBean> listm = new ArrayList<>();
        for(int i=0;i<monitorBeanList.size();i++){


            if(i%PER_MONITOR_NUM == 0) {
                listm = new ArrayList<>();
                //listm.clear();
            }

                listm.add(monitorBeanList.get(i));
                if(i == (monitorBeanList.size()-1)){
                    GridView grid = new GridView(context);
                    this.gridViews.add(grid);
                    MyviewAdapter myviewAdapter = new MyviewAdapter(context,showPosition,listm,i/PER_MONITOR_NUM);
                    myviewAdapters.add(myviewAdapter);
                    break;
                }


              if(i%PER_MONITOR_NUM == (PER_MONITOR_NUM - 1 )){
                  GridView grid = new GridView(context);
                  this.gridViews.add(grid);
                  MyviewAdapter myviewAdapter = new MyviewAdapter(context,showPosition,listm,i/PER_MONITOR_NUM);
                  myviewAdapters.add(myviewAdapter);
              }

        }


        int ivSize = list.size()%PER_MONITOR_NUM>0?(list.size()/PER_MONITOR_NUM+1):list.size()/PER_MONITOR_NUM;
        // 设置指示器
        indicators = new ImageView[ivSize];

        indicatorLayout.removeAllViews();
        for (int i = 0; i < indicators.length; i++) {
            View view = LayoutInflater.from(context).inflate(
                    R.layout.view_cycle_viewpager_indicator, null);
            indicators[i] = (ImageView) view.findViewById(R.id.image_indicator);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            view.setLayoutParams(layoutParams);
            indicatorLayout.addView(view);
        }

        if(adapter!=null){
            adapter = null;
        }

        adapter = new MyViewPagerAdapter();

        // 默认指向第一项，下方viewPager.setCurrentItem将触发重新计算指示器指向
        setIndicator(show_index/PER_MONITOR_NUM);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setOnPageChangeListener(this);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(show_index/PER_MONITOR_NUM);

    }


    /**
     * 设置指示器
     *
     * @param selectedPosition
     *            默认指示器位置
     */
    private void setIndicator(int selectedPosition) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i]
                    .setBackgroundResource(R.mipmap.icon_point_sub);
        }
        if (indicators.length > selectedPosition)
            indicators[selectedPosition]
                    .setBackgroundResource(R.mipmap.icon_point_pre_sub);
    }

    /**
     * 设置指示器居中，默认指示器在右方
     */
    public void setIndicatorCenter() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        indicatorLayout.setLayoutParams(params);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setIndicator(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 页面适配器 返回对应的view
     *
     * @author Yuedong Li
     *
     */
    private class MyViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {

            int yu = monitorBeanList.size()%PER_MONITOR_NUM;
            int s = monitorBeanList.size()/PER_MONITOR_NUM;

            return yu>0?(s+1):s;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (View)arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public View instantiateItem(ViewGroup container, final int position) {
            GridView v = gridViews.get(position);
            v.setNumColumns(PER_MONITOR_NUM);
            container.addView(v);
            MyviewAdapter myviewAdapter = myviewAdapters.get(position);
            v.setAdapter(myviewAdapter);
            v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                     if(itemOnClickListener!=null){
                         itemOnClickListener.onImageClick( position*PER_MONITOR_NUM + i);
                     }

                }
            });
            return v;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }




    }



    public  interface ItemOnClickListener {
       void onImageClick(int position);
    }



    public static class MyviewAdapter extends BaseAdapter {

        private List<MonitorBean> lists;
        private ViewHolder viewHolder;
        private Context context;
        private int current_index = -1;
        private int page_index;

        public MyviewAdapter(Context context, int current_index, List<MonitorBean> lists,int index) {
            this.context = context;
            this.current_index = current_index;
            this.lists = lists;
            this.page_index = index;
        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public MonitorBean getItem(int i) {
            return lists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            viewHolder = null;
            final MonitorBean eq = lists.get(position);
            if( convertView == null){
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.cell_sub_monitor,null);
                viewHolder.imageView= (ImageView) convertView.findViewById(R.id.sub_monitor);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.sub_monitor_name);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.textView.setText(eq.getName());
            if(getCurrent_index()==(position+page_index*PER_MONITOR_NUM)){
                viewHolder.imageView.setImageResource(R.mipmap.ship_sel);
                viewHolder.textView.setTextColor(context.getResources().getColor(R.color.monitor_sel));
            }else{
                viewHolder.imageView.setImageResource(R.mipmap.ship);
                viewHolder.textView.setTextColor(context.getResources().getColor(R.color.monitor_unsel));
            }

            return convertView;
        }

        public int getCurrent_index() {
            return current_index;
        }

        public void setCurrent_index(int current_index) {
            this.current_index = current_index;
        }

        public List<MonitorBean> getLists() {
            return lists;
        }

        public void setLists(List<MonitorBean> lists) {
            this.lists = lists;
        }

        private class ViewHolder{
            ImageView imageView;
            TextView textView;
        }
    }

}
