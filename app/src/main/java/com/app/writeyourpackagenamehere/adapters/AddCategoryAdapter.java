package com.app.writeyourpackagenamehere.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.writeyourpackagenamehere.R;
import com.app.writeyourpackagenamehere.models.ItemCategory;
import com.app.writeyourpackagenamehere.utilities.LocalDatabaseRepo;

import java.util.ArrayList;
import java.util.List;

public class AddCategoryAdapter extends ArrayAdapter {

    private List<ItemCategory> categoryList;
    private Context mContext;
    private int itemLayout;

    private LocalDatabaseRepo localDatabaseRepo = new LocalDatabaseRepo();
    private AddCategoryAdapter.ListFilter listFilter = new AddCategoryAdapter.ListFilter();

    public AddCategoryAdapter(Context context, int resource, List<ItemCategory> storeDataLst) {
        super(context, resource, storeDataLst);
        categoryList = storeDataLst;
        mContext = context;
        itemLayout = resource;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public ItemCategory getItem(int position) {
        return categoryList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        }

        TextView strCategory = view.findViewById(R.id.tv_category);
        strCategory.setText(getItem(position).getCategoryName());

        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class ListFilter extends Filter {
        private Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = new ArrayList<String>();
                    results.count = 0;
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();
                List<ItemCategory> matchValues = localDatabaseRepo.getCategoryInfo(mContext, searchStrLowerCase);

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) categoryList = (ArrayList<ItemCategory>) results.values;
            else {
                categoryList = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
}
