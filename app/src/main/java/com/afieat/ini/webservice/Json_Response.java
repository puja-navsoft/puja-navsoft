package com.afieat.ini.webservice;


/**
 * Created by zabingo on 8/2/17.
 */

public class Json_Response {
    private int response_code;
    private String response_msg;
    //  private UploadVideo uploadVideo;
    //  private UserLogin[] userLogins;

    public Json_Response() {

    }

    public int getResponseCode() {
        return response_code;
    }

    public void setResponseCode(int rCode) {
        this.response_code = rCode;
    }

    public String getResponseMsg() {
        return response_msg;
    }

    public void setResponseMsg(String rMsg) {
        this.response_msg = rMsg;
    }


    //................object type................



   /* public UploadVideo getUploadVideo() {
        return uploadVideo;
    }

    public void setUploadVideo(UploadVideo uploadVideo) {
        this.uploadVideo = uploadVideo;
    }
   */


//..........................



   /* public UserLogin[] getUserLoginDetails() {
        return userLogins;
    }

    public void setUserLoginDetails(UserLogin[] userLogins) {
        this.userLogins = userLogins;
    }*/


}
