package com.app.writeyourpackagenamehere.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.writeyourpackagenamehere.Config;
import com.app.writeyourpackagenamehere.R;
import com.app.writeyourpackagenamehere.json.JsonConfig;
import com.app.writeyourpackagenamehere.json.JsonUtils;
import com.app.writeyourpackagenamehere.models.ItemFavorite;
import com.app.writeyourpackagenamehere.models.ItemRecipesList;
import com.app.writeyourpackagenamehere.utilities.DatabaseHandler;
import com.app.writeyourpackagenamehere.utilities.GDPR;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityGroupsDetail extends AppCompatActivity {

    String str_cid, str_cat_id, str_cat_image, str_cat_name, str_title, str_image, str_desc, str_date;
    TextView news_title, news_date, cat;
    Button joinBtn;
    ImageView img_news, img_fav;
    DatabaseHandler db;
    List<ItemRecipesList> arrayItemRecipesList;
    ItemRecipesList itemRecipesList;
    final Context context = this;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    ProgressBar progressBar;
    static final String TAG = "RecipesDetail";
    CoordinatorLayout coordinatorLayout;
    private AdView adView;
    private Menu menu;

    FloatingActionButton facebook, whatsapp, share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_groups_detail);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("RTL Mode", "Working in Normal Mode, RTL Mode is Disabled");
        }

        loadAdMobBannerAd();

       final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        img_news = (ImageView) findViewById(R.id.image);
        img_fav = (FloatingActionButton) findViewById(R.id.img_fav);

        news_title = (TextView) findViewById(R.id.title);
        news_date = (TextView) findViewById(R.id.date);
        cat = (TextView) findViewById(R.id.cat);
        joinBtn = findViewById(R.id.join_group);

        facebook = findViewById(R.id.facebook);
        whatsapp = findViewById(R.id.whatsapp);
        share = findViewById(R.id.share);

        joinBtn.setOnClickListener(view -> {
            String formattedString = android.text.Html.fromHtml(str_date).toString();
            Intent Intent = new Intent();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(formattedString)));
            Intent.setPackage("com.whatsapp");
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sendInt = new Intent(Intent.ACTION_SEND);
                sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                sendInt.putExtra(Intent.EXTRA_TEXT, "Join This Group \n" + str_date + "\n"+ getString(R.string.share_text) + "\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
                sendInt.setType("text/plain");
                sendInt.setPackage("com.facebook.katana");
                startActivity(Intent.createChooser(sendInt, "Share"));


            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sendInt = new Intent(Intent.ACTION_SEND);
                sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                sendInt.putExtra(Intent.EXTRA_TEXT, "Join This Group \n" + str_date + "\n"+ getString(R.string.share_text) + "\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
                sendInt.setType("text/plain");
                sendInt.setPackage("com.whatsapp");
                startActivity(Intent.createChooser(sendInt, "Share"));

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendInt = new Intent(Intent.ACTION_SEND);
                sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                sendInt.putExtra(Intent.EXTRA_TEXT, "Join This Group \n" + str_date + "\n"+ getString(R.string.share_text) + "\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
                sendInt.setType("text/plain");
                startActivity(Intent.createChooser(sendInt, "Share"));
            }
        });

        db = new DatabaseHandler(ActivityGroupsDetail.this);

        arrayItemRecipesList = new ArrayList<ItemRecipesList>();

        if (JsonUtils.isNetworkAvailable(ActivityGroupsDetail.this)) {
            new MyTask().execute(Config.SERVER_URL + "/api.php?nid=" + JsonConfig.NEWS_ITEMID);
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_connect_network), Toast.LENGTH_SHORT).show();
        }

    }

    private class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressBar.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.VISIBLE);

            if (null == result || result.length() == 0) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_connect_network), Toast.LENGTH_SHORT).show();
                coordinatorLayout.setVisibility(View.GONE);
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(JsonConfig.CATEGORY_ARRAY_NAME);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        ItemRecipesList objItem = new ItemRecipesList();

                        objItem.setCId(objJson.getString(JsonConfig.CATEGORY_ITEM_CID));
                        objItem.setCategoryName(objJson.getString(JsonConfig.CATEGORY_ITEM_NAME));
                        objItem.setCategoryImage(objJson.getString(JsonConfig.CATEGORY_ITEM_IMAGE));
                        objItem.setCatId(objJson.getString(JsonConfig.CATEGORY_ITEM_CAT_ID));
                        objItem.setNewsImage(objJson.getString(JsonConfig.CATEGORY_ITEM_NEWSIMAGE));
                        objItem.setNewsHeading(objJson.getString(JsonConfig.CATEGORY_ITEM_NEWSHEADING));
                        objItem.setNewsDescription(objJson.getString(JsonConfig.CATEGORY_ITEM_NEWSDESCRI));
                        objItem.setNewsDate(objJson.getString(JsonConfig.CATEGORY_ITEM_NEWSDATE));

                        arrayItemRecipesList.add(objItem);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setAdapterToRecyclerView();
            }

        }
    }

    public void setAdapterToRecyclerView() {

        if (Config.ENABLE_RTL_MODE) {

            itemRecipesList = arrayItemRecipesList.get(0);
            str_cid = itemRecipesList.getCId();
            str_cat_name = itemRecipesList.getCategoryName();
            str_cat_image = itemRecipesList.getCategoryImage();
            str_cat_id = itemRecipesList.getCatId();
            str_title = itemRecipesList.getNewsHeading();
            str_desc = itemRecipesList.getNewsDescription();
            str_image = itemRecipesList.getNewsImage();
            str_date = itemRecipesList.getNewsDate();

            news_title.setText(str_title);
            news_date.setText(str_date);
            cat.setText(str_cat_name);


            Picasso
                    .with(context)
                    .load(Config.SERVER_URL + "/upload/category/" + itemRecipesList.getNewsImage())
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(img_news);

            List<ItemFavorite> itemFavorites = db.getFavRow(str_cat_id);
            if (itemFavorites.size() == 0) {
                img_fav.setImageResource(R.drawable.ic_favorite_outline_white);
            } else {
                if (itemFavorites.get(0).getCatId().equals(str_cat_id)) {
                    img_fav.setImageResource(R.drawable.ic_favorite_white);
                }
            }

            img_fav.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    List<ItemFavorite> itemFavorites = db.getFavRow(str_cat_id);
                    if (itemFavorites.size() == 0) {

                        db.AddtoFavorite(new ItemFavorite(str_cat_id, str_cid, str_cat_name, str_title, str_image, str_desc, str_date));
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.favorite_added), Toast.LENGTH_SHORT).show();
                        img_fav.setImageResource(R.drawable.ic_favorite_white);

                    } else {
                        if (itemFavorites.get(0).getCatId().equals(str_cat_id)) {

                            db.RemoveFav(new ItemFavorite(str_cat_id));
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();
                            img_fav.setImageResource(R.drawable.ic_favorite_outline_white);
                        }
                    }
                }
            });

        } else {

            itemRecipesList = arrayItemRecipesList.get(0);
            str_cid = itemRecipesList.getCId();
            str_cat_name = itemRecipesList.getCategoryName();
            str_cat_image = itemRecipesList.getCategoryImage();
            str_cat_id = itemRecipesList.getCatId();
            str_title = itemRecipesList.getNewsHeading();
            str_desc = itemRecipesList.getNewsDescription();
            str_image = itemRecipesList.getNewsImage();
            str_date = itemRecipesList.getNewsDate();

            news_title.setText(str_title);
            news_date.setText(str_date);

            cat.setText(str_cat_name);

            Picasso
                    .with(context)
                    .load(Config.SERVER_URL + "/upload/category/" + itemRecipesList.getCategoryImage())
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(img_news);

        }

    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        this.menu = menu;
        addToFavorite();
        return super.onCreateOptionsMenu(menu);
    }

    public void addToFavorite() {

        List<ItemFavorite> itemFavorites = db.getFavRow(str_cat_id);
        if (itemFavorites.size() == 0) {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_outline_white));
        } else {
            if (itemFavorites.get(0).getCatId().equals(str_cat_id)) {
                menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_white));
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_share:

                String formattedString = android.text.Html.fromHtml(str_date).toString();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, str_title + "\n" + formattedString + "\n" + getResources().getString(R.string.share_text) + "https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

                break;

            case R.id.img_fav:

                List<ItemFavorite> itemFavorites = db.getFavRow(str_cat_id);
                if (itemFavorites.size() == 0) {

                    db.AddtoFavorite(new ItemFavorite(
                            str_cat_id,
                            str_cid,
                            str_cat_name,
                            str_title,
                            str_image,
                            str_desc,
                            str_date));

                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.favorite_added), Toast.LENGTH_SHORT).show();
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_white));

                } else {
                        if (itemFavorites.get(0).getCatId().equals(str_cat_id)) {

                            db.RemoveFav(new ItemFavorite(str_cat_id));
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();
                            menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_outline_white));
                        }

    }

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void loadAdMobBannerAd() {
        if (Config.ENABLE_ADMOB_BANNER_ADS) {
            adView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(ActivityGroupsDetail.this)).build();
            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                }

                @Override
                public void onAdFailedToLoad(int error) {
                    adView.setVisibility(View.GONE);
                }

                @Override
                public void onAdLeftApplication() {
                }

                @Override
                public void onAdOpened() {
                }

                @Override
                public void onAdLoaded() {
                    adView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

}
