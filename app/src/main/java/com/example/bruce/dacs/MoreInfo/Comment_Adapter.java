package com.example.bruce.dacs.MoreInfo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bruce.dacs.R;

import java.util.ArrayList;

/**
 * Created by BRUCE on 8/31/2017.
 */

public class Comment_Adapter extends RecyclerView.Adapter<Comment_Adapter.ViewHolder> {

    ArrayList<Comment_Contructor> comment_contructors;
    Context context;


    public Comment_Adapter(ArrayList<Comment_Contructor> comment_contructors, Context context) {
        this.comment_contructors = comment_contructors;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View mView = layoutInflater.inflate(R.layout.comment_adapter,parent,false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Comment_Contructor cC = comment_contructors.get(position);
        holder.txtComment.setText(cC.comment);
        holder.txtDateOfComment.setText("Since: 11:45 pm 1/1/96");
        holder.txtUsername.setText(cC.userName);
    }

    @Override
    public int getItemCount() {
        return comment_contructors.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtUsername,txtComment,txtDateOfComment,txtLike;
        ImageView imgUserImage,imgComment;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txtUsername = (TextView) itemView.findViewById(R.id.userName);
            txtComment = (TextView) itemView.findViewById(R.id.Comment);
            txtDateOfComment = (TextView) itemView.findViewById(R.id.dateofComment);
            txtLike = (TextView) itemView.findViewById(R.id.like);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
