package com.example.bruce.dacs.Users;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bruce.dacs.CircleTransform;
import com.example.bruce.dacs.LocationAndInfoActivity;
import com.example.bruce.dacs.R;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.bruce.dacs.R.id.txtUserProfile_Mail;

public class User_Profile extends AppCompatActivity {


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Profile profile = Profile.getCurrentProfile();
    int REQUEST_CODE_IMAGE = 1;
    CollapsingToolbarLayout toolbarLayout;
    TextView txtUserEmail,txtUserPass,txtUserProfile_Name_toolbar;
    ImageView imageView;
    ImageButton btnchange,btnCalendar;
    EditText txtUserName,txtUserPhone,txtUserBirthday,edtold,edtnew,edtconfirm;
    Button btnUpdate_information,btnSave,btnsave,btncancle;
    LinearLayout linearcanlendar;
    DatabaseReference mData;
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    Dialog dia ;
    Calendar calendar= Calendar.getInstance();
    SimpleDateFormat spddate = new SimpleDateFormat("dd/MM/yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__profile);
        //khong cho quay man hinh dien thoai
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mData=FirebaseDatabase.getInstance().getReference();

        initilize();
        btnCalendar.setEnabled(false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intentCamera, REQUEST_CODE_IMAGE);


            }
        });

        if(profile!=null) {
            btnchange.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.INVISIBLE);
        }
        //toolbarLayout.setTitle(Username(profile,user));
        Uri uriImgUser = ImageUser(profile,user);
        Picasso.with(this).load(uriImgUser).transform(new CircleTransform()).into(imageView);
//        Picasso.with(this).load(uriImgUser).into(new com.squareup.picasso.Target() {
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                toolbarLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable errorDrawable) {
//                Toast.makeText(User_Profile.this, "Can't load your main image", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//            }
//        });

        UserInfomation(profile,user);
        btnUpdate_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtUserName.setEnabled(true);
                txtUserPhone.setEnabled(true);
                txtUserBirthday.setEnabled(true);
                btnSave.setEnabled(true);
                btnUpdate_information.setEnabled(false);
                btnCalendar.setEnabled(true);
                linearcanlendar.setBackgroundColor(Color.BLACK);
                if(profile!=null) {
                    txtUserName.setEnabled(false);
                }

            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSave.setEnabled(false);
                txtUserName.setEnabled(false);
                txtUserPhone.setEnabled(false);
                txtUserBirthday.setEnabled(false);
                btnCalendar.setEnabled(false);
                btnUpdate_information.setEnabled(true);
                linearcanlendar.setBackgroundColor(Color.GRAY);
                if(user!=null) {
                    Constructer_UserProfile constructerUserProfile = new Constructer_UserProfile(txtUserEmail.getText().toString(), txtUserName.getText().toString(),txtUserPhone.getText().toString(),txtUserBirthday.getText().toString());
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(txtUserName.getText().toString().trim())
                            .build();
                    user.updateProfile(profileUpdates);

                    String tempt = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    int index = tempt.indexOf(".");
                    String key = tempt.substring(0, index);
                    mData.child("User").child(key).setValue(constructerUserProfile);
                    Toast.makeText(User_Profile.this, "Save Successful !", Toast.LENGTH_SHORT).show();
                }
                if(profile!=null)
                {

                    String Email=txtUserEmail.getText().toString();
                    int index = Email.indexOf(".");
                    String key = Email.substring(0, index);
                    Constructer_UserProfile constructerUserProfile = new Constructer_UserProfile(Email,txtUserName.getText().toString(),txtUserPhone.getText().toString(),txtUserBirthday.getText().toString());
                    mData.child("User").child("fb_"+key).setValue(constructerUserProfile);
                    Toast.makeText(User_Profile.this, "Save Successful !", Toast.LENGTH_SHORT).show();

                }
            }
        });
            btnchange.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dia = new Dialog(User_Profile.this);
                    dia.setContentView(R.layout.dialog_changepass);
                    dia.show();
                    edtold = (EditText) dia.findViewById(R.id.edtoldpass);
                    edtnew = (EditText) dia.findViewById(R.id.edtnewpass);
                    edtconfirm = (EditText) dia.findViewById(R.id.edtconfirmPass);
                    btnsave = (Button) dia.findViewById(R.id.btnsavepass);
                    btncancle = (Button) dia.findViewById(R.id.btncancelpass);
                    btncancle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dia.dismiss();
                        }
                    });

                    btnsave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(edtold.length() != 0){
                                firebaseAuth.signInWithEmailAndPassword(user.getEmail(), edtold.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {

                                                    if (task.isSuccessful()) {
                                                        if (edtnew.length() != 0 && edtconfirm.length() != 0) {
                                                            if (edtnew.getText().toString().equals(edtconfirm.getText().toString())) {
                                                                user.updatePassword(edtnew.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Toast.makeText(User_Profile.this, "Password has changed", Toast.LENGTH_SHORT).show();
                                                                        dia.dismiss();
                                                                    }
                                                                });
                                                            } else {
                                                                Toast.makeText(User_Profile.this, "Confirm Password is wrong ", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                        else {
                                                            Toast.makeText(User_Profile.this, "Please type your new password \n" +
                                                                    "and your confirm password", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                    else {
                                                        Toast.makeText(User_Profile.this, "Old password is wrong", Toast.LENGTH_SHORT).show();
                                                    }
                                            }
                                        });
                            }
                            else {
                                Toast.makeText(User_Profile.this, "Please type your old password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });
    }

    private void datePicker() {
            DatePickerDialog.OnDateSetListener callback=new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(Calendar.YEAR,year);
                    calendar.set(Calendar.MONTH,month);
                    calendar.set(Calendar.DATE,dayOfMonth);
                    txtUserBirthday.setText(spddate.format(calendar.getTime()));


                }
            };
            DatePickerDialog datePickerDialog=new DatePickerDialog(
                    User_Profile.this,
                    callback,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DATE));
            datePickerDialog.show();
        }

    public void initilize(){


         linearcanlendar= (LinearLayout) findViewById(R.id.linearCalendar);
        txtUserEmail = (TextView) findViewById(txtUserProfile_Mail);
        txtUserName = (EditText) findViewById(R.id.txtUserProfile_Name);
        txtUserPass = (TextView) findViewById(R.id.txtUserProfile_Pass);
        txtUserPhone = (EditText) findViewById(R.id.txtUserProfile_Phone);
        txtUserBirthday = (EditText) findViewById(R.id.txtUserProfile_Birthday);
        imageView = (ImageView) findViewById(R.id.imageView) ;
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        txtUserProfile_Name_toolbar = (TextView) findViewById(R.id.txtUserProfile_Name_toolbar);
        btnUpdate_information = (Button) findViewById(R.id.btnUpdate_information);
        btnSave= (Button) findViewById(R.id.btnSave);
        btnchange= (ImageButton) findViewById(R.id.btnchangepass);
        btnCalendar= (ImageButton) findViewById(R.id.btnCalendar);

    }
    public void UserInfomation(final Profile profile,final FirebaseUser user)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //lay email fb da luu trong may' ra
        final String name = preferences.getString("OldEmail", "");
        String Email1 = preferences.getString("Name", "");
        if(!name.equalsIgnoreCase(""))
        {
            Email1 = name ;  /* Edit the value here*/
        }
        final String Email = Email1;
        mData = FirebaseDatabase.getInstance().getReference();
        mData.child("User").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Constructer_UserProfile currentUser = dataSnapshot.getValue(Constructer_UserProfile.class);

                if(profile != null){
                    if(dataSnapshot.getKey().equals(user.getUid())) {
                        txtUserEmail.setText(currentUser.Email);
                        txtUserName.setText(currentUser.Name);
                        txtUserBirthday.setText(currentUser.BirthDay);
                        txtUserPhone.setText(currentUser.PhoneNumber);
                        txtUserProfile_Name_toolbar.setText(currentUser.Name);
                    }
                }
                if(user != null){
                    if(currentUser.Email.equals(user.getEmail())) {
                        txtUserEmail.setText(currentUser.Email);
                        txtUserName.setText(currentUser.Name);
                        txtUserBirthday.setText(currentUser.BirthDay);
                        txtUserPhone.setText(currentUser.PhoneNumber);
                        txtUserProfile_Name_toolbar.setText(currentUser.Name);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Constructer_UserProfile currentUser = dataSnapshot.getValue(Constructer_UserProfile.class);
                if(profile != null){
                    if(currentUser.Email.equals(Email)) {
                        txtUserEmail.setText(currentUser.Email);
                        txtUserName.setText(currentUser.Name);
                        txtUserBirthday.setText(currentUser.BirthDay);
                        txtUserPhone.setText(currentUser.PhoneNumber);
                        txtUserProfile_Name_toolbar.setText(currentUser.Name);
                    }
                }
                if(user != null){
                    if(currentUser.Email.equals(user.getEmail())) {
                        txtUserEmail.setText(currentUser.Email);
                        txtUserName.setText(currentUser.Name);
                        txtUserBirthday.setText(currentUser.BirthDay);
                        txtUserPhone.setText(currentUser.PhoneNumber);
                        txtUserProfile_Name_toolbar.setText(currentUser.Name);
                    }
                }
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
    @RequiresApi(api = Build.VERSION_CODES.N)
    FirebaseStorage storage=FirebaseStorage.getInstance();
    StorageReference storageRef=storage.getReference();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE_IMAGE && resultCode==RESULT_OK && data!=null)
        {
            Bitmap bitmap= (Bitmap) data.getExtras().get("data");
            //Drawable d = new BitmapDrawable(getResources(), bitmap);
            //toolbarLayout.setBackground(d);
            CircleTransform circleTransform = new CircleTransform();
            Bitmap circleBitmap = circleTransform.transform(bitmap);
            imageView.setImageBitmap(circleBitmap);

            StorageReference mountainsRef = storageRef.child("image_"+user.getEmail());
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap1 = imageView.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap1.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] data1 = baos.toByteArray();

            UploadTask uploadTask = mountainsRef.putBytes(data1);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder()

                            .setPhotoUri(Uri.parse(downloadUrl.toString()))
                            .build();
                    user.updateProfile(profileChangeRequest);
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public String Username(Profile profile,FirebaseUser user){

        if(profile != null){
            return  profile.getName();
        }
        if(user != null){
            return  user.getDisplayName();
        }
        else {
            return null;
        }
    }
    public Uri ImageUser ( Profile profile,FirebaseUser user)
    {
        if(profile != null){
            return  profile.getProfilePictureUri(200,200);
        }
        if(user != null){
            return  user.getPhotoUrl();
        }
        else {
            return null;
        }
    }
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(getApplicationContext(), LocationAndInfoActivity.class));
        super.onBackPressed();
    }
}
