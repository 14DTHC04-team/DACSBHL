package com.example.bruce.dacs.BigMap;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bruce.dacs.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Admin on 03/11/2017.
 */

public class SearchLocationAdapter extends RecyclerView.Adapter<SearchLocationAdapter.ViewHolder> {
    ArrayList<Tourist_Location> tourist_locations;
    RecyclerViewClicklistener clicklistener;
    Context context;

    public SearchLocationAdapter(ArrayList<Tourist_Location> tourist_locations, Context context) {
        this.tourist_locations = tourist_locations;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.adapter_search_location,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Tourist_Location tourist_location = tourist_locations.get(position);
        holder.txtSearchLocationAddress.setText(tourist_location.Address);
        holder.txtSearchLocationName.setText(tourist_location.LocationName);
        Picasso.with(context).load(tourist_location.LocationImg).into(holder.imgSearch, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public int getItemCount() {
        return tourist_locations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgSearch;
        TextView txtSearchLocationAddress,txtSearchLocationName;
        ProgressBar progressBar;
        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            txtSearchLocationName = (TextView) itemView.findViewById(R.id.txtSearchLocationName);
            imgSearch = (ImageView) itemView.findViewById(R.id.imgSearch);
            txtSearchLocationAddress = (TextView) itemView.findViewById(R.id.txtSearchLocationAddress);
            progressBar= (ProgressBar) itemView.findViewById(R.id.progress_bar_download_search);
        }
        @Override
        public void onClick(View v) {
            if(clicklistener != null){
                clicklistener.itemClick(v,getPosition());
            }

        }

    }
    public void setClickListener(RecyclerViewClicklistener clickListener){
        this.clicklistener=clickListener;
    }

    public interface RecyclerViewClicklistener {
        public void itemClick(View view, int position);
    }
}
