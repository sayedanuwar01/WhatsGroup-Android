package com.app.writeyourpackagenamehere.activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.app.writeyourpackagenamehere.fragments.FragmentAddCategory;
import com.app.writeyourpackagenamehere.fragments.FragmentCategory;
import com.app.writeyourpackagenamehere.fragments.FragmentFavoriteGroups;
import com.app.writeyourpackagenamehere.json.JsonConfig;
import com.app.writeyourpackagenamehere.json.JsonUtils;
import com.app.writeyourpackagenamehere.models.ItemCategory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.app.writeyourpackagenamehere.Config;
import com.app.writeyourpackagenamehere.R;
import com.app.writeyourpackagenamehere.fragments.FragmentGroupsList;
import com.app.writeyourpackagenamehere.utilities.GDPR;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.app.writeyourpackagenamehere.utilities.Utils.gAllCategories;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {
    FragmentGroupsList mainFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    DrawerLayout drawer;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;

    BottomNavigationView bottomNavigationView;

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        Config.frgHeight = displayMetrics.heightPixels - 170 - 150;

        initUIViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (Config.ENABLE_EXIT_DIALOG) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setIcon(R.mipmap.ic_launcher);
                dialog.setTitle(R.string.app_name);
                dialog.setMessage(R.string.dialog_close_msg);
                dialog.setPositiveButton(R.string.dialog_option_yes, (dialogInterface, i) -> MainActivity.this.finish());

                dialog.setNegativeButton(R.string.dialog_option_rate_us, (dialogInterface, i) -> {
                    final String appName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                    }
                    finish();
                });

                dialog.setNeutralButton(R.string.dialog_option_more, (dialogInterface, i) -> {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps))));
                    finish();
                });
                dialog.show();

            } else {
                super.onBackPressed();
            }
        }
    }

    private void initUIViews(){
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        mainFragment = new FragmentGroupsList();
        loadFragment(mainFragment);

        loadAdMobBannerAd();
        loadAllCategories();
    }

    private void loadAdMobBannerAd() {
        if (Config.ENABLE_ADMOB_BANNER_ADS) {

            adView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(MainActivity.this)).build();
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
                   // adView.setVisibility(View.VISIBLE);
                }
            });
        }
    }
    public void loadMainActivity(){
        loadFragment(new FragmentGroupsList());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        closeDrawer();
        if(menuItem.getItemId() == R.id.drawer_recent){
            loadFragment(new FragmentGroupsList());
        }else if(menuItem.getItemId() == R.id.drawer_category){
            loadFragment(new FragmentCategory());
        }else if(menuItem.getItemId() == R.id.drawer_favorite){
            loadFragment(new FragmentFavoriteGroups());
        }else if(menuItem.getItemId() == R.id.drawer_add){
            loadFragment(new FragmentAddCategory(MainActivity.this));
            //startActivity(new Intent(getApplicationContext(), AddNewGroup.class));
        }/*else if(menuItem.getItemId() == R.id.drawer_facebook){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.facebbok.com/"+Config.usernameFacebook));
            startActivity(browserIntent);
            Toast.makeText(this, "Facebook", Toast.LENGTH_SHORT).show();
        }else if(menuItem.getItemId() == R.id.drawer_twitter){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.facebbok.com/"+Config.usernameTwitter));
            startActivity(browserIntent);
            Toast.makeText(this, "Facebook", Toast.LENGTH_SHORT).show();

        }else if(menuItem.getItemId() == R.id.drawer_instagram){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.facebbok.com/"+Config.usernameInstagram));
            startActivity(browserIntent);
            Toast.makeText(this, "Facebook", Toast.LENGTH_SHORT).show();

        }*/else if(menuItem.getItemId() == R.id.drawer_rate){
            final String appName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
            }
        }else if(menuItem.getItemId() == R.id.drawer_share){
            Intent sendInt = new Intent(Intent.ACTION_SEND);
            sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            sendInt.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + "\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
            sendInt.setType("text/plain");
            startActivity(Intent.createChooser(sendInt, "Share"));
        }else if(menuItem.getItemId() == R.id.drawer_about){
            showDialog();
        }else if(menuItem.getItemId() == R.id.drawer_policy){
            Intent intent = new Intent(this, CustomUrlActivity.class);
            startActivity(intent);
        }else if(menuItem.getItemId() == R.id.nav_home){
            loadFragment(new FragmentGroupsList());
        }else if(menuItem.getItemId() == R.id.nav_category){
            loadFragment(new FragmentCategory());
        }else if(menuItem.getItemId() == R.id.nav_add_group){
            loadFragment(new FragmentAddCategory(MainActivity.this));
        }else if(menuItem.getItemId() == R.id.nav_favorite){
            loadFragment(new FragmentFavoriteGroups());
        }
        return true;
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_about);
        dialog.show();
    }

    private void loadFragment(Fragment fragment) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void closeDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }

    private void loadAllCategories(){
        new Handler().postDelayed(() -> new MyTask().execute(Config.SERVER_URL + "/api.php"), 1500);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null == result || result.length() == 0) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.failed_connect_network), Toast.LENGTH_SHORT).show();
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(JsonConfig.CATEGORY_ARRAY_NAME);
                    JSONObject objJson;

                    gAllCategories.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {

                        objJson = jsonArray.getJSONObject(i);
                        ItemCategory objItem = new ItemCategory();
                        objItem.setCategoryName(objJson.getString(JsonConfig.CATEGORY_NAME));
                        objItem.setCategoryId(objJson.getInt(JsonConfig.CATEGORY_CID));
                        objItem.setCategoryImageUrl(objJson.getString(JsonConfig.CATEGORY_IMAGE));
                        gAllCategories.add(objItem);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}