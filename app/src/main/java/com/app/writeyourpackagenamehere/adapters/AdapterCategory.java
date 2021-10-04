package com.app.writeyourpackagenamehere.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.writeyourpackagenamehere.Config;
import com.app.writeyourpackagenamehere.R;
import com.app.writeyourpackagenamehere.activities.ActivityGroupsByCategory;
import com.app.writeyourpackagenamehere.json.JsonConfig;
import com.app.writeyourpackagenamehere.models.ItemCategory;
import com.app.writeyourpackagenamehere.utilities.GDPR;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.ViewHolder> {

    ItemCategory itemCategory;
    private Context context;
    private List<ItemCategory> arrayItemCategory;
    private InterstitialAd interstitialAd;
    int counter = 1;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public ImageView image;
        public RelativeLayout relativeLayout;

        public ViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.category_title);
            image = (ImageView) view.findViewById(R.id.category_image);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);

        }

    }

    public AdapterCategory(Context mContext, List<ItemCategory> arrayItemCategory) {
        this.context = mContext;
        this.arrayItemCategory = arrayItemCategory;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);

        loadInterstitialAd();

        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        itemCategory = arrayItemCategory.get(position);

        Typeface font2 = Typeface.createFromAsset(context.getAssets(), "fonts/Ubuntu-Title.ttf");
        holder.title.setTypeface(font2);

        holder.title.setText(itemCategory.getCategoryName());

        Picasso.with(context).load(Config.SERVER_URL + "/upload/category/" +
                itemCategory.getCategoryImageUrl()).placeholder(R.drawable.ic_thumbnail).into(holder.image);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                itemCategory = arrayItemCategory.get(position);
                int catId = itemCategory.getCategoryId();
                JsonConfig.CATEGORY_IDD = itemCategory.getCategoryId();
                Log.e("cat_id", "" + catId);
                JsonConfig.CATEGORY_TITLE = itemCategory.getCategoryName();

                Intent intent = new Intent(context, ActivityGroupsByCategory.class);
                context.startActivity(intent);

                showInterstitialAd();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayItemCategory.size();
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
