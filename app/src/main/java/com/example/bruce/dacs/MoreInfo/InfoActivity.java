package com.example.bruce.dacs.MoreInfo;

import android.content.Intent;
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
    SectionsPageAdapter adapter;
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

//-----------------------------------------floatingActionButton(btnComment)------------------------------------------------------------------------------------

        fabComment = (FloatingActionButton) findViewById(R.id.floatingActionButton_Comment);

        fabComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),Write_Comment_Activity.class);
                intent.putExtra("location_id",getIntent().getIntExtra("location_id",0));
                startActivity(intent);


            }
        });
    }

    private void setupViewPager(ViewPager viewPager){
        adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainInfo_Fragment(),"Infomation");
        adapter.addFragment(new Comment_Fragment(),"Comments");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }


}
