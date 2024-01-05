package com.dhparlin.plugins.scancellplugin;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScanCellPlugin {
    private Context context;
    public String echo(String value) {
        Log.i("Echo", value);
        return value;
    }

    public void scanCellInfo() {
        // Periksa izin sebelum mengakses informasi jaringan
        if (hasPermission()) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            if (telephonyManager != null) {
                Set<String> uniqueCellIds = new HashSet<>();

                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();


//                Log.d("Cell Info List", "scanCellInfo: " + cellInfoList);

                if (cellInfoList != null) {
                    for (CellInfo cellInfo : cellInfoList) {
                        Log.d("CellInfoScanner", "CellInfo: " + cellInfo.toString());
                    }
                }
            }
        } else {
            // Jika izin tidak diberikan, berikan informasi atau minta izin pada pengguna
            Log.e("Not Permission", "Permission not granted to access phone state.");
        }
    }

    private boolean hasPermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE)
                        == PackageManager.PERMISSION_GRANTED;
    }
}
