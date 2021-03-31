package com.afieat.ini;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.afieat.ini.Security.Relogin;
import com.afieat.ini.adapters.CardAdapter;
import com.afieat.ini.models.PrepaidCard;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalletActivity extends AppCompatActivity {


    private ListView lvCards;
    private TextView tvNoCard,
            tvWalletBalance;
    private FloatingActionButton fabAddCard;

    private AlertDialog alertNewCard;

    private String mUserId;
    private String mWalletBalance;

    private List<PrepaidCard> cards;

    private final int REQ_LOGIN = 100;

    public WalletActivity() {
        cards = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.appbar);
        assert mToolbar != null;
        mToolbar.setTitle(getString(R.string.title_my_wallet));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserId = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID);

        tvNoCard = (TextView) findViewById(R.id.tvNoCard);
        tvWalletBalance = (TextView) findViewById(R.id.tvWalletBalance);

        TextView tvAddCard;
        tvAddCard = (TextView) findViewById(R.id.tvAddCard);
        fabAddCard = (FloatingActionButton) findViewById(R.id.fabAddCard);
        lvCards = (ListView) findViewById(R.id.lvCards);

        View cardNoInputView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_input_card_number, null);
        final EditText etCardNo = (EditText) cardNoInputView.findViewById(R.id.etCardNo);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(cardNoInputView);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String cardNo = etCardNo.getText().toString().trim();
                etCardNo.setText("");
                addNewCard(cardNo);
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                etCardNo.setText("");
            }
        });

        alertNewCard = builder.create();

        tvAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabAddCard.performClick();
            }
        });

        fabAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertNewCard.show();
            }
        });

        if (mUserId.length() > 0)
            fetchCardsFromNW();
        else {
            startActivityForResult(new Intent(WalletActivity.this, LoginActivity.class), REQ_LOGIN);
        }
    }

    private void addNewCard(final String cardNo) {
        final ProgressDialog progressDialog = AppUtils.showProgress(this, "", getString(R.string.msg_please_wait));
        Map<String, String> params = new HashMap<>();
        params.put("cardno", cardNo);
        params.put("shopuser_id", mUserId);
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.ADD_PREPAID_CARD, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AppUtils.hideProgress(progressDialog);
                        AppUtils.log(response);
                        if (response.has("code") && response.optInt("code") == 999) {
                            new Relogin(WalletActivity.this, new Relogin.OnLoginlistener() {
                                @Override
                                public void OnLoginSucess() {
                                    addNewCard(cardNo);
                                }

                                @Override
                                public void OnError(String Errormessage) {
                                    startActivityForResult(new Intent(WalletActivity.this, LoginActivity.class), AppUtils.REQ_LOGIN);

                                }
                            }).execute();
                        } else if (response.has("status") && response.optInt("status") == 0) {
                            new AlertDialog.Builder(WalletActivity.this).setMessage(response.optString("msg")).setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create().show();
                        } else
                            fetchCardsFromNW();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        AppUtils.hideProgress(progressDialog);
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void fetchCardsFromNW() {
        cards.clear();
        tvNoCard.setVisibility(View.GONE);
        final ProgressDialog progressDialog = AppUtils.showProgress(this, "", getString(R.string.msg_please_wait));
        Map<String, String> params = new HashMap<>();
        params.put("shopuser_id", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_PREPAID_CARDS, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AppUtils.hideProgress(progressDialog);
                        AppUtils.log(response.toString());
                        if (response.has("code") && response.optInt("code") == 999) {
                            new Relogin(WalletActivity.this, new Relogin.OnLoginlistener() {
                                @Override
                                public void OnLoginSucess() {
                                    fetchCardsFromNW();
                                }

                                @Override
                                public void OnError(String Errormessage) {
                                    startActivityForResult(new Intent(WalletActivity.this, LoginActivity.class), AppUtils.REQ_LOGIN);

                                }
                            }).execute();
                        } else {
                            JSONArray cardsArray = response.optJSONArray("prepaid_cards");
                            mWalletBalance = response.optString("wallet_balance");
                            for (int i = 0; i < cardsArray.length(); i++) {
                                JSONObject cardObject = cardsArray.optJSONObject(i);
                                PrepaidCard card = new PrepaidCard();
                                card.setCardNo(cardObject.optString("prepaid_code"));
                                card.setBalance(cardObject.optString("aomunt"));
                                card.setRechargeDate(cardObject.optString("recharge_on"));
                                cards.add(card);
                            }
                            tvWalletBalance.setText(getString(R.string.currency_us) + mWalletBalance);
                            if (cards.size() > 0) {
                                lvCards.setAdapter(new CardAdapter(cards));
                            } else {
                                tvNoCard.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppUtils.hideProgress(progressDialog);
                        error.printStackTrace();
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_LOGIN:
                if (resultCode == RESULT_OK) {
                    fetchCardsFromNW();
                } else {
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
    }
}
