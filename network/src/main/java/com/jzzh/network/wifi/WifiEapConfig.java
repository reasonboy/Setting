package com.jzzh.network.wifi;

import android.net.wifi.WifiEnterpriseConfig;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Holds the EAP settings of WPA2/WPA3-Enterprise (802.1x) and applies them to {@link WifiEnterpriseConfig}.
 * The option set and the display rules follow WifiConfigController of AOSP Settings.
 */
public class WifiEapConfig {

    private static final String TAG = WifiEapConfig.class.getSimpleName();

    /** Do not validate the server certificate. */
    public static final int CA_CERT_DO_NOT_VALIDATE = 0;
    /** Validate the server with the built-in system CA certificates. */
    public static final int CA_CERT_USE_SYSTEM = 1;
    /** Validate the server with a CA certificate installed by the user. */
    public static final int CA_CERT_ALIAS = 2;

    /** System CA certificate store path, same as AOSP Settings. */
    private static final String SYSTEM_CA_CERT_PATH = "/system/etc/security/cacerts";
    /** android.security.Credentials.CA_CERTIFICATE */
    private static final String PREFIX_CA_CERTIFICATE = "CACERT_";
    /** android.security.Credentials.USER_PRIVATE_KEY */
    private static final String PREFIX_USER_PRIVATE_KEY = "USRPKEY_";

    private int mEapMethod = WifiEnterpriseConfig.Eap.PEAP;
    private int mPhase2Method = WifiEnterpriseConfig.Phase2.MSCHAPV2;
    private int mCaCertMode = CA_CERT_DO_NOT_VALIDATE;
    private String mCaCertAlias = "";
    private String mUserCertAlias = "";
    private String mDomain = "";
    private String mIdentity = "";
    private String mAnonymousIdentity = "";

    public int getEapMethod() {
        return mEapMethod;
    }

    public void setEapMethod(int eapMethod) {
        mEapMethod = eapMethod;
        // Reset Phase 2 to the default when the new EAP method does not support it.
        if (!isPhase2Supported()) {
            mPhase2Method = WifiEnterpriseConfig.Phase2.NONE;
        } else if (mPhase2Method == WifiEnterpriseConfig.Phase2.NONE) {
            mPhase2Method = WifiEnterpriseConfig.Phase2.MSCHAPV2;
        }
    }

    public int getPhase2Method() {
        return mPhase2Method;
    }

    public void setPhase2Method(int phase2Method) {
        mPhase2Method = phase2Method;
    }

    public int getCaCertMode() {
        return mCaCertMode;
    }

    public void setCaCertMode(int caCertMode) {
        mCaCertMode = caCertMode;
        if (caCertMode != CA_CERT_ALIAS) {
            mCaCertAlias = "";
        }
    }

    public String getCaCertAlias() {
        return mCaCertAlias;
    }

    public void setCaCertAlias(String caCertAlias) {
        mCaCertAlias = caCertAlias == null ? "" : caCertAlias;
    }

    public String getUserCertAlias() {
        return mUserCertAlias;
    }

    public void setUserCertAlias(String userCertAlias) {
        mUserCertAlias = userCertAlias == null ? "" : userCertAlias;
    }

    public String getDomain() {
        return mDomain;
    }

    public void setDomain(String domain) {
        mDomain = domain == null ? "" : domain;
    }

    public String getIdentity() {
        return mIdentity;
    }

    public void setIdentity(String identity) {
        mIdentity = identity == null ? "" : identity;
    }

    public String getAnonymousIdentity() {
        return mAnonymousIdentity;
    }

    public void setAnonymousIdentity(String anonymousIdentity) {
        mAnonymousIdentity = anonymousIdentity == null ? "" : anonymousIdentity;
    }

    /** Only PEAP/TTLS use Phase 2 authentication. */
    public boolean isPhase2Supported() {
        return mEapMethod == WifiEnterpriseConfig.Eap.PEAP
                || mEapMethod == WifiEnterpriseConfig.Eap.TTLS;
    }

    /** The SIM based methods authenticate with SIM credentials, so they use no certificate. */
    public boolean isCaCertSupported() {
        return mEapMethod == WifiEnterpriseConfig.Eap.PEAP
                || mEapMethod == WifiEnterpriseConfig.Eap.TTLS
                || mEapMethod == WifiEnterpriseConfig.Eap.TLS;
    }

    /** EAP-TLS authenticates with a client certificate. */
    public boolean isUserCertSupported() {
        return mEapMethod == WifiEnterpriseConfig.Eap.TLS;
    }

    public boolean isIdentitySupported() {
        return mEapMethod == WifiEnterpriseConfig.Eap.PEAP
                || mEapMethod == WifiEnterpriseConfig.Eap.TTLS
                || mEapMethod == WifiEnterpriseConfig.Eap.TLS
                || mEapMethod == WifiEnterpriseConfig.Eap.PWD;
    }

    public boolean isAnonymousIdentitySupported() {
        return mEapMethod == WifiEnterpriseConfig.Eap.PEAP
                || mEapMethod == WifiEnterpriseConfig.Eap.TTLS;
    }

    /** TLS authenticates with a certificate and the SIM based methods with the SIM, so neither takes a password. */
    public boolean isPasswordSupported() {
        return mEapMethod == WifiEnterpriseConfig.Eap.PEAP
                || mEapMethod == WifiEnterpriseConfig.Eap.TTLS
                || mEapMethod == WifiEnterpriseConfig.Eap.PWD;
    }

    /** The domain is only entered when the server is validated with a CA certificate. */
    public boolean isDomainSupported() {
        return isCaCertSupported() && mCaCertMode != CA_CERT_DO_NOT_VALIDATE;
    }

    /**
     * Returns whether the current input is complete enough to try a connection.
     *
     * @param password the password entered by the user
     */
    public boolean isValid(String password) {
        if (isIdentitySupported() && TextUtils.isEmpty(mIdentity)) {
            return false;
        }
        if (isPasswordSupported() && TextUtils.isEmpty(password)) {
            return false;
        }
        if (isUserCertSupported() && TextUtils.isEmpty(mUserCertAlias)) {
            return false;
        }
        if (mCaCertMode == CA_CERT_ALIAS && TextUtils.isEmpty(mCaCertAlias)) {
            return false;
        }
        // When the server certificate is validated, both the Root CA and the domain are required to save the config.
        if (isCaCertSupported() && mCaCertMode != CA_CERT_DO_NOT_VALIDATE && TextUtils.isEmpty(mDomain)) {
            return false;
        }
        return true;
    }

    /**
     * Applies the settings to {@link WifiEnterpriseConfig}.
     *
     * @param config   the target config
     * @param password the password entered by the user
     */
    public void applyTo(WifiEnterpriseConfig config, String password) {
        config.setEapMethod(mEapMethod);
        config.setPhase2Method(isPhase2Supported()
                ? mPhase2Method : WifiEnterpriseConfig.Phase2.NONE);
        config.setIdentity(isIdentitySupported() ? mIdentity : "");
        config.setAnonymousIdentity(isAnonymousIdentitySupported() ? mAnonymousIdentity : "");
        config.setPassword(isPasswordSupported() && password != null ? password : "");

        if (isCaCertSupported()) {
            switch (mCaCertMode) {
                case CA_CERT_USE_SYSTEM:
                    setCaCertificateAliases(config, null);
                    setCaPath(config, SYSTEM_CA_CERT_PATH);
                    break;
                case CA_CERT_ALIAS:
                    setCaPath(config, null);
                    setCaCertificateAliases(config, new String[]{mCaCertAlias});
                    break;
                case CA_CERT_DO_NOT_VALIDATE:
                default:
                    setCaPath(config, null);
                    setCaCertificateAliases(config, null);
                    break;
            }
        } else {
            setCaPath(config, null);
            setCaCertificateAliases(config, null);
        }

        config.setDomainSuffixMatch(isDomainSupported() ? mDomain : "");
        setClientCertificateAlias(config, isUserCertSupported() ? mUserCertAlias : "");

        // Since AOSP 13 an Enterprise config without both a Root CA and a domain is rejected on save.
        // Without a CA it is saved only with TOFU enabled, and the system asks to confirm the server certificate on the first connection.
        boolean noCaCert = !isCaCertSupported() || mCaCertMode == CA_CERT_DO_NOT_VALIDATE;
        invokeHiddenMethod(config, "enableTrustOnFirstUse", boolean.class, noCaCert);
    }

    /**
     * Aliases of the installed CA certificates.
     */
    public static List<String> getCaCertificateAliases() {
        return listKeyStoreAliases(PREFIX_CA_CERTIFICATE);
    }

    /**
     * Aliases of the installed user certificates (private keys).
     */
    public static List<String> getUserCertificateAliases() {
        return listKeyStoreAliases(PREFIX_USER_PRIVATE_KEY);
    }

    /**
     * Reads the aliases with the given prefix from the key store.
     * AOSP 11 uses android.security.KeyStore, AOSP 12 and later use Keystore2 (AndroidKeyStore).
     */
    private static List<String> listKeyStoreAliases(String prefix) {
        List<String> result = new ArrayList<>();
        try {
            Class<?> clazz = Class.forName("android.security.KeyStore");
            Object keyStore = clazz.getMethod("getInstance").invoke(null);
            Method list = clazz.getMethod("list", String.class);
            String[] aliases = (String[]) list.invoke(keyStore, prefix);
            if (aliases != null) {
                Collections.addAll(result, aliases);
            }
        } catch (Exception e) {
            Log.d(TAG, "android.security.KeyStore is unavailable: " + e);
        }
        if (result.isEmpty()) {
            try {
                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
                Enumeration<String> aliases = keyStore.aliases();
                while (aliases.hasMoreElements()) {
                    String alias = aliases.nextElement();
                    if (alias.startsWith(prefix)) {
                        result.add(alias.substring(prefix.length()));
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "AndroidKeyStore is unavailable: " + e);
            }
        }
        return result;
    }

    // setCaPath/setCaCertificateAliases/setClientCertificateAlias are @hide, so they are called by reflection.
    private static void setCaPath(WifiEnterpriseConfig config, String path) {
        invokeHiddenMethod(config, "setCaPath", String.class, path);
    }

    private static void setCaCertificateAliases(WifiEnterpriseConfig config, String[] aliases) {
        invokeHiddenMethod(config, "setCaCertificateAliases", String[].class, aliases);
    }

    private static void setClientCertificateAlias(WifiEnterpriseConfig config, String alias) {
        invokeHiddenMethod(config, "setClientCertificateAlias", String.class, alias);
    }

    private static void invokeHiddenMethod(WifiEnterpriseConfig config, String name,
            Class<?> parameterType, Object value) {
        try {
            Method method = WifiEnterpriseConfig.class.getMethod(name, parameterType);
            method.invoke(config, value);
        } catch (Exception e) {
            Log.e(TAG, "invoke " + name + " failed: " + e);
        }
    }
}
