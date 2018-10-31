package me.hekr.sthome.configuration.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.InvalidClassException;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.CodeEdit;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.crc.CoderUtils;
import me.hekr.sthome.tools.ECPreferenceSettings;
import me.hekr.sthome.tools.ECPreferences;
import me.hekr.sthome.tools.UnitTools;

public class EsptouchDemoActivity extends TopbarSuperActivity implements View.OnClickListener {

	private static final String TAG = "EsptouchDemoActivity";

	private TextView mTvApSsid;

	private CodeEdit mEdtApPassword;

	private Button mBtnConfirm;

	private EspWifiAdminSimple mWifiAdmin;

	private CheckBox rem;
	private String gatewaytype;
	private ECAlertDialog ecAlertDialog;
	@Override
	protected void onCreateInit() {
		gatewaytype= getIntent().getStringExtra("gatewaytype");
		mWifiAdmin = new EspWifiAdminSimple(this);
		mTvApSsid = (TextView) findViewById(R.id.tvApSssidConnected);
		mEdtApPassword = (CodeEdit) findViewById(R.id.edtApPassword);
		mBtnConfirm = (Button) findViewById(R.id.btnConfirm);
		rem = (CheckBox)findViewById(R.id.is_remember);
		rem.setChecked(isRememberPassword());
		mBtnConfirm.setOnClickListener(this);
		initGuider();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_esptouch;
	}





	private void initGuider()
	{
		getTopBarView().setTopBarStatus(1, 1, getResources().getString(R.string.net_configuration), null, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideSoftKeyboard();
				finish();
			}
		},null);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// display the connected ap's ssid
		String apSsid = mWifiAdmin.getWifiConnectedSsid();
		if (apSsid != null) {
			mTvApSsid.setText(apSsid);
		} else {
			mTvApSsid.setText("");
		}

		UnitTools unitTools = new UnitTools(this);
		String ds = unitTools.readSSidcode(apSsid);
       if(ds!=null){
		   mEdtApPassword.getCodeEdit().setText(CoderUtils.getDecrypt(ds));
		   mEdtApPassword.getCodeEdit().setSelection(CoderUtils.getDecrypt(ds).length());
		   mEdtApPassword.setCodeShow(true);
	   }

	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.btnConfirm) {
			hideSoftKeyboard();
			String apSsid = mTvApSsid.getText().toString();
			String apPassword = mEdtApPassword.getCodeEdit().getText().toString();

			if(TextUtils.isEmpty(apSsid)){

				if(ecAlertDialog==null || !ecAlertDialog.isShowing()){
					ecAlertDialog = ECAlertDialog.buildPositiveAlert(this, R.string.no_wifi,null);
					ecAlertDialog.show();
				}

			}
			else if(TextUtils.isEmpty(apPassword)){
				if(ecAlertDialog==null || !ecAlertDialog.isShowing()){
					ecAlertDialog = ECAlertDialog.buildPositiveAlert(this, R.string.password_is_empty,null);
					ecAlertDialog.show();
				}
			}else{
				if(rem.isChecked()){
					UnitTools unitTools = new UnitTools(this);
					unitTools.writeSSidcode(apSsid,CoderUtils.getEncrypt(apPassword));
					try {
						ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_CONFIG_REMEMBER_PASSWORD,true, true);
					} catch (InvalidClassException e) {
						e.printStackTrace();
					}

				}else{
					try {
						ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_CONFIG_REMEMBER_PASSWORD,false, true);
					} catch (InvalidClassException e) {
						e.printStackTrace();
					}
					UnitTools unitTools = new UnitTools(this);
					unitTools.writeSSidcode(apSsid,"");
				}
				Intent intent = new Intent(this,EsptouchAnimationActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("ssid",apSsid);
				bundle.putString("psw",apPassword);
				bundle.putString("gatewaytype",gatewaytype);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
			}


		}
	}

	private boolean isRememberPassword(){

		SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
		ECPreferenceSettings flag = ECPreferenceSettings.SETTINGS_CONFIG_REMEMBER_PASSWORD;
		boolean autoflag = sharedPreferences.getBoolean(flag.getId(), (boolean) flag.getDefaultValue());
		return autoflag;
	}

}
