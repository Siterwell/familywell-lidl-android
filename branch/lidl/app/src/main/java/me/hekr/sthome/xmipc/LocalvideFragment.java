package me.hekr.sthome.xmipc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.R;

/**
 * Created by Administrator on 2017/7/17.
 */
@SuppressLint("ValidFragment")
public class LocalvideFragment extends Fragment {

    private ListView listView;
    private View view = null;
    private static final String TAG = "LocalvideFragment";
    private List<Localfile> list;
    private LocalVideoAdapter localVideoAdapter;
    public LocalvideFragment()
    {
        super();

    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_ipc_video, null);
            intdata();
            initview();
        }
        //缓存的rootView需要判断是否已经被加载过parent,如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误
        ViewGroup viewparent = (ViewGroup) view.getParent();
        if (viewparent != null) {
            viewparent.removeView(view);
        }


        return view;
    }

    private void initview(){
        listView = (ListView) view.findViewById(R.id.videolistview);
        localVideoAdapter = new LocalVideoAdapter(this.getActivity(),list);
        listView.setAdapter(localVideoAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent("android.intent.action.VIEW");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                String type = "video/*";

                Uri uri = Uri.fromFile(new File(localVideoAdapter.getItem(i).getFilepath()));
                intent.setDataAndType(uri, type);
                startActivity(intent);
            }
        });
    }




    private void intdata(){

        list = new ArrayList<>();
    }

    public void refresh(List<Localfile> localfiles){
        localVideoAdapter.refreshList(localfiles);
    }
}
