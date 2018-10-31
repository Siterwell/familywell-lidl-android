package me.hekr.sthome.commonBaseView;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.R;

/**
 * Created by gc-0001 on 2017/2/6.
 */
public class LoginLogPopupwindow extends PopupWindow {

    private final String TAG = "LoginLogPopupwindow";
    private ListView listView;
    private UserAdapter userAdapter;
    private ArrayList<UserBean> userlist;
    private int laywidth;
    private int layheight;
    private ItemOperationListener itemOperationListener;



    public LoginLogPopupwindow(Context context,String log,int width,int height) {
        super(context);
        laywidth  = width;
        layheight = height;
        listView = new ListView(context);
        this.setContentView(listView);
        this.setWidth(laywidth);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(dw);
        userAdapter = new UserAdapter(context);
        initdata(log);
        userAdapter.setData(userlist);
        listView.setDivider(null);
        listView.setAdapter(userAdapter);
        listView.setOnItemClickListener(onItemGroupClickListener);

    }

   private void initdata(String json){
       userlist = new ArrayList<UserBean>();
       try {
           JSONArray array = new JSONArray(json);
           for(int i=0;i<array.length();i++){

               UserBean bean = new UserBean();
               bean.setUsername(array.getJSONObject(i).getString("username"));
               bean.setPwd(array.getJSONObject(i).getString("pwd"));
               userlist.add(bean);
           }

       } catch (JSONException e) {
           Log.i(TAG,"string is null");
       }
   }

    private final AdapterView.OnItemClickListener onItemGroupClickListener
            = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {

            if(userAdapter == null || userAdapter.getItem(position) == null) {
                return ;
            }
            String name = userAdapter.getItem(position).getUsername();
            String pwd = userAdapter.getItem(position).getPwd();
            itemOperationListener.setNameAndPwd(name,pwd);
        }
    };

    class UserAdapter extends ArrayAdapter<UserBean> {

        Context mContext;
        public UserAdapter(Context context) {
            super(context, 0);
            mContext = context;
        }


        public void setData(List<UserBean> data) {

            setNotifyOnChange(false);
            clear();
            setNotifyOnChange(true);
            if (data != null) {
                for (UserBean appEntry : data) {
                    add(appEntry);
                }
            }
        }

        @Override
        public View getView(final  int position, View convertView, ViewGroup parent) {

            View view ;
            ViewHolder mViewHolder;
            if(convertView == null || convertView.getTag() == null) {
                view = View.inflate(mContext, R.layout.cell_logpick, null);
                mViewHolder = new ViewHolder();
                mViewHolder.relativeLayout = (RelativeLayout)view.findViewById(R.id.liner_phone);
                mViewHolder.item_delete=(ImageButton)view.findViewById(R.id.delete);
                mViewHolder.txt_username = (TextView) view.findViewById(R.id.et_username);
                view.setTag(mViewHolder);
            } else {
                view = convertView;
                mViewHolder = (ViewHolder) view.getTag();
            }

            UserBean contacts = getItem(position);
            if(contacts != null) {
                mViewHolder.txt_username.setText(contacts.getUsername());
                mViewHolder.item_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemOperationListener.deleteItem(position);
                    }
                });

            }

            ListView.LayoutParams params = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT,layheight);//设置宽度和高度
            view.setLayoutParams(params);
            return view;
        }



        class ViewHolder {
            ImageButton item_delete;
            TextView txt_username;
            RelativeLayout relativeLayout;

        }
    }

    /**
     * riqi
     * Created by xjj
     */
    public static class UserBean  implements Parcelable {

        public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
            @Override
            public UserBean[] newArray(int size) {
                return new UserBean[size];
            }

            @Override
            public UserBean createFromParcel(Parcel in) {
                return new UserBean(in);
            }
        };


        private String username;
        /**联系人昵称*/
        private String pwd;

        public String getUsername() {
            return username;
        }
        /**
         * @param username the nickname to set
         */
        public void setUsername(String username) {
            this.username = username;
        }

        /**
         * @return the contactid
         */
        public String getPwd() {
            return pwd;
        }
        /**
         * @param pwd the contactid to set
         */
        public void setPwd(String pwd) {
            this.pwd = pwd;
        }


        private UserBean (Parcel in) {
            this.username = in.readString();
            this.pwd = in.readString();
        }






        public UserBean() {

        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.username);
            dest.writeString(this.pwd);
        }

        @Override
        public String toString() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username" , username);
                jsonObject.put("pwd" , pwd);

                return jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }


    //为listview动态设置高度（有多少条目就显示多少条目）
    public void setListViewHeight() {
        //获取listView的adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        //listAdapter.getCount()返回数据项的数目
        for (int i = 0,len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() *  (listAdapter .getCount() - 1));
        Log.i("ceshi","params.height"+params.height);
        listView.setLayoutParams(params);
        userAdapter.notifyDataSetChanged();
    }

    static public interface ItemOperationListener{
        void setNameAndPwd(String name, String pwd);
        void deleteItem(int index);
    }

    public ItemOperationListener getItemOperationListener() {
        return itemOperationListener;
    }

    public void setItemOperationListener(ItemOperationListener itemOperationListener) {
        this.itemOperationListener = itemOperationListener;
    }
}
