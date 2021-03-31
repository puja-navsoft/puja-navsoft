package com.afieat.ini;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocationDrawerActivityNew extends AppCompatActivity {

    private AppInstance appInstance;
    private String mUserId;
    private final String[] navItems = {"Address", "Restaurants", "My Orders", "My Account", "My Wallet", "My Reviews", "My Deals", "Language", "Help Center"};
    private final String[] navItems_ar = {"عنوان", "المطاعم", "طلبياتي", "حسابي", "قسائم الشراء الخاصة بي", " التعليقات و التقييم", "عروضي", "لغة", "مركز المساعدة"};
    private final int[] navIcons = {
            R.drawable.nav_address,
            R.drawable.nav_restaurants,
            R.drawable.nav_orders,
            R.drawable.nav_account,
            R.drawable.nav_wallet,
            R.drawable.nav_star,
            R.drawable.nav_deal,
            R.drawable.nav_language,
            /*R.drawable.nav_settings,*/
            /*R.drawable.nav_contact,*/
            R.drawable.nav_help,
            /*R.drawable.nav_signout*/
    };
    private ListView lvNav;
    private String[] temp_navitem;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private final int REQUEST_CHECKIN = 101;
    private int[] temp_navicon = new int[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_drawer_new);
        setContentView(R.layout.activity_location_drawer_new);

        lvNav = (ListView) findViewById(R.id.lvNav);

        appInstance = AppInstance.getInstance(getApplicationContext());

        AppUtils.APPTOKEN = appInstance.getAuthkey();

        mUserId = appInstance.getFromSharedPref(AppUtils.PREF_USER_ID);

        System.out.println("LocationDrawerActivityNew : jh");
        Toast.makeText(getApplicationContext(), "vuyfuyvu ", Toast.LENGTH_LONG).show();
        mNavigationView = (NavigationView) findViewById(R.id.navView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        lvNav.setAdapter(new NavListAdapter());
        lvNav.setOnItemClickListener(new NavMenuItemClickListener());

    }


    class NavListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return temp_navicon.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String item = temp_navitem[position];
            int drawable = temp_navicon[position];
            TextView view = (TextView) LayoutInflater.from(LocationDrawerActivityNew.this).inflate(R.layout.layout_nav_list_item, null, false);
            view.setText(item);
            if (position == 1)
                view.setTextColor(Color.parseColor("#C63A2B"));
            view.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
            view.setCompoundDrawablePadding(55);
            return view;
        }
    }


    private class NavMenuItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            mDrawerLayout.closeDrawer(mNavigationView);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent;
                    switch (position) {
                        case 0:
                            //TODO: Change preferred address
                            intent = new Intent(LocationDrawerActivityNew.this, AddressSelectionActivity.class);
                            startActivity(intent);
                            break;
                        case 2:
                            startActivity(new Intent(LocationDrawerActivityNew.this, OrdersActivity.class));
                            break;
                        case 3:
                            //TODO: Account
                            if (mUserId.length() > 0) {
                                intent = new Intent(LocationDrawerActivityNew.this, ProfileActivity.class);
                                startActivity(intent);
                            } else {
                               intent = new Intent(LocationDrawerActivityNew.this, CheckInActivity.class);
                               startActivityForResult(intent, REQUEST_CHECKIN);
                            }
                            break;
                        case 4:
                            intent = new Intent(LocationDrawerActivityNew.this, WalletActivity.class);
                            startActivity(intent);
                            break;
                        case 5:
                            if (mUserId.length() > 0) {
                                intent = new Intent(LocationDrawerActivityNew.this, ReviewsActivity.class);
                                startActivity(intent);
                            } else {
                                intent = new Intent(LocationDrawerActivityNew.this, LanguageSelectionActivity.class);
                                startActivity(intent);
                            }

                            break;
                        case 6:
                            /*if (mUserId.length() > 0) {
                                intent = new Intent(LocationDrawerActivityNew.this, DealsActivity.class);
                                startActivity(intent);
                            } else {*/
                            intent = new Intent(LocationDrawerActivityNew.this, HelpCenterActivity.class);
                            startActivity(intent);
                            // }

                            break;
                        case 7:
                            if (mUserId.length() > 0) {
                                startActivityForResult(new Intent(LocationDrawerActivityNew.this, LanguageSelectionActivity.class), AppUtils.REQ_CHANGE_LANG);
                            } else {
                                startActivityForResult(new Intent(LocationDrawerActivityNew.this, HelpCenterActivity.class), AppUtils.REQ_CHANGE_LANG);
                            }

                            break;
                        case 8:
                            startActivityForResult(new Intent(LocationDrawerActivityNew.this, HelpCenterActivity.class), AppUtils.REQ_CHANGE_LANG);
                            break;
                        case 9:
                            //TODO: Sign Out

                            appInstance.setAuthkey("");
                            AppUtils.APPTOKEN = "";
                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_ID, "");
                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_LOGGED_IN, String.valueOf(false));
                            checkForLoggedInStatus();
                            Snackbar.make(findViewById(R.id.page), getString(R.string.msg_logout_success), Snackbar.LENGTH_SHORT).show();
                            LoginManager.getInstance().logOut();
                            break;
                        default:
                            break; }
                }
            }, 250);
        }
    }


    private void checkForLoggedInStatus() {


        String lang = appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG);
        if ("ar".equals(lang)) {
            temp_navitem = navItems_ar;
        } else {
            temp_navitem = navItems;
        }

        List<String> navList = new ArrayList<>(Arrays.asList(temp_navitem));
        List<Integer> navIconsList = new ArrayList<>();
        for (int navIcon : navIcons) {
            navIconsList.add(navIcon);
        }
        mUserId = appInstance.getFromSharedPref(AppUtils.PREF_USER_ID);
        View userPanel = findViewById(R.id.navHeader);
        assert userPanel != null;
        TextView tvUsername = (TextView) userPanel.findViewById(R.id.tvUsername);
        TextView tvUserEmail = (TextView) userPanel.findViewById(R.id.tvUserEmail);
        AppUtils.log("UserId: " + mUserId);

        if (mUserId.length() > 0) {
            // logged in

            // Sunit 27-01-2017


            if (!navList.contains("Sign Out") && !navList.contains("خروج")) {
                AppUtils.log("test11");

                if ("ar".equals(appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                    navList.add("خروج");

                } else {

                    navList.add("Sign Out");

                    AppUtils.log("test11-33");
                }

                navIconsList.add(R.drawable.nav_signout);


                AppUtils.showViews(userPanel);
                ((TextView) userPanel.findViewById(R.id.LogInBtn)).setVisibility(View.GONE);
                tvUsername.setText(appInstance.getFromSharedPref(AppUtils.PREF_USER_FNAME));
                AppUtils.log("@@ CALL- Callll" + appInstance.getFromSharedPref(AppUtils.PREF_USER_FNAME));
                tvUserEmail.setText(appInstance.getFromSharedPref(AppUtils.PREF_USER_EMAIL));
                tvUsername.setVisibility(View.VISIBLE);
                tvUserEmail.setVisibility(View.VISIBLE);
            }
        } else {

            // not loggeed in
            //     AppUtils.hideViews(userPanel);

            if (navList.contains("Sign Out") || navList.contains("خروج")) {
                AppUtils.log("test22");
                if ("ar".equals(appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                    navList.remove(navList.indexOf("خروج"));
                    navList.remove(navList.indexOf("عروضي"));
                    navList.remove(navList.indexOf("حسابي"));
                    AppUtils.log("test11-44");
                } else {
                    navList.remove(navList.indexOf("Sign Out"));
                    navList.remove(navList.indexOf("My Account"));
                    navList.remove(navList.indexOf("My Deals"));

                    AppUtils.log("test11-55");
                }
                navIconsList.remove(navIconsList.indexOf(R.drawable.nav_account));
                navIconsList.remove(navIconsList.indexOf(R.drawable.nav_deal));

                AppUtils.showViews(userPanel);
                ((TextView) userPanel.findViewById(R.id.LogInBtn)).setVisibility(View.VISIBLE);
                tvUsername.setVisibility(View.GONE);
                tvUserEmail.setVisibility(View.GONE);
            } else {

                if (navList.contains("My Account") || navList.contains("حسابي")) {
                    if ("ar".equals(appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG))) {

                        navList.remove(navList.indexOf("عروضي"));
                        navList.remove(navList.indexOf("حسابي"));
                    } else {
                        navList.remove(navList.indexOf("My Account"));
                        navList.remove(navList.indexOf("My Deals"));
                    }

                    navIconsList.remove(navIconsList.indexOf(R.drawable.nav_account));
                    navIconsList.remove(navIconsList.indexOf(R.drawable.nav_deal));
                }


            }
            AppUtils.showViews(userPanel);
            ((TextView) userPanel.findViewById(R.id.LogInBtn)).setVisibility(View.VISIBLE);
            tvUsername.setVisibility(View.GONE);
            tvUserEmail.setVisibility(View.GONE);
        }

        temp_navitem = new String[navList.size()];
        temp_navicon = new int[navIconsList.size()];
        for (int i = 0; i < navList.size(); i++) {
            temp_navitem[i] = navList.get(i);
            temp_navicon[i] = navIconsList.get(i);
        }
        AppUtils.log("Changing menu");
        ((CategoryListActivity.NavListAdapter) lvNav.getAdapter()).notifyDataSetChanged();
    }
}
