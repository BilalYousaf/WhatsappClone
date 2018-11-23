package com.example.calcounter.whattappclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button UpdateAccountsSettings;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;
    private String currentUserID;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private static final int GalleryPick = 1;
    private StorageReference UserProfileImagestorageReference;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        UserProfileImagestorageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");



        Initializefields ();


        userName.setVisibility(View.INVISIBLE);

        UpdateAccountsSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UpadateSettings();

            }
        });


        RetrieveUserInfo();


        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // to send user to their own mobile phone gallery

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);


            }
        });







    }



    private void Initializefields() {

        UpdateAccountsSettings = (Button) findViewById(R.id.update_setting_button);
        userStatus = (EditText) findViewById(R.id.set_user_name);
        userName = (EditText) findViewById(R.id.set_profile_status);
        userProfileImage = (CircleImageView) findViewById(R.id.set_profile_image);
        progressDialog = new ProgressDialog(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick && resultCode == RESULT_OK && data != null)
        {
            Uri ImageUrl = data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON) // will open crop activity where user can crop its image
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (requestCode == RESULT_OK);

            progressDialog.setTitle("Set Profile Image");
            progressDialog.setMessage("Please Wait, Profile image is updating");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            {
                Uri resultUri = result.getUri();

                StorageReference filePath = UserProfileImagestorageReference.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SettingsActivity.this, "Profile Image has been Successfully Updated", Toast.LENGTH_SHORT).show();
                           final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            databaseReference.child("Users").child(currentUserID).child("image")
                                    .setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful())  // means that the image has been storaed in firebase database
                                            {
                                                Toast.makeText(SettingsActivity.this, "Image Svaed in Database Successfully", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }

                                            else {
                                                String message = task.getException().toString();
                                                Toast.makeText(SettingsActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }

                                        }
                                    });


                        }
                        else {
                            String message = task.getException().toString();
                            Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });

            }


        }

    }

    private void UpadateSettings() { //user can update its settings
        String setUserName = userName.getText().toString(); //must needed
        String setStatus = userStatus.getText().toString(); /// must needed

        if (TextUtils.isEmpty(setUserName)) {//Verify, if username empty

            Toast.makeText(this, "Please Your UserName", Toast.LENGTH_SHORT).show();

        }
        if (TextUtils.isEmpty(setStatus)) {//Verify, if username empty

            Toast.makeText(this, "Please Add Your Status", Toast.LENGTH_SHORT).show();

        } else { // else if b9oth verified then store data inside Firebase
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("name", setUserName);
            profileMap.put("status", setStatus);

            databaseReference.child("Users").child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            if (task.isSuccessful()) {

                                SendUserToMainActivity();
                                Toast.makeText(SettingsActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            } else {

                                String message = task.getException().toString();
                                Toast.makeText(SettingsActivity.this, "Error Profile Not Updated" + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    private void RetrieveUserInfo() {

        databaseReference.child("Users").child(currentUserID) // searching for user that iss online.
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {

                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")&&(dataSnapshot.hasChild("image")))) // if users already exists
                        //
                        {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrievesStatus = dataSnapshot.child("status").getValue().toString();
                            String retrievesProfileImage = dataSnapshot.child("image").getValue().toString();

                            userName.setText(retrieveUserName);
                            userStatus.setText(retrievesStatus);
                            Picasso.get().load(retrievesProfileImage).into(userProfileImage);

                        }


                        else if (((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))))

                        {

                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrievesStatus = dataSnapshot.child("status").getValue().toString();

                            userName.setText(retrieveUserName);
                            userStatus.setText(retrievesStatus);

                        }

                        else
                        //if user has created a new acccunt do this

                        {
                            userName.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingsActivity.this, "Please Update Yur Informationn", Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }








    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();


    }
}
