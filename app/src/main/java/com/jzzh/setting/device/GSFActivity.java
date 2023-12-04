package com.jzzh.setting.device;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

public class GSFActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = GSFActivity.class.getSimpleName();
    private static final Uri sUri = Uri.parse("content://com.google.android.gsf.gservices");
    public static final String gsf_reg_url = "https://www.google.com/android/uncertified/";
    public static final String GOOGLE_PLAY = "com.android.vending";
    public static final String GOOGLE_PLAY_MAIN = "com.android.vending.AssetBrowserActivity";

    private View mGsfContentLayout;
    private ImageView mGsfSwitch;
    private ImageView mManualSwitch;
    private TextView mManualTv;
    private TextView mGsfIdState;
    private TextView mGsfIdBtn;
    private boolean mManualShow = false;

    private Dialog dialog;

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
            mGsfIdBtn.setText(R.string.gsf_btn_select);
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

        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
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
            onClickGsfIdBtn();
        }
    }

    private void onClickGsfIdBtn() {
        String gsfKey = getGsfId();
        updateGsfIdState();

        if (!isWifiConnected()) {
            Toast.makeText(this, R.string.msg_wifi_not_connected, Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, "gsfKey is empty");
        if (gsfKey == null || gsfKey.isEmpty()) {
            Log.d(TAG, "gsfKey is empty");
            dialog = new AlertDialog.Builder(this)
                    .setMessage(R.string.msg_gsf_id_empty)
                    .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    })
                    .show();
            return;
        }

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("gsfid", gsfKey);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        } else {
            Log.e(TAG, "clipboard service is null!!");
        }

        Toast.makeText(this, R.string.toast_msg_gsf_select, Toast.LENGTH_LONG).show();

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(gsf_reg_url));
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "call web page failed!!");
            e.printStackTrace();
        }
    }

    public boolean isWifiConnected() {
        boolean result = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            result = cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
