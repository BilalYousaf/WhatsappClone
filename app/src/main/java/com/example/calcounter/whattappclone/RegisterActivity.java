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

public class RegisterActivity extends AppCompatActivity {


    private Button CreateAccountButton;
    private EditText UserEmail, UserPassword;
    private TextView AlreadyHaveAccountLink;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();


        InitializeFields();

        AlreadyHaveAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendUserToLoginActivity();

            }
        });

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });


    }

    private void CreateNewAccount() {

        String email = UserEmail.getText().toString(); // getting our email text and converting to string
        String password = UserPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {// if the email field is empty- inform user input email

            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT);
        }

        if (TextUtils.isEmpty(password)) {

            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT);

        } else {

            progressDialog.setTitle("Creating New Account");
            progressDialog.setMessage("Please Wait, While We create Your New Account");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                SendUserToMainActivity();
                                Toast.makeText(RegisterActivity.this, "Account created Successfully..", Toast.LENGTH_SHORT);
                                progressDialog.dismiss();
                            } else {
                                String message = task.getException().toString(); // exception error
                                Toast.makeText(RegisterActivity.this, "Error Account NOT created Successfully..", Toast.LENGTH_SHORT);
                                progressDialog.dismiss();

                            }
                        }
                    });


        }

    }


    private void InitializeFields() {


        CreateAccountButton = (Button) findViewById(R.id.register);
        UserEmail = (EditText) findViewById(R.id.register_email);
        UserPassword = (EditText) findViewById(R.id.register_password);
        AlreadyHaveAccountLink = (TextView) findViewById(R.id.already_have_account_link);
        progressDialog = new ProgressDialog(this);
    }


    private void SendUserToLoginActivity() {
        Intent registerIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(registerIntent);
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
       mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();


    }
}





