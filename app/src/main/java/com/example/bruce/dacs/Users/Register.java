package com.example.bruce.dacs.Users;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bruce.dacs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Register extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
    Button btnOk;
    EditText edtEmail,edtPass,edtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //khong cho quay man hinh dien thoai
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btnOk= (Button) findViewById(R.id.btnOK);
        edtEmail= (EditText) findViewById(R.id.edtEmail);
        edtPass= (EditText) findViewById(R.id.edtPass);
        edtName= (EditText) findViewById(R.id.edtUsername);


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtEmail.length() != 0 && edtName.length() != 0 && edtPass.length() != 0){

                    Register();
                }
                else
                {
                    Toast.makeText(Register.this, "Please type all information request below!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    void Register(){
        String email = edtEmail.getText().toString();
        String pass = edtPass.getText().toString();
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(edtName.getText().toString())
                                  //  .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                    .build();

                            firebaseAuth.getCurrentUser().updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(Register.this, "Register has successed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Register.this, "Register has failed", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
