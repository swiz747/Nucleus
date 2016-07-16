package com.tritiumlabs.arthur.servertest;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import fragments.Chats;
import fragments.FriendsList;
import fragments.SomethingElse;

//TODO: DEPRECATED SHOULD BE REMOVED -AB

/**
 * Created by Arthur on 6/8/2016.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter
{

    public SectionsPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0)
        {
            return new SomethingElse();

        }
        else if(position == 1)
        {
            return new Chats();
        }
        //TODO Holy shit this fucker caused issues, check 3rd tab before ripping hair out.
        else
        {
            return new FriendsList();
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Something Else";
            case 1:
                return "Chat";
            case 2:
                return "Friends List";
        }
        return null;
    }
}
