package com.jzzh.setting.device;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

public class GSFActivity extends BaseActivity implements View.OnClickListener {

    private static final Uri sUri = Uri.parse("content://com.google.android.gsf.gservices");

    private View mGsfContentLayout;
    private ImageView mGsfSwitch;
    private ImageView mManualSwitch;
    private TextView mManualTv;
    private TextView mGsfIdState;
    private TextView mGsfIdBtn;
    private boolean mManualShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gsf);
        mGsfSwitch = findViewById(R.id.title_function_1);
        mGsfSwitch.setOnClickListener(this);
        mGsfContentLayout = findViewById(R.id.gsf_content_layout);
        mManualSwitch = findViewById(R.id.gsf_how_to_use_msg_switch);
        mManualSwitch.setOnClickListener(this);
        mManualTv = findViewById(R.id.gsf_how_to_use_msg);
        mManualTv.setText(Html.fromHtml(getString(R.string.gsf_how_to_use_msg)));
        mGsfIdState = findViewById(R.id.gsf_id_state_tv);
        mGsfIdBtn = findViewById(R.id.gsf_id_state_btn);
        mGsfIdBtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGooglePlayEnableState();
        updateManualState();
        updateGsfIdState();
    }

    private void updateGsfIdState() {
        if(getGsfId() != null) {
            mGsfIdState.setText(getString(R.string.gsf_id_obtain, getGsfId()));
            mGsfIdBtn.setText(R.string.gsf_btn_select);
        } else {
            mGsfIdState.setText(getString(R.string.gsf_id_miss));
            mGsfIdBtn.setText(R.string.gsf_btn_login);
        }
    }

    private void updateManualState() {
        if(mManualShow) {
            mManualSwitch.setSelected(true);
            mManualTv.setVisibility(View.VISIBLE);
        } else {
            mManualSwitch.setSelected(false);
            mManualTv.setVisibility(View.GONE);
        }
    }

    private void updateGooglePlayEnableState() {
        int state = getGooglePlayEnable();
        if(state == 1) {
            mGsfSwitch.setSelected(true);
            mGsfContentLayout.setVisibility(View.VISIBLE);
        } else {
            mGsfSwitch.setSelected(false);
            mGsfContentLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setGooglePlayEnable(int enable) {
        Settings.System.putInt(getContentResolver(),"google_play_enable", enable);
    }

    private int getGooglePlayEnable() {
        return Settings.System.getInt(getContentResolver(),"google_play_enable", 0);
    }

    private String getGsfId() {
        try {
            Cursor query = getContentResolver().query(sUri, null, null, new String[] { "android_id" }, null);
            if (query == null) {
                return "Not found";
            }
            if (!query.moveToFirst() || query.getColumnCount() < 2) {
                query.close();
                return "Not found";
            }

            final String String = query.getString(1);
            query.close();

            return String.toUpperCase().trim();
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    @Override
    public void onClick(View view) {
        if(view == mGsfSwitch) {
            int state = getGooglePlayEnable();
            setGooglePlayEnable(state == 1 ? 0 : 1);
            updateGooglePlayEnableState();
        } else if(view == mManualSwitch) {
            mManualShow = !mManualShow;
            updateManualState();
        } else if(view == mGsfIdBtn) {

        }
    }
}
