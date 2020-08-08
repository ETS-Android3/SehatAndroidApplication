package com.sehat.tracker;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.sehat.tracker.adapters.ViewPagerTabsAdapter;
import com.sehat.tracker.fragments.FragmentMap;
import com.sehat.tracker.fragments.FragmentStatsInfo;

public class TabsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private Toolbar appBarlayout;
    private ImageButton back;
    public static ImageButton error;
    private ViewPager viewPager;
    public static int indicator;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sh = getSharedPreferences("DarkMode", MODE_PRIVATE);

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES || sh.getBoolean("DarkMode",false)) {
            setTheme(R.style.Sehat_style_dark);
            indicator=1;
        }
        else {
            setTheme(R.style.Sehat_style_not);
            indicator=0;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_more_activity);
        tabLayout = findViewById(R.id.tabLayout);
        appBarlayout = findViewById(R.id.appBarId);
        viewPager = findViewById(R.id.viewpager);


        ViewPagerTabsAdapter adapter = new ViewPagerTabsAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.AddFragment(new FragmentStatsInfo()," Stats ");
        adapter.AddFragment(new FragmentMap()," Map ");

        back = findViewById(R.id.backBt);
        back.setOnClickListener(view -> finish());
        error = findViewById(R.id.errorBt);
        error.setOnClickListener(view -> {
            Dialog d = new Dialog(TabsActivity.this);
            d.setContentView(R.layout.custom_dialog_error);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            d.show();
        });
 //       Toast.makeText(this, df.format(dateobj), Toast.LENGTH_SHORT).show();
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {
                    //InputMethodManager is used to hide the virtual keyboard from the user after finishing the user input
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isAcceptingText()) {
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                } catch (NullPointerException e) {
                    Log.e("Exception", e.getMessage() + ">>");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
    }
}
