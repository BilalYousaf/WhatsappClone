package com.example.calcounter.whattappclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {


    private Button LoginButton, PhoneLoginButton;
    private EditText UserEmail, UserPassword;
    private TextView NeedNewAccountLink, FogetPasswordLink;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //dataebaseReference = we are refering to our database created in Firebase


        InitializeFields();

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendUserToRegisterActivity();

            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();

            }
        });
        PhoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent PhoneloginIntent = new Intent(LoginActivity.this,PhoneLoginActivity.class);
                startActivity(PhoneloginIntent);
            }
        });
    }

    private void AllowUserToLogin() {

        String email = UserEmail.getText().toString(); // getting our email text and converting to string
        String  password = UserPassword.getText().toString();

        if (TextUtils.isEmpty(email)){// if the email field is empty- inform user input email

            Toast.makeText(this,"Please Enter Email", Toast.LENGTH_SHORT);
        }

        if (TextUtils.isEmpty(password)){

            Toast.makeText(this,"Please Enter Password", Toast.LENGTH_SHORT);

        }
        else {


            progressDialog.setTitle("Signing in");
            progressDialog.setMessage("Please Wait, while we Sign You in");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();



            firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                String currentUserID = firebaseAuth.getCurrentUser().getUid();
                                databaseReference.child("User").child(currentUserID).setValue("");
                                //will aadd user into the firebase database, when new user creates account- will be gievn a uniquye ID



                                SendUserToMainActivity();
                                Toast.makeText(LoginActivity.this, "Logged in Successfully..", Toast.LENGTH_SHORT );
                                progressDialog.dismiss();
                            }
                            else {
                                String message = task.getException().toString(); // exception error
                                Toast.makeText(LoginActivity.this, "Error Login Not Successfully..", Toast.LENGTH_SHORT );
                                progressDialog.dismiss();

                            }

                            }

                    });

        }





    }

    private void InitializeFields() {


        LoginButton = (Button) findViewById(R.id.login_button);
        PhoneLoginButton = (Button) findViewById(R.id.Phone_login);
        UserEmail = (EditText) findViewById(R.id.Login_email);
        UserPassword = (EditText) findViewById(R.id.Login_password);
        NeedNewAccountLink = (TextView) findViewById(R.id.need_new_account_link);
        FogetPasswordLink = (TextView) findViewById(R.id.forget_password_link);
        progressDialog = new ProgressDialog(this);

    }


    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();


    }


    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);


    }
}
