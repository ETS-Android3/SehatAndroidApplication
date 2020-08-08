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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sehat.tracker.R;
import com.sehat.tracker.adapters.StateAdapter;
import com.sehat.tracker.models.StateData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.paperdb.Paper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class StatesFrag extends Fragment {
    RecyclerView recyclerView;
    ArrayList<StateData> stateData = new ArrayList<>();
    StateAdapter adapter;
    SharedPreferences stateDataSh;
    SharedPreferences.Editor editor;
    String todayDate;
    double diff,diffHours;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = getLayoutInflater().inflate(R.layout.states_frag,container,false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);


        Paper.init(getContext());
        stateDataSh = getActivity().getSharedPreferences("State", MODE_PRIVATE);
        editor = stateDataSh.edit();


        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        todayDate = format.format(c);


        String prevDate = stateDataSh.getString("Date",null);
        if(prevDate!=null&& stateDataSh.getString("StateArray2",null)!=null) {
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
                //  Toast.makeText(getContext(),"Old Data loaded",Toast.LENGTH_SHORT).show();
                adapter = new StateAdapter(Paper.book().read("StateArray", new ArrayList<>()),getActivity());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }else
            new LoadStats().execute();





        return rootView;
    }

    private class LoadStats extends AsyncTask<Void, Void, String> {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.covid19india.org/data.json")
                .method("GET", null)
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
                if(stateDataSh.getString("StateArray2",null)!=null){
                    adapter = new StateAdapter(Paper.book().read("StateArray", new ArrayList<>()),getActivity());
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

            }
        }


    }

    private void intialisedata(JSONObject jsonObject) {
        JSONArray jsonArray = null;
        String name,total,active,recov,dead,newdead,newrecover,newcase;
        JSONObject object;
        try {
            jsonArray = jsonObject.getJSONArray("statewise");
            for(int i=1;i<jsonArray.length();i++){

                object = jsonArray.getJSONObject(i);
                name = object.getString("state");
                total = object.getString("confirmed");
                active = object.getString("active");
                recov = object.getString("recovered");
                dead = object.getString("deaths");
                newcase = object.getString("deltaconfirmed");
                newdead = object.getString("deltadeaths");
                newrecover = object.getString("deltarecovered");

                stateData.add(new StateData(name,total,dead,recov,active,newcase,newrecover,newdead));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(stateData!=null){
            adapter = new StateAdapter(stateData,getContext());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            Paper.book().write("StateArray", stateData);
            editor.putString("Date",todayDate);
            editor.putString("StateArray2","Data Entered");
            editor.apply();

        }
    }
}
