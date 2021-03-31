package com.afieat.ini.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.ContextThemeWrapper;

import com.afieat.ini.LocationActivity;
import com.afieat.ini.R;

import org.json.JSONObject;

/**
 * Created by amartya on 18/10/17 with love.
 */

public class ForceUpdateAsync extends AsyncTask<String, String, JSONObject> {

    private String latestVersion;
    private String currentVersion;
    private Context context;

    public ForceUpdateAsync(String currentVersion, Context context) {
        this.currentVersion = currentVersion;
        this.context = context;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
      //  latestVersion = "1.0.28";
        latestVersion = currentVersion;
       /* try {
            latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id="+context.getPackageName()+"&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div[itemprop=softwareVersion]")
                    .first()
                    .ownText();

        } catch (IOException e) {
            e.printStackTrace();
        }catch (NullPointerException e)
        {

        }*/


        // implement API in background and store the response in current variable
       /* String current = "";
        try {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
               // url = new URL(apiUrl);

                urlConnection = (HttpURLConnection) url
                        .openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader isw = new InputStreamReader(in);

                int data = isw.read();
                while (data != -1) {
                    current += (char) data;
                    data = isw.read();
                    System.out.print(current);

                }
                // return the data to onPostExecute method
                return new JSONObject();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
*/

        return new JSONObject();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        AppUtils.log("@@ LATEST VERSION- " + latestVersion + currentVersion);
        if (latestVersion != null) {
            if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                // Toast.makeText(context,"update is available.",Toast.LENGTH_LONG).show();
                if ((context instanceof LocationActivity)) {
                    if (!((Activity) context).isFinishing()) {
                        showForceUpdateDialog();
                    }
                }
            }
        }
        super.onPostExecute(jsonObject);
    }

    public void showForceUpdateDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context,
                R.style.Theme_AppCompat_DayNight_Dialog));

        alertDialogBuilder.setTitle(context.getString(R.string.youAreNotUpdatedTitle));
        alertDialogBuilder.setMessage(context.getString(R.string.youAreNotUpdatedMessage));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
                dialog.cancel();
            }
        });
        alertDialogBuilder.setNegativeButton(context.getString(R.string.skip), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialogBuilder.show();
    }
}
