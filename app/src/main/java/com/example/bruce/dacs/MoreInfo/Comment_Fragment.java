package com.example.bruce.dacs.MoreInfo;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bruce.dacs.R;
import com.example.bruce.dacs.Users.Constructer_UserProfile;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by BRUCE on 8/31/2017.
 */

public class Comment_Fragment extends android.support.v4.app.Fragment {
    ViewGroup mView;

    RecyclerView recyclerView_Comment;


    Comment_Adapter adaper;
    ArrayList<Comment_Contructor> comment_contructors;

    Adapter_Comment_Image adapter_comment_image;
    DatabaseReference mData;
    int location_ID;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        mView = (ViewGroup) inflater.inflate(R.layout.comment_fragment,container,false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView_Comment = (RecyclerView) mView.findViewById(R.id.recyclerView_Comment);
        adaper = new Comment_Adapter(comment_contructors,getActivity(),location_ID);

        recyclerView_Comment.setLayoutManager(layoutManager);
        recyclerView_Comment.setAdapter(adaper);


        return mView;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        location_ID = getActivity().getIntent().getIntExtra("location_id",0);
        Firebase_Comment();

    }




    private void Firebase_Comment() {
        comment_contructors = new ArrayList<>();

        mData = FirebaseDatabase.getInstance().getReference();
        mData.child("Comments").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Comment_Contructor comment_contructor = dataSnapshot.getValue(Comment_Contructor.class);
                comment_contructor.commentImages = new ArrayList<>();
                if(location_ID == comment_contructor.locationID) {

                    for(DataSnapshot commentImage: dataSnapshot.getChildren()) {
                        for (DataSnapshot child_of_CommentImage : commentImage.getChildren()) {
                            comment_contructor.commentImages.add(child_of_CommentImage.getValue().toString());
                        }
                    }
                    mData.child("User").child(comment_contructor.userID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Constructer_UserProfile constructer_userProfile=dataSnapshot.getValue(Constructer_UserProfile.class);

                            comment_contructor.userImage=constructer_userProfile.Image;
                            comment_contructors.add(comment_contructor);
                            adaper.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



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