package com.example.calcounter.whattappclone;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {


    private View groupFragmentView;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_groups = new ArrayList<>();

    private DatabaseReference databaseReference;//creating a referance t view the gtoups


    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Groups");

        //this will be used to reterive our groups


        InitalizeFields();

        ReteriveandDisplayGroups();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) { // int i = postion of the groups and long i = ID of groups

                String cuurentGroupName =  adapterView.getItemAtPosition(position).toString(); // group name the user will click to open
                // it means if user clicks on group name to open, it will ghet the name and strope it currentGroupName variable

                //then send user to grup chat activity along with group name

                Intent groupChatIntent = new Intent(getContext(), GroupChatActivity.class);
               groupChatIntent.putExtra("groupName", cuurentGroupName);
                startActivity(groupChatIntent);
            }
        });

        return groupFragmentView;

    }



    private void InitalizeFields() {


        listView = (ListView) groupFragmentView.findViewById(R.id.list_view); //  displaying groups
        arrayAdapter = new ArrayAdapter<String >(getContext(), android.R.layout.simple_list_item_1, list_of_groups); // to display the groups
        listView.setAdapter(arrayAdapter);
    }
    private void ReteriveandDisplayGroups() {

        // we will be using a iterate method that will reterive each group line by line

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String > set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator(); // we can now read every group name of the parent group
                while (iterator.hasNext()){
                    /// we can use temp hashmap to  aviod duplicates  of values in the groups
                    set.add(((DataSnapshot)iterator.next()).getKey()); // getkey = all group names

                }

                list_of_groups.clear(); // clearing the current list of groups
                list_of_groups.addAll(set); // contains all the group names that can be displayed
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    }
