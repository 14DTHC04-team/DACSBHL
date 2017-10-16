package com.example.bruce.dacs;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LocationAndInfoActivity extends AppCompatActivity {
    ImageView imgFriendProfilePicture;
    TextView txtGreeting, txtAddress,txtEmail;

    ActionBarDrawerToggle mToggle;
    DrawerLayout mDrawerLayout;

    GPSTracker gps;
    double latitude;
    double longtitude;

    FirebaseUser user;


    DatabaseReference mData;
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 99;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_and_info);
        //khong cho quay man hinh dien thoai
        // this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        user = FirebaseAuth.getInstance().getCurrentUser();
//-------------------------------------------------------------UserLocationInfomation-------------------------------------------------------+        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gps = new GPSTracker(this);
        if(!gps.canGetLocation())
        {
            gps.showSettingAlert();
        }
        else {

        }
        txtAddress = (TextView) findViewById(R.id.textViewInfor);
        txtAddress.setText(gps.Address(gps.getLatitude(),gps.getLongtitude()));

        imgFriendProfilePicture = (ImageView) findViewById(R.id.imgUser);

        //neu dang nhap bang facebook thi lay hinh cua facebook
        final Profile profile = Profile.getCurrentProfile();

        txtGreeting = (TextView) findViewById(R.id.txtDisplayName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);

        if (profile != null) {


            Picasso.with(this).load(profile.getProfilePictureUri(200,200).toString()).transform(new CircleTransform()).into(imgFriendProfilePicture);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            //lay email fb da luu trong may' ra
            String name = preferences.getString("OldEmail", "");
            String Email1 = preferences.getString("Name", "");
            if(!name.equalsIgnoreCase(""))
            {
                Email1 = name ;  /* Edit the value here*/
            }
            txtEmail.setText(Email1);

            txtGreeting.setText(profile.getName());

        } else {

            if(user!=null) {

                //neu ko dang nhap bang facebook thi` ...
                Picasso.with(this).load(user.getPhotoUrl()).transform(new CircleTransform()).into(imgFriendProfilePicture);
                txtGreeting.setText(user.getDisplayName());
                txtEmail.setText(user.getEmail());
            }
        }
//----------------------------------------------------------------------------------------------------------------------------------
        //hien thi nut memu
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    //cho click tren nut menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    //khong cho bam' nut' Back khi dang trong layout nay
    public void onBackPressed() {
        // Khi an back thi lam` zi` (ghi trong day)
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (statusOfGPS == true) {
            finish();
            startActivity(getIntent());
        }
    }
}
