package com.app.writeyourpackagenamehere.fragments;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.writeyourpackagenamehere.Config;
import com.app.writeyourpackagenamehere.R;
import com.app.writeyourpackagenamehere.adapters.AdapterRecent;
import com.app.writeyourpackagenamehere.json.JsonConfig;
import com.app.writeyourpackagenamehere.json.JsonUtils;
import com.app.writeyourpackagenamehere.models.ItemRecipesList;
import com.app.writeyourpackagenamehere.utilities.ItemOffsetDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentGroupsList extends Fragment {

    List<ItemRecipesList> arrayItemRecipesList = new ArrayList<>();
    AdapterRecent recipesAdapter;
    ArrayList<String> array_news, array_news_cat_name, array_cid, array_cat_id, array_cat_name, array_title, array_image, array_desc, array_date;
    String[] str_news, str_news_cat_name, str_cid, str_cat_id, str_cat_name, str_title, str_image, str_desc, str_date;
    JsonUtils jsonUtils;
    int textLength = 0;

    RecyclerView recyclerView;

    private RelativeLayout rootLayout;
    private RelativeLayout relativeLayout;

    boolean isFirst = true;
    boolean isLoading = false;
    private int pageNum = 0;

    private int passedHeight = 0;
    private int totalHeight = 0;
    int visibleCount = 0;

    ProgressBar bottomLoadingBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_groups, container, false);
        setHasOptionsMenu(true);

        passedHeight = Config.frgHeight;
        totalHeight = 0;
        visibleCount = Config.frgHeight / 240;

        bottomLoadingBar = v.findViewById(R.id.bottom_loading);
        bottomLoadingBar.setVisibility(View.INVISIBLE);

        rootLayout = v.findViewById(R.id.rootLayout);
        if (Config.ENABLE_RTL_MODE) {
            rootLayout.setRotationY(180);
        }
        relativeLayout = v.findViewById(R.id.no_network);

        arrayItemRecipesList = new ArrayList<>();
        array_news = new ArrayList<>();
        array_news_cat_name = new ArrayList<>();
        array_cid = new ArrayList<>();
        array_cat_id = new ArrayList<>();
        array_cat_name = new ArrayList<>();
        array_title = new ArrayList<>();
        array_image = new ArrayList<>();
        array_desc = new ArrayList<>();
        array_date = new ArrayList<>();

        str_news = new String[array_news.size()];
        str_news_cat_name = new String[array_news_cat_name.size()];
        str_cid = new String[array_cid.size()];
        str_cat_id = new String[array_cat_id.size()];
        str_cat_name = new String[array_cat_name.size()];
        str_title = new String[array_title.size()];
        str_image = new String[array_image.size()];
        str_desc = new String[array_desc.size()];
        str_date = new String[array_date.size()];

        jsonUtils = new JsonUtils(getActivity());

        recyclerView = v.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        recipesAdapter = new AdapterRecent(getActivity(), arrayItemRecipesList);
        recyclerView.setAdapter(recipesAdapter);

        if (JsonUtils.isNetworkAvailable(getActivity())) {
            int pn = pageNum+1;
            new MyTask().execute(Config.SERVER_URL + "/api.php?groups_page=" + pn);
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.failed_connect_network), Toast.LENGTH_SHORT).show();
            relativeLayout.setVisibility(View.VISIBLE);
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(passedHeight >= Config.frgHeight){
                    passedHeight += dy;
                    if(totalHeight - passedHeight <= 50){
                        if (!isLoading) {
                            bottomLoadingBar.setVisibility(View.VISIBLE);
                            isLoading = true;
                            int pn = pageNum+1;
                            new MyTask().execute(Config.SERVER_URL + "/api.php?groups_page=" + pn);
                        }
                    }
                }
            }
        });

        return v;
    }

    public void clearData() {
        int size = this.arrayItemRecipesList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.arrayItemRecipesList.remove(0);
            }

            recipesAdapter.notifyItemRangeRemoved(0, size);
        }
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

            bottomLoadingBar.setVisibility(View.INVISIBLE);

            if (null == result || result.length() == 0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.failed_connect_network), Toast.LENGTH_SHORT).show();
                relativeLayout.setVisibility(View.VISIBLE);
            }else {

//                array_news.clear();
//                array_news_cat_name.clear();
//                array_cid.clear();
//                array_cat_id.clear();
//                array_cat_name.clear();
//                array_title.clear();
//                array_image.clear();
//                array_desc.clear();
//                array_date.clear();

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(JsonConfig.CATEGORY_ARRAY_NAME);
                    JSONObject objJson = null;


                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        ItemRecipesList objItem = new ItemRecipesList();

                        objItem.setCId(objJson.getString(JsonConfig.CATEGORY_ITEM_CID));
                        objItem.setCategoryName(objJson.getString(JsonConfig.CATEGORY_ITEM_NAME));
                        objItem.setCatId(objJson.getString(JsonConfig.CATEGORY_ITEM_CAT_ID));
                        objItem.setNewsImage(objJson.getString(JsonConfig.CATEGORY_ITEM_NEWSIMAGE));
                        objItem.setNewsHeading(objJson.getString(JsonConfig.CATEGORY_ITEM_NEWSHEADING));
                        objItem.setNewsString(objJson.getString(JsonConfig.CATEGORY_ITEM_NEWSHEADING).substring(0,1));
                        objItem.setNewsDescription(objJson.getString(JsonConfig.CATEGORY_ITEM_NEWSDESCRI));
                        objItem.setNewsDate(objJson.getString(JsonConfig.CATEGORY_ITEM_NEWSDATE));

                        arrayItemRecipesList.add(objItem);

                        array_cat_id.add(objItem.getCatId());
                        str_cat_id = array_cat_id.toArray(str_cat_id);

                        array_cat_name.add(objItem.getCategoryName());
                        str_cat_name = array_cat_name.toArray(str_cat_name);

                        array_cid.add(String.valueOf(objItem.getCId()));
                        str_cid = array_cid.toArray(str_cid);

                        array_image.add(String.valueOf(objItem.getNewsImage()));
                        str_image = array_image.toArray(str_image);

                        array_title.add(String.valueOf(objItem.getNewsHeading()));
                        str_title = array_title.toArray(str_title);

                        array_desc.add(String.valueOf(objItem.getNewsDescription()));
                        str_desc = array_desc.toArray(str_desc);

                        array_date.add(String.valueOf(objItem.getNewsDate()));
                        str_date = array_date.toArray(str_date);

                        totalHeight += 240;
                    }
                    isFirst = false;
                    pageNum += 1;

                } catch (JSONException e) {
                    isLoading = false;
                    e.printStackTrace();
                }
//                for (int j = 0; j < arrayItemRecipesList.size(); j++) {
//
//                    itemRecipesList = arrayItemRecipesList.get(j);
//
//                    array_cat_id.add(itemRecipesList.getCatId());
//                    str_cat_id = array_cat_id.toArray(str_cat_id);
//
//                    array_cat_name.add(itemRecipesList.getCategoryName());
//                    str_cat_name = array_cat_name.toArray(str_cat_name);
//
//                    array_cid.add(String.valueOf(itemRecipesList.getCId()));
//                    str_cid = array_cid.toArray(str_cid);
//
//                    array_image.add(String.valueOf(itemRecipesList.getNewsImage()));
//                    str_image = array_image.toArray(str_image);
//
//                    array_title.add(String.valueOf(itemRecipesList.getNewsHeading()));
//                    str_title = array_title.toArray(str_title);
//
//                    array_desc.add(String.valueOf(itemRecipesList.getNewsDescription()));
//                    str_desc = array_desc.toArray(str_desc);
//
//                    array_date.add(String.valueOf(itemRecipesList.getNewsDate()));
//                    str_date = array_date.toArray(str_date);
//                }

                isLoading = false;
                setAdapterToRecyclerView();
            }

        }
    }

    public void setAdapterToRecyclerView() {
        recipesAdapter.notifyDataSetChanged();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search, menu);

        final androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView)
                MenuItemCompat.getActionView(menu.findItem(R.id.search));

        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        searchView.setQueryHint(getString(R.string.search_hint));

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                searchMenuItem.collapseActionView();
                searchView.setQuery("", false);
            }
        });

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {

                textLength = newText.length();
                arrayItemRecipesList.clear();

                for (int i = 0; i < str_title.length; i++) {
                    if (textLength <= str_title[i].length()) {
                        if (str_title[i].toLowerCase().contains(newText.toLowerCase())) {

                            ItemRecipesList objItem = new ItemRecipesList();

                            objItem.setCategoryName((str_cat_name[i]));
                            objItem.setCatId(str_cat_id[i]);
                            objItem.setCId(str_cid[i]);
                            objItem.setNewsDate(str_date[i]);
                            objItem.setNewsDescription(str_desc[i]);
                            objItem.setNewsHeading(str_title[i]);
                            objItem.setNewsImage(str_image[i]);
                            arrayItemRecipesList.add(objItem);
                        }
                    }
                }

                setAdapterToRecyclerView();
                return false;
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
            case android.R.id.home:

                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
