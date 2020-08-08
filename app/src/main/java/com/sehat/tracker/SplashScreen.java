package com.sehat.tracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SplashScreen extends AppCompatActivity implements LocationListener {

    public static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    LocationManager locationManager;
    double lat=0;
    double longi = 0;
    SharedPreferences sh;
    SharedPreferences.Editor editor;
    Boolean ModeBoolean;
    String countryCode,coutnryName;
    Boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sh = getSharedPreferences("DarkMode",
                MODE_PRIVATE);
        editor = sh.edit();
        ModeBoolean = sh.getBoolean("DarkMode",false);

        if(ModeBoolean==true)
            setTheme(R.style.Sehat_style_dark);
        else
            setTheme(R.style.Sehat_style);
        setContentView(R.layout.activity_splash_screen);

        if(sh.getBoolean("LocAccess",false)==false)
            checklocperm();
        else {
            if (sh.getString("Code", null) != null) {
                countryCode = sh.getString("Code", null);
                coutnryName = sh.getString("Name", null);
                startMain();
            } else
                checklocperm();
        }


        //setting duration for the screen display
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                editor.putBoolean("LocAccess",true);

                getLocation();



            } else {
                Toast.makeText(getApplicationContext(), "Please grant permission", Toast.LENGTH_SHORT).show();
                countryCode = "IND";
                editor.putBoolean("LocAccess",false);
                startMain();
            }
        }
    }

    private void checklocperm() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        getLocation();
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        longi = location.getLongitude();
        if(lat!=0&&longi!=0)
            update_city(lat,longi);

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

        Toast.makeText(SplashScreen.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
        countryCode = "IND";
        coutnryName = "India";
        startMain();       // recreate();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    public void update_city(double x,double y){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = null;
            addresses = geocoder.getFromLocation(x, y, 1);
            countryCode =addresses.get(0).getCountryCode();
            coutnryName = addresses.get(0).getCountryName();
            editor.putString("Code",countryCode);
            editor.putString("Name",coutnryName);
            editor.apply();
            startMain();
        } catch (IOException e) {
            countryCode = "IND";
            coutnryName = "India";
            startMain();
            e.printStackTrace();
        }
    }

    private void startMain() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                    final Intent launchIntent = new Intent(SplashScreen.this, MainActivity.class);
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    launchIntent.putExtra("Code", countryCode);
                    launchIntent.putExtra("Name",coutnryName);
                    startActivity(launchIntent);
                    finish();

            }
        }, 1000);
    }


}
