package com.example.bruce.dacs.MoreInfo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bruce.dacs.R;

public class InfoActivity extends AppCompatActivity {

    SectionsPageAdapter mSectionsPageAdapter;
    ViewPager mViewPage;
    TextView txtTitle;
    RatingBar ratingBar;
    String locationName;
    FloatingActionButton fabComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

//----------------------------------------ViewPage-------------------------------------------------------------------------------------
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPage = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPage);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPage);
//-----------------------------------------------Title Toolbar------------------------------------------------------------------------------

        txtTitle = (TextView) findViewById(R.id.title);
        locationName = getIntent().getStringExtra("locationname");
        txtTitle.setMaxLines(1);
        txtTitle.setEllipsize(TextUtils.TruncateAt.END);
        txtTitle.setText(locationName);
        
//---------------------------------------------------RatingBar--------------------------------------------------------------------------

        ratingBar = (RatingBar) findViewById(R.id.ratingBar_Main);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if(rating <= 1)
                {
                    Toast.makeText(InfoActivity.this, "You just rating "+ rating+ " star for this post", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(InfoActivity.this, "You just rating "+ rating+ " stars for this post", Toast.LENGTH_SHORT).show();
                }

            }
        });
//-----------------------------------------floatingActionButton(btnComment)------------------------------------------------------------------------------------

        fabComment = (FloatingActionButton) findViewById(R.id.floatingActionButton_Comment);
        fabComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InfoActivity.this, "Say something i'm giving up on you", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainInfo_Fragment(),"Infomation");
        adapter.addFragment(new Comment_Fragment(),"Comment");
        viewPager.setAdapter(adapter);
    }
}
