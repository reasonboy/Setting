package com.jzzh.setting.time;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.jzzh.setting.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.util.ArrayList;
import java.util.List;

public class ZoneGetter {

    private static final String XMLTAG_TIMEZONE = "timezone";

    public static List<String[]> readTimezonesToDisplay(Context context) {
        List<String[]> olsonIds = new ArrayList<String[]>();
        try (XmlResourceParser xrp = context.getResources().getXml(R.xml.timezones)) {
            while (xrp.next() != XmlResourceParser.START_TAG) {
                continue;
            }
            xrp.next();
            while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                while (xrp.getEventType() != XmlResourceParser.START_TAG) {
                    if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT) {
                        return olsonIds;
                    }
                    xrp.next();
                }
                if (xrp.getName().equals(XMLTAG_TIMEZONE)) {
                    String olsonId = xrp.getAttributeValue(0);
                    String olsonRegion = "";
                    if (xrp.next() == XmlPullParser.TEXT) {
                        olsonRegion = xrp.getText();
                    }
                    olsonIds.add(new String[]{olsonRegion,olsonId});
                }
                while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                    xrp.next();
                }
                xrp.next();
            }
        } catch (XmlPullParserException xppe) {
            Log.e("xml_log_zg", "Ill-formatted timezones.xml file");
        } catch (java.io.IOException ioe) {
            Log.e("xml_log_zg", "Unable to read timezones.xml file");
        }
        return olsonIds;
    }

    public static String offsetToGMT(int offset) {
        int minOffset = Math.abs(offset / 1000 / 60);
        int min = minOffset % 60;
        int hour = minOffset / 60;
        String gmt = "";
        if (offset < 0) {
            gmt = "GMT-";
        } else {
            gmt = "GMT+";
        }
        if(min != 0) {
            return gmt + hour + ":" + min;
        }
        return gmt + hour + ":0" + min;
    }

}
