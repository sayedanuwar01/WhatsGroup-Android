package com.app.writeyourpackagenamehere.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.writeyourpackagenamehere.R;
import com.app.writeyourpackagenamehere.activities.ActivityGroupsDetail;
import com.app.writeyourpackagenamehere.json.JsonConfig;
import com.app.writeyourpackagenamehere.models.ItemFavorite;

import java.util.List;

public class AdapterFavorite extends RecyclerView.Adapter<AdapterFavorite.ViewHolder> {

    private Context context;
    private List<ItemFavorite> arrayItemFavorite;
    ItemFavorite itemFavorite;

    public class ViewHolder extends RecyclerView.ViewHolder {


        public TextView title , title1;
        public LinearLayout relativeLayout;

        public ViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.news_title);
            title1 = (TextView) view.findViewById(R.id.news_title1);
            relativeLayout = view.findViewById(R.id.verticalListTypeItem);

        }

    }

    public AdapterFavorite(Context mContext, List<ItemFavorite> arrayItemFavorite) {
        this.context = mContext;
        this.arrayItemFavorite = arrayItemFavorite;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_groups_list, parent, false);

        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        itemFavorite = arrayItemFavorite.get(position);

        Typeface font2 = Typeface.createFromAsset(context.getAssets(), "fonts/Ubuntu-Title.ttf");
        holder.title.setTypeface(font2);

        holder.title.setText(itemFavorite.getNewsHeading());
        holder.title1.setText(itemFavorite.getCategoryName());

        holder.relativeLayout.setOnClickListener(view -> {

            itemFavorite = arrayItemFavorite.get(position);
            int pos = Integer.parseInt(itemFavorite.getCatId());

            Intent intent = new Intent(context, ActivityGroupsDetail.class);
            intent.putExtra("POSITION", pos);
            JsonConfig.NEWS_ITEMID = itemFavorite.getCatId();

            context.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return arrayItemFavorite.size();
    }

}
