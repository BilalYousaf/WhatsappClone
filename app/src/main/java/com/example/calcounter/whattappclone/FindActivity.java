package com.example.calcounter.whattappclone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


// find friends activity
public class FindActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView FindFriendRecyclerList;
    //RecyclerView is a more advanced and more flexible version of the ListView.
    private DatabaseReference UserRef;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");


        FindFriendRecyclerList = (RecyclerView) findViewById(R.id.find_friends_recyclers_list);
        FindFriendRecyclerList.setLayoutManager(new LinearLayoutManager(this));


        toolbar = (Toolbar) findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> opetions =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(UserRef, Contacts.class) // here we pass the reference to our firebase database
                        .build();


        FirebaseRecyclerAdapter<Contacts, FindFriendsViewHoler> adapter =  // passing our class and paramtere  here
                new FirebaseRecyclerAdapter<Contacts, FindFriendsViewHoler>(opetions) {
                    @Override //to update the RecyclerView.ViewHolder contents with the item at the given position and also sets up some private fields to be used by RecyclerView.
                    protected void onBindViewHolder(@NonNull FindFriendsViewHoler holder, final int position, @NonNull Contacts model) {
                    // we will set to the files that we inilisedf in the findfrindholder

                        holder.userName.setText(model.getName());
                        holder.userStatus.setText(model.getStatus());
                   //    Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileImage);

                       holder.itemView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               String visit_user_id  = getRef(position).getKey();//
                               Intent profileIntent = new Intent(FindActivity.this, ProfileActivity.class);
                               profileIntent.putExtra( "visit_user_id",visit_user_id);
                               startActivity(profileIntent);

                           }
                       }); //
                    }

                    @NonNull
                    @Override // this method is for user display layout where we connect
                    public FindFriendsViewHoler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        //we are connecting our display layout to our findfrinedhholder classa
                       View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        FindFriendsViewHoler viewHoler = new FindFriendsViewHoler(view);
                        return viewHoler;
                    }
                };

        FindFriendRecyclerList.setAdapter(adapter);

        adapter.startListening();


    }


    public static class FindFriendsViewHoler extends RecyclerView.ViewHolder {

        TextView userName, userStatus;
        CircleImageView profileImage;

        public FindFriendsViewHoler(@NonNull View itemView) { // initilising our class here in the constructor


            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.set_profile_image);

        }
    }


}
