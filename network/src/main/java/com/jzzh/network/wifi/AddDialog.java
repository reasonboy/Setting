package com.jzzh.network.wifi;

import static com.jzzh.network.wifi.WifiUtils.METERED_OVERRIDE_METERED;
import static com.jzzh.network.wifi.WifiUtils.METERED_OVERRIDE_NONE;
import static com.jzzh.network.wifi.WifiUtils.METERED_OVERRIDE_NOT_METERED;

import android.app.Dialog;
import android.content.Context;
import android.net.ProxyInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jzzh.network.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private EditText mSSID,mPassword;
    private EditText mIpAddressEt;
    private EditText mGatewayEt;
    private EditText mNetworkPrefixLengthEt;
    private EditText mDns1Et;
    private EditText mPACUrlEt;
    private EditText mProxyHostnameEt;
    private EditText mProxyPortEt;
    private EditText mBypassProxyForEt;
    private ImageView mNone,mWep,mWpa;
    private ArrayList<ImageView> mImageList = new ArrayList<>();
    private Button mCancle,mConnect;
    private DialogCallback mCallback;
    private String mCapabilities;
    private ImageView mAdvancedOptions;
    private boolean mAdvancedOptionsVisible = false;
    private LinearLayout mAdvancedOptionsLayout;
    private LinearLayout mStaticIpSettingsLayout;
    private RelativeLayout mShowAdvancedOptions;
    private LinearLayout mProxyAutoConfig;
    private LinearLayout mProxyManual;
    private ImageView mMeteredDownDrop;
    private ImageView mProxyDownDrop;
    private ImageView mIpSettingsDownDrop;
    private MeteredDialog mMeteredDialog;
    private ProxyDialog mProxyDialog;
    private IPSettingsDialog mIPSettingsDialog;
    private TextView mMeteredResult;
    private TextView mProxyResult;
    private TextView mIPAssignmentResult;
    private int mMeteredType = METERED_OVERRIDE_NONE;
    private String mIPAssignment = "DHCP";
    private String mProxySettings = "NONE";

    private static final String NONE = "OPEN";
    private static final String WEP = "WEP";
    private static final String WPA = "WPA";


    public AddDialog(Context context, int style, DialogCallback callback) {
        super(context, style);
        Log.v("xml_log_dia","AddDialog" );
        mContext = context;
        mCallback = callback;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.wifi_add_dialog);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //getWindow().clearFlags( WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mSSID = findViewById(R.id.wifi_add_dialog_name);
        mPassword = findViewById(R.id.wifi_add_dialog_password);
        mNone = findViewById(R.id.wifi_add_dialog_none);
        mWep = findViewById(R.id.wifi_add_dialog_wep);
        mWpa = findViewById(R.id.wifi_add_dialog_wpa);
        mCancle = findViewById(R.id.wifi_add_dialog_cancle);
        mConnect = findViewById(R.id.wifi_add_dialog_connect);
        mNone.setOnClickListener(this);
        mWep.setOnClickListener(this);
        mWpa.setOnClickListener(this);
        mCancle.setOnClickListener(this);
        mConnect.setOnClickListener(this);
        mImageList.add(mNone);
        mImageList.add(mWep);
        mImageList.add(mWpa);
        setCheck(WPA);
        mIpAddressEt = findViewById(R.id.wifi_ip_address);
        mGatewayEt = findViewById(R.id.wifi_gateway);
        mNetworkPrefixLengthEt = findViewById(R.id.wifi_network_prefix_length);
        mDns1Et = findViewById(R.id.wifi_dns1);
        mPACUrlEt = findViewById(R.id.wifi_pac_url);
        mProxyHostnameEt = findViewById(R.id.wifi_proxy_hostname);
        mProxyPortEt = findViewById(R.id.wifi_proxy_port);
        mBypassProxyForEt = findViewById(R.id.wifi_bypass_proxy_for);
        mAdvancedOptions = findViewById(R.id.wifi_connect_dialog_show_advanced_options);
        mAdvancedOptions.setOnClickListener(this);
        mAdvancedOptionsLayout = findViewById(R.id.ll_advanced_options);
        mMeteredDownDrop = findViewById(R.id.metered_down_drop);
        mMeteredDownDrop.setOnClickListener(this);
        mProxyDownDrop = findViewById(R.id.proxy_down_drop);
        mProxyDownDrop.setOnClickListener(this);
        mIpSettingsDownDrop = findViewById(R.id.ip_settings_down_drop);
        mIpSettingsDownDrop.setOnClickListener(this);
        mMeteredResult = findViewById(R.id.metered_result);
        mProxyResult = findViewById(R.id.proxy_result);
        mIPAssignmentResult = findViewById(R.id.ip_settings_result);
        mStaticIpSettingsLayout = findViewById(R.id.ll_static_ip_settings);
        mShowAdvancedOptions = findViewById(R.id.ll_show_advanced_options);
        mProxyAutoConfig = findViewById(R.id.ll_proxy_auto_config);
        mProxyManual = findViewById(R.id.ll_proxy_manual);
        // Metered Dialog
        mMeteredDialog = new MeteredDialog(mContext, R.style.ZhDialog, new MeteredDialog.DialogCallback() {
            @Override
            public void callBackData(MeteredDialog.Type data) {
                switch (data) {
                    case AUTO:
                        mMeteredResult.setText(R.string.metered_dialog_auto);
                        mMeteredType = METERED_OVERRIDE_NONE;
                        break;
                    case METERED:
                        mMeteredResult.setText(R.string.metered_dialog_metered);
                        mMeteredType = METERED_OVERRIDE_METERED;
                        break;
                    case UNMETERED:
                        mMeteredResult.setText(R.string.metered_dialog_unmetered);
                        mMeteredType = METERED_OVERRIDE_NOT_METERED;
                        break;
                }
            }
        });
        // ProxyDialog
        mProxyDialog = new ProxyDialog(mContext, R.style.ZhDialog, new ProxyDialog.DialogCallback() {
            @Override
            public void callBackData(ProxyDialog.Type data) {
                switch (data){
                    case NONE:
                        mProxyResult.setText(R.string.proxy_dialog_none);
                        setProxyAutoConfigVisible(false);
                        setProxyManualVisible(false);
                        mProxySettings = "NONE";
                        break;
                    case MANUAL:
                        mProxyResult.setText(R.string.proxy_dialog_manual);
                        setProxyAutoConfigVisible(false);
                        setProxyManualVisible(true);
                        mProxySettings = "STATIC";
                        break;
                    case AUTO_CONFIG:
                        mProxyResult.setText(R.string.proxy_dialog_auto_config);
                        setProxyAutoConfigVisible(true);
                        setProxyManualVisible(false);
                        mProxySettings = "PAC";
                        break;
                }
            }
        });
        // IPSettings Dialog
        mIPSettingsDialog = new IPSettingsDialog(mContext, R.style.ZhDialog, new IPSettingsDialog.DialogCallback() {
            @Override
            public void callBackData(IPSettingsDialog.Type data) {
                switch (data) {
                    case DHCP:
                        mIPAssignmentResult.setText(R.string.ip_settings_dialog_dhcp);
                        mIPAssignment = "DHCP";
                        setStaticIpSettingsVisible(false);
                        break;
                    case STATIC:
                        mIPAssignmentResult.setText(R.string.ip_settings_dialog_static);
                        mIPAssignment = "STATIC";
                        setStaticIpSettingsVisible(true);
                        break;
                }
            }
        });
    }

    private void setCheck(String capabilities) {
        Log.v("xml_log_dia","capabilities = " + capabilities);
        //closeKeybord();
        mCapabilities = capabilities;
        for(int i = 0;i<mImageList.size();i++) {
            mImageList.get(i).setImageResource(R.drawable.check_off);
        }
        if(capabilities.equals(NONE)) {
            mNone.setImageResource(R.drawable.check_on);
            mPassword.setVisibility(View.GONE);
        } else if(capabilities.equals(WEP)){
            mWep.setImageResource(R.drawable.check_on);
            mPassword.setVisibility(View.VISIBLE);
        } else if(capabilities.equals(WPA)){
            mWpa.setImageResource(R.drawable.check_on);
            mPassword.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.wifi_add_dialog_none) {
            setCheck(NONE);
        } else if (id == R.id.wifi_add_dialog_wep) {
            setCheck(WEP);
        } else if (id == R.id.wifi_add_dialog_wpa) {
            setCheck(WPA);
        } else if (id == R.id.wifi_add_dialog_cancle) {
            dismiss();
        } else if (id == R.id.wifi_add_dialog_connect) {
            String[] ipSettingsData = new String[]{mIpAddressEt.getText().toString(), "255.255.255.0", mGatewayEt.getText().toString(), mDns1Et.getText().toString()};
            ProxyInfo proxyInfo = null;
            if (mProxySettings.equals("PAC")) {
                String pacUrl = mPACUrlEt.getText().toString();
                Uri uri = Uri.parse(pacUrl);
                proxyInfo = ProxyInfo.buildPacProxy(uri);
            } else if (mProxySettings.equals("STATIC")) {
                String hostName = mProxyHostnameEt.getText().toString();
                String portStr = mProxyPortEt.getText().toString();
                int port = Integer.parseInt(portStr);
                String bypassProxyFor = mBypassProxyForEt.getText().toString();
                List<String> bypassProxyForList = Arrays.asList(bypassProxyFor.split(","));
                proxyInfo = ProxyInfo.buildDirectProxy(hostName, port, bypassProxyForList);
            }
            mCallback.callBackData(new String[]{mSSID.getText().toString(), mPassword.getText().toString(), mCapabilities},mMeteredType, mIPAssignment, ipSettingsData, mProxySettings, proxyInfo);
            dismiss();
        } else if (id == R.id.wifi_connect_dialog_show_advanced_options) {
            mAdvancedOptionsVisible = !mAdvancedOptionsVisible;
            setAdvancedOptionsVisible(mAdvancedOptionsVisible);
        } else if (id == R.id.metered_down_drop) {
            setMeteredDialogPosition();
            mMeteredDialog.show();
        } else if (id == R.id.proxy_down_drop) {
            setProxyDialogPosition();
            mProxyDialog.show();
        } else if (id == R.id.ip_settings_down_drop) {
            setIPSettingsDialogPosition();
            mIPSettingsDialog.show();
        }
    }

    private void closeKeybord() {
        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getWindow().peekDecorView();
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    private void setAdvancedOptionsVisible(boolean visible) {
        if (visible) {
            mAdvancedOptions.setImageResource(R.drawable.check_on);
            mAdvancedOptionsLayout.setVisibility(View.VISIBLE);
        } else {
            mAdvancedOptions.setImageResource(R.drawable.check_off);
            mAdvancedOptionsLayout.setVisibility(View.GONE);
        }
    }

    private void setStaticIpSettingsVisible(boolean visible) {
        if (visible) {
            mStaticIpSettingsLayout.setVisibility(View.VISIBLE);
        } else {
            mStaticIpSettingsLayout.setVisibility(View.GONE);
        }
    }

    private void setProxyAutoConfigVisible(boolean visible){
        if(visible){
            mProxyAutoConfig.setVisibility(View.VISIBLE);
        }else {
            mProxyAutoConfig.setVisibility(View.GONE);
        }
    }

    private void setProxyManualVisible(boolean visible) {
        if (visible) {
            mProxyManual.setVisibility(View.VISIBLE);
        } else {
            mProxyManual.setVisibility(View.GONE);
        }
    }

    private void setMeteredDialogPosition() {
        Window meteredDialogWindow = mMeteredDialog.getWindow();
        WindowManager.LayoutParams meteredDialogLp = meteredDialogWindow.getAttributes();
        meteredDialogWindow.setGravity(Gravity.CENTER | Gravity.TOP);
        int[] location = new int[2];
        mMeteredDownDrop.getLocationOnScreen(location);
        meteredDialogLp.y = location[1];
        meteredDialogWindow.setAttributes(meteredDialogLp);
    }

    private void setProxyDialogPosition(){
        Window meteredDialogWindow = mProxyDialog.getWindow();
        WindowManager.LayoutParams meteredDialogLp = meteredDialogWindow.getAttributes();
        meteredDialogWindow.setGravity(Gravity.CENTER | Gravity.TOP);
        int[] location = new int[2];
        mProxyDownDrop.getLocationOnScreen(location);
        meteredDialogLp.y = location[1];
        meteredDialogWindow.setAttributes(meteredDialogLp);
    }

    private void setIPSettingsDialogPosition() {
        Window iPSettingsDialogWindow = mIPSettingsDialog.getWindow();
        WindowManager.LayoutParams iPSettingsDialogLp = iPSettingsDialogWindow.getAttributes();
        iPSettingsDialogWindow.setGravity(Gravity.CENTER | Gravity.TOP);
        int[] location = new int[2];
        mIpSettingsDownDrop.getLocationOnScreen(location);
        iPSettingsDialogLp.y = location[1];
        iPSettingsDialogWindow.setAttributes(iPSettingsDialogLp);
    }

    public interface DialogCallback {
        void callBackData(String[] data, int meteredType, String ipAssignment, String[] ipSettingsData, String proxySettings, ProxyInfo proxyInfo);
    }
}
