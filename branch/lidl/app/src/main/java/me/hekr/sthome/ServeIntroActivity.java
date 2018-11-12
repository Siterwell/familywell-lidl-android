package me.hekr.sthome;

import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.Locale;

import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.tools.LOG;

/**
 * Created by gc-0001 on 2017/1/20.
 */
public class ServeIntroActivity extends TopbarSuperActivity {
    private TextView zh;

    @Override
    protected void onCreateInit() {
        initGuider();
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_intro;
    }

    private void initGuider(){
         getTopBarView().setTopBarStatus(1, 2, getResources().getString(R.string.serve), null, new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 finish();
             }
         },null);
    }

    private void initView(){
        WebView webView = findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setTextZoom(75);

        String language = Locale.getDefault().getLanguage();
        LOG.D(ServeIntroActivity.class.getSimpleName(), "[RYAN] language : " + language);

        String url = "file:///android_asset/privacy_english.html";
        if (language.equals("cs")){
            url = "file:///android_asset/privacy_czech.html";
        } else if (language.equals("de")){
            url = "file:///android_asset/privacy_dutch.html";
        } else if (language.equals("fr")) {
            url = "file:///android_asset/privacy_french.html";
        } else if (language.equals("it")) {
            url = "file:///android_asset/privacy_italian.html";
        } else if (language.equals("sl")) {
            url = "file:///android_asset/privacy_slovenian.html";
        }

        webView.loadUrl(url);


    }
}
