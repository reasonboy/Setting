package com.jzzh.setting.device;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.setting.device.licenseinfo.LicenseHtmlLoaderCompat;

import java.io.File;

public class LicenseInfo extends BaseActivity implements
        LoaderManager.LoaderCallbacks<File> {
    private static final String TAG = LicenseInfo.class.getSimpleName();

    public static final String FILE_PROVIDER_AUTHORITY = "com.jzzh.setting.files";
    private static final String LICENSE_PATH = "/system/etc/NOTICE.html.gz";
    private static final int LOADER_ID_LICENSE_HTML_LOADER = 0;
    private WebView mInfoWebView;
    private TextView mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info_license);
        mInfoWebView = (WebView) findViewById(R.id.info_webview);
        mLoading = findViewById(R.id.loading);
        mInfoWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (mInfoWebView.getContentHeight() > 0) {
                    mLoading.setVisibility(View.GONE);
                }
                if (progress == 100) {
                    //加载完成
                    Log.d(TAG, "加载完成");
                }
            }
        });


        File file = new File(LICENSE_PATH);
        if (isFileValid(file)) {
            webViewShowHtmlFromUri(Uri.fromFile(file));
        } else {
            showHtmlFromDefaultXmlFiles();
        }
    }

    private void showGeneratedHtmlFile(File generatedHtmlFile) {
        if (generatedHtmlFile != null) {
            webViewShowHtmlFromUri(getUriFromGeneratedHtmlFile(generatedHtmlFile));
        } else {
            Log.e(TAG, "Failed to generate.");
//            showErrorAndFinish();
        }
    }

    private void webViewShowHtmlFromUri(Uri uri) {
        mInfoWebView.loadUrl(String.valueOf(uri));
    }

    Uri getUriFromGeneratedHtmlFile(File generatedHtmlFile) {
        return FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY,
                generatedHtmlFile);
    }

    boolean isFileValid(final File file) {
        return file.exists() && file.length() != 0;
    }

    @NonNull
    @Override
    public Loader<File> onCreateLoader(int id, @Nullable Bundle args) {
        mLoading.setVisibility(View.VISIBLE);
        return new LicenseHtmlLoaderCompat(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<File> loader, File data) {
        showGeneratedHtmlFile(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<File> loader) {
    }

    private void showHtmlFromDefaultXmlFiles() {
        getSupportLoaderManager().initLoader(LOADER_ID_LICENSE_HTML_LOADER, Bundle.EMPTY, this);
    }

    @Override
    protected void onPause() {
        mInfoWebView.stopLoading();
        super.onPause();
    }
}
