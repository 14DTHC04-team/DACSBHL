package com.example.bruce.dacs.MoreInfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bruce.dacs.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by BRUCE on 8/31/2017.
 */

public class Comment_Fragment extends android.support.v4.app.Fragment {
    View mView;

    RecyclerView recyclerView_Comment, recyclerView_CommentImage;
    Comment_Adapter adaper;
    ArrayList<Comment_Contructor> comment_contructors;

    DatabaseReference mData;
    int location_ID;

    ArrayList<String> urlImages;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.comment_fragment,container,false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView_Comment = (RecyclerView) mView.findViewById(R.id.recyclerView_Comment);
        recyclerView_Comment.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));

        adaper = new Comment_Adapter(comment_contructors,getActivity());
        recyclerView_Comment.setLayoutManager(layoutManager);
        recyclerView_Comment.setAdapter(adaper);
        recyclerView_Comment.setLayoutManager(new LinearLayoutManager(getActivity()));


        return mView;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase_Comment();
    }

    private void Firebase_Comment() {
        location_ID = getActivity().getIntent().getIntExtra("location_id",0);
        comment_contructors = new ArrayList<>();
        urlImages = new ArrayList<>();
        mData = FirebaseDatabase.getInstance().getReference();
        mData.child("Comments").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comment_Contructor comment_contructor = dataSnapshot.getValue(Comment_Contructor.class);
                if(location_ID == Integer.parseInt(comment_contructor.locationID)) {
                    comment_contructors.add(new Comment_Contructor(comment_contructor.userID, comment_contructor.locationID, comment_contructor.userName, comment_contructor.comment, comment_contructor.date, comment_contructor.like));
                    for(DataSnapshot commentImage: dataSnapshot.getChildren()){

                        for(DataSnapshot child_of_CommentImage : commentImage.getChildren()){
                            urlImages.add(child_of_CommentImage.getValue().toString());
                            Toast.makeText(getContext(), child_of_CommentImage.getValue().toString()+ " ", Toast.LENGTH_SHORT).show();
                        }
                    }
                    adaper.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}