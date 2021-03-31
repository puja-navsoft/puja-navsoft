package com.afieat.ini.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afieat.ini.R;
import com.afieat.ini.models.PrepaidCard;
import com.afieat.ini.utils.AppUtils;

import java.util.List;

/**
 * Created by amartya on 07/07/16 with love.
 */
public class CardAdapter extends BaseAdapter {

    private List<PrepaidCard> cards;

    public CardAdapter(List<PrepaidCard> cards) {
        this.cards = cards;
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Object getItem(int position) {
        return cards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View mConvertView=convertView;
        if (mConvertView == null) {
            mConvertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_prepaid_card, parent, false);
        }
        TextView tvCardRechargeDate = (TextView) mConvertView.findViewById(R.id.tvCardRechargeDate);
        TextView tvCardNo = (TextView) mConvertView.findViewById(R.id.tvCardNo);
        TextView tvCardBalance = (TextView) mConvertView.findViewById(R.id.tvCardBalance);

        PrepaidCard card = cards.get(position);

        tvCardRechargeDate.setText(AppUtils.formatDate(card.getRechargeDate()));
        tvCardNo.setText(card.getCardNo());
        tvCardBalance.setText(card.getBalance());
        return mConvertView;
    }
}
