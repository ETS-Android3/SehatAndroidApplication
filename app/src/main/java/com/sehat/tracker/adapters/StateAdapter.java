package com.sehat.tracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sehat.tracker.R;
import com.sehat.tracker.models.StateData;

import java.util.ArrayList;

public class StateAdapter extends RecyclerView.Adapter<StateAdapter.MyViewHolder> {

    private ArrayList<StateData> stateData;

    private Context context;

    public StateAdapter(ArrayList<StateData> stateData, Context context) {
        this.stateData = stateData;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.table_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holders, int position) {
        final MyViewHolder holder = holders;
       StateData model = stateData.get(position);

       holder.name.setText(model.getStateName());
       if(model.getNewCases().equals("0"))
       holder.total.setText(model.getTotalCases());
       else {
           holder.total.setText(model.getTotalCases() + " +" + model.getNewCases());

       }

        if(model.getNewRecovered().equals("0"))
            holder.recover.setText(model.getTotalRecovered());
        else
            holder.recover.setText(model.getTotalRecovered()+" +" + model.getNewRecovered());
       // holder.active.setText(model.getActiveCases());
        if(model.getNewDeaths().equals("0"))
            holder.death.setText(model.getTotalDeaths());
        else
            holder.death.setText(model.getTotalDeaths()+" +" + model.getNewDeaths());



    }


    @Override
    public int getItemCount() {
        return stateData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name,total,recover,active,death;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
           name = itemView.findViewById(R.id.name);
           total = itemView.findViewById(R.id.total);
           recover = itemView.findViewById(R.id.recovered);
           death = itemView.findViewById(R.id.deaths);
        }
    }

}
