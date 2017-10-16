package com.example.bruce.dacs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bruce.dacs.Users.Constructer_UserProfile;
import com.example.bruce.dacs.Users.Register;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static com.example.bruce.dacs.LocationAndInfoActivity.REQUEST_ID_ACCESS_COURSE_FINE_LOCATION;


public class LoginActivity extends AppCompatActivity {

    ImageView imgLogo;
    TextView txtStatus;
    LoginButton loginButton;
    CallbackManager callbackManager;
    ProfilePictureView profilePictureView;

    AccessTokenTracker mTokenTracker;
    ProfileTracker mProfileTracker;

    Profile profile;

    EditText edtUsername,edtPass;
    Button btnCreate,btnLogin;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_facebook);

        //xin cap quyen su dung GPS cua thiet bi
        if (Build.VERSION.SDK_INT >= 23) {
            int accessCoarsePermission
                    = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int accessFinePermission
                    = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);


            if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED
                    || accessFinePermission != PackageManager.PERMISSION_GRANTED) {

                // Các quyền cần người dùng cho phép.
                String[] permissions = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION};

                // Hiển thị một Dialog hỏi người dùng cho phép các quyền trên.
                ActivityCompat.requestPermissions(this, permissions,
                        REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);
            }
        }
        else
        {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        //khong cho quay man hinh dien thoai
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Animation animation = AnimationUtils.loadAnimation(LoginActivity.this,R.anim.anim);
        firebaseAuth=FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        LoginChecker();
        initilize();
        NewAcc();
        loginWithFB();

        imgLogo.startAnimation(animation);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent target = new Intent(LoginActivity.this, Register.class);
                startActivity(target);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logign();
            }
        });


    }

    public void LoginChecker()
    {
        profile = Profile.getCurrentProfile();
        if(profile != null)
        {
            Intent target = new Intent(LoginActivity.this,LocationAndInfoActivity.class);
            startActivity(target);
        }
        if(user!=null) {
            Intent target = new Intent(LoginActivity.this, LocationAndInfoActivity.class);
            startActivity(target);
        }

    }

    //anh xa
    public void initilize()
    {

        loginButton = (LoginButton) findViewById(R.id.login_button);
        txtStatus = (TextView) findViewById(R.id.TextViewStatus);
        btnCreate = (Button) findViewById(R.id.Create);
        btnLogin = (Button) findViewById(R.id.Login);
        edtUsername= (EditText) findViewById(R.id.editTextUserName);
        edtPass= (EditText) findViewById(R.id.editTextPassword);
        imgLogo = (ImageView)findViewById(R.id.imageView2);
    }

    //đăng nhập khi đã đăng nhập tài khoản Facebook
    public void loginWithFB()
    {
        callbackManager = CallbackManager.Factory.create();

        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //dang nhap thanh cong thi mo activity LocationAndInfo
                final Intent target = new Intent(LoginActivity.this,LocationAndInfoActivity.class);

                // Facebook Email address
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                //Log.v("LoginActivity Response ", response.toString());

                                try {
                                    String Name = object.getString("name");
                                    String Email = object.getString("email");
                                   // String Birthday = object.getString("birthday");

                                    //day~ email facebook vao trong bo nho may'
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("OldEmail",Email);
                                    editor.apply();

                                    //chuyen email facebook qua LocationAndInfoActivity
                                    target.putExtra("Email",Email);
                                    finish();
                                    startActivity(target);

                                    //bo chuoi~ .com or .vn ra khoi email khi dang nhap bang facebook
                                    int index = Email.indexOf(".");
                                    String key = Email.substring(0, index);
                                    Constructer_UserProfile constructerUserProfile = new Constructer_UserProfile(Email,Name,"","");
                                    mData.child("User").child("fb_"+key).setValue(constructerUserProfile);

                                } catch (JSONException e) {
                                    Log.e("wqe",e.toString());
                                    Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();



            }
            @Override
            public void onCancel() {
                txtStatus.setText("Login Canceled");
            }

            @Override
            public void onError(FacebookException error) {
                txtStatus.setText("Error:"+error.getMessage());
            }
        });
    }
    //đăng nhập khi chưa đăng nhập tài khoản Facebook
    public void NewAcc()
    {
        mTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken == null) {
                    //nếu logout thì txtStatus trở lại như ban đầu
                    txtStatus.setText("");
                }
            }
        };

         mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
            }
        };
        mTokenTracker.startTracking();
        mProfileTracker.startTracking();
    }


    @Override
    protected void onStop() {
        super.onStop();
        mProfileTracker.stopTracking();
        mTokenTracker.stopTracking();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Profile profile = Profile.getCurrentProfile();
//        if(profile != null) {
//            LoginManager.getInstance().logOut();
//            finish();
//            Intent target = new Intent(this, LoginActivity.class);
//            startActivity(target);
//        }
//    }

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    DatabaseReference mData= FirebaseDatabase.getInstance().getReference();
    void Logign()
    {

        if(edtUsername.length()==0 || edtPass.length()==0)
        {
            if(edtUsername.length()==0){

                Toast.makeText(this, "Please insert your email !!!", Toast.LENGTH_SHORT).show();
            }
            else{

                Toast.makeText(this, "Please insert your password !!!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            final String email = edtUsername.getText().toString();
            String pass = edtPass.getText().toString();
            firebaseAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful() ) {
                                if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified() == true){

                                    finish();
                                    startActivity(new Intent(getApplicationContext(),LocationAndInfoActivity.class));

                                    //day thong tin ngdung len Firebase
                                    Constructer_UserProfile constructerUserProfile = new Constructer_UserProfile(FirebaseAuth.getInstance().getCurrentUser().getEmail(),FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),"","");
                                    String tempt = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                    int index = tempt.indexOf(".");
                                    String key = tempt.substring(0, index);
                                    mData.child("User").child(key).setValue(constructerUserProfile);
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "Please check your email !", Toast.LENGTH_SHORT).show();
                                    FirebaseAuth.getInstance().signOut();
                                }

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Wrong Password or Email. Please retype !", Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }
    }
}
