package br.com.jylabtech.getnet_payment_tech.services;

import android.os.Build;
import java.util.HashMap;
import java.util.Map;

public class DeviceInfo {
    public Map<String, Object> getSerialNumberAndDeviceModel() {
        String model = Build.MODEL;
        String serial;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            serial = "Block Info";
        } else {
            serial = Build.SERIAL;
        }

        Map<String, Object> deviceInfo = new HashMap<>();
        deviceInfo.put("deviceModel", model);
        deviceInfo.put("serialNumber", serial);

        return deviceInfo;
    }
}

