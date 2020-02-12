package me.hekr.sthome.xmipc;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.tools.LOG;
import me.hekr.sthome.wheelwidget.helper.FileUtils;

/**
 * Created by Administrator on 2017/7/17.
 */
@SuppressLint("ValidFragment")
public class LocalpicFragment extends Fragment {
    private ListView listView;
    private View view = null;
    private static final String TAG = "LocalpicFragment";
    private List<Localfile> list;
    private LocalPicAdapter localPicAdapter;
    public LocalpicFragment()
    {
        super();

    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_ipc_pic, null);

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
        listView = (ListView) view.findViewById(R.id.piclistview);
        localPicAdapter = new LocalPicAdapter(this.getActivity(),list);
        listView.setAdapter(localPicAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                File file=new File(localPicAdapter.getItem(i).getFilepath());
                Intent it =new Intent(Intent.ACTION_VIEW);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    LOG.I(TAG, "initview > package: " + getActivity().getPackageName());
                    it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri contentUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName()+".fileprovider", file);
                    it.setDataAndType(contentUri, "image/*");
                    startActivity(it);
                } else {
                    Uri mUri = Uri.parse("file://"+file.getPath());
                    it.setDataAndType(mUri, "image/*");
                    startActivity(it);
                }

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view,final int i, long l) {

                ECAlertDialog ecAlertDialog = ECAlertDialog.buildAlert(LocalpicFragment.this.getActivity(), R.string.delete_or_not, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int m) {
                        File file=new File(list.get(i).getFilepath());
                        FileUtils.delete(file);
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);   //, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            Uri uri = Uri.fromFile(new File(list.get(i).getFilepath()));
                            intent.setData(uri);
                            LocalpicFragment.this.getActivity().sendBroadcast(intent);
                            list.remove(i);
                        localPicAdapter.refreshList(list);
                    }
                });
                ecAlertDialog.setCancelable(false);
                ecAlertDialog.show();
                return true;
            }
        });
    }


    private void intdata(){

        list = new ArrayList<>();


    }

    public void refresh(List<Localfile> localfiles){
        list.clear();
        list.addAll(localfiles);
        localPicAdapter.refreshList(localfiles);
    }

}
