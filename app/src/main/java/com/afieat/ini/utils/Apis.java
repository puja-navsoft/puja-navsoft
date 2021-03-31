package com.afieat.ini.utils;

/**
 * Created by amartya on 30/03/16 with love.
 */
public final class Apis {

      /*public static final String BASE = "https://afieat.com/Websnew/";
      public static final String BASE2 = "https://afieat.com/Websnew/"; */

   // public static final String BASE = "https://afieat.com/webs/";  // This for live server
   // private static final String BASE2 = "https://afieat.com/webs/";  // This for live server
   // private static final String CHAT_URl="https://afieat.com/webs/";//https://afieat.com/webs/gethash?a=user&b=himadri.biswas@navsoft.in&c=9898786

   public static final String BASE = "http://34.205.164.176/webs/";  // This for Dev Server
   public static final String BASE2 = "http://34.205.164.176/webs/";  // This for Dev Server
   private static final String CHAT_URl="http://34.205.164.176/webs/";

    //test
    public static final String IMG_PATH = "https://d22vvrqrexhw5s.cloudfront.net/upload/";

    public static final String get_playstore_version="GetAppVersion";
    public  static final String AD_IMAGE_PATH="https://d22vvrqrexhw5s.cloudfront.net/upload/ads/thumb_375_312/";

    //  public static final String SIGN_UP = BASE + "UserSignup";
    public static final String SIGN_UP = BASE + "usersignupdevice";
    //    public static final String LOG_IN = BASE + "UserLogin";
    public static final String LOG_IN = BASE + "userlogindevice";
    public static final String OTP_SEND = BASE + "sendregistrationotp";
    public static final String OTP_VERIFY = BASE + "verifyuserphone";


    public static final String FORGOT_PASSWORD = BASE + "SendResetPasswordLink";
    public static final String RESET_PASSWORD = BASE + "SetNewPassword";
    public static final String USER_DETAILS = BASE + "ShopuserDetails"; //POST PARAM: user_id0
    public static final String UPDATE_USER = BASE + "UpdatePersonalInfo"; //POST PARAM: too many
    public static final String CHANGE_PASSWORD = BASE + "ChangesPassword";  //POST PARAM: user_id, oldpwd, newpwd
    public static final String LIST_CITIES = BASE + "citylist";
    public static final String LIST_REGIONS = BASE + "regionlist"; //POST PARAM: city_id
    public static final String LIST_RESTAURANTS = BASE + "resturantlisregionwise";  //POST PARAM: region_id
    public static final String GET_RESTAURANTS_LIST = BASE + "getrestaurants";  //POST PARAM: rid=3&cid=2&restofst=10&lmt=10
    public static final String RESTAURANT_DETAILS = BASE + "restaurantdetails"; //POST PARAM: restaurant_id

    public static final String SOCIAL_LOGIN = BASE + "sociallogin"; //post social login
    public static final String USER_PHONE = BASE + "adduserphone"; //post user phone number
    public static final String GET_NOTIFICATIONS = BASE + "getnotifications";   //POST PARAM: user_id, limit, offset

    public static final String GET_CONV_RATE = BASE + "getcurrencyconversionrate";

    //    public static final String GET_FOOD_ITEMS = BASE + "getfooditems";  //POST PARAM: resid
   // public static final String GET_FOOD_ITEMS = BASE2 + "getfooditems";  //POST PARAM: resid
     public static final String GET_FOOD_ITEMS = BASE2 + "getfooditemsapp";  //POST PARAM: resid
    public static final String CUISINE_LIST = BASE + "cuisinelist";
    public static final String GET_CRAZY_DEALS = BASE + "getcrazydeals";    // get crazy deals
    public static final String FOOD_ITEM_DETAILS = BASE + "itemdetails";    //POST PARAM: item_id

    public static final String GET_RESTAURANT_INFO = BASE + "restaurantinfo";   //POST PARAM: resid
    public static final String GET_MY_VOUCHERS = BASE + "getmyvouchers";   //POST PARAM: user_id,limit
    public static final String GET_RESTAURANT_REVIEW = BASE + "restaurantreview";   //POST PARAM: resid
    public static final String GET_RESTAURANT_STATUS_OFFER = BASE + "checkrestauranisopen"; //POST PARAM: tm, dt, res
    public static final String GET_DELIVERY_CHARGE = BASE + "GetDeliveryChargeRegion";  //POST PARAM: merchant_id, region_id
    public static final String GET_POPULAR_PRODUCTS = BASE + "getpopularItems"; //POST PARAM: merchant_id

    public static final String LIST_ADDRESS = BASE + "listUserAddress"; //POST PARAM: userid
    public static final String SAVE_ADDRESS = BASE + "adduseraddress"; //POST PARAM: too many to mention
    public static final String GET_ADDRESS_DETAILS = BASE + "getaddressdetail";  //POST PARAM: address_id
    public static final String SET_ADDRESS_DEFAULT = BASE + "setaddressdefault";    //POST PARAM: address_id
    public static final String GET_ADDRESS_DEFAULT = BASE + "getdefaultaddress";    //POST PARAM: userid
    public static final String DELETE_ADDRESS = BASE + "deleteaddress"; //POST PARAM: address_id
    public static final String DELETE_REVIEWIMAGE = BASE + "DeleteReviewImage"; //POST PARAM: review_id,review_image

    public static final String GET_PREPAID_CARDS = BASE + "ActiveCard"; //POST PARAM: shopuser_id
    public static final String ADD_PREPAID_CARD = BASE + "AddPrepaidCard"; //POST PARAM: cardno, shopuser_id
    public static final String APPLY_PROMO = BASE + "applypromo";   //POST PARAM: promocode, merchant_id, shopuserid, delivery_date

    public static final String GET_MY_REVIEWS = BASE + "MyRatingsReview";   //POST PARAM: user_id
    public static final String POST_REVIEW = BASE + "Postreview";   //POST PARAM: user_id, ratecount, review, resid
    public static final String DELETE_REVIEW = BASE + "DeleteReviews";  //POST: rvwid

    public static final String PLACE_ORDER_CC = BASE + "putcardpayment";
    public static final String PLACE_ORDER = BASE + "placeorder";   //POST PARAM: too many
    public static final String GET_MY_ORDERS = BASE + "MyOrders";   //POST: user_id, ofst, lmt
    public static final String ORDER_DETAILS = BASE + "OrderDetail";   //POST: order_id
    public static final String TRACK_DRIVER = BASE + "getdrivertracking";    //POST: order_id
    public static final String HELPCENTER = BASE + "getHelpandsupport";
    public static final String UPDATEDEVICEID = BASE + "updateip";  //POST: device_id,device_type
    public static final String GET_GRUPCATEGOTY_LIST_REGION = BASE2 + "GroupByCityRegion"; //city_id,region_id
    public static final String DEMOGROPULISTCHECK = BASE2 + "GetRestaurantsByGroup?"; //city_id,region_id
    public static final String SEARCH_RESTAURANT_BY_ITEM_NAME = BASE2 + "GetestaurantsByItemName"; //city_id,region_id

    public static final String GET_NOTIFICATIONCOUNT = BASE + "getUnreadNotification"; //
    public static final String GET_KEY_WITHOUTLOGIN = BASE + "getauthkey"; // header authorization as a device id
    public static final String ADD_REMOVE_TO_FAVOURITE = BASE + "addremovetofavourite"; //
    public static final String FAVOURITE = BASE + "getmyfavouriterestaurants"; //
    public static final String VOUCHER_VERIFY = BASE+"verifyvoucher/";
    public static final String MY_POINTS=BASE+"Mypoint";
    public static final String RES_ADS = BASE+"getadds/";
    public static final String GET_UNDELIVER_ORDERS="usersordertodeliver";
    public static final String GET_ALL_RESTAURANTS=BASE+"getallrestaurants";
    public static final String GET_ORDER_NO=BASE+"getorderno";
    public static final String GET_DELIVERY_CHARGE_BUTLER=BASE+"getdeliverycharge";
    public static final String PLACE_DRIVER_ORDER_BUTLER=BASE+"placedriverorder";
    public static final String CHAT_URL_USER_ID=CHAT_URl+"gethash";



    public static final int selectedSize=-1;


}
