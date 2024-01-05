package com.dhparlin.plugins.scancellplugin;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.core.app.ActivityCompat;

import com.dhparlin.plugins.scancellplugin.API.ApiResponse;
import com.dhparlin.plugins.scancellplugin.API.ServiceApi;
import com.dhparlin.plugins.scancellplugin.model.CellInfoObject;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CellIdScanner {

    public interface CellScanListener {
        void onCellScanResult(List<String> uniqueCellIds, int totalData);
    }

    private static final long SCAN_INTERVAL = 5000; // Interval pemindaian dalam milidetik (misalnya, 5000 ms = 5 detik)

    private Context context;
    private CellScanListener cellScanListener;
    private Handler scanHandler;
    private StringBuffer details = new StringBuffer();
    private Set<String> processedCellInfo = new HashSet<>();
    public CellIdScanner(Context context, CellScanListener cellScanListener) {
        this.context = context;
        this.cellScanListener = cellScanListener;
        this.scanHandler = new Handler(Looper.getMainLooper());
    }

    public void startContinuousScan() {
        // Memulai pemindaian pertama kali
        scanHandler.post(scanRunnable);
    }

    public void stopContinuousScan() {
        // Menghentikan pemindaian berulang
        scanHandler.removeCallbacks(scanRunnable);
    }

    private Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            // Melakukan pemindaian
            scanCellInfo();

            // Menjadwalkan pemindaian berikutnya setelah jeda waktu tertentu
            scanHandler.postDelayed(this, SCAN_INTERVAL);
        }
    };

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

                        ApiResponse apiResponse = new ApiResponse() ;
                        String cellDetails = getCellDetails(cellInfo, apiResponse);
                        if (cellDetails != null) {
                            Log.d("Cell Info", "CellDetails: " + cellDetails);
                            uniqueCellIds.add(cellDetails);

                            // Tambahkan logika untuk mengambil Earfcn atau Arfcn dari cellInfo
                            int channel = getChannelFromCellInfo(cellInfo);

                            // Gunakan channel ini untuk mengambil data dari API
                            fetchDataFromApi(channel, cellInfo);
                        }
                    }
                }

                // Convert Set to List for consistency in the onCellScanResult method
                List<String> uniqueCellIdsList = new ArrayList<>(uniqueCellIds);

                // Panggil metode listener untuk memberikan hasil pemindaian ke aktivitas pemanggil
                if (cellScanListener != null) {
                    Log.d("Total Cell Info", "Sending scan result to listener. Total unique cell IDs: " + uniqueCellIdsList.size());
                    cellScanListener.onCellScanResult(uniqueCellIdsList, uniqueCellIdsList.size());
                }
            }
        } else {
            // Jika izin tidak diberikan, berikan informasi atau minta izin pada pengguna
            Log.e("Not Permission", "Permission not granted to access phone state.");
        }
    }
    public List<CellInfoObject> convertToCellInfoObjects(List<String> uniqueCellIdsList) {
        List<CellInfoObject> cellInfoObjects = new ArrayList<>();

        for (String cellDetails : uniqueCellIdsList) {
            try {
                JSONObject jsonObject = new JSONObject(cellDetails);

                // Here, you need to extract values from the JSON object and create a CellInfoObject
                // The details depend on the structure of your JSON and the fields in CellInfoObject

                CellInfoObject cellInfoObject = new CellInfoObject();
                // Set values to cellInfoObject based on jsonObject fields
                // ...

                cellInfoObjects.add(cellInfoObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return cellInfoObjects;
    }
//    private void calculateFrequencies(CellInfoLte cellInfoLte, StringBuffer details) {
//        CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
//        int earfcn = cellIdentityLte.getEarfcn();
//
//        // DL_Base dan UL_Base sesuai dengan standar LTE
//        int DL_Base = 0;
//        int UL_Base = 0;
//        int DL_Offset = 0;
//        int UL_Offset = 18000;
//
//        // Hitung frekuensi downlink (DL)
//        double dlFrequency = DL_Offset + (0.1 * (earfcn - DL_Base));
//
//        // Hitung frekuensi uplink (UL)
//        double ulFrequency = UL_Offset + (0.1 * (earfcn - UL_Base));
//
//        // Tambahkan informasi ke StringBuilder details
//        details.append("\nDL Frequency: ").append(dlFrequency).append(" MHz");
//        details.append("\nUL Frequency: ").append(ulFrequency).append(" MHz");
//    }

    private void calculateFrequencies(CellInfoLte cellInfoLte, StringBuffer details) {
        CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
        int earfcn = cellIdentityLte.getEarfcn();

        // DL_Base dan UL_Base sesuai dengan standar LTE
        int DL_Base = 0;
        int UL_Base = 18000; // sesuai dengan standar LTE

        // Hitung frekuensi downlink (DL) dan uplink (UL)
        double dlFrequency = 0.0;
        double ulFrequency = 0.0;

        if (earfcn >= 0 && earfcn <= 599) {
            dlFrequency = 2110.0 + 0.1 * earfcn;
            ulFrequency = dlFrequency - 190.0;
        } else if (earfcn >= 600 && earfcn <= 1199) {
            dlFrequency = 1930.0 + 0.1 * (earfcn - 600);
            ulFrequency = dlFrequency - 180.0;
        } else if (earfcn >= 1200 && earfcn <= 1949) {
            dlFrequency = 2110.0 + 0.1 * (earfcn - 1200);
            ulFrequency = dlFrequency - 170.0;
        } else if (earfcn >= 1950 && earfcn <= 2399) {
            dlFrequency = 1710.0 + 0.1 * (earfcn - 1950);
            ulFrequency = dlFrequency - 180.0;
        } else if (earfcn >= 2400 && earfcn <= 2649) {
            dlFrequency = 2110.0 + 0.1 * (earfcn - 2400);
            ulFrequency = dlFrequency - 65.0;
        } else if (earfcn >= 2650 && earfcn <= 2749) {
            dlFrequency = 925.0 + 0.1 * (earfcn - 2650);
            ulFrequency = dlFrequency + 45.0;
        } else if (earfcn >= 2750 && earfcn <= 3449) {
            dlFrequency = 2110.0 + 0.1 * (earfcn - 2750);
            ulFrequency = dlFrequency - 100.0;
        } else if (earfcn >= 3450 && earfcn <= 3799) {
            dlFrequency = 3400.0 + 0.1 * (earfcn - 3450);
            ulFrequency = dlFrequency - 201.0;
        }

        // Tambahkan informasi ke StringBuilder details
        details.append("\nDL Frequency: ").append(dlFrequency).append(" MHz");
        details.append("\nUL Frequency: ").append(ulFrequency).append(" MHz");

        // Tambahkan log
        Log.d("Frequencies", "DL Frequency: " + dlFrequency + " MHz, UL Frequency: " + ulFrequency + " MHz");

        // Bandwidth (BW) dapat dicari berdasarkan DL dan UL
        double bandwidth = ulFrequency - dlFrequency;
        details.append("\nBandwidth: ").append(bandwidth).append(" MHz");
    }



    private String getCellDetails(CellInfo cellInfo, ApiResponse apiResponse) {
//        StringBuilder details = new StringBuilder();

        if (cellInfo instanceof CellInfoLte ) {
            CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
            CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
            CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();

            if (cellIdentityLte != null && cellSignalStrengthLte != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    details.append("\n\nNetwork Mode: LTE")
                            .append("\nCell ID: ").append(cellIdentityLte.getCi())
                            .append("\nPCI: ").append(cellIdentityLte.getPci())
                            .append("\nTracking Area Code (TAC): ").append(cellIdentityLte.getTac())
                            .append("\nEarfcn: ").append(cellIdentityLte.getEarfcn())
                            .append("\nBands: ").append(Arrays.toString(cellIdentityLte.getBands()))
                            .append("\nMCC: ").append(cellIdentityLte.getMccString())
                            .append("\nMNC: ").append(cellIdentityLte.getMncString())
                            .append("\nOperator Alpha Long: ").append(cellIdentityLte.getOperatorAlphaLong())
                            .append("\nOperator Alpha Short: ").append(cellIdentityLte.getOperatorAlphaShort())
                            .append("\nRSSI: ").append(cellSignalStrengthLte.getDbm())
                            .append("\nRSRP: ").append(cellSignalStrengthLte.getRsrp())
                            .append("\nRSRQ: ").append(cellSignalStrengthLte.getRsrq())
                            .append("\nRSSNR: ").append(cellSignalStrengthLte.getRssnr())
                            .append("\nSignal Level: ").append(cellSignalStrengthLte.getLevel());

                    calculateFrequencies(cellInfoLte, details);
                    appendApiResponseData(apiResponse);
                    // Tambahkan informasi tentang Band LTE
                    int earfcn = cellIdentityLte.getEarfcn();
                    String bandInfo = getLTEBandInfo(earfcn, "LTE");
                    details.append("\n").append(bandInfo);


                }
            }  else {
                details.append("Network Mode: LTE - Invalid Data");
            }
        }
        else if (cellInfo instanceof CellInfoGsm) {
            CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
            CellIdentityGsm cellIdentityGsm = cellInfoGsm.getCellIdentity();
            CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();

            if (cellIdentityGsm != null && cellSignalStrengthGsm != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    details.append("\n\nNetwork Mode: GSM")
                            .append("\nCell ID: ").append(cellIdentityGsm.getCid())
                            .append("\nLAC : ").append(cellIdentityGsm.getLac())
                            .append("\nArfcn: ").append(cellIdentityGsm.getArfcn())
                            .append("\nMCC: ").append(cellIdentityGsm.getMccString())
                            .append("\nMNC: ").append(cellIdentityGsm.getMncString())
                            .append("\nBSIC: ").append(cellIdentityGsm.getBsic())
                            .append("\nOperator Alpha Long: ").append(cellIdentityGsm.getOperatorAlphaLong())
                            .append("\nOperator Alpha Short: ").append(cellIdentityGsm.getOperatorAlphaShort())
                            .append("\nDBM: ").append(cellSignalStrengthGsm.getDbm())
                            .append("\nRSSI: ").append(cellSignalStrengthGsm.getRssi())
                            .append("\nSignal Level: ").append(cellSignalStrengthGsm.getLevel());

                    appendApiResponseData(apiResponse);
                }
            }  else {
                details.append("Network Mode: GSM - Invalid Data");
            }
        }
        else if (cellInfo instanceof CellInfoWcdma) {
            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
            CellIdentityWcdma cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
            CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();

            if (cellIdentityWcdma != null && cellSignalStrengthWcdma != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    details.append("\n\nNetwork Mode: LTE")
                            .append("\nCell ID: ").append(cellIdentityWcdma.getCid())
                            .append("\nPSC: ").append(cellIdentityWcdma.getPsc())
                            .append("\nLAC: ").append(cellIdentityWcdma.getLac())
                            .append("\nUarfcn: ").append(cellIdentityWcdma.getUarfcn())
                            .append("\nMCC: ").append(cellIdentityWcdma.getMccString())
                            .append("\nMNC: ").append(cellIdentityWcdma.getMncString())
                            .append("\nOperator Alpha Long: ").append(cellIdentityWcdma.getOperatorAlphaLong())
                            .append("\nOperator Alpha Short: ").append(cellIdentityWcdma.getOperatorAlphaShort())
                            .append("\nRSSI: ").append(cellSignalStrengthWcdma.getDbm())
                            .append("\nSignal Level: ").append(cellSignalStrengthWcdma.getLevel());
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    details.append("\n\nNetwork Mode: LTE")
                            .append("\nCell ID: ").append(cellIdentityWcdma.getCid())
                            .append("\nPSC: ").append(cellIdentityWcdma.getPsc())
                            .append("\nLAC: ").append(cellIdentityWcdma.getLac())
                            .append("\nUarfcn: ").append(cellIdentityWcdma.getUarfcn())
                            .append("\nMCC: ").append(cellIdentityWcdma.getMccString())
                            .append("\nMNC: ").append(cellIdentityWcdma.getMncString())
                            .append("\nOperator Alpha Long: ").append(cellIdentityWcdma.getOperatorAlphaLong())
                            .append("\nOperator Alpha Short: ").append(cellIdentityWcdma.getOperatorAlphaShort())
                            .append("\nRSSI: ").append(cellSignalStrengthWcdma.getDbm())
                            .append("\nSignal Level: ").append(cellSignalStrengthWcdma.getLevel());
                }
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    details.append("\n\nNetwork Mode: LTE")
                            .append("\nCell ID: ").append(cellIdentityWcdma.getCid())
                            .append("\nPSC: ").append(cellIdentityWcdma.getPsc())
                            .append("\nLAC: ").append(cellIdentityWcdma.getLac())
                            .append("\nUarfcn: ").append(cellIdentityWcdma.getUarfcn())
                            .append("\nMCC: ").append(cellIdentityWcdma.getMccString())
                            .append("\nMNC: ").append(cellIdentityWcdma.getMncString())
                            .append("\nOperator Alpha Long: ").append(cellIdentityWcdma.getOperatorAlphaLong())
                            .append("\nOperator Alpha Short: ").append(cellIdentityWcdma.getOperatorAlphaShort())
                            .append("\nRSSI: ").append(cellSignalStrengthWcdma.getDbm())
                            .append("\nSignal Level: ").append(cellSignalStrengthWcdma.getLevel());
                }
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    details.append("\n\nNetwork Mode: LTE")
                            .append("\nCell ID: ").append(cellIdentityWcdma.getCid())
                            .append("\nPSC: ").append(cellIdentityWcdma.getPsc())
                            .append("\nLAC: ").append(cellIdentityWcdma.getLac())
                            .append("\nUarfcn: ").append(cellIdentityWcdma.getUarfcn())
                            .append("\nMCC: ").append(cellIdentityWcdma.getMccString())
                            .append("\nMNC: ").append(cellIdentityWcdma.getMncString())
                            .append("\nOperator Alpha Long: ").append(cellIdentityWcdma.getOperatorAlphaLong())
                            .append("\nOperator Alpha Short: ").append(cellIdentityWcdma.getOperatorAlphaShort())
                            .append("\nRSSI: ").append(cellSignalStrengthWcdma.getDbm())
                            .append("\nSignal Level: ").append(cellSignalStrengthWcdma.getLevel());
                }
            }  else {
                details.append("Network Mode: WCDMA - Invalid Data");
            }
        }
        else {
            // Handle other network types if needed
            details.append("Network Mode: Unknown");
        }

        return details.toString();
    }

    private void appendApiResponseData(ApiResponse apiResponse) {
        if (apiResponse != null) {
            ApiResponse.ResponseData responseData = apiResponse.getResponseData();
            if (responseData != null) {
                String netType = responseData.getNetType();
                int frequency = responseData.getFrequency();
                String bandName = responseData.getBandName();
                int bandNumber = responseData.getBandNumber();
                double rxFrequency = responseData.getRxFrequency();
                double txFrequency = responseData.getTxFrequency();
                String modulation = responseData.getModulation();
                String[] possibleBandwidth = responseData.getPossibleBandwidth();

                Log.d("Ini Response", "responseData" + responseData );
                details.append("\n NetType: ").append(netType);
                details.append("\n Frequency: ").append(frequency);
                details.append("\n Band Name: ").append(bandName);
                details.append("\n Band Number: ").append(bandNumber);
                details.append("\n Uplink: ").append(rxFrequency);
                details.append("\n Downlink: ").append(txFrequency);
                details.append("\n Modulation: ").append(modulation);
                details.append("\n Bandwidth: ").append(Arrays.toString(possibleBandwidth));

            }
        }
    }
    private int getChannelFromCellInfo(CellInfo cellInfo) {
        if (cellInfo instanceof CellInfoLte) {
            CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return cellInfoLte.getCellIdentity().getEarfcn();
            }
        } else if (cellInfo instanceof CellInfoGsm) {
            CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return cellInfoGsm.getCellIdentity().getArfcn();
            }
        } else {
            // Tambahkan logika untuk teknologi seluler lainnya jika diperlukan
            return -1;
        }
        return 0;
    }
    // Declare a Set to store unique cell information

    private void fetchDataFromApi(int channel, CellInfo cellInfo) {
        // Check if the cell information has already been processed
        String cellInfoKey = generateCellInfoKey(cellInfo);
        if (!processedCellInfo.contains(cellInfoKey)) {
            // Process the cell information
            processedCellInfo.add(cellInfoKey);

            // Call the method to fetch data from the API
            ServiceApi.getFrequency(String.valueOf(channel), "LTE").enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()) {
                        ApiResponse apiResponse = response.body();
                        // Create a new StringBuilder or reuse an existing one
                        StringBuilder details = new StringBuilder();
                        // Get cell details
                        getCellDetails(cellInfo, apiResponse);
                        // Display or log cell details as needed
                        Log.d("Cell Details", details.toString());
                    } else {
                        Log.e("API Error", "Failed to fetch data from API");
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.e("API Error", "API request failed", t);
                }
            });
        } else {
            Log.d("Duplicate Data", "CellInfo already processed: " + cellInfoKey);
        }
    }

    // Helper method to generate a unique key for CellInfo
    private String generateCellInfoKey(CellInfo cellInfo) {
        // Customize this method based on the unique properties of CellInfo
        if (cellInfo instanceof CellInfoLte) {
            CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
            CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
            return "LTE_" + cellIdentityLte.getCi();
        } else if (cellInfo instanceof CellInfoGsm) {
            // Handle other cell types similarly
            // ...
        }

        // Default case if cell type is not recognized
        return "Unknown_" + cellInfo.hashCode();
    }

    private boolean hasPermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE)
                        == PackageManager.PERMISSION_GRANTED;
    }
    private String getLTEBandInfo(int earfcn, String networkMode) {
        int band;
        String bandInfo;

        if (earfcn >= 0 && earfcn <= 100) {
            band = 1;
        } else if (earfcn >= 600 && earfcn <= 1199) {
            band = 3;
        } else if (earfcn >= 1200 && earfcn <= 1949) {
            band = 5;
        } else if (earfcn >= 1950 && earfcn <= 2399) {
            band = 8;
        } else if (earfcn >= 2400 && earfcn <= 2649) {
            band = 7;
        } else if (earfcn >= 2650 && earfcn <= 2749) {
            band = 7;
        } else if (earfcn >= 2750 && earfcn <= 3449) {
            band = 20;
        } else if (earfcn >= 3450 && earfcn <= 3799) {
            band = 8;
        } else if (earfcn >= 3800 && earfcn <= 3849) {
            band = 8;
        } else if (earfcn >= 3850 && earfcn <= 4149) {
            band = 1;
        } else if (earfcn >= 4150 && earfcn <= 4749) {
            band = 3;
        } else if (earfcn >= 4750 && earfcn <= 4949) {
            band = 0;
        } else if (earfcn >= 5010 && earfcn <= 5179) {
            band = 33;
        } else if (earfcn >= 5180 && earfcn <= 5279) {
            band = 34;
        } else if (earfcn >= 5280 && earfcn <= 5379) {
            band = 35;
        } else if (earfcn >= 5730 && earfcn <= 5849) {
            band = 36;
        } else if (earfcn >= 5850 && earfcn <= 5999) {
            band = 37;
        } else if (earfcn == 38750) {
            band = 40;
        } else {
            band = -1; // Unknown Band
        }

        if (band != -1) {
            bandInfo = "Band " + band + " " + networkMode;
        } else {
            bandInfo = "Unknown Band";
        }

        return bandInfo;
    }


}
