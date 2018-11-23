package com.example.calcounter.whattappclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {


    private Button SendVerificationCodeButton, Verifyutton;
    private EditText InputPhoneNumber, InputVerificationCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        firebaseAuth = FirebaseAuth.getInstance();

        SendVerificationCodeButton = (Button) findViewById(R.id.send_ver_code_button);
        Verifyutton = (Button) findViewById(R.id.verify_button);
        InputPhoneNumber = (EditText) findViewById(R.id.phone_number_input);
        InputVerificationCode = (EditText) findViewById(R.id.verification_code_input);
        progressDialog = new ProgressDialog(this);

        SendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // once the user clicks the sendvercbutton both fields below become invisiable


                //getting phone number
                String phoneNumber = InputPhoneNumber.getText().toString();

                if (TextUtils.isEmpty(phoneNumber))
                {

                    Toast.makeText(PhoneLoginActivity.this, "Phone Number is required ", Toast.LENGTH_SHORT).show();
                }
                else {

                    progressDialog.setTitle("Phone Verifcation");
                    progressDialog.setMessage("Please Wait, while we authenticate your phone");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration to verifiy number
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks
                }

            }
        });


        Verifyutton.setOnClickListener(new View.OnClickListener() {
            //This is the process of user entering the verification code
            //that the server sends to its mobile phone
            @Override
            public void onClick(View v) {


                SendVerificationCodeButton.setVisibility(View.INVISIBLE);
                InputPhoneNumber.setVisibility(View.INVISIBLE); //

                String VerificationCode = InputVerificationCode.getText().toString();
                if (TextUtils.isEmpty(VerificationCode)){
                    Toast.makeText(PhoneLoginActivity.this, "Please Enter Verification Code First", Toast.LENGTH_SHORT).show();

                    }
                    else {

                    progressDialog.setTitle("Verifcation Code ");
                    progressDialog.setMessage("Please Wait, while we Verifying your Verification Code ");
                     progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    // creating a phoneAuthCredential object using verifcation code and ID
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, VerificationCode);
                    signInWithPhoneAuthCredential(credential);
                }


            }
        });



        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            // this method will be called when a verification has been called

            signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressDialog.dismiss();
            // this will be called when the verification has been failed
                Toast.makeText(PhoneLoginActivity.this, "Invalid Phone Number, Please Enter Correct phone number With Your Country code. ", Toast.LENGTH_SHORT).show();

                SendVerificationCodeButton.setVisibility(View.VISIBLE);
                InputPhoneNumber.setVisibility(View.VISIBLE); //


                Verifyutton.setVisibility(View.INVISIBLE);
                InputVerificationCode.setVisibility(View.INVISIBLE);




            }

            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token)
            {
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                progressDialog.dismiss();

                Toast.makeText(PhoneLoginActivity.this, "Code has been Sent, Please Complete Verification ", Toast.LENGTH_SHORT).show();

                SendVerificationCodeButton.setVisibility(View.INVISIBLE);
                InputPhoneNumber.setVisibility(View.INVISIBLE); //


                Verifyutton.setVisibility(View.VISIBLE);
                InputVerificationCode.setVisibility(View.VISIBLE);


            }



        };



    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, ser is ready to go to to the Main app
                                progressDialog .dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "Congratulations, you're logged in Successfully", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();
                                
                        } else {

                            String message = task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Error; PLease Check Your Verification code ", Toast.LENGTH_SHORT).show();


                            // Sign in failed, display a message and update the UI
                             {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void SendUserToMainActivity() {


        Intent intent = new Intent(PhoneLoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}


