package com.sehat.tracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sehat.tracker.R;
import com.sehat.tracker.models.AllStats;

import java.util.ArrayList;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.MyViewHolder> {

    private ArrayList<AllStats> stats;
    private Context context;

    public CountryAdapter(ArrayList<AllStats> stats, Context context) {
        this.stats = stats;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.case_by_country_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holders, int position) {
        final MyViewHolder holder = holders;
        AllStats model = stats.get(position);

        switch (position) {
            case 0:
                holder.header.setText("Total Cases");
                if (!model.getIncrease().isEmpty())
                    holder.increase.setText("+" + model.getIncrease());
                break;
            case 1:
                holder.header.setText("Active Cases");
                holder.header.setTextColor(context.getResources().getColor(R.color.active));
                holder.value.setTextColor(context.getResources().getColor(R.color.active));
                break;
            case 2:
                holder.header.setText("Recovered");
                holder.header.setTextColor(context.getResources().getColor(R.color.recoveredColor));
                holder.value.setTextColor(context.getResources().getColor(R.color.recoveredColor));
                break;
            case 3:
                holder.header.setText("Deaths");
                holder.header.setTextColor(context.getResources().getColor(R.color.deathColor));
                holder.value.setTextColor(context.getResources().getColor(R.color.deathColor));
                holder.increase.setTextColor(context.getResources().getColor(R.color.deathColor));
                if (!model.getIncrease().isEmpty())
                    holder.increase.setText("+" + model.getIncrease());
                break;
            case 4:
                holder.header.setText("Serious Cases");
                if (model.getTotalValue().isEmpty())
                    holder.value.setText("N/A");
                break;
            case 5:
                holder.header.setText("Total Tests");
                break;
        }
        if (model.getTotalValue().isEmpty())
            holder.value.setText("N/A");
        else
            holder.value.setText(model.getTotalValue());
    }


    @Override
    public int getItemCount() {
        return stats.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView header, value, increase;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.header);
            value = itemView.findViewById(R.id.value);
            increase = itemView.findViewById(R.id.increase);
        }
    }

}
