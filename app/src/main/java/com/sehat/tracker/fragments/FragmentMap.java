package com.sehat.tracker.fragments;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageForwardRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sehat.tracker.R;
import com.sehat.tracker.TabsActivity;
import com.sehat.tracker.models.MapModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;


public class FragmentMap extends Fragment implements OnMapReadyCallback {
    private final OkHttpClient client = new OkHttpClient();
    View view;
    GoogleMap map;
    List<MapModel> mapData = new ArrayList<>();
    SupportMapFragment mapFragment;
    DateFormat df = new SimpleDateFormat("M/d/yy");
    Date dateobj = new Date();
    ProgressBar progressBar;
    SharedPreferences apiData;
    SharedPreferences.Editor apiDataEditor;
    String todayDate;
    long diff,diffHours;
    String x;

    public FragmentMap() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.map_fragment, container, false);

        Paper.init(getContext());

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        todayDate = format.format(c);
        apiData = getActivity().getSharedPreferences("MapInfo",MODE_PRIVATE);
        apiDataEditor = apiData.edit();

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        progressBar = view.findViewById(R.id.progressBar);


        if (mapFragment == null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map, mapFragment).commit();
        }

        String prevDate = apiData.getString("Date",null);
        if(prevDate!=null&& apiData.getString("MapData",null)!=null) {
            try {
                Date d1 = format.parse(prevDate);
                Date d2 = format.parse(todayDate);
                diff = d2.getTime()-d1.getTime();
                diffHours = diff / (60 * 60 * 1000);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(diffHours>8) {
                new LoadStats().execute();
            }
            else {
                try {
                    initialiseMap(new JSONArray(apiData.getString("MapData",null)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else
            new LoadStats().execute();

        JOpenCageGeocoder jOpenCageGeocoder = new JOpenCageGeocoder("5597096c3df6410ea9adb7b3ecb67370");
        JOpenCageForwardRequest request = new JOpenCageForwardRequest("");
        request.setRestrictToCountryCode("za"); // restrict results to a specific country
        request.setBounds(18.367, -34.109, 18.770, -33.704); // restrict results to a geographic bounding box (southWestLng, southWestLat, northEastLng, northEastLat)

        JOpenCageResponse response = jOpenCageGeocoder.forward(request);
        JOpenCageLatLng firstResultLatLng = response.getFirstPosition(); // get the coordinate pair of the first result
        System.out.println(firstResultLatLng.getLat().toString() + "," + firstResultLatLng.getLng().toString());

        return view;
    }

    private void initialiseMap(JSONArray array) {
        String status, locName;
        status="A";


        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject country = array.getJSONObject(i);
                JSONObject coordinates = country.getJSONObject("coordinates");

                if (!country.getString("province").isEmpty())
                    locName = country.getString("province");
                else
                    locName = country.getString("country");


                mapData.add(new MapModel(coordinates.getString("lat"), coordinates.getString("long"), status, country.getString("latest"), locName));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            mapFragment.getMapAsync(this);
        }catch (Exception e){
            e.printStackTrace();
        }


    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng loc;
        BitmapDrawable bitmapdraw;
        Bitmap b;
        Bitmap smallMarker;
        Marker marker;
        boolean success;
        try {
            if(TabsActivity.indicator!=1)
            success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.map_style_normal));
            else
                success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                getContext(), R.raw.map_style_dark));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        int markerx,markery;

        for (int i = 0; i < mapData.size(); i++) {
            MapModel model = mapData.get(i);
            int value = Integer.parseInt(model.getValue());
            if(value>100000)
            {
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.circle2);
                markerx = (int) Math.round(height*0.035)+20;
                markery = (int) Math.round(height*0.035)+20;
            }
            else if(value<100000&&value>25000)
            {
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.circle3);
                markerx = (int) Math.round(height*0.030)+20;
                markery = (int) Math.round(height*0.030)+20;
            }
            else if(value>8000&&value<25000)
            {
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.circle1);
                markerx = (int) Math.round(height*0.030);
                markery = (int) Math.round(height*0.030);
            }
            else
            {
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.circle4);
                markerx = (int) Math.round(height*0.025);
                markery = (int) Math.round(height*0.025);
            }


            b = bitmapdraw.getBitmap();
            smallMarker = Bitmap.createScaledBitmap(b, markerx, markery, false);
            loc = new LatLng(Double.valueOf(model.getLatitude()), Double.valueOf(model.getLongitude()));
            marker = map.addMarker(new MarkerOptions().position(loc).flat(true).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).alpha(0.6f));
            marker.setTag(i);
            if(i==mapData.size()-1)
                progressBar.setVisibility(View.GONE);
        }

        map.setOnMarkerClickListener(marker1 -> {
            Dialog d = new Dialog(getActivity());
            d.setContentView(R.layout.custom_dialog);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView title = d.findViewById(R.id.header);
            TextView value = d.findViewById(R.id.value);
            String a = mapData.get((Integer) marker1.getTag()).getValue();
            x = NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(a));
            value.setText(x + " cases");
            title.setText(mapData.get((Integer) marker1.getTag()).getName());
            d.show();
            return false;
        });


    }

    private class LoadStats extends AsyncTask<Void, Void, JSONArray> {

        Request request = new Request.Builder()
                .url("https://coronavirus-tracker-api.herokuapp.com/confirmed")
                .get()
                .build();

        @Override
        protected JSONArray doInBackground(Void... params) {
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    return null;
                }
                JSONObject jsonObject = new JSONObject(response.body().string());
                JSONArray jsonArray = jsonObject.getJSONArray("locations");
                return jsonArray;
            } catch (Exception e) {
                e.printStackTrace();

                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray s) {
            super.onPostExecute(s);
            if (s!= null && s.length()>0) {
//                Dialog d = new Dialog(getActivity());
//                d.setContentView(R.layout.custom_dialog_error);
//                d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                  d.show();
                //   TabsActivity.error.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
              //      initialiseMap(s);
//                apiDataEditor.putString("Date",todayDate);
//                apiDataEditor.putString("MapData",s.toString());
//                apiDataEditor.apply();
            }else {
//                Dialog d = new Dialog(getActivity());
//                d.setContentView(R.layout.custom_dialog_error);
//                d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
             //   d.show();
             //   TabsActivity.error.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                if(apiData.getString("MapData",null)!=null)
                try {
                    initialiseMap(new JSONArray(apiData.getString("MapData",null)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }




    }




}
