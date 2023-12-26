package com.jzzh.network.wifi;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.jzzh.network.wifi.WifiUtils.METERED_OVERRIDE_METERED;
import static com.jzzh.network.wifi.WifiUtils.METERED_OVERRIDE_NONE;
import static com.jzzh.network.wifi.WifiUtils.METERED_OVERRIDE_NOT_METERED;
import static com.jzzh.network.wifi.WifiUtils.getIPv4Address;
import static com.jzzh.network.wifi.WifiUtils.getNetworkPart;
import static com.jzzh.network.wifi.WifiUtils.proxyValidate;

import android.app.Dialog;
import android.content.Context;
import android.net.ProxyInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddDialog extends Dialog implements View.OnClickListener , TextWatcher {

    private Context mContext;
    private EditText mSsidEt,mPasswordEt;
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
    private RelativeLayout mShowPasswordLayout;
    private ImageView mShowPassword;
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
    private ProxyInfo mHttpProxyInfo;
    private boolean mPasswordVisible = false;

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
        mSsidEt = findViewById(R.id.wifi_add_dialog_name);
        mSsidEt.addTextChangedListener(this);
        mSsidEt.requestFocus();
        mPasswordEt = findViewById(R.id.wifi_add_dialog_password);
        mPasswordEt.addTextChangedListener(this);
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
        mConnect.setEnabled(false);
        mConnect.setTextAppearance(mContext, R.style.NegativeDialogButtonDividerStyle);
        mImageList.add(mNone);
        mImageList.add(mWep);
        mImageList.add(mWpa);
        mShowPasswordLayout = findViewById(R.id.ll_enable_show_password);
        setCheck(WPA);
        mIpAddressEt = findViewById(R.id.wifi_ip_address);
        mIpAddressEt.addTextChangedListener(this);
        mGatewayEt = findViewById(R.id.wifi_gateway);
        mGatewayEt.addTextChangedListener(this);
        mNetworkPrefixLengthEt = findViewById(R.id.wifi_network_prefix_length);
        mNetworkPrefixLengthEt.addTextChangedListener(this);
        mDns1Et = findViewById(R.id.wifi_dns1);
        mDns1Et.addTextChangedListener(this);
        mPACUrlEt = findViewById(R.id.wifi_pac_url);
        mPACUrlEt.addTextChangedListener(this);
        mProxyHostnameEt = findViewById(R.id.wifi_proxy_hostname);
        mProxyHostnameEt.addTextChangedListener(this);
        mProxyPortEt = findViewById(R.id.wifi_proxy_port);
        mProxyPortEt.addTextChangedListener(this);
        mBypassProxyForEt = findViewById(R.id.wifi_bypass_proxy_for);
        mBypassProxyForEt.addTextChangedListener(this);
        mShowPassword = findViewById(R.id.wifi_add_dialog_show_password);
        mShowPassword.setOnClickListener(this);
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
                enableSubmitIfAppropriate();
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
                enableSubmitIfAppropriate();
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
            mPasswordEt.setVisibility(GONE);
            mShowPasswordLayout.setVisibility(GONE);
        } else if(capabilities.equals(WEP)){
            mWep.setImageResource(R.drawable.check_on);
            mPasswordEt.setVisibility(VISIBLE);
            mShowPasswordLayout.setVisibility(VISIBLE);
        } else if(capabilities.equals(WPA)){
            mWpa.setImageResource(R.drawable.check_on);
            mPasswordEt.setVisibility(VISIBLE);
            mShowPasswordLayout.setVisibility(VISIBLE);
        }
        mPasswordEt.getText().clear();

        enableSubmitIfAppropriate();
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
            String[] ipSettingsData = new String[]{mIpAddressEt.getText().toString(), mNetworkPrefixLengthEt.getText().toString(), mGatewayEt.getText().toString(), mDns1Et.getText().toString()};
            mCallback.callBackData(new String[]{mSsidEt.getText().toString(), mPasswordEt.getText().toString(), mCapabilities},mMeteredType, mIPAssignment, ipSettingsData, mProxySettings, mHttpProxyInfo);
            dismiss();
        } else if (id == R.id.wifi_add_dialog_show_password) {
            mPasswordVisible = !mPasswordVisible;
            setPasswordVisible(mPasswordVisible);
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

    private void setPasswordVisible(boolean visible) {
        if (visible) {
            mShowPassword.setImageResource(R.drawable.check_on);
            mPasswordEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mPasswordEt.setSelection(mPasswordEt.getText().toString().length());
        } else {
            mShowPassword.setImageResource(R.drawable.check_off);
            mPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mPasswordEt.setSelection(mPasswordEt.getText().toString().length());
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
            mAdvancedOptionsLayout.setVisibility(VISIBLE);
        } else {
            mAdvancedOptions.setImageResource(R.drawable.check_off);
            mAdvancedOptionsLayout.setVisibility(GONE);
        }
    }

    private void setStaticIpSettingsVisible(boolean visible) {
        if (visible) {
            mStaticIpSettingsLayout.setVisibility(VISIBLE);
        } else {
            mStaticIpSettingsLayout.setVisibility(GONE);
        }
    }

    private void setProxyAutoConfigVisible(boolean visible){
        if(visible){
            mProxyAutoConfig.setVisibility(VISIBLE);
        }else {
            mProxyAutoConfig.setVisibility(GONE);
        }
    }

    private void setProxyManualVisible(boolean visible) {
        if (visible) {
            mProxyManual.setVisibility(VISIBLE);
        } else {
            mProxyManual.setVisibility(GONE);
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

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        enableSubmitIfAppropriate();
    }

    public boolean isSubmittable() {
        boolean enable = false;
        int ssidLength = 0;
        int passwordLength = 0;
        ssidLength = mSsidEt.getText().toString().length();
        passwordLength = mPasswordEt.getText().toString().length();
        if ((mCapabilities.equals("NONE") && ssidLength == 0) || (mCapabilities.equals("WEP") && (ssidLength == 0 || passwordLength < 1)) || (mCapabilities.equals("WPA") && (ssidLength == 0 || passwordLength < 8))) {
            enable = false;
        } else {
            enable = ipAndProxyFieldsAreValid();
        }
        return enable;
    }

    private void enableSubmitIfAppropriate() {
        if (isSubmittable()) {
            mConnect.setEnabled(true);
            mConnect.setTextAppearance(mContext, R.style.DialogButtonDividerStyle);
        } else {
            mConnect.setEnabled(false);
            mConnect.setTextAppearance(mContext, R.style.NegativeDialogButtonDividerStyle);
        }
    }

    private boolean ipAndProxyFieldsAreValid() {  // 判断IP及proxy是否有效
        int result = 0;

        if (mIPAssignment.equals("STATIC")) {
            result = validateIpConfigFields();
            if (result != 0) {
                return false;
            }
        }
        if (mProxySettings.equals("PAC")) {
            CharSequence uriSequence = mPACUrlEt.getText();
            if (TextUtils.isEmpty(uriSequence)) {
                return false;
            }
            Uri uri = Uri.parse(uriSequence.toString());
            if (uri == null) {
                return false;
            }
            mHttpProxyInfo = ProxyInfo.buildPacProxy(uri);
        } else if (mProxySettings.equals("STATIC")) {
            String hostName = mProxyHostnameEt.getText().toString();
            String portStr = mProxyPortEt.getText().toString();
            String bypassProxyFor = mBypassProxyForEt.getText().toString();
            int port = 0;
            try {
                port = Integer.parseInt(portStr);
                result = proxyValidate(hostName, portStr, bypassProxyFor);
            } catch (NumberFormatException e) {
                result = -1;
            }
            if (result == 0) {
                List<String> bypassProxyForList = Arrays.asList(bypassProxyFor.split(","));
                mHttpProxyInfo = ProxyInfo.buildDirectProxy(hostName, port, bypassProxyForList);
            } else {
                return false;
            }
        }
        return true;
    }

    public int validateIpConfigFields() {
        String ipAddr = mIpAddressEt.getText().toString();
        if (TextUtils.isEmpty(ipAddr)) return -1;

        Inet4Address inetAddr = getIPv4Address(ipAddr);
        if (inetAddr == null) {
            return -1;
        }

        int networkPrefixLength = -1;
        try {
            networkPrefixLength = Integer.parseInt(mNetworkPrefixLengthEt.getText().toString());
            if (networkPrefixLength < 0 || networkPrefixLength > 32) {
                return -1;
            }
        } catch (NumberFormatException e) {
            // Set the hint as default after user types in ip address
            mNetworkPrefixLengthEt.setText(R.string.hint_wifi_network_prefix_length);
        } catch (IllegalArgumentException e) {
            return -1;
        }

        String gateway = mGatewayEt.getText().toString();
        if (TextUtils.isEmpty(gateway)) {
            try {
                //Extract a default gateway from IP address
                InetAddress netPart = getNetworkPart(inetAddr, networkPrefixLength);
                byte[] addr = netPart.getAddress();
                addr[addr.length - 1] = 1;
                mGatewayEt.setText(InetAddress.getByAddress(addr).getHostAddress());
            } catch (RuntimeException ee) {
            } catch (java.net.UnknownHostException u) {
            }
        } else {
            InetAddress gatewayAddr = getIPv4Address(gateway);
            if (gatewayAddr == null) {
                return -1;
            }
            if (gatewayAddr.isMulticastAddress()) {
                return -1;
            }
        }

        String dns = mDns1Et.getText().toString();
        InetAddress dnsAddr = null;

        if (TextUtils.isEmpty(dns)) {
            //If everything else is valid, provide hint as a default option
            mDns1Et.setText(R.string.hint_wifi_dns1);
        } else {
            dnsAddr = getIPv4Address(dns);
            if (dnsAddr == null) {
                return -1;
            }
        }

        return 0;
    }

    public interface DialogCallback {
        void callBackData(String[] data, int meteredType, String ipAssignment, String[] ipSettingsData, String proxySettings, ProxyInfo proxyInfo);
    }
}
