package com.sehat.tracker.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sehat.tracker.R;
import com.sehat.tracker.TabsActivity;
import com.sehat.tracker.adapters.StatsAdapter;
import com.sehat.tracker.models.AllStats;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class StatsFragment extends Fragment {


    private HeightWrappingViewPager2 viewPager;
    private StatsAdapter adapter;
    ImageView imageView;
    private ArrayList<AllStats> values = new ArrayList<>();
    private WormDotsIndicator wormDotsIndicator;
    private final OkHttpClient client = new OkHttpClient();
    private Button button;
    SharedPreferences ApiData;
    SharedPreferences.Editor ApiDataEditor;
    String todayDate;
    ProgressBar progressBar;

    long diff,diffHours;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.stats_fragment, container, false);
        Paper.init(getContext());

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        todayDate = format.format(c);

        ApiData = getActivity().getSharedPreferences("StatsData", MODE_PRIVATE);
        ApiDataEditor = ApiData.edit();

        viewPager = rootView.findViewById(R.id.viewpager);
        viewPager.setClipToPadding(false);
        wormDotsIndicator = rootView.findViewById(R.id.worm_dots_indicator);
        button = rootView.findViewById(R.id.view_button);
        imageView = rootView.findViewById(R.id.imageView);
        progressBar = rootView.findViewById(R.id.progressBar);


        String prevDate = ApiData.getString("Date",null);
        if(prevDate!=null&& ApiData.getString("StatsArray2",null)!=null) {
            try {
                Date d1 = format.parse(prevDate);
                Date d2 = format.parse(todayDate);
                diff = d2.getTime()-d1.getTime();
                diffHours = diff / (60 * 60 * 1000);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(diffHours>4) {
                new LoadStats().execute();
            }
            else {

                adapter = new StatsAdapter(Paper.book().read("StatsArray", new ArrayList<>()),getActivity(),progressBar);
                viewPager.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                wormDotsIndicator.setViewPager(viewPager);
                wormDotsIndicator.refreshDrawableState();


            }
        }else
            new LoadStats().execute();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), TabsActivity.class));
                try {
                    getActivity().overridePendingTransition(R.anim.enter_right_anim, R.anim.exit_left_anim);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

            }
        });


        return rootView;
    }

    private class LoadStats extends AsyncTask<Void, Void, String>{

        Request request = new Request.Builder()
                .url("https://coronavirus-monitor.p.rapidapi.com/coronavirus/worldstat.php")
                .get()
                .addHeader("x-rapidapi-host", "coronavirus-monitor.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "68151b38eamsh20fe03826d37f38p1638d8jsn94971d84bc83")
                .build();

        @Override
        protected String doInBackground(Void... params) {
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    return null;
                }
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    intialisedata(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(getContext(), "No response", Toast.LENGTH_SHORT).show();
                Dialog d = new Dialog(getActivity());
                d.setContentView(R.layout.custom_dialog_error);
                d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                d.show();
                if(ApiData.getString("StatsArray2",null)!=null){
                    adapter = new StatsAdapter(Paper.book().read("StatsArray", new ArrayList<>()),getActivity(),progressBar);
                    viewPager.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    wormDotsIndicator.setViewPager(viewPager);
                    wormDotsIndicator.refreshDrawableState();
                }

            }
        }


    }

    private void intialisedata(JSONObject jsonObject) {
        try {
            String s1,s2,s3;
            s1 = jsonObject.getString("total_cases");
            s2= jsonObject.getString("total_recovered");
            s3= jsonObject.getString("total_deaths");
            values.add(new AllStats(s1,jsonObject.getString("new_cases")));
            values.add(new AllStats(s2,""));
            values.add(new AllStats(getActiveCases(s1,s2,s3),""));
            values.add(new AllStats(s3,jsonObject.getString("new_deaths")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
         if(values!=null){
             String Z = values.toString();
//             Toast.makeText(getActivity(),s1,Toast.LENGTH_SHORT).show();
             
             adapter = new StatsAdapter(values,getActivity(),progressBar);
             viewPager.setAdapter(adapter);
             adapter.notifyDataSetChanged();
             wormDotsIndicator.setViewPager(viewPager);
             wormDotsIndicator.refreshDrawableState();
             Paper.book().write("StatsArray", values);
             ApiDataEditor.putString("Date",todayDate);
             ApiDataEditor.putString("StatsArray2","Data Entered");
             ApiDataEditor.apply();
         }

        /**while (x.hasNext()) {
            String key = (String) x.next();
            try {
                jsonArray.put(jsonObject.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                values.add(jsonArray.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }**/
    }

    private String getActiveCases(String s1, String s2, String s3) {

       int result = Integer.parseInt(s1.replace(",","")) - Integer.parseInt(s2.replace(",",""))- Integer.parseInt(s3.replace(",",""));
       return NumberFormat.getNumberInstance(Locale.US).format(result);
    }



    @Override
    public void onResume() {
        super.onResume();
    }
}
