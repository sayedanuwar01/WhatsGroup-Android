package com.app.writeyourpackagenamehere.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.writeyourpackagenamehere.Config;
import com.app.writeyourpackagenamehere.R;
import com.app.writeyourpackagenamehere.activities.ActivityGroupsDetail;
import com.app.writeyourpackagenamehere.json.JsonConfig;
import com.app.writeyourpackagenamehere.models.ItemRecipesList;
import com.app.writeyourpackagenamehere.utilities.GDPR;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.List;

public class AdapterRecent extends RecyclerView.Adapter<AdapterRecent.ViewHolder> {

    private Context context;
    private List<ItemRecipesList> arrayItemRecipesList;
    private ItemRecipesList itemRecipesList;
    private InterstitialAd interstitialAd;
    private int counter = 1;

    public class ViewHolder extends RecyclerView.ViewHolder {

         public TextView title , title1 , join;
        public LinearLayout relativeLayout;

        public ViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.news_title);
            title1 = view.findViewById(R.id.news_title1);
            join = view.findViewById(R.id.txt_join_group);
            relativeLayout = view.findViewById(R.id.llt_groups_cell);
        }

    }

    public AdapterRecent(Context context, List<ItemRecipesList> arrayItemRecipesList) {
        this.context = context;
        this.arrayItemRecipesList = arrayItemRecipesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_groups_list, parent, false);

        loadInterstitialAd();

        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        itemRecipesList = arrayItemRecipesList.get(position);

        Typeface font2 = Typeface.createFromAsset(context.getAssets(), "fonts/Ubuntu-Title.ttf");
        holder.title.setTypeface(font2);

        holder.title.setText(itemRecipesList.getNewsHeading());
        holder.title1.setText(itemRecipesList.getCategoryName());

        holder.join.setOnClickListener(v -> {
            itemRecipesList = arrayItemRecipesList.get(position);

            int pos = Integer.parseInt(itemRecipesList.getCatId());

            Intent intent = new Intent(context, ActivityGroupsDetail.class);
            intent.putExtra("POSITION", pos);
            JsonConfig.NEWS_ITEMID = itemRecipesList.getCatId();

            context.startActivity(intent);
            showInterstitialAd();
        });

        holder.relativeLayout.setOnClickListener(view -> {
            itemRecipesList = arrayItemRecipesList.get(position);

            int pos = Integer.parseInt(itemRecipesList.getCatId());

            Intent intent = new Intent(context, ActivityGroupsDetail.class);
            intent.putExtra("POSITION", pos);
            JsonConfig.NEWS_ITEMID = itemRecipesList.getCatId();

            context.startActivity(intent);
            showInterstitialAd();

        });

    }

    @Override
    public int getItemCount() {
        return arrayItemRecipesList.size();
    }

    private void loadInterstitialAd() {
        if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS) {
            interstitialAd = new InterstitialAd(context);
            interstitialAd.setAdUnitId(context.getResources().getString(R.string.admob_interstitial_id));
            final AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd((Activity) context)).build();
            interstitialAd.loadAd(adRequest);
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    interstitialAd.loadAd(adRequest);
                }
            });
        }
    }

    private void showInterstitialAd() {
        if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS) {
            if (interstitialAd != null && interstitialAd.isLoaded()) {
                if (counter == Config.ADMOB_INTERSTITIAL_ADS_INTERVAL) {
                    interstitialAd.show();
                    counter = 1;
                } else {
                    counter++;
                }
            }
        }
    }

}
