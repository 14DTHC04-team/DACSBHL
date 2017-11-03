package com.example.bruce.dacs.MoreInfo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.bruce.dacs.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by BRUCE on 10/31/2017.
 */

public class Adapter_Comment_Image extends RecyclerView.Adapter<Adapter_Comment_Image.ViewHolder> {
    Context context;
    ArrayList<String> listImageComment;

    public Adapter_Comment_Image(Context context, ArrayList<String> listImageComment) {
        this.context = context;
        this.listImageComment = listImageComment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View mView = layoutInflater.inflate(R.layout.adapter_comment_image,parent,false);

        return  new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String image_link = listImageComment.get(position);
        Picasso.with(context).load(image_link).into(holder.imageView_Comment, new Callback() {
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
        return listImageComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView_Comment;
        ProgressBar progressBar;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView_Comment = (ImageView) itemView.findViewById(R.id.image_Comment);
            progressBar= (ProgressBar) itemView.findViewById(R.id.progress_bar_load_image_comment);
        }
    }
}
