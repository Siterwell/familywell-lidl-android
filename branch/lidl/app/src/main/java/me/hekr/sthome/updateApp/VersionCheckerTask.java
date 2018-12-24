package me.hekr.sthome.updateApp;

import android.os.AsyncTask;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import me.hekr.sthome.BuildConfig;
import me.hekr.sthome.tools.LOG;

/**
 * Created by ryanhsueh on 2018/11/8
 */
public class VersionCheckerTask extends AsyncTask<String, Document, Document> {

    private Document document;

    @Override
    protected Document doInBackground(String... params) {

        try {
            LOG.D("VersionCheckerTask", "[RYAN] doInBackground > ");


            document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
                    .get();
        } catch (HttpStatusException ex2) {
            ex2.printStackTrace();
        } catch (IOException ex1) {
            ex1.printStackTrace();
        }
        return document;
    }

}