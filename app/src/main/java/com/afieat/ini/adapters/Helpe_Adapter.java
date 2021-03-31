package com.afieat.ini.adapters;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afieat.ini.HelpCenterActivity;
import com.afieat.ini.R;
import com.afieat.ini.utils.AppUtils;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by amartya on 05/10/17 with love.
 */

public class Helpe_Adapter extends RecyclerView.Adapter<Helpe_Adapter.ViewH> {
    private HelpCenterActivity helpCenterActivity;
   private JSONArray helpandsupport;

    public Helpe_Adapter(HelpCenterActivity helpCenterActivity, JSONArray helpandsupport) {
        this.helpCenterActivity = helpCenterActivity;
        this.helpandsupport = helpandsupport;

    }

    @Override
    public Helpe_Adapter.ViewH onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.help_item, parent, false);
        return new ViewH(item);
    }

    @Override
    public void onBindViewHolder(Helpe_Adapter.ViewH holder, int position) {
        final JSONObject itemobject = helpandsupport.optJSONObject(position);
        holder.Name.setText(itemobject.optString("categoryname"));
        holder.Description.setText(itemobject.optString("question"));

        final String[] Path = itemobject.optString("mediadescription").split("/");
        AppUtils.log("@@TTT-" + Path[Path.length - 1]);
        Glide.with(helpCenterActivity).load("https://img.youtube.com/vi/" + Path[Path.length - 1] + "/0.jpg").placeholder(R.drawable.placeholder_land).into(holder.Webv);
        AppUtils.log("@@KK" + "https://img.youtube.com/vi/" + Path[Path.length - 1] + "/0.jpg");
        holder.Webv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent applicationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + Path[Path.length - 1]));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(itemobject.optString("mediadescription")));
                try {
                    helpCenterActivity.startActivity(applicationIntent);
                } catch (ActivityNotFoundException ex) {
                    helpCenterActivity.startActivity(browserIntent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return helpandsupport.length();
    }

    public class ViewH extends RecyclerView.ViewHolder {
        TextView Name, Description;
        ImageView Webv;

        public ViewH(View itemView) {
            super(itemView);
            Description = (TextView) itemView.findViewById(R.id.Description);
            Name = (TextView) itemView.findViewById(R.id.Name);
            Webv = (ImageView) itemView.findViewById(R.id.webView);
        }
    }
}
