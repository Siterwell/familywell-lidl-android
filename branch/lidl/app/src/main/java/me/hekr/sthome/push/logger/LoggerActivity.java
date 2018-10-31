package me.hekr.sthome.push.logger;

import android.app.Activity;

import me.hekr.sthome.R;
import me.hekr.sthome.push.LogFragment;


public class LoggerActivity extends Activity {
	
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	initializeLogging();
    }
    
	
    private void initializeLogging() {
        LogFragment logFragment = (LogFragment) getFragmentManager().findFragmentById(R.id.framelog);

        LogCatWrapper logcat = new LogCatWrapper();
        logcat.setNext(logFragment.getLogView());

        Log.setLogNode(logcat);
    }
}
