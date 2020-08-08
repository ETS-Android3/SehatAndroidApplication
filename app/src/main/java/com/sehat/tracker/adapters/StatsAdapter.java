package com.sehat.tracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.sehat.tracker.R;
import com.sehat.tracker.models.AllStats;

import java.util.ArrayList;


public class StatsAdapter extends PagerAdapter {

    private ArrayList<AllStats> stats;
    private Context context;
    private LayoutInflater layoutInflater;
    private TextView title, value,increase;
    int i=0;
    ProgressBar progressBar;

    public StatsAdapter(ArrayList<AllStats> stats, Context context, ProgressBar progressBar) {
        this.stats = stats;
        this.context = context;
        this.progressBar = progressBar;
        if(context!=null)
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return stats.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }



    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final View itemView = layoutInflater.inflate(R.layout.stats_item,container,false);

        title = itemView.findViewById(R.id.header);
        value = itemView.findViewById(R.id.value);
        increase =itemView.findViewById(R.id.increase);


        switch (position){
            case 0: title.setText("Total Cases");
                if (!stats.get(position).getIncrease().isEmpty())
                increase.setText("+" + stats.get(position).getIncrease());
                break;

            case 1: title.setText("Recovered");
                    value.setTextColor(context.getResources().getColor(R.color.recoveredColor));
                title.setTextColor(context.getResources().getColor(R.color.recoveredColor));

                break;

            case 2: title.setText("Active Cases");
                    title.setTextColor(context.getResources().getColor(R.color.active));
                value.setTextColor(context.getResources().getColor(R.color.active));
                    break;

            case 3: title.setText("Deaths");
                value.setTextColor(context.getResources().getColor(R.color.deathColor));
                increase.setTextColor(context.getResources().getColor(R.color.deathColor));
                title.setTextColor(context.getResources().getColor(R.color.deathColor));
                if (!stats.get(position).getIncrease().isEmpty())
                    increase.setText("+" + stats.get(position).getIncrease());
                break;

        }
        value.setText(stats.get(position).getTotalValue());
        progressBar.setVisibility(View.GONE);

        container.addView(itemView);

        return itemView;
    }



}
