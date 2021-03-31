package com.afieat.ini.webservice;

public class PlaystoreVersionReq {

    /**
     * code : 1
     * android : 31
     * ios : 31
     * msg : Version found.
     * status : 1
     */

    private int code;
    private String android;
    private String ios;
    private String msg;
    private int status;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getAndroid() {
        return android;
    }

    public void setAndroid(String android) {
        this.android = android;
    }

    public String getIos() {
        return ios;
    }

    public void setIos(String ios) {
        this.ios = ios;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
