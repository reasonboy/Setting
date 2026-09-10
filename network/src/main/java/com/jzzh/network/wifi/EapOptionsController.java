package com.jzzh.network.wifi;

import android.content.Context;
import android.net.wifi.WifiEnterpriseConfig;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jzzh.network.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles the views of the Enterprise (802.1x) options.
 * AddDialog and ConnectDialog share the same layout (layout_wifi_eap_options).
 */
public class EapOptionsController implements View.OnClickListener, TextWatcher {

    /** WifiEnterpriseConfig.Eap values, in the same order as the EAP method items */
    private static final int[] EAP_METHOD_VALUES = {
            WifiEnterpriseConfig.Eap.PEAP,
            WifiEnterpriseConfig.Eap.TLS,
            WifiEnterpriseConfig.Eap.TTLS,
            WifiEnterpriseConfig.Eap.PWD,
            WifiEnterpriseConfig.Eap.SIM,
            WifiEnterpriseConfig.Eap.AKA,
            WifiEnterpriseConfig.Eap.AKA_PRIME
    };

    /** WifiEnterpriseConfig.Phase2 values, in the same order as the PEAP Phase 2 items */
    private static final int[] PEAP_PHASE2_VALUES = {
            WifiEnterpriseConfig.Phase2.NONE,
            WifiEnterpriseConfig.Phase2.MSCHAPV2,
            WifiEnterpriseConfig.Phase2.GTC,
            WifiEnterpriseConfig.Phase2.SIM,
            WifiEnterpriseConfig.Phase2.AKA,
            WifiEnterpriseConfig.Phase2.AKA_PRIME
    };

    /** WifiEnterpriseConfig.Phase2 values, in the same order as the TTLS Phase 2 items */
    private static final int[] TTLS_PHASE2_VALUES = {
            WifiEnterpriseConfig.Phase2.NONE,
            WifiEnterpriseConfig.Phase2.PAP,
            WifiEnterpriseConfig.Phase2.MSCHAP,
            WifiEnterpriseConfig.Phase2.MSCHAPV2,
            WifiEnterpriseConfig.Phase2.GTC
    };

    private final Context mContext;
    private final WifiEapConfig mConfig = new WifiEapConfig();
    private final OnEapOptionsChangedListener mListener;

    private final View mRootView;
    private final LinearLayout mEapMethodLayout;
    private final LinearLayout mPhase2Layout;
    private final LinearLayout mCaCertLayout;
    private final LinearLayout mDomainLayout;
    private final LinearLayout mUserCertLayout;
    private final LinearLayout mIdentityLayout;
    private final LinearLayout mAnonymousIdentityLayout;
    private final TextView mEapMethodResult;
    private final TextView mPhase2Result;
    private final TextView mCaCertResult;
    private final TextView mUserCertResult;
    private final EditText mDomainEt;
    private final EditText mIdentityEt;
    private final EditText mAnonymousIdentityEt;
    private final ImageView mEapMethodDownDrop;
    private final ImageView mPhase2DownDrop;
    private final ImageView mCaCertDownDrop;
    private final ImageView mUserCertDownDrop;

    public EapOptionsController(Context context, View rootView, OnEapOptionsChangedListener listener) {
        mContext = context;
        mRootView = rootView;
        mListener = listener;

        mEapMethodLayout = rootView.findViewById(R.id.ll_eap_method);
        mPhase2Layout = rootView.findViewById(R.id.ll_eap_phase2);
        mCaCertLayout = rootView.findViewById(R.id.ll_eap_ca_cert);
        mDomainLayout = rootView.findViewById(R.id.ll_eap_domain);
        mUserCertLayout = rootView.findViewById(R.id.ll_eap_user_cert);
        mIdentityLayout = rootView.findViewById(R.id.ll_eap_identity);
        mAnonymousIdentityLayout = rootView.findViewById(R.id.ll_eap_anonymous_identity);
        mEapMethodResult = rootView.findViewById(R.id.eap_method_result);
        mPhase2Result = rootView.findViewById(R.id.eap_phase2_result);
        mCaCertResult = rootView.findViewById(R.id.eap_ca_cert_result);
        mUserCertResult = rootView.findViewById(R.id.eap_user_cert_result);
        mDomainEt = rootView.findViewById(R.id.wifi_eap_domain);
        mIdentityEt = rootView.findViewById(R.id.wifi_eap_identity);
        mAnonymousIdentityEt = rootView.findViewById(R.id.wifi_eap_anonymous_identity);

        mEapMethodDownDrop = rootView.findViewById(R.id.eap_method_down_drop);
        mPhase2DownDrop = rootView.findViewById(R.id.eap_phase2_down_drop);
        mCaCertDownDrop = rootView.findViewById(R.id.eap_ca_cert_down_drop);
        mUserCertDownDrop = rootView.findViewById(R.id.eap_user_cert_down_drop);
        mEapMethodDownDrop.setOnClickListener(this);
        mPhase2DownDrop.setOnClickListener(this);
        mCaCertDownDrop.setOnClickListener(this);
        mUserCertDownDrop.setOnClickListener(this);
        // Open the list when the whole row is tapped, not only the arrow.
        mEapMethodLayout.setOnClickListener(this);
        mPhase2Layout.setOnClickListener(this);
        mCaCertLayout.setOnClickListener(this);
        mUserCertLayout.setOnClickListener(this);

        mDomainEt.addTextChangedListener(this);
        mIdentityEt.addTextChangedListener(this);
        mAnonymousIdentityEt.addTextChangedListener(this);

        updateViews();
    }

    public WifiEapConfig getConfig() {
        return mConfig;
    }

    public void setVisible(boolean visible) {
        mRootView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public boolean isPasswordSupported() {
        return mConfig.isPasswordSupported();
    }

    public boolean isValid(String password) {
        return mConfig.isValid(password);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.eap_method_down_drop || id == R.id.ll_eap_method) {
            showEapMethodDialog(mEapMethodDownDrop);
        } else if (id == R.id.eap_phase2_down_drop || id == R.id.ll_eap_phase2) {
            showPhase2Dialog(mPhase2DownDrop);
        } else if (id == R.id.eap_ca_cert_down_drop || id == R.id.ll_eap_ca_cert) {
            showCaCertDialog(mCaCertDownDrop);
        } else if (id == R.id.eap_user_cert_down_drop || id == R.id.ll_eap_user_cert) {
            showUserCertDialog(mUserCertDownDrop);
        }
    }

    private void showEapMethodDialog(View anchor) {
        final List<String> items = getStringList(R.array.wifi_eap_method_entries);
        showListDialog(anchor, items, new WifiListDialog.DialogCallback() {
            @Override
            public void callBackData(int position, String item) {
                mConfig.setEapMethod(EAP_METHOD_VALUES[position]);
                updateViews();
                notifyChanged();
            }
        });
    }

    private void showPhase2Dialog(View anchor) {
        final int[] values = getPhase2Values();
        List<String> items = getStringList(mConfig.getEapMethod() == WifiEnterpriseConfig.Eap.TTLS
                ? R.array.wifi_eap_ttls_phase2_entries : R.array.wifi_eap_peap_phase2_entries);
        showListDialog(anchor, items, new WifiListDialog.DialogCallback() {
            @Override
            public void callBackData(int position, String item) {
                mConfig.setPhase2Method(values[position]);
                updateViews();
                notifyChanged();
            }
        });
    }

    private void showCaCertDialog(View anchor) {
        final List<String> aliases = WifiEapConfig.getCaCertificateAliases();
        List<String> items = new ArrayList<>();
        items.add(mContext.getString(R.string.wifi_eap_ca_cert_do_not_validate));
        items.add(mContext.getString(R.string.wifi_eap_ca_cert_use_system));
        items.addAll(aliases);
        showListDialog(anchor, items, new WifiListDialog.DialogCallback() {
            @Override
            public void callBackData(int position, String item) {
                if (position == 0) {
                    mConfig.setCaCertMode(WifiEapConfig.CA_CERT_DO_NOT_VALIDATE);
                } else if (position == 1) {
                    mConfig.setCaCertMode(WifiEapConfig.CA_CERT_USE_SYSTEM);
                } else {
                    mConfig.setCaCertMode(WifiEapConfig.CA_CERT_ALIAS);
                    mConfig.setCaCertAlias(aliases.get(position - 2));
                }
                updateViews();
                notifyChanged();
            }
        });
    }

    private void showUserCertDialog(View anchor) {
        final List<String> aliases = WifiEapConfig.getUserCertificateAliases();
        List<String> items = new ArrayList<>();
        items.add(mContext.getString(R.string.wifi_eap_user_cert_none));
        items.addAll(aliases);
        showListDialog(anchor, items, new WifiListDialog.DialogCallback() {
            @Override
            public void callBackData(int position, String item) {
                mConfig.setUserCertAlias(position == 0 ? "" : aliases.get(position - 1));
                updateViews();
                notifyChanged();
            }
        });
    }

    private void showListDialog(View anchor, List<String> items, WifiListDialog.DialogCallback callback) {
        WifiListDialog dialog = new WifiListDialog(mContext, R.style.ZhDialog, items, callback);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER | Gravity.TOP);
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        lp.y = location[1];
        window.setAttributes(lp);
        dialog.show();
    }

    private int[] getPhase2Values() {
        return mConfig.getEapMethod() == WifiEnterpriseConfig.Eap.TTLS
                ? TTLS_PHASE2_VALUES : PEAP_PHASE2_VALUES;
    }

    /**
     * Updates the visible rows and the selected values for the current EAP method.
     */
    private void updateViews() {
        mEapMethodResult.setText(getEapMethodLabel());
        mPhase2Result.setText(getPhase2Label());
        mCaCertResult.setText(getCaCertLabel());
        mUserCertResult.setText(getUserCertLabel());

        mPhase2Layout.setVisibility(mConfig.isPhase2Supported() ? View.VISIBLE : View.GONE);
        mCaCertLayout.setVisibility(mConfig.isCaCertSupported() ? View.VISIBLE : View.GONE);
        mDomainLayout.setVisibility(mConfig.isDomainSupported() ? View.VISIBLE : View.GONE);
        mUserCertLayout.setVisibility(mConfig.isUserCertSupported() ? View.VISIBLE : View.GONE);
        mIdentityLayout.setVisibility(mConfig.isIdentitySupported() ? View.VISIBLE : View.GONE);
        mAnonymousIdentityLayout.setVisibility(
                mConfig.isAnonymousIdentitySupported() ? View.VISIBLE : View.GONE);
    }

    private String getEapMethodLabel() {
        List<String> entries = getStringList(R.array.wifi_eap_method_entries);
        for (int i = 0; i < EAP_METHOD_VALUES.length; i++) {
            if (EAP_METHOD_VALUES[i] == mConfig.getEapMethod()) {
                return entries.get(i);
            }
        }
        return entries.get(0);
    }

    private String getPhase2Label() {
        int[] values = getPhase2Values();
        List<String> entries = getStringList(mConfig.getEapMethod() == WifiEnterpriseConfig.Eap.TTLS
                ? R.array.wifi_eap_ttls_phase2_entries : R.array.wifi_eap_peap_phase2_entries);
        for (int i = 0; i < values.length; i++) {
            if (values[i] == mConfig.getPhase2Method()) {
                return entries.get(i);
            }
        }
        return entries.get(0);
    }

    private String getCaCertLabel() {
        switch (mConfig.getCaCertMode()) {
            case WifiEapConfig.CA_CERT_USE_SYSTEM:
                return mContext.getString(R.string.wifi_eap_ca_cert_use_system);
            case WifiEapConfig.CA_CERT_ALIAS:
                return mConfig.getCaCertAlias();
            case WifiEapConfig.CA_CERT_DO_NOT_VALIDATE:
            default:
                return mContext.getString(R.string.wifi_eap_ca_cert_do_not_validate);
        }
    }

    private String getUserCertLabel() {
        return mConfig.getUserCertAlias().isEmpty()
                ? mContext.getString(R.string.wifi_eap_user_cert_none) : mConfig.getUserCertAlias();
    }

    private List<String> getStringList(int arrayResId) {
        return Arrays.asList(mContext.getResources().getStringArray(arrayResId));
    }

    private void notifyChanged() {
        if (mListener != null) {
            mListener.onEapOptionsChanged();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        mConfig.setDomain(mDomainEt.getText().toString());
        mConfig.setIdentity(mIdentityEt.getText().toString());
        mConfig.setAnonymousIdentity(mAnonymousIdentityEt.getText().toString());
        notifyChanged();
    }

    public interface OnEapOptionsChangedListener {
        void onEapOptionsChanged();
    }
}
