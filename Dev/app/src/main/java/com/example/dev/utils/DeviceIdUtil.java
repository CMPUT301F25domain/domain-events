package com.example.dev.utils;

import android.content.Context;
import android.provider.Settings;

public class DeviceIdUtil {

    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
    }
}
