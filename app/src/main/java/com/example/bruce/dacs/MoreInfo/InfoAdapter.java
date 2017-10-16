package com.example.bruce.dacs.MoreInfo;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bruce.dacs.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by BRUCE on 5/22/2017.
 */

public class InfoAdapter extends ArrayAdapter<MoreInfo> {
    @NonNull Activity context;
    @LayoutRes int resource;
    @NonNull List<MoreInfo> objects;
    public InfoAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<MoreInfo> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater=this.context.getLayoutInflater();
        View row=layoutInflater.inflate(this.resource,null);

        ImageView imageView = (ImageView) row.findViewById(R.id.infoImg);
        TextView txtInfo= (TextView) row.findViewById(R.id.infotxt);

        MoreInfo menu=this.objects.get(position);

        Picasso.with(getContext()).load(menu.Image).into(imageView);
        txtInfo.setText(menu.Info);
        return row;
    }
}
