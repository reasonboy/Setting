package com.jzzh.setting.device.licenseinfo;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * LicenseHtmlLoader is a loader which loads a license html file from default license xml files.
 */
public class LicenseHtmlLoaderCompat extends AsyncLoaderCompat<File> {
    private static final String TAG = "LicenseHtmlLoaderCompat";

    static final String[] DEFAULT_LICENSE_XML_PATHS = {
            "/system/etc/NOTICE.xml.gz",
            "/vendor/etc/NOTICE.xml.gz",
            "/odm/etc/NOTICE.xml.gz",
            "/oem/etc/NOTICE.xml.gz",
            "/product/etc/NOTICE.xml.gz",
            "/system_ext/etc/NOTICE.xml.gz"};
    static final String NOTICE_HTML_FILE_NAME = "NOTICE.html";

    private final Context mContext;

    public LicenseHtmlLoaderCompat(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public File loadInBackground() {
        return generateHtmlFromDefaultXmlFiles();
    }

    @Override
    protected void onDiscardResult(File f) {
    }

    private File generateHtmlFromDefaultXmlFiles() {
        final List<File> xmlFiles = getVaildXmlFiles();
        if (xmlFiles.isEmpty()) {
            Log.e(TAG, "No notice file exists.");
            return null;
        }

        File cachedHtmlFile = getCachedHtmlFile(mContext);
        if (!isCachedHtmlFileOutdated(xmlFiles, cachedHtmlFile)
                || generateHtmlFile(mContext, xmlFiles, cachedHtmlFile)) {
            return cachedHtmlFile;
        }

        return null;
    }

    private List<File> getVaildXmlFiles() {
        final List<File> xmlFiles = new ArrayList();
        for (final String xmlPath : DEFAULT_LICENSE_XML_PATHS) {
            File file = new File(xmlPath);
            if (file.exists() && file.length() != 0) {
                xmlFiles.add(file);
            }
        }
        return xmlFiles;
    }

    private File getCachedHtmlFile(Context context) {
        return new File(context.getCacheDir(), NOTICE_HTML_FILE_NAME);
    }

    private boolean isCachedHtmlFileOutdated(List<File> xmlFiles, File cachedHtmlFile) {
        boolean outdated = true;
        if (cachedHtmlFile.exists() && cachedHtmlFile.length() != 0) {
            outdated = false;
            for (File file : xmlFiles) {
                if (cachedHtmlFile.lastModified() < file.lastModified()) {
                    outdated = true;
                    break;
                }
            }
        }
        return outdated;
    }

    private boolean generateHtmlFile(Context context, List<File> xmlFiles, File htmlFile) {
        return LicenseHtmlGeneratorFromXml.generateHtml(xmlFiles, htmlFile,
                "noticeHeader");//context.getString(R.string.notice_header));
    }
}
