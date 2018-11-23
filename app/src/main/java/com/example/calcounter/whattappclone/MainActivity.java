package com.example.calcounter.whattappclone;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar; // our tool bar#
    private ViewPager myViewPager; // Layout manager that allows
    // the user to flip left and right through pages of data
    private TabLayout myTabLayout; // TabLayout provides a horizontal layout to display tabs
    private TabsAccessorAdapter mytabsAccessorAdapter;
    private FirebaseUser currentUser; // autehntication
   private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("WhatsApp");


        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        mytabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(mytabsAccessorAdapter);

        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null) // if current user is null
        {
            // will send user to login screen.
            SendUserToLoginActivity();
        }
        else{
            VerifyUserExistance();
        }

    }

    private void VerifyUserExistance() { // method for verifying if the user exists or not


         String currentUserID = firebaseAuth.getCurrentUser().getUid(); // getting the id of the current logged in user
         databaseReference.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 if ((dataSnapshot.child("name").exists())){// checking name of  existing the users that have registered

                     Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();

                 }
                 
                 else {

                     SendUserToSettingsActivity(); // sending user to setting activity to update its details
                 }

                 
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         }); //checking for the the user ID if they user has registered or not.


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // going to acces sthe options xml file

        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // acessing each option that has been created


        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_find_Logout_optiona) {
            firebaseAuth.signOut();
            SendUserToLoginActivity();

        }

        if (item.getItemId() == R.id.main_create_groups_optiona) {

          RequestNewGroup();
        }

        if (item.getItemId() == R.id.main_settings_options) {

            SendUserToSettingsActivity();



        }

        if (item.getItemId() == R.id.main_find_friends_optionjs) {


        SendUserToFindActivity();



        }

        return true;

    }

    private void RequestNewGroup() { // Ask the user to insert username and then add it to the database
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name :");

        final EditText groupnameField = new EditText(MainActivity.this);
        groupnameField.setHint("e.g ONEPLUS");
        builder.setView(groupnameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {

                String groupName = groupnameField.getText().toString();

                if (TextUtils.isEmpty(groupName)) // if the grup name is empty

                {
                    Toast.makeText(MainActivity.this, "Please Write Your Group Name ", Toast.LENGTH_SHORT).show();

                }
                else {

                    CreateNewGroup(groupName);

                }

            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // Cancel Buttn
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
                    //using dialog interface we can cancel the group
            {
                dialogInterface.cancel();

            }
        });


        builder.show();





    }

    private void CreateNewGroup(final String groupName) {// creating new group


        databaseReference.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()){
                           Toast.makeText(MainActivity.this, groupName + " group is created Sucessfully", Toast.LENGTH_SHORT).show();
                       }
                    }
                });
    }


    private void SendUserToLoginActivity() {

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);


    }


    private void SendUserToSettingsActivity() {

        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        finish();

    }

    private void SendUserToFindActivity() {

        Intent findfriendsIntent = new Intent(MainActivity.this, FindActivity.class);
        startActivity(findfriendsIntent);


    }



}


