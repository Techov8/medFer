package com.techov8.medfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Token_Res {


    @SerializedName("head")
    @Expose
    private Head head;
    @SerializedName("body")
    @Expose
    private Body body;

    public Body getBody() {
        return body;
    }

    public class ResultInfo {

        @SerializedName("resultStatus")
        @Expose
        private String resultStatus;
        @SerializedName("resultCode")
        @Expose
        private String resultCode;
        @SerializedName("resultMsg")
        @Expose
        private String resultMsg;


    }
    public class Head {

        @SerializedName("responseTimestamp")
        @Expose
        private String responseTimestamp;
        @SerializedName("version")
        @Expose
        private String version;
        @SerializedName("signature")
        @Expose
        private String signature;


        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }


    }

    public class Body {

        @SerializedName("resultInfo")
        @Expose
        private ResultInfo resultInfo;
        @SerializedName("txnToken")
        @Expose
        private String txnToken;
        @SerializedName("isPromoCodeValid")
        @Expose
        private boolean isPromoCodeValid;
        @SerializedName("authenticated")
        @Expose
        private boolean authenticated;

        public ResultInfo getResultInfo() {
            return resultInfo;
        }

        public String getTxnToken() {
            return txnToken;
        }



    }
}