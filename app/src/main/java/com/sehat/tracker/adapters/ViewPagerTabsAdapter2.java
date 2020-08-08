package com.sehat.tracker.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerTabsAdapter2 extends FragmentPagerAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();


    public ViewPagerTabsAdapter2(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }


    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void AddFragment(Fragment fragment){
        fragmentList.add(fragment);
    }
}
