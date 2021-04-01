package com.afieat.ini.databinding;
import com.afieat.ini.R;
import com.afieat.ini.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityDeliveryPointBindingImpl extends ActivityDeliveryPointBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.appbar, 1);
        sViewsWithIds.put(R.id.scrollView, 2);
        sViewsWithIds.put(R.id.fromLayout, 3);
        sViewsWithIds.put(R.id.fromImage, 4);
        sViewsWithIds.put(R.id.tvDeliverFrom, 5);
        sViewsWithIds.put(R.id.toLayout, 6);
        sViewsWithIds.put(R.id.toImage, 7);
        sViewsWithIds.put(R.id.tvDeliverTo, 8);
        sViewsWithIds.put(R.id.deliveryType, 9);
        sViewsWithIds.put(R.id.deliveryTypeTv, 10);
        sViewsWithIds.put(R.id.boxImage, 11);
        sViewsWithIds.put(R.id.tvPackage, 12);
        sViewsWithIds.put(R.id.orderAmountLayout, 13);
        sViewsWithIds.put(R.id.orderAmountHeader, 14);
        sViewsWithIds.put(R.id.tvOrderAmount, 15);
        sViewsWithIds.put(R.id.paymentImage, 16);
        sViewsWithIds.put(R.id.paymentType, 17);
        sViewsWithIds.put(R.id.tvDeliveryCharge, 18);
        sViewsWithIds.put(R.id.tvDeliveryChargeHeader, 19);
        sViewsWithIds.put(R.id.tvOrderPrice, 20);
        sViewsWithIds.put(R.id.tvOrderPriceHeader, 21);
        sViewsWithIds.put(R.id.tvTotalPrice, 22);
        sViewsWithIds.put(R.id.tvTotalPriceHeader, 23);
        sViewsWithIds.put(R.id.buttonLayout, 24);
        sViewsWithIds.put(R.id.deliveryTimeLayout, 25);
        sViewsWithIds.put(R.id.tvTimeDel, 26);
        sViewsWithIds.put(R.id.confirmOrderBtn, 27);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityDeliveryPointBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 28, sIncludes, sViewsWithIds));
    }
    private ActivityDeliveryPointBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.view.View) bindings[1]
            , (android.widget.ImageView) bindings[11]
            , (android.widget.LinearLayout) bindings[24]
            , (android.widget.Button) bindings[27]
            , (android.widget.RelativeLayout) bindings[25]
            , (android.widget.ImageView) bindings[9]
            , (android.widget.TextView) bindings[10]
            , (android.widget.ImageView) bindings[4]
            , (android.widget.LinearLayout) bindings[3]
            , (android.widget.TextView) bindings[14]
            , (android.widget.RelativeLayout) bindings[13]
            , (android.widget.ImageView) bindings[16]
            , (android.widget.TextView) bindings[17]
            , (android.widget.ScrollView) bindings[2]
            , (android.widget.ImageView) bindings[7]
            , (android.widget.LinearLayout) bindings[6]
            , (android.widget.TextView) bindings[5]
            , (android.widget.TextView) bindings[8]
            , (android.widget.TextView) bindings[18]
            , (android.widget.TextView) bindings[19]
            , (android.widget.EditText) bindings[15]
            , (android.widget.TextView) bindings[20]
            , (android.widget.TextView) bindings[21]
            , (android.widget.EditText) bindings[12]
            , (android.widget.TextView) bindings[26]
            , (android.widget.TextView) bindings[22]
            , (android.widget.TextView) bindings[23]
            );
        this.mboundView0 = (android.widget.RelativeLayout) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x1L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
            return variableSet;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): null
    flag mapping end*/
    //end
}