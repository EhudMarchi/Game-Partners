package com.example.gamepartners.controller.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.gamepartners.ui.Activities_Fragments.ExploreFragment;
import com.example.gamepartners.ui.Activities_Fragments.FriendsFragment;
import com.example.gamepartners.ui.Activities_Fragments.GroupsFragment;
import com.example.gamepartners.ui.Activities_Fragments.UserProfileFragment;

public class TabAccessorAdapter extends FragmentPagerAdapter {

    public TabAccessorAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case  0:
                ExploreFragment exploreFragment = new ExploreFragment();
                return  exploreFragment;
            case  1:
                GroupsFragment groupsFragment = new GroupsFragment();
                return  groupsFragment;
            case  2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            case  3:
                UserProfileFragment userProfileFragment = new UserProfileFragment();
                return  userProfileFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case  0:
                return  "Explore";
            case  1:
                return  "Groups";
            case  2:
                return  "Friends";
            case  3:
                return  "Profile";

            default:
                return null;
        }
    }
}
