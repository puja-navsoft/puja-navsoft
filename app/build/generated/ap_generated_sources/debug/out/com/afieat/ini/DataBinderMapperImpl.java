package com.afieat.ini;

import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import androidx.databinding.DataBinderMapper;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.ViewDataBinding;
import com.afieat.ini.databinding.ActivityAddressFromMapBindingImpl;
import com.afieat.ini.databinding.ActivityDeliveryPointBindingImpl;
import com.afieat.ini.databinding.ActivityPhoneOtpScreenBindingImpl;
import com.afieat.ini.databinding.ActivityPhoneRegistrationBindingImpl;
import com.afieat.ini.databinding.ActivityProfileBindingImpl;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBinderMapperImpl extends DataBinderMapper {
  private static final int LAYOUT_ACTIVITYADDRESSFROMMAP = 1;

  private static final int LAYOUT_ACTIVITYDELIVERYPOINT = 2;

  private static final int LAYOUT_ACTIVITYPHONEOTPSCREEN = 3;

  private static final int LAYOUT_ACTIVITYPHONEREGISTRATION = 4;

  private static final int LAYOUT_ACTIVITYPROFILE = 5;

  private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(5);

  static {
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.afieat.ini.R.layout.activity_address_from_map, LAYOUT_ACTIVITYADDRESSFROMMAP);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.afieat.ini.R.layout.activity_delivery_point, LAYOUT_ACTIVITYDELIVERYPOINT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.afieat.ini.R.layout.activity_phone_otp_screen, LAYOUT_ACTIVITYPHONEOTPSCREEN);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.afieat.ini.R.layout.activity_phone_registration, LAYOUT_ACTIVITYPHONEREGISTRATION);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.afieat.ini.R.layout.activity_profile, LAYOUT_ACTIVITYPROFILE);
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = view.getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
        case  LAYOUT_ACTIVITYADDRESSFROMMAP: {
          if ("layout/activity_address_from_map_0".equals(tag)) {
            return new ActivityAddressFromMapBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_address_from_map is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYDELIVERYPOINT: {
          if ("layout/activity_delivery_point_0".equals(tag)) {
            return new ActivityDeliveryPointBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_delivery_point is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYPHONEOTPSCREEN: {
          if ("layout/activity_phone_otp_screen_0".equals(tag)) {
            return new ActivityPhoneOtpScreenBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_phone_otp_screen is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYPHONEREGISTRATION: {
          if ("layout/activity_phone_registration_0".equals(tag)) {
            return new ActivityPhoneRegistrationBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_phone_registration is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYPROFILE: {
          if ("layout/activity_profile_0".equals(tag)) {
            return new ActivityProfileBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_profile is invalid. Received: " + tag);
        }
      }
    }
    return null;
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View[] views, int layoutId) {
    if(views == null || views.length == 0) {
      return null;
    }
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = views[0].getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
      }
    }
    return null;
  }

  @Override
  public int getLayoutId(String tag) {
    if (tag == null) {
      return 0;
    }
    Integer tmpVal = InnerLayoutIdLookup.sKeys.get(tag);
    return tmpVal == null ? 0 : tmpVal;
  }

  @Override
  public String convertBrIdToString(int localId) {
    String tmpVal = InnerBrLookup.sKeys.get(localId);
    return tmpVal;
  }

  @Override
  public List<DataBinderMapper> collectDependencies() {
    ArrayList<DataBinderMapper> result = new ArrayList<DataBinderMapper>(1);
    result.add(new androidx.databinding.library.baseAdapters.DataBinderMapperImpl());
    return result;
  }

  private static class InnerBrLookup {
    static final SparseArray<String> sKeys = new SparseArray<String>(1);

    static {
      sKeys.put(0, "_all");
    }
  }

  private static class InnerLayoutIdLookup {
    static final HashMap<String, Integer> sKeys = new HashMap<String, Integer>(5);

    static {
      sKeys.put("layout/activity_address_from_map_0", com.afieat.ini.R.layout.activity_address_from_map);
      sKeys.put("layout/activity_delivery_point_0", com.afieat.ini.R.layout.activity_delivery_point);
      sKeys.put("layout/activity_phone_otp_screen_0", com.afieat.ini.R.layout.activity_phone_otp_screen);
      sKeys.put("layout/activity_phone_registration_0", com.afieat.ini.R.layout.activity_phone_registration);
      sKeys.put("layout/activity_profile_0", com.afieat.ini.R.layout.activity_profile);
    }
  }
}
