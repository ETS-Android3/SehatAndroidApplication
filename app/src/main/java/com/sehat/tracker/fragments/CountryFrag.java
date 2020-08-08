package com.sehat.tracker.fragments;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sehat.tracker.R;
import com.sehat.tracker.adapters.CountryAdapter;
import com.sehat.tracker.models.AllStats;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import io.paperdb.Paper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class CountryFrag extends Fragment {

    RecyclerView recyclerView;
    ArrayList<AllStats> values = new ArrayList<>();
    String CountryCode, countryName;
    String todayDate;
    SharedPreferences countryData;
    ProgressBar progressBar;
    SharedPreferences.Editor editor;
    double diff, diffHours;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.case_by_country, container, false);

        Paper.init(getContext());


        countryData = getActivity().getSharedPreferences("Country", MODE_PRIVATE);
        editor = countryData.edit();


        recyclerView = rootView.findViewById(R.id.recyclerView);
        progressBar = rootView.findViewById(R.id.progressBar);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        // Toast.makeText(getContext(),"Hello",Toast.LENGTH_SHORT).show();


        getCountry();

        return rootView;
    }


    private class LoadStats extends AsyncTask<Void, Void, String> {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://coronavirus-monitor.p.rapidapi.com/coronavirus/latest_stat_by_iso_alpha_3.php?alpha3=" + CountryCode)
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
                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "No response", Toast.LENGTH_SHORT).show();
                Dialog d = new Dialog(getActivity());
                d.setContentView(R.layout.custom_dialog_error);
                d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                d.show();
                if (countryData.getString("CountryArray2", null) != null) {
                    CountryAdapter adapter = new CountryAdapter(Paper.book().read("CountryArray", new ArrayList<>()), getContext());
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);

                    adapter.notifyDataSetChanged();
                }
            }
        }


    }

    private void getCountry() {

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        todayDate = format.format(c);

        if (Objects.requireNonNull(getActivity()).getIntent().getStringExtra("Code") != null) {
            CountryCode = getActivity().getIntent().getStringExtra("Code");
        } else {
            CountryCode = "IND";
        }

        String prevDate = countryData.getString("Date", null);
        if (prevDate != null && countryData.getString("CountryArray2", null) != null) {
            try {
                Date d1 = format.parse(prevDate);
                Date d2 = format.parse(todayDate);
                diff = d2.getTime() - d1.getTime();
                diffHours = diff / (60 * 60 * 1000);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (diffHours > 4) {
                new LoadStats().execute();
            } else {
                CountryAdapter adapter = new CountryAdapter(Paper.book().read("CountryArray", new ArrayList<>()), getContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

            }
        } else
            new LoadStats().execute();



    }

    private void intialisedata(JSONObject jsonObject) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("latest_stat_by_country");

            JSONObject data = jsonArray.getJSONObject(0);

            values.add(new AllStats(data.getString("total_cases"), data.getString("new_cases")));
            values.add(new AllStats(data.getString("active_cases"), ""));
            values.add(new AllStats(data.getString("total_recovered"), ""));
            values.add(new AllStats(data.getString("total_deaths"), data.getString("new_deaths")));
            values.add(new AllStats(data.getString("serious_critical"), ""));
            values.add(new AllStats(data.getString("total_tests"), ""));


        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (values != null) {
            CountryAdapter adapter = new CountryAdapter(values, getContext());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            Paper.book().write("CountryArray", values);
            editor.putString("Date", todayDate);
            editor.putString("CountryArray2", "Data Entered");
            editor.apply();
        }
    }
}
