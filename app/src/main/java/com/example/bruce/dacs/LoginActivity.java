package com.example.bruce.dacs;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.facebook.login.Login;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static com.example.bruce.dacs.LocationAndInfoActivity.REQUEST_ID_ACCESS_COURSE_FINE_LOCATION;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    ImageView imgLogo;
    TextView txtStatus;
    LoginButton btnLoginFB;
    SignInButton btnloginGG;
    CallbackManager callbackManager;

    AccessTokenTracker mTokenTracker;
    ProfileTracker mProfileTracker;

    Profile profile;

    EditText edtUsername,edtPass;
    Button btnCreate,btnLogin;
    //Login Firebsae
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    ProgressDialog progressDialoglogin;
    //Login GG
    public static int RC_SIGN_IN=111;
    GoogleApiClient mGoogleApiClient;
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
        loginWithGG();

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

    private void loginWithGG() {
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient=new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(LoginActivity.this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        btnloginGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(intent,RC_SIGN_IN);
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

        btnLoginFB = (LoginButton) findViewById(R.id.login_fb);
        btnloginGG= (SignInButton) findViewById(R.id.Login_gg);
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

        btnLoginFB.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));

        btnLoginFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());

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

    private void handleFacebookAccessToken(AccessToken accessToken) {
        progressDialoglogin=new ProgressDialog(this);
        progressDialoglogin.show();
        progressDialoglogin.setMessage("Login by facebook....");
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            finish();
                            if(progressDialoglogin.isShowing()){
                                progressDialoglogin.dismiss();
                            }
                            Intent target = new Intent(LoginActivity.this, LocationAndInfoActivity.class);
                            startActivity(target);
                            Constructer_UserProfile constructerUserProfile = new Constructer_UserProfile(FirebaseAuth.getInstance().getCurrentUser().getEmail(),FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
                            mData.child("User").child(firebaseAuth.getCurrentUser().getUid()).setValue(constructerUserProfile);

                        } else {
                            if(progressDialoglogin.isShowing()){
                                progressDialoglogin.dismiss();
                            }
                            // If sign in fails, display a message to the user.
                            AlertDialog.Builder tb2=new AlertDialog.Builder(LoginActivity.this)
                                    .setMessage("Login by Facebook Failed!!")
                                    .setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            tb2.create().show();
                        }

                        // ...
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
        GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if(requestCode==RC_SIGN_IN) {
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        progressDialoglogin=new ProgressDialog(this);
        progressDialoglogin.show();
        progressDialoglogin.setMessage("Login by google....");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            finish();
                            if(progressDialoglogin.isShowing()){
                                progressDialoglogin.dismiss();
                            }
                            startActivity(new Intent(getApplicationContext(), LocationAndInfoActivity.class));
                            Constructer_UserProfile constructerUserProfile = new Constructer_UserProfile(FirebaseAuth.getInstance().getCurrentUser().getEmail(),FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
                            mData.child("User").child(firebaseAuth.getCurrentUser().getUid()).setValue(constructerUserProfile);
                        } else {

                            AlertDialog.Builder tb2=new AlertDialog.Builder(LoginActivity.this)
                                    .setMessage("Login by Google Failed!!")
                                    .setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            tb2.create().show();
                        }

                        // ...
                    }
                });
    }

    DatabaseReference mData= FirebaseDatabase.getInstance().getReference();
    void Logign()
    {
        progressDialoglogin=new ProgressDialog(this);
        progressDialoglogin.show();
        progressDialoglogin.setMessage("Login by Gmail....");
        if(edtUsername.length()==0 || edtPass.length()==0)
        {
            if(edtUsername.length()==0){
                if(progressDialoglogin.isShowing()){
                    progressDialoglogin.dismiss();
                }
                Toast.makeText(this, "Please insert your email !!!", Toast.LENGTH_SHORT).show();
            }
            else{
                if(progressDialoglogin.isShowing()){
                    progressDialoglogin.dismiss();
                }
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
                               // if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified() == true){
                                    if(progressDialoglogin.isShowing()){
                                        progressDialoglogin.dismiss();
                                    }
                                    finish();

                                    startActivity(new Intent(getApplicationContext(),LocationAndInfoActivity.class));

                                    //day thong tin ngdung len Firebase
                                    Constructer_UserProfile constructerUserProfile = new Constructer_UserProfile(FirebaseAuth.getInstance().getCurrentUser().getEmail(),FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());

                                    mData.child("User").child(firebaseAuth.getCurrentUser().getUid()).setValue(constructerUserProfile);
                          //      }
//                                else{
//                                    Toast.makeText(getApplicationContext(), "Please check your email !", Toast.LENGTH_SHORT).show();
//                                    FirebaseAuth.getInstance().signOut();
//                                }

                            }
                            else {
                                if(progressDialoglogin.isShowing()){
                                    progressDialoglogin.dismiss();
                                }
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Wrong Password or Email. Please retype !", Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
