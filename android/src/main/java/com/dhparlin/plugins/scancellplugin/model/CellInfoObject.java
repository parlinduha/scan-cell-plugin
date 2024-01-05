package com.dhparlin.plugins.scancellplugin.model;


import com.google.gson.annotations.SerializedName;

public class CellInfoObject {
    @SerializedName("networkMode")
    private String networkMode;
    @SerializedName("cellId")
    private int cellId;
    @SerializedName("lac")
    private int lac;
    @SerializedName("arfcn")
    private int arfcn;
    @SerializedName("mcc")
    private int mcc;
    @SerializedName("mnc")
    private int mnc;
    @SerializedName("bsic")
    private int bsic;
    @SerializedName("operatorAlphaLong")
    private String operatorAlphaLong;
    @SerializedName("operatorAlphaShort")
    private String operatorAlphaShort;
    @SerializedName("dbm")
    private int dbm;
    @SerializedName("rssi")
    private int rssi;
    @SerializedName("signalLevel")
    private int signalLevel;
    @SerializedName("pci")
    private int pci;
    @SerializedName("tac")
    private int tac;
    @SerializedName("earfcn")
    private int earfcn;
    @SerializedName("rsrp")
    private int rsrp;
    @SerializedName("rsrq")
    private int rsrq;
    @SerializedName("rssnr")
    private int rssnr;

    public CellInfoObject() {
        // Default constructor
    }

    // Include a constructor to initialize the fields
    public CellInfoObject(String networkMode, int cellId, int lac, int arfcn, int mcc, int mnc, int bsic,
                          String operatorAlphaLong, String operatorAlphaShort, int dbm, int rssi, int signalLevel) {
        this.networkMode = networkMode;
        this.cellId = cellId;
        this.lac = lac;
        this.arfcn = arfcn;
        this.mcc = mcc;
        this.mnc = mnc;
        this.bsic = bsic;
        this.operatorAlphaLong = operatorAlphaLong;
        this.operatorAlphaShort = operatorAlphaShort;
        this.dbm = dbm;
        this.rssi = rssi;
        this.signalLevel = signalLevel;
    }

    // Include a constructor for LTE
    public CellInfoObject(String networkMode, int cellId, int pci, int tac, int earfcn, int mcc, int mnc,
                          String operatorAlphaLong, String operatorAlphaShort, int rsrp, int rsrq, int rssnr, int signalLevel) {
        this.networkMode = networkMode;
        this.cellId = cellId;
        this.pci = pci;
        this.tac = tac;
        this.earfcn = earfcn;
        this.mcc = mcc;
        this.mnc = mnc;
        this.operatorAlphaLong = operatorAlphaLong;
        this.operatorAlphaShort = operatorAlphaShort;
        this.rsrp = rsrp;
        this.rsrq = rsrq;
        this.rssnr = rssnr;
        this.signalLevel = signalLevel;
    }

    // Include a constructor for WCDMA
    public CellInfoObject(String networkMode, int cellId, int psc, int lac, int uarfcn, int mcc, int mnc,
                          String operatorAlphaLong, String operatorAlphaShort, int dbm, int signalLevel) {
        this.networkMode = networkMode;
        this.cellId = cellId;
        // ... initialize other fields ...
    }

    // Include getters for the fields
    // ... getters ...
}

