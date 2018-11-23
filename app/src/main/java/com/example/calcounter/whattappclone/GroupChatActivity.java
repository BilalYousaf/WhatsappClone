package com.example.calcounter.whattappclone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

// to view and use the group chat
public class GroupChatActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private ImageButton imageButton;
    private EditText userMessageInput;
    private ScrollView scrollView;
    private TextView displayTextMessages;
    private String currentGroupName, currentUserID, currentUserName, currentDate, currentTime;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference, GroupNameRef, GroupMEssageKeyRef; // it will create a ref to the key;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName = getIntent().getExtras().get("groupName").toString(); // with this line we are getting our group name from currentgroupname
        Toast.makeText(GroupChatActivity.this, currentGroupName, Toast.LENGTH_SHORT).show();


        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid(); // with the help of current userID we can reterive the user name of
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName); //using currentGroupName as referance
        // refeerence to any group the user clicks , once userr click ona  group it will create referance in the DB


        InitializeFields();

        GetUserInfo();

        imageButton.setOnClickListener(new View.OnClickListener() { // if user clicks on the button - the button will
            @Override
            public void onClick(View v) {

                SaveMessageInfoDatabase(); // method

                userMessageInput.setText("");


                scrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });


    }

    @Override
    protected void onStart() { //on start method executes when ever a activity starts
        super.onStart();

        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.exists()) {// if the group exists

                DisplayMessages(dataSnapshot);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.exists()) {// if the group exists

                    DisplayMessages(dataSnapshot);
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



    private void InitializeFields() {

        toolbar = (Toolbar) findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle( currentGroupName);

        imageButton = (ImageButton)findViewById(R.id.send_message_button);
        userMessageInput = (EditText) findViewById(R.id.input_group_message);
        displayTextMessages = (TextView) findViewById(R.id.group_chat_text_display); //display message to user
        scrollView = (ScrollView) findViewById(R.id.my_scroll_view); // display all the text messgaes of group users




    }

    private void GetUserInfo() { /// creating a reference to our Database - link to the sers

        databaseReference.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

               if (dataSnapshot.exists()){
                   currentUserName = dataSnapshot.child("name").getValue().toString();// first we can check if users exists of not


               }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


    private void SaveMessageInfoDatabase() {

        String message = userMessageInput.getText().toString();
        String messageKey = GroupNameRef.push().getKey(); // will create a key
        //GroupNameRef will, will go to the group anmes and create a key using the getkey method,
        
        if (TextUtils.isEmpty(message)){
            Toast.makeText(this, "Please Write Message irst", Toast.LENGTH_SHORT).show();
        }
        else {

            Calendar calForDate = Calendar.getInstance(); // getting the date and Time
            SimpleDateFormat currentDateformat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateformat.format(calForDate.getTime());

            Calendar calForTime= Calendar.getInstance(); // getting the date and Time
            SimpleDateFormat currentTimeformat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeformat.format(calForTime.getTime());


            //using hashmap to save in the database
            HashMap<String,Object > groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);


            GroupMEssageKeyRef = GroupNameRef.child(messageKey); // we are getting the message key and storing it in the groupmessageRefKey
            HashMap<String , Object> messageInfoMap = new HashMap<>();
                messageInfoMap.put("name", currentUserName);
                messageInfoMap.put("message", message);
                messageInfoMap.put("date", currentDate);
                messageInfoMap.put("time", currentTime);
            GroupMEssageKeyRef.updateChildren(messageInfoMap);

            //using the groupmessagekeyref we can safe data on the Db





        }


    }

    private void DisplayMessages(DataSnapshot dataSnapshot) {
        // here we can reterive and display all the messages for each group

        Iterator iterator = dataSnapshot.getChildren().iterator(); // it will move line by line and get each message from each specific group

        while (iterator.hasNext()){
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();

            displayTextMessages.append(chatName +" :\n" + chatMessage + "\n" + chatTime +  "   " + chatDate +  "\n\n\n" );
            /// here we are reteriving all the messages and displaying them on the screen from the DB

            scrollView.fullScroll(ScrollView.FOCUS_DOWN); // will automatically show new message
        }


    }



}
