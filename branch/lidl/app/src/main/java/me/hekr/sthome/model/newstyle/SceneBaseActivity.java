package me.hekr.sthome.model.newstyle;

import android.content.DialogInterface;
import android.view.KeyEvent;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;

public class SceneBaseActivity extends TopbarSuperActivity {
    @Override
    protected void onCreateInit() {

    }

    @Override
    protected int getLayoutId() {
        return 0;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            ModelConditionPojo.getInstance().cleanModleCondition();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
