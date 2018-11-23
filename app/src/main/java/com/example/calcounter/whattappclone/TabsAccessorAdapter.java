package com.example.calcounter.whattappclone;

import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by byo8 on 09/10/2018.
 */
// created for to access the fragments that are created
public class TabsAccessorAdapter extends FragmentPagerAdapter {


    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public android.support.v4.app.Fragment getItem(int i) {

        // creating a switch to get the position of our fragments

        switch (i) {
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;

            case 1:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;

            case 2:
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;


            default:
                return null;

        }

    }

    @Override
    public int getCount() {
        return 3;
    }
    // setting title for the 3 fragments;


    @Override
    public CharSequence getPageTitle(int position) {


        switch (position) {
            case 0:
                return "Chats";

            case 1:
                return "Contacts";

            case 2:
                return "Groups";
            default:
                return null;


        }
    }

}
