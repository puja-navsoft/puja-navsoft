package com.afieat.ini.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.afieat.ini.models.Food;
import com.afieat.ini.models.Notification;
import com.afieat.ini.models.RestaurantModel.Restaurant;
import com.afieat.ini.models.RestaurantModel.RestaurantListModel;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amartya on 03/05/16 with love.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "afieatDBase.db";

    private static final String TABLE_BASKET = "tableBasket";
    private static final String ENTRY_ID = "entryId";
    private static final String USER_ID = "userId";
    private static final String MERCHANT_ID = "merchantId";
    private static final String FOOD_ID = "foodId";
    private static final String FOOD_NAME = "foodName";
    private static final String FOOD_SIZE = "foodSize";
    private static final String FOOD_SIZE_ID = "foodSizeId";
    private static final String FOOD_COUNT = "foodCount";
    private static final String FOOD_PRICE = "foodPrice";
    private static final String FOOD_UNIT_PRICE = "foodUnitPrice";
    private static final String FOOD_ADDONS = "foodAddons";
    private static final String FOOD_ADDON_IDS = "foodAddonIds";
    private static final String FOOD_ADDON_PRICES = "foodAddOnPrices";
    private static final String FOOD_INGREDIENTS = "foodIngredients";
    private static final String FOOD_INGREDIENT_IDS = "foodIngredientIds";
    private static final String FOOD_INGREDIENT_PRICES = "foodIngredientPrices";
    private static final String FOOD_COMMENT = "foodComment";

    private static final String TABLE_NOTIFICATION = "tableNotification";
    private static final String NOTIF_ID = "notifId";
    private static final String DRIVER_ID = "driverId";
    private static final String MESSAGE = "message";
    private static final String DATE = "notifDate";
    private static final String TIME = "notifTime";
    private Context myContext;


    private static final String TABLE_RESTAURANT = "tableRestaurant";
    private static final String RES_ID = "id";
    private static final String RESTAURANT_ID = "restaurant_id";
    private static final String RESTAURANT_NAME = "restaurant_name";
    private static final String RESTAURANT_NAME_AR = "restaurant_name_ar";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableBasket = "CREATE TABLE " + TABLE_BASKET + " ("
                + ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USER_ID + " INTEGER, "
                + MERCHANT_ID + " INTEGER, "
                + FOOD_ID + " INTEGER, "
                + FOOD_NAME + " TEXT, "
                + FOOD_SIZE + " TEXT, "
                + FOOD_SIZE_ID + " TEXT, "
                + FOOD_COUNT + " INTEGER, "
                + FOOD_PRICE + " TEXT, "
                + FOOD_UNIT_PRICE + " TEXT, "
                + FOOD_ADDONS + " TEXT, "
                + FOOD_ADDON_IDS + " TEXT, "
                + FOOD_ADDON_PRICES + " TEXT, "
                + FOOD_INGREDIENTS + " TEXT, "
                + FOOD_INGREDIENT_IDS + " TEXT, "
                + FOOD_INGREDIENT_PRICES + " TEXT, "
                + FOOD_COMMENT + " TEXT"
                + ")";
        AppUtils.dbLog(createTableBasket);
        db.execSQL(createTableBasket);

        String createTableNotif = "CREATE TABLE " + TABLE_NOTIFICATION + " ("
                + NOTIF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DRIVER_ID + " INTEGER, "
                + MESSAGE + " TEXT, "
                + DATE + " TEXT, "
                + TIME + " TEXT"
                + ")";
        AppUtils.log(createTableNotif);
        db.execSQL(createTableNotif);

        String createtableRestaurant = "CREATE TABLE " + TABLE_RESTAURANT + " ("
                + RES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RESTAURANT_ID + " INTEGER, "
                + RESTAURANT_NAME + " TEXT, "
                + RESTAURANT_NAME_AR + " TEXT "
                + ")";
        AppUtils.log(createtableRestaurant);
        db.execSQL(createtableRestaurant);

    }

    public void updateSizeFoodBasket(String userId, Food food, String entryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        userId = "".equals(userId) ? "-1" : userId;

        if (checkForIdenticalItem(userId, food)) {

            System.out.println("dbHelperR : addFoodBasket : userId : " + userId);
            System.out.println("dbHelperR : addFoodBasket : getMerchantId() : " + food.getMerchantId());
            System.out.println("dbHelperR : addFoodBasket : getId : " + food.getId());
            System.out.println("dbHelperR : addFoodBasket : getName() : " + food.getName());
            System.out.println("dbHelperR : addFoodBasket : getSizeBasket() : " + food.getSizeBasket());
            System.out.println("dbHelperR : addFoodBasket : getSizeBasketId : " + food.getSizeBasketId());
            System.out.println("dbHelperR : addFoodBasket : getBasketCount() : " + food.getBasketCount());
            System.out.println("dbHelperR : addFoodBasket : getPriceBasket : " + food.getPriceBasket());
            System.out.println("dbHelperR : addFoodBasket : getUnitPrice() : " + food.getUnitPrice());
            System.out.println("dbHelperR : addFoodBasket : getAddOns()() : " + food.getAddOns());

            System.out.println("addFoodBasket : getAddOnIds : " + food.getAddOnIds());
            System.out.println("addFoodBasket : getAddOnPrices : " + food.getAddOnPrices());
            System.out.println("addFoodBasket : getIngredients : " + food.getIngredients());
            System.out.println("addFoodBasket : getIngredientIds : " + food.getIngredientIds());
            System.out.println("addFoodBasket : getIngredientPrices : " + food.getIngredientPrices());
            System.out.println("addFoodBasket : getComment : " + food.getComment());

            ContentValues cv = new ContentValues();
            cv.put(USER_ID, userId);
            cv.put(MERCHANT_ID, food.getMerchantId());
            cv.put(FOOD_ID, food.getId());
            cv.put(FOOD_NAME, food.getName());
            cv.put(FOOD_SIZE, food.getSizeBasket());
            cv.put(FOOD_SIZE_ID, food.getSizeBasketId());
            cv.put(FOOD_COUNT, food.getBasketCount());
            cv.put(FOOD_PRICE, food.getPriceBasket());
            cv.put(FOOD_UNIT_PRICE, food.getUnitPrice());
            cv.put(FOOD_ADDONS, food.getAddOns());
            cv.put(FOOD_ADDON_IDS, food.getAddOnIds());
            cv.put(FOOD_ADDON_PRICES, food.getAddOnPrices());
            cv.put(FOOD_INGREDIENTS, food.getIngredients());
            cv.put(FOOD_INGREDIENT_IDS, food.getIngredientIds());
            cv.put(FOOD_INGREDIENT_PRICES, food.getIngredientPrices());
            cv.put(FOOD_COMMENT, food.getComment());
            db.update(TABLE_BASKET, cv, FOOD_ID + " = ? ", new String[]{entryId});
            //db.update(TABLE_BASKET, null, cv);
            db.close();
        }
    }

    public void addFoodBasket(String userId, Food food) {
        AppInstance.getInstance(myContext).setCurrentResName(AppUtils.CURRENT_RESTAURANT_NAME);
        AppInstance.getInstance(myContext).setCurrentResNameAR(AppUtils.CURRENT_RESTAURANT_NAME_AR);
        AppInstance.getInstance(myContext).setCurrentResImage(AppUtils.CURRENT_RESTAURANT_IMAGE);
        AppUtils.IS_CART_VISIBLE = "";
        SQLiteDatabase db = this.getWritableDatabase();
        userId = "".equals(userId) ? "-1" : userId;

        if (checkForIdenticalItem(userId, food)) {

            System.out.println("dbHelperR : addFoodBasket : userId : " + userId);
            System.out.println("dbHelperR : addFoodBasket : getMerchantId() : " + food.getMerchantId());
            System.out.println("dbHelperR : addFoodBasket : getId : " + food.getId());
            System.out.println("dbHelperR : addFoodBasket : getName() : " + food.getName());
            System.out.println("dbHelperR : addFoodBasket : getSizeBasket() : " + food.getSizeBasket());
            System.out.println("dbHelperR : addFoodBasket : getSizeBasketId : " + food.getSizeBasketId());
            System.out.println("dbHelperR : addFoodBasket : getBasketCount() : " + food.getBasketCount());
            System.out.println("dbHelperR : addFoodBasket : getPriceBasket : " + food.getPriceBasket());
            System.out.println("dbHelperR : addFoodBasket : getUnitPrice() : " + food.getUnitPrice());
            System.out.println("dbHelperR : addFoodBasket : getAddOns()() : " + food.getAddOns());

            System.out.println("addFoodBasket : getAddOnIds : " + food.getAddOnIds());
            System.out.println("addFoodBasket : getAddOnPrices : " + food.getAddOnPrices());
            System.out.println("addFoodBasket : getIngredients : " + food.getIngredients());
            System.out.println("addFoodBasket : getIngredientIds : " + food.getIngredientIds());
            System.out.println("addFoodBasket : getIngredientPrices : " + food.getIngredientPrices());
            System.out.println("addFoodBasket : getComment : " + food.getComment());

            ContentValues cv = new ContentValues();
            cv.put(USER_ID, userId);
            cv.put(MERCHANT_ID, food.getMerchantId());
            cv.put(FOOD_ID, food.getId());
            cv.put(FOOD_NAME, food.getName());
            cv.put(FOOD_SIZE, food.getSizeBasket());
            cv.put(FOOD_SIZE_ID, food.getSizeBasketId());
            cv.put(FOOD_COUNT, food.getBasketCount());
            cv.put(FOOD_PRICE, food.getPriceBasket());
            cv.put(FOOD_UNIT_PRICE, food.getUnitPrice());
            cv.put(FOOD_ADDONS, food.getAddOns());
            cv.put(FOOD_ADDON_IDS, food.getAddOnIds());
            cv.put(FOOD_ADDON_PRICES, food.getAddOnPrices());
            cv.put(FOOD_INGREDIENTS, food.getIngredients());
            cv.put(FOOD_INGREDIENT_IDS, food.getIngredientIds());
            cv.put(FOOD_INGREDIENT_PRICES, food.getIngredientPrices());
            cv.put(FOOD_COMMENT, food.getComment());

            db.insert(TABLE_BASKET, null, cv);
            db.close();
        }
    }

    public void addRestauranttable(Restaurant argRestaurant) {

        SQLiteDatabase db = this.getWritableDatabase();


        System.out.println("dbHelperR : addRestauranttable : getMerchantId : " + argRestaurant.getMerchantId());
        System.out.println("dbHelperR : addRestauranttable : getRestaurantName() : " + argRestaurant.getRestaurantName());


        ContentValues cv = new ContentValues();
        cv.put(RESTAURANT_ID, argRestaurant.getMerchantId());
        cv.put(RESTAURANT_NAME, argRestaurant.getRestaurantName());
        cv.put(RESTAURANT_NAME_AR, argRestaurant.getRestaurantNameAr());

        db.insert(TABLE_RESTAURANT, null, cv);
        db.close();

    }

    private boolean checkForIdenticalItem(String userId, Food food) {
        String entryId = "";
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_BASKET + " WHERE "
                + USER_ID + " = ? AND "
                + FOOD_ID + " = ? AND "
                + FOOD_SIZE_ID + " = ? AND "
                + FOOD_INGREDIENT_IDS + " = ? AND "
                + FOOD_ADDON_IDS + "= ?";
        Cursor cursor = db.rawQuery(query, new String[]{userId, food.getId(), food.getSizeBasketId(), food.getIngredientIds(), food.getAddOnIds()});
        if (cursor.moveToFirst()) {
            entryId = cursor.getString(cursor.getColumnIndex(ENTRY_ID));
            ContentValues cv = new ContentValues();
            cv.put(FOOD_COUNT, Integer.parseInt(cursor.getString(cursor.getColumnIndex(FOOD_COUNT))) + Integer.parseInt(food.getBasketCount()));
            cv.put(FOOD_PRICE, Double.parseDouble(cursor.getString(cursor.getColumnIndex(FOOD_PRICE))) + Double.parseDouble(food.getPriceBasket()));
            db.update(TABLE_BASKET, cv, ENTRY_ID + "=" + entryId, null);
            db.close();
        }
        cursor.close();
        return entryId.length() == 0;
    }

    public List<Food> getFoodsBasket(String userId) {
        List<Food> foods = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        userId = "".equals(userId) ? "-1" : userId;

        String query = "SELECT * FROM " + TABLE_BASKET + " WHERE " + USER_ID + "=" + userId;
        AppUtils.log(query);
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Food food = new Food();
                food.setEntryId(cursor.getString(cursor.getColumnIndex(ENTRY_ID)));
                food.setId(cursor.getString(cursor.getColumnIndex(FOOD_ID)));
                System.out.println("dbHelperR : getFoodsBasket : FOOD_ID: " + cursor.getString(cursor.getColumnIndex(FOOD_ID)));
                food.setMerchantId(cursor.getString(cursor.getColumnIndex(MERCHANT_ID)));
                food.setName(cursor.getString(cursor.getColumnIndex(FOOD_NAME)));
                food.setPriceBasket(cursor.getString(cursor.getColumnIndex(FOOD_PRICE)));
                food.setUnitPrice(cursor.getString(cursor.getColumnIndex(FOOD_UNIT_PRICE)));
                food.setSizeBasket(cursor.getString(cursor.getColumnIndex(FOOD_SIZE)));
                food.setSizeBasketId(cursor.getString(cursor.getColumnIndex(FOOD_SIZE_ID)));
                food.setBasketCount(cursor.getString(cursor.getColumnIndex(FOOD_COUNT)));
                food.setAddOns(cursor.getString(cursor.getColumnIndex(FOOD_ADDONS)));
                food.setAddOnIds(cursor.getString(cursor.getColumnIndex(FOOD_ADDON_IDS)));
                food.setAddOnPrices(cursor.getString(cursor.getColumnIndex(FOOD_ADDON_PRICES)));
                food.setIngredients(cursor.getString(cursor.getColumnIndex(FOOD_INGREDIENTS)));
                food.setIngredientIds(cursor.getString(cursor.getColumnIndex(FOOD_INGREDIENT_IDS)));
                food.setIngredientPrices(cursor.getString(cursor.getColumnIndex(FOOD_INGREDIENT_PRICES)));
                food.setComment(cursor.getString(cursor.getColumnIndex(FOOD_COMMENT)));
                foods.add(food);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return foods;
    }

    public String getRestaurantNameById(String argRestaurantId) {

        SQLiteDatabase db = this.getReadableDatabase();
        String resName = "";

        String query = "SELECT * FROM " + TABLE_RESTAURANT + " WHERE " + RESTAURANT_ID + "=" + argRestaurantId;
        AppUtils.log(query);
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                resName = cursor.getString(cursor.getColumnIndex(RESTAURANT_NAME));


            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return resName;
    }

    public String getRestaurantNameArById(String argRestaurantId) {

        SQLiteDatabase db = this.getReadableDatabase();
        String resName = "";

        String query = "SELECT * FROM " + TABLE_RESTAURANT + " WHERE " + RESTAURANT_ID + "=" + argRestaurantId;
        AppUtils.log(query);
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                resName = cursor.getString(cursor.getColumnIndex(RESTAURANT_NAME_AR));


            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return resName;
    }

    public String getBasketMerchantId() {
        String merchantId = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + MERCHANT_ID + " FROM " + TABLE_BASKET + " LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            merchantId = cursor.getString(cursor.getColumnIndex(MERCHANT_ID));
        } else {
            AppUtils.log("No orders yet");
        }
        cursor.close();
        db.close();
        return merchantId;
    }

    public void updateFoodBasket(String priceNew, int countNew, String entryId) {

        System.out.println("dbHelperR : updateFoodBasket : priceNew : " + priceNew);
        System.out.println("dbHelperR : updateFoodBasket : countNew : " + countNew);
        System.out.println("dbHelperR : updateFoodBasket : entryId : " + entryId);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(FOOD_PRICE, priceNew);
        cv.put(FOOD_COUNT, countNew);
        db.update(TABLE_BASKET, cv, FOOD_ID + " = ? ", new String[]{entryId});
        db.close();
        AppInstance.getInstance(myContext).setCurrentResName(AppUtils.CURRENT_RESTAURANT_NAME);
        AppInstance.getInstance(myContext).setCurrentResNameAR(AppUtils.CURRENT_RESTAURANT_NAME_AR);
        AppInstance.getInstance(myContext).setCurrentResImage(AppUtils.CURRENT_RESTAURANT_IMAGE);
        AppUtils.IS_CART_VISIBLE = "";
    }

    public void updateLoggedInUser(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USER_ID, userId);
        db.update(TABLE_BASKET, cv, USER_ID + " = ? ", new String[]{"-1"});
        db.close();
    }

    public boolean removeFoodBasket(String entryId) {
        System.out.println("dbHelperR : removeFoodBasket : entryId " + entryId);
        SQLiteDatabase db = this.getWritableDatabase();
        int flag = db.delete(TABLE_BASKET, FOOD_ID + " = " + entryId, null);
        System.out.println("dbHelperR : removeFoodBasket : entryId  : flag : " + flag);
        db.close();
        return flag > 0;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_BASKET);
        db.close();
        AppInstance.getInstance(myContext).setCurrentResName("");
        AppInstance.getInstance(myContext).setCurrentResNameAR("");
        AppInstance.getInstance(myContext).setCurrentResImage("");
    }

    public void deleteAllRestaurant() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_RESTAURANT);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
        System.out.println("Rahul : DBHelper : onUpgrade : oldVersion : " + oldVersion);
        System.out.println("Rahul : DBHelper : onUpgrade : newVersion : " + oldVersion);
        if (oldVersion < newVersion) {
            String createtableRestaurant = "CREATE TABLE " + TABLE_RESTAURANT + " ("
                    + RES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + RESTAURANT_ID + " INTEGER, "
                    + RESTAURANT_NAME + " TEXT, "
                    + RESTAURANT_NAME_AR + " TEXT "
                    + ")";
            AppUtils.log(createtableRestaurant);
            db.execSQL(createtableRestaurant);
        }
    }

    public void addNotification(String driverId, Notification notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        if ("".equals(driverId)) return;

        ContentValues cv = new ContentValues();
        cv.put(DRIVER_ID, driverId);
        cv.put(MESSAGE, notification.getMessage());
        cv.put(DATE, notification.getDateCreated());
        cv.put(TIME, notification.getTime());

        db.insert(TABLE_NOTIFICATION, null, cv);
        db.close();
    }

    public List<Notification> getNotifications(String driverId) {
        List<Notification> notifications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        if ("".equals(driverId)) {
            return null;
        }

        String query = "SELECT * FROM " + TABLE_NOTIFICATION + " WHERE " + DRIVER_ID + "=" + driverId;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Notification notification = new Notification();
                notification.setId(cursor.getString(cursor.getColumnIndex(NOTIF_ID)));
                notification.setMessage(cursor.getString(cursor.getColumnIndex(MESSAGE)));
                notification.setDateCreated(cursor.getString(cursor.getColumnIndex(DATE)));
                notification.setTime(cursor.getString(cursor.getColumnIndex(TIME)));
                notifications.add(notification);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notifications;
    }
}
