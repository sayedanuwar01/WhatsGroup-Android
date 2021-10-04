package com.app.writeyourpackagenamehere.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.writeyourpackagenamehere.Config;
import com.app.writeyourpackagenamehere.R;
import com.app.writeyourpackagenamehere.activities.MainActivity;
import com.app.writeyourpackagenamehere.json.JsonConfig;
import com.app.writeyourpackagenamehere.json.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FragmentAddCategory extends Fragment {

    MainActivity mainActivity;
    EditText groupName, groupLink;
    Button addButton;

    ArrayList<String> categoryNames = new ArrayList<>();
    ArrayList<String> categoryIds = new ArrayList<>();

    ArrayAdapter<String> spinnerAdapter;
    Spinner categoriesSpinner;
    String categoryId = "";

    String invitePrefix1 = "https://chat.whatsapp.com/";
    String invitePrefix2 = "https://chat.whatsapp.com/invite/";

    public FragmentAddCategory(MainActivity activity){
        mainActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_add_new_group, container, false);
        setHasOptionsMenu(true);

        categoryNames.clear();
        categoryIds.clear();

        groupName = v.findViewById(R.id.group_name);
        groupLink = v.findViewById(R.id.group_link);
        addButton = v.findViewById(R.id.group_Btn);

        spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, categoryNames);
        categoriesSpinner = v.findViewById(R.id.spinnerCategory);
        categoriesSpinner.setAdapter(spinnerAdapter);

        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                categoryId = categoryIds.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertdata();
            }

            private void insertdata() {

                final String name = groupName.getText().toString().trim();
                final String link = groupLink.getText().toString().trim();
                final String image = "logo.jpg";

                if (name.isEmpty()){
                    Toast.makeText(getActivity(), "Enter Group Name", Toast.LENGTH_SHORT).show();
                    return;
                }else if(categoryId.isEmpty()){
                    Toast.makeText(getActivity(), "Select Category", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (link.isEmpty()){
                    Toast.makeText(getActivity(), "Enter Link", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    if(link.toLowerCase().contains(invitePrefix1.toLowerCase()) || link.toLowerCase().contains(invitePrefix2.toLowerCase())){
                        String apiUrl = Config.SERVER_URL + "/insert.php";
                        StringRequest request = new StringRequest(Request.Method.POST, apiUrl, response -> {

                            if (response.contains("Data Inserted")){
                                Toast.makeText(getActivity(), "Your group is under review", Toast.LENGTH_LONG).show();
                                mainActivity.loadMainActivity();
                            }
                            else {
                                Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                            }
                        }, error -> Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show()

                        ){
                            @Override
                            protected Map<String, String> getParams() {

                                Map<String,String> params = new HashMap<String, String>();

                                params.put("name", name);
                                params.put("category", categoryId);
                                params.put("link", link);
                                params.put("image", "logo.jpg");

                                return params;
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                        requestQueue.add(request);
                    }else{
                        Toast.makeText(getActivity(), "Enter Valid Link", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        new MyTask().execute(Config.SERVER_URL + "/api.php");

        return v;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
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
                Toast.makeText(getActivity(), getResources().getString(R.string.failed_connect_network), Toast.LENGTH_SHORT).show();
            } else {
                categoryNames.clear();
                categoryIds.clear();
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(JsonConfig.CATEGORY_ARRAY_NAME);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        String tmpName = objJson.getString(JsonConfig.CATEGORY_NAME);
                        if(!tmpName.isEmpty()){
                            categoryNames.add(objJson.getString(JsonConfig.CATEGORY_NAME));
                            categoryIds.add(objJson.getString(JsonConfig.CATEGORY_CID));
                        }
                    }
                    spinnerAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}