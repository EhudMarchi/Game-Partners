package com.example.gamepartners.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

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
                GroupsFragment groupsFragment = new GroupsFragment();
                return  groupsFragment;
            case  1:
                ExploreFragment exploreFragment = new ExploreFragment();
                return  exploreFragment;
            case  2:
                GamesFragment gamesFragment = new GamesFragment();
                return  gamesFragment;
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
                return  "Groups";
            case  1:
                return  "Explore";
            case  2:
                return  "Games";
            case  3:
                return  "Profile";

            default:
                return null;
        }
    }
}
