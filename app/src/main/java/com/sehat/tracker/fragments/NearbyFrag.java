package com.sehat.tracker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.sehat.tracker.R;
import com.sehat.tracker.adapters.ViewPagerTabsAdapter2;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class NearbyFrag extends Fragment {

    private HeightWrappingViewPager2 pager;
    ViewPagerTabsAdapter2 pagerAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        pagerAdapter = new ViewPagerTabsAdapter2(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.third_frag, container, false);
        List<Fragment> list = new ArrayList<>();
        list.add(new CountryFrag());
        list.add(new StatesFrag());

        TextView country = rootView.findViewById(R.id.countryName);
        WormDotsIndicator wormDotsIndicator = rootView.findViewById(R.id.worm_dots_indicator);


        if(Objects.requireNonNull(getActivity()).getIntent().getStringExtra("Name")!=null)
            country.setText(getActivity().getIntent().getStringExtra("Name"));


        pager = rootView.findViewById(R.id.viewpager);

        if(country.getText().toString().equals("India")) {
            pagerAdapter.AddFragment(new CountryFrag());
            pagerAdapter.AddFragment(new StatesFrag());
        }
        else {
            pagerAdapter.AddFragment(new CountryFrag());
            wormDotsIndicator.setVisibility(View.GONE);

        }


        pager.setAdapter(pagerAdapter);
        wormDotsIndicator.setViewPager(pager);



        return rootView;
    }
}
