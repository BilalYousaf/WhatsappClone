package com.example.calcounter.whattappclone;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserID,senderUserID, Current_State; //varibables
    private CircleImageView userProfileImage;
    private TextView userProfileName, userProfileStatus;
    private Button SendMessageRequestButton, DeclineChatRequestButton;
    private DatabaseReference userRef,ChatRequestRef,ContactRef;
    private FirebaseAuth firebaseAuth;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance(); // by using this we will be reterving current user id
        //creating a linkk to our user node
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Request");
         ContactRef= FirebaseDatabase.getInstance().getReference().child("Contacts");



        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();
        senderUserID = firebaseAuth.getCurrentUser().getUid();


    userProfileImage = (CircleImageView) findViewById(R.id.visit_profile_image);
    userProfileName = (TextView) findViewById(R.id.visit_user_name);
    userProfileStatus = (TextView) findViewById(R.id.visit_profile_status);
    SendMessageRequestButton = (Button) findViewById(R.id.send_message_request_button);
    Current_State = "new"; //this is beacsue the two users are new to eachother when send message
        //sending request
        DeclineChatRequestButton = (Button) findViewById(R.id.decline_message_request_button);
    ReteriveuserInfo();





    }

    private void ReteriveuserInfo() { // created so we can reterive innfo and siplay in our profile activity

//using userRef we are going to reterive data of the users from any profile
        userRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image")))
                {
                    // check if pfofile pic displayed
                    String userImage = dataSnapshot.child("image").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userstatus = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userstatus);


                    // calling a method
                    manageChatRequest();

                }
                else {
                    // if not displayed will reterive username and status

                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userstatus = dataSnapshot.child("status").getValue().toString();
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userstatus);

                    manageChatRequest();




                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void manageChatRequest() {

        ChatRequestRef.child(senderUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.hasChild(receiverUserID)) // checking receiver user ID
                        {
                            String request_type = dataSnapshot.child(receiverUserID).child("request_type").getValue().toString();

                            if (request_type.equals("sent"))
                            {
                                Current_State = "request_sent";
                                SendMessageRequestButton.setText("Cancel Chat Request");
                            }
                            else if (request_type.equals("received"))
                            {
                                Current_State = "request_recieved";
                                SendMessageRequestButton.setText("Accept Chat Request"); //reciever will seen thsi button

                                DeclineChatRequestButton.setVisibility(View.VISIBLE);
                                DeclineChatRequestButton.setEnabled(true);

                                DeclineChatRequestButton.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {

                                       //caslling the cancelchat methiod
                                       CancelChatRequest();

                                   }
                               });

                            }



                        }
                        else
                            {
                                ContactRef.child(senderUserID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot)
                                        {

                                            if (dataSnapshot.hasChild(receiverUserID))
                                            {
                                                Current_State = "friends";
                                                SendMessageRequestButton.setText("Romove This Contact");
                                            }
                                        }


                                       @Override
                                       public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                       }


//
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });





       // adding a validation so user can only send message to friends and not himself
        if (!senderUserID.equals(receiverUserID))
        {
            SendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { //once user clicks the send message button then
                    SendMessageRequestButton.setEnabled(false);

                    if (Current_State.equals("new")) // and current user is new and they can send chat request to other user
                    {
                        SendChatRequest();

                    }

                    if (Current_State.equals("request_sent")) // if request has already been sent{
                    {
                        CancelChatRequest ();//user can now cancel the request
                    }
                   if (Current_State.equals("request_received")) // if request has already been sent{
                    {
                      AcceptChatRequest ();// once the chat request is received what will happen is
                      }
                }
            });

        }
        else
        {
            SendMessageRequestButton.setVisibility(View.INVISIBLE);
        }


    }

    private void AcceptChatRequest() {
        ContactRef.child(senderUserID).child(receiverUserID)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                   public void onComplete(@NonNull Task<Void> task)
                   {

                       if (task.isSuccessful())
                       {
//
                           ContactRef.child(receiverUserID).child(senderUserID)
                                   .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                       public void onComplete(@NonNull Task<Void> task)
                                        {

                                           if (task.isSuccessful())
                                           {

                                                ChatRequestRef.child(senderUserID).child(receiverUserID)
                                                       .removeValue()
                                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                               if (task.isSuccessful())
                                                               {
                                                                    ChatRequestRef.child(receiverUserID).child(senderUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    SendMessageRequestButton.setEnabled(true);
                                                                                    Current_State = "friends";
                                                                                    SendMessageRequestButton.setText("Remove This Content");


                                                                                    DeclineChatRequestButton.setVisibility(View.INVISIBLE);
                                                                                    DeclineChatRequestButton.setEnabled(false);


                                                                               }
                                                                           });

                                                              }




                                                           }
                                                      });

                                        }

                                      }
                                 });


                     }

                  }
              });







    }

    private void CancelChatRequest() { // cancel chat req

        ChatRequestRef.child(senderUserID).child(receiverUserID)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful())
                {
                    ChatRequestRef.child(receiverUserID).child(senderUserID)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {

                            if (task.isSuccessful())
                            {
                                SendMessageRequestButton.setEnabled(true);
                                Current_State = "new";
                                SendMessageRequestButton.setText("Send Messsage ");
                                DeclineChatRequestButton.setVisibility(View.INVISIBLE);
                                DeclineChatRequestButton.setEnabled(false);
                            }



                        }
                    });
                }



            }
        });


    }

    private void SendChatRequest() {

        ChatRequestRef.child(senderUserID).child(receiverUserID) //first stroe the sender user ID and then the reciever
        .child("request_type").setValue("sent")
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {

                if (task.isSuccessful())
                {
                    ChatRequestRef.child(receiverUserID).child(senderUserID)
                            .child("request_type").setValue("received")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        SendMessageRequestButton.setEnabled(true);
                                        Current_State ="Request Sent ";
                                        SendMessageRequestButton.setText("Cancel Chat Request");


                                    }

                                }
                            });

                }

            }
        });



    }
}
