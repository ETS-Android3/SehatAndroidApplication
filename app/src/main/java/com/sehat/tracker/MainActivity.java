package com.sehat.tracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.polyak.iconswitch.IconSwitch;
import com.sehat.tracker.fragments.About;
import com.sehat.tracker.fragments.NearbyFrag;
import com.sehat.tracker.fragments.NewsHomeFragment;
import com.sehat.tracker.fragments.StatsFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, IconSwitch.CheckedChangeListener {

    BottomNavigationView navigationView;
    int key;
    SharedPreferences sh;
    SharedPreferences.Editor editor;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    String currentFrag;
    IconSwitch iconSwitch;
    Boolean ModeBoolean;
    public static String countryCode;


    @Override
    protected void onResume() {
        if (dl.isDrawerOpen(GravityCompat.START)) {

            dl.closeDrawer(GravityCompat.START);

        }
        checkFragState();
        super.onResume();
    }

    public void changeFragment(Fragment fragment, String tagFragmentName) {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment currentFragment = mFragmentManager.getPrimaryNavigationFragment();
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }
        Fragment fragmentTemp = mFragmentManager.findFragmentByTag(tagFragmentName);
        if (fragmentTemp == null) {
            fragmentTemp = fragment;
            fragmentTransaction.add(R.id.frame, fragmentTemp, tagFragmentName);

        } else {
            fragmentTransaction.show(fragmentTemp);
        }
        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commitNowAllowingStateLoss();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragment = new NewsHomeFragment();
                changeFragment(fragment, "home");
                break;
            case R.id.moreInfo:
                fragment = new StatsFragment();
                changeFragment(fragment, "stats");
                break;
            case R.id.stats:
                fragment = new NearbyFrag();
                changeFragment(fragment,"nearby");
                break;
        }
        return true;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        countryCode = getIntent().getStringExtra("Code");

        sh = getSharedPreferences("DarkMode",
                MODE_PRIVATE);
        editor = sh.edit();
        ModeBoolean = sh.getBoolean("DarkMode",false);

        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES|| ModeBoolean) {
            setTheme(R.style.Sehat_style_dark);

        }
        else {
            setTheme(R.style.Sehat_style);

        }
        setContentView(R.layout.activity_main);
        iconSwitch = findViewById(R.id.icon_switch);
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES||ModeBoolean){
            iconSwitch.setChecked(IconSwitch.Checked.LEFT);
        }else
            iconSwitch.setChecked(IconSwitch.Checked.RIGHT);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String todayDate = format.format(c);

  //      Toast.makeText(this,todayDate,Toast.LENGTH_SHORT).show();


        Toolbar toolbar = findViewById(R.id.toolbar);
        dl = findViewById(R.id.activity_main);
        navigationView = findViewById(R.id.bottom_nav);
        nv = findViewById(R.id.nv);


        setSupportActionBar(toolbar);
        t = new ActionBarDrawerToggle(this, dl, toolbar, R.string.open, R.string.close);
        dl.addDrawerListener(t);
        t.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        getSupportActionBar().setTitle("");

        iconSwitch.setCheckedChangeListener(this);

        checkFragState();
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void checkFragState() {

        Fragment fragment = null;
        int check = sh.getInt("APP_PROCESS_ID",0);
        int currentId = android.os.Process.myPid();

        if(check==0|| check!=currentId){
            fragment = new NewsHomeFragment();
            changeFragment(fragment, "home");
            navigationView.setSelectedItemId(R.id.navigation_home);
        }
        else if(sh.getString("FragName","home").equals("home")) {
            fragment = new NewsHomeFragment();
            changeFragment(fragment, "home");
            navigationView.setSelectedItemId(R.id.navigation_home);
        }
        else if(sh.getString("FragName","home").equals("stats")){
            fragment = new StatsFragment();
            changeFragment(fragment, "stats");
            navigationView.setSelectedItemId(R.id.moreInfo);
        }
        else if(sh.getString("FragName","home").equals("nearby")){
            fragment = new NearbyFrag();
            changeFragment(fragment, "nearby");
            navigationView.setSelectedItemId(R.id.stats);
        }
    }

    public void restartApp() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        finish();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.item1:
              //  Toast.makeText(MainActivity.this, "Prevention", Toast.LENGTH_SHORT).show();
                openPreventions();
                break;

            case R.id.item2:
            //    Toast.makeText(MainActivity.this, "Helplines", Toast.LENGTH_SHORT).show();
                getHelp();
                break;

            case R.id.item3:
            //    Toast.makeText(MainActivity.this, "About", Toast.LENGTH_SHORT).show();
                openAbout();
                break;

            case R.id.menuClose:
                dl.closeDrawer(GravityCompat.START);
                break;


        }

    }

    private void openAbout() {
        startActivity(new Intent(MainActivity.this, About.class));
        overridePendingTransition(R.anim.enter_right_anim,R.anim.exit_left_anim);


    }

    private void openPreventions() {
        startActivity(new Intent(MainActivity.this, Preventions.class));
        overridePendingTransition(R.anim.enter_right_anim,R.anim.exit_left_anim);


    }
    private void getHelp() {

        String name = getIntent().getStringExtra("Name");
        if(name!=null) {
            if (name.contains("India")) {
                Uri uri = Uri.parse("https://www.mohfw.gov.in/pdf/coronvavirushelplinenumber.pdf"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
            else if (name.contains("United States")|| name.contains("US")) {
                Uri uri = Uri.parse("https://www.coronavirus.gov/"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
            else{
                {
                    Uri uri = Uri.parse("https://www.who.int/health-topics/coronavirus"); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        }else{
            Uri uri = Uri.parse("https://www.who.int/health-topics/coronavirus"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        overridePendingTransition(R.anim.enter_right_anim,R.anim.exit_left_anim);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onCheckChanged(IconSwitch.Checked current) {
        editor.putInt("APP_PROCESS_ID", android.os.Process.myPid());

        Fragment fragment = getSupportFragmentManager().getPrimaryNavigationFragment();
        if(fragment!=null)
            currentFrag = fragment.getTag();
        switch (current) {
            case LEFT:
         //       Toast.makeText(this,currentFrag,Toast.LENGTH_SHORT).show();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("DarkMode",true);
                editor.putString("FragName",currentFrag);
                editor.commit();
                recreate();
                break;
            case RIGHT:
         //       Toast.makeText(this,currentFrag,Toast.LENGTH_SHORT).show();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("DarkMode",false);
                editor.putString("FragName",currentFrag);
                editor.commit();
                recreate();
                break;

        }
    }

    @Override
    protected void onPause() {
        Fragment fragment = getSupportFragmentManager().getPrimaryNavigationFragment();
        if(fragment!=null)
            currentFrag = fragment.getTag();
        editor.putString("FragName",currentFrag);
        editor.putInt("APP_PROCESS_ID", android.os.Process.myPid());
        editor.commit();
        super.onPause();
    }
}
