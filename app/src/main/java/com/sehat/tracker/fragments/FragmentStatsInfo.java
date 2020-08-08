package com.sehat.tracker.fragments;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sehat.tracker.R;
import com.sehat.tracker.adapters.RecyclerAdapter;
import com.sehat.tracker.models.TabData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.paperdb.Paper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;


public class FragmentStatsInfo extends Fragment {
    View view;
    private EditText search;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter adapter;
    private static int positionView;
    private int height;
    List<TabData> dataArray = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();
    private LinearLayout layout;
    SharedPreferences apiData;
    SharedPreferences.Editor apiDataEditor;
    String todayDate;
    long diff,diffHours;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.totalcases_fragment,container,false);
        Paper.init(getContext());

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        todayDate = format.format(c);

        apiData = getActivity().getSharedPreferences("DetailedStats",MODE_PRIVATE);
        apiDataEditor = apiData.edit();

        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        search = view.findViewById(R.id.searchBar);
        recyclerView.setLayoutManager(layoutManager);
        layout = view.findViewById(R.id.searchBarLayout);

        String prevDate = apiData.getString("Date",null);
        if(prevDate!=null&& apiData.getString("DetStats2",null)!=null) {
            try {
                Date d1 = format.parse(prevDate);
                Date d2 = format.parse(todayDate);
                diff = d2.getTime()-d1.getTime();
                diffHours = diff / (60 * 60 * 1000);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(diffHours>4) {
                new LoadCases().execute();
            //    Toast.makeText(getContext(),"New Data loaded",Toast.LENGTH_SHORT).show();

            }
            else {
              //  Toast.makeText(getContext(),"Old Data loaded",Toast.LENGTH_SHORT).show();
                ProgressBar progressBar = view.findViewById(R.id.progressBar);
                dataArray = Paper.book().read("DetStats",new ArrayList<>());
                adapter = new RecyclerAdapter(Paper.book().read("DetStats", new ArrayList<>()),getActivity(),progressBar);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }else
            new LoadCases().execute();


        search.clearFocus();


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());

            }
        });


        return view;
    }

    private void filter(String search) {
        ArrayList<TabData> filteredList = new ArrayList<>();

        for(TabData item: dataArray){
            if(item.getCountryName().toLowerCase().contains(search.toLowerCase().trim())){
                filteredList.add(item);
            }
        }

        adapter.filterList(filteredList);
    }

    private class LoadCases extends AsyncTask<Void, Void, String> {

        Request request = new Request.Builder()
                .url("https://coronavirus-monitor.p.rapidapi.com/coronavirus/cases_by_country.php")
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
                    JSONArray jArray = jsonObject.getJSONArray("countries_stat");
                    initialiseArray(jArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Dialog d = new Dialog(getActivity());
                d.setContentView(R.layout.custom_dialog_error);
                d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                d.show();
                if( apiData.getString("DetStats2",null)!=null){
                    ProgressBar progressBar = view.findViewById(R.id.progressBar);
                    adapter = new RecyclerAdapter(Paper.book().read("DetStats", new ArrayList<>()),getActivity(),progressBar);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        }


    }

    private void initialiseArray(JSONArray jArray) {

        JSONObject jsonObject;

        for(int i= 0;i<jArray.length();i++){
            try {
                jsonObject = jArray.getJSONObject(i);
                TabData data = new TabData(jsonObject.getString("country_name"),
                        jsonObject.getString("cases"),
                        jsonObject.getString("deaths"),
                        jsonObject.getString("total_recovered"),
                        jsonObject.getString("active_cases"),
                        jsonObject.getString("new_cases"),
                        jsonObject.getString("new_deaths"));

                if(!jsonObject.getString("country_name").isEmpty())
                dataArray.add(data);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        adapter = new RecyclerAdapter(dataArray,getActivity(),progressBar);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Paper.book().write("DetStats", dataArray);
        apiDataEditor.putString("Date",todayDate);
        apiDataEditor.putString("DetStats2","Data Entered");
        apiDataEditor.apply();
    }

}
