package com.sehat.tracker.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.sehat.tracker.HeightWrappingViewPager;
import com.sehat.tracker.MainActivity;
import com.sehat.tracker.R;
import com.sehat.tracker.adapters.NewsAdapter;
import com.sehat.tracker.api.ApiClient;
import com.sehat.tracker.api.ApiInterface;
import com.sehat.tracker.models.Article;
import com.sehat.tracker.models.ResponseObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.paperdb.Paper;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class NewsHomeFragment extends Fragment {

    public static String API_KEY = "834c67560f954b649141433f66a0b77e";
    private List<Article> articles = new ArrayList<>();
    private HeightWrappingViewPager viewPager;
    private NewsAdapter adapter;
    long diff,diffHours;
    SharedPreferences sh,apiData;
    SharedPreferences.Editor editor, apiDataEditor;
    private final OkHttpClient client = new OkHttpClient();
    int i=0;
    String todayDate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        Paper.init(getContext());

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        todayDate = format.format(c);


        viewPager = rootView.findViewById(R.id.viewpager);
        viewPager.setClipToPadding(false);
        apiData = getActivity().getSharedPreferences("NewsData",MODE_PRIVATE);
        apiDataEditor = apiData.edit();

        String prevDate = apiData.getString("Date",null);
        if(prevDate!=null && apiData.getString("NewsArray2",null)!=null ) {
            try {
                Date d1 = format.parse(prevDate);
                Date d2 = format.parse(todayDate);
                diff = d2.getTime()-d1.getTime();
                diffHours = diff / (60 * 60 * 1000);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(diffHours>4) {
                LoadJson();

            }
            else {
                adapter = new NewsAdapter(Paper.book().read("NewsArray", new ArrayList<>()), getContext());
                viewPager.setAdapter(adapter);
                viewPager.setCoverWidth(120f);
                viewPager.setMaxScale(1.0f);
                viewPager.setMinScale(0.9f);
                adapter.notifyDataSetChanged();
            }
        }else
        LoadJson();

        LinearLayout linearLayout = rootView.findViewById(R.id.share);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentInvite = new Intent(Intent.ACTION_SEND);
                intentInvite.setType("text/plain");
                String body = "Hey! Check out this cool Covid-19 tracker app which gives you both global and country specific updates!\n\n" +
                        "https://sehat-tracker.herokuapp.com/Sehat.apk";
                String subject = "Share Sehat";
                intentInvite.putExtra(Intent.EXTRA_SUBJECT, subject);
                intentInvite.putExtra(Intent.EXTRA_TEXT, body);
                startActivity(Intent.createChooser(intentInvite, "Share using"));
            }
        });


        return rootView;
    }



    public void LoadJson(){


        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ResponseObject> call;
        call = apiInterface.getNews("coronavirus%20OR%20covid",
                "cb1a4e75-4cd8-417e-8ad7-20e704e5daed",
                "20",
                "newest",
                "trailText,thumbnail","true");
      //  Toast.makeText(getActivity(),country+" "+ API_KEY, Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful() && response.body().getNews().getArticle()!= null){
                    if(!articles.isEmpty()){
                        articles.clear();
                    }
                    articles = response.body().getNews().getArticle();
                    adapter = new NewsAdapter(articles, getContext());
                    viewPager.setAdapter(adapter);
                    viewPager.setMaxScale(1.0f);
                    viewPager.setMinScale(0.9f);
                    adapter.notifyDataSetChanged();
                    Paper.book().write("NewsArray", articles);
                    apiDataEditor.putString("Date",todayDate);
                    apiDataEditor.putString("NewsArray2","Data Entered");
                    apiDataEditor.apply();
                }else {
                    Toast.makeText(getActivity(),"No result", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                Dialog d = new Dialog(getActivity());
                d.setContentView(R.layout.custom_dialog_error);
                d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                d.show();
                Toast.makeText(getContext(),"No ResponseObject", Toast.LENGTH_SHORT).show();
                if(apiData.getString("NewsArray2",null)!=null ) {
                    adapter = new NewsAdapter(Paper.book().read("NewsArray", new ArrayList<>()), getContext());
                    viewPager.setAdapter(adapter);
                    viewPager.setCoverWidth(120f);
                    viewPager.setMaxScale(1.0f);
                    viewPager.setMinScale(0.9f);
                    adapter.notifyDataSetChanged();
                }


            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
