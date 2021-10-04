package com.app.writeyourpackagenamehere.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.app.writeyourpackagenamehere.Config;
import com.app.writeyourpackagenamehere.R;
import com.app.writeyourpackagenamehere.adapters.AdapterFavorite;
import com.app.writeyourpackagenamehere.json.JsonUtils;
import com.app.writeyourpackagenamehere.models.ItemFavorite;
import com.app.writeyourpackagenamehere.utilities.DatabaseHandler;
import com.app.writeyourpackagenamehere.utilities.DatabaseHandler.DatabaseManager;
import com.app.writeyourpackagenamehere.utilities.ItemOffsetDecoration;

import java.util.ArrayList;
import java.util.List;

public class FragmentFavoriteGroups extends Fragment {

    RecyclerView recyclerView;
    DatabaseHandler databaseHandler;
    private DatabaseManager databaseManager;
    AdapterFavorite adapterFavorite;
    LinearLayout relativeLayout;
    JsonUtils util;
    List<ItemFavorite> arrayItemFavorite;
    ArrayList<String> array_news, array_news_cat_name, array_cid, array_cat_id, array_cat_name, array_title, array_image, array_desc, array_date;
    String[] str_news, str_news_cat_name, str_cid, str_cat_id, str_cat_name, str_title, str_image, str_desc, str_date;
    int textLength = 0;
    private RelativeLayout rootLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorite, container, false);
        setHasOptionsMenu(true);

        recyclerView = v.findViewById(R.id.recycler_view);

        rootLayout = v.findViewById(R.id.rootLayout);
        if (Config.ENABLE_RTL_MODE) {
            rootLayout.setRotationY(180);
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        relativeLayout = (LinearLayout) v.findViewById(R.id.relativeLayout);
        databaseHandler = new DatabaseHandler(getActivity());
        databaseManager = DatabaseManager.INSTANCE;
        databaseManager.init(getActivity());
        util = new JsonUtils(getActivity());

        arrayItemFavorite = databaseHandler.getAllData();
        adapterFavorite = new AdapterFavorite(getActivity(), arrayItemFavorite);
        recyclerView.setAdapter(adapterFavorite);
        if (arrayItemFavorite.size() == 0) {
            relativeLayout.setVisibility(View.VISIBLE);
        } else {
            relativeLayout.setVisibility(View.INVISIBLE);
        }

        return v;
    }

    public void onDestroy() {
        if (!databaseManager.isDatabaseClosed())
            databaseManager.closeDatabase();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!databaseManager.isDatabaseClosed())
            databaseManager.closeDatabase();
    }

    @Override
    public void onResume() {
        super.onResume();
        arrayItemFavorite = databaseHandler.getAllData();
        adapterFavorite = new AdapterFavorite(getActivity(), arrayItemFavorite);
        recyclerView.setAdapter(adapterFavorite);
        if (arrayItemFavorite.size() == 0) {
            relativeLayout.setVisibility(View.VISIBLE);
        } else {
            relativeLayout.setVisibility(View.INVISIBLE);
        }

        array_news = new ArrayList<String>();
        array_news_cat_name = new ArrayList<String>();
        array_cid = new ArrayList<String>();
        array_cat_id = new ArrayList<String>();
        array_cat_name = new ArrayList<String>();
        array_title = new ArrayList<String>();
        array_image = new ArrayList<String>();
        array_desc = new ArrayList<String>();
        array_date = new ArrayList<String>();

        str_news = new String[array_news.size()];
        str_news_cat_name = new String[array_news_cat_name.size()];
        str_cid = new String[array_cid.size()];
        str_cat_id = new String[array_cat_id.size()];
        str_cat_name = new String[array_cat_name.size()];
        str_title = new String[array_title.size()];
        str_image = new String[array_image.size()];
        str_desc = new String[array_desc.size()];
        str_date = new String[array_date.size()];

        for (int j = 0; j < arrayItemFavorite.size(); j++) {
            ItemFavorite objAllBean = arrayItemFavorite.get(j);

            array_cat_id.add(objAllBean.getCatId());
            str_cat_id = array_cat_id.toArray(str_cat_id);

            array_cid.add(String.valueOf(objAllBean.getCId()));
            str_cid = array_cid.toArray(str_cid);

            array_cat_name.add(objAllBean.getCategoryName());
            str_cat_name = array_cat_name.toArray(str_cat_name);

            array_title.add(String.valueOf(objAllBean.getNewsHeading()));
            str_title = array_title.toArray(str_title);

            array_title.add(String.valueOf(objAllBean.getNewsString()));
            str_title = array_title.toArray(str_title);


            array_image.add(String.valueOf(objAllBean.getNewsImage()));
            str_image = array_image.toArray(str_image);

            array_desc.add(String.valueOf(objAllBean.getNewsDesc()));
            str_desc = array_desc.toArray(str_desc);

            array_date.add(String.valueOf(objAllBean.getNewsDate()));
            str_date = array_date.toArray(str_date);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search, menu);

        final androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView)
                MenuItemCompat.getActionView(menu.findItem(R.id.search));

        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        searchView.setQueryHint(getString(R.string.search_hint));

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    searchMenuItem.collapseActionView();
                    searchView.setQuery("", false);
                }
            }
        });

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {

                textLength = newText.length();
                arrayItemFavorite.clear();

                for (int i = 0; i < str_title.length; i++) {
                    if (textLength <= str_title[i].length()) {
                        if (str_title[i].toLowerCase().contains(newText.toLowerCase())) {

                            ItemFavorite objItem = new ItemFavorite();

                            objItem.setCatId(str_cat_id[i]);
                            objItem.setCId(str_cid[i]);
                            objItem.setCategoryName(str_cat_name[i]);
                            objItem.setNewsHeading(str_title[i]);
                            objItem.setNewsString(str_title[i]); //
                            objItem.setNewsImage(str_image[i]);
                            objItem.setNewsDesc(str_desc[i]);
                            objItem.setNewsDate(str_date[i]);

                            arrayItemFavorite.add(objItem);

                        }
                    }
                }

                adapterFavorite = new AdapterFavorite(getActivity(), arrayItemFavorite);
                recyclerView.setAdapter(adapterFavorite);

                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
