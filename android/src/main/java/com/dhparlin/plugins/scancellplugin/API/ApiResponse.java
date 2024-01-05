package com.dhparlin.plugins.scancellplugin.API;


import com.google.gson.annotations.SerializedName;

public class ApiResponse {

    // Other properties...

    @SerializedName("statusCode")
    private String statusCode;

    @SerializedName("responseData")
    private ResponseData responseData;

    // Getter for statusCode
    public String getStatusCode() {
        return statusCode;
    }

    // Getter for responseData
    public ResponseData getResponseData() {
        return responseData;
    }

    // Inner class for responseData
    public static class ResponseData {

        @SerializedName("netType")
        private String netType;

        @SerializedName("Frequency")
        private int frequency;

        @SerializedName("bandName")
        private String bandName;

        @SerializedName("bandNumber")
        private int bandNumber;

        @SerializedName("rxFrequency")
        private double rxFrequency;

        @SerializedName("txFrequency")
        private double txFrequency;

        @SerializedName("modulation")
        private String modulation;

        @SerializedName("possibleBandwidth")
        private String[] possibleBandwidth;

        // Add getters for all fields in ResponseData
        // ...

        public String getNetType() {
            return netType;
        }

        public int getFrequency() {
            return frequency;
        }

        public String getBandName() {
            return bandName;
        }

        public int getBandNumber() {
            return bandNumber;
        }

        public double getRxFrequency() {
            return rxFrequency;
        }

        public double getTxFrequency() {
            return txFrequency;
        }

        public String getModulation() {
            return modulation;
        }

        public String[] getPossibleBandwidth() {
            return possibleBandwidth;
        }
    }
}
