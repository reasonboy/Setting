package com.jzzh.network.bt;

import android.bluetooth.BluetoothDevice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BtUtils {

    public static String getAlias(BluetoothDevice device) {
        Method getAliasMethod = null;
        String alias = null;
        try {
            getAliasMethod = device.getClass().getMethod("getAlias");
            alias = (String) getAliasMethod.invoke(device);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return alias;
    }

    public static boolean setAlias(BluetoothDevice device, String alias) {
        Method setAliasMethod;
        boolean success = false;
        try {
            setAliasMethod = device.getClass().getMethod("setAlias", String.class);
            success = (boolean) setAliasMethod.invoke(device, alias);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return success;
    }
}
