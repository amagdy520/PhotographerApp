package com.stylist.stylist.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.stylist.stylist.fragment.ChatFragment;
import com.stylist.stylist.fragment.EmptyFragment;
import com.stylist.stylist.fragment.StoryFragment;

/**
 * Created by Ahmed Magdy on 8/24/2017.
 */

public class MainPagerAdapter extends FragmentPagerAdapter {
    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return ChatFragment.create();
            case 1:
                return EmptyFragment.create();
            case 2:
                return StoryFragment.create();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
