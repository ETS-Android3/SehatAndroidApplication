package com.sehat.tracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sehat.tracker.R;
import com.sehat.tracker.models.TabData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private List<TabData> tabData;
    private Context context;
    private ProgressBar progressBar;

    public RecyclerAdapter(List<TabData> tabData, Context context, ProgressBar progressBar) {
        this.tabData = tabData;
        this.context = context;
        this.progressBar = progressBar;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holders, int position) {
        final MyViewHolder holder = holders;
        TabData model = tabData.get(position);

        if (model.getCountryName().equals("USA"))
            Picasso.get().load("https://countryflagsapi.com/png/us").placeholder(R.drawable.globe).into(holder.flag);
        else if (model.getCountryName().equals("UK"))
            Picasso.get().load("https://countryflagsapi.com/png/gb").placeholder(R.drawable.globe).into(holder.flag);
        else if (model.getCountryName().equals("S. Korea"))
            Picasso.get().load("https://countryflagsapi.com/png/kr").placeholder(R.drawable.globe).into(holder.flag);
        else if (model.getCountryName().equals("UAE"))
            Picasso.get().load("https://countryflagsapi.com/png/ae").placeholder(R.drawable.globe).into(holder.flag);
        else if (model.getCountryName().equals("Macao"))
            Picasso.get().load("https://countryflagsapi.com/png/mo").placeholder(R.drawable.globe).into(holder.flag);
        else if (model.getCountryName().equals("Myanmar"))
            Picasso.get().load("https://countryflagsapi.com/png/mm").placeholder(R.drawable.globe).into(holder.flag);

        else if (!getCountryCode(model.getCountryName()).isEmpty())
            Picasso.get().load("https://countryflagsapi.com/png/"+ getCountryCode(model.getCountryName()).toLowerCase()).placeholder(R.drawable.globe).into(holder.flag);
        else
            holder.flag.setImageResource(R.drawable.globe);


        holder.name.setText(model.getCountryName());
        holder.cases.setText(model.getTotalCases());
        holder.death.setText(model.getTotalDeaths());
        holder.recover.setText(model.getTotalRecovered());
        holder.active.setText(model.getActiveCases());

        if (!model.getNewCases().isEmpty() && !model.getNewCases().equals("0"))
            holder.newCases.setText("+" + model.getNewCases());
        else
            holder.newCases.setText("");
        if (!model.getNewDeaths().isEmpty() && !model.getNewDeaths().equals("0"))
            holder.newDeaths.setText("+" + model.getNewDeaths());
        else
            holder.newDeaths.setText("");
        holder.newRecover.setText("");
        holder.newActive.setText("");

        progressBar.setVisibility(View.GONE);


    }

    public String getCountryCode(String countryName) {
        String[] isoCountryCodes = Locale.getISOCountries();
        for (String code : isoCountryCodes) {
            Locale locale = new Locale("", code);
            if (countryName.equalsIgnoreCase(locale.getDisplayCountry())) {
                return code;
            }
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return tabData.size();
    }

    public void filterList(ArrayList<TabData> filteredList) {
        tabData = filteredList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, cases, death, recover, active, newCases, newDeaths, newActive, newRecover;
        ImageView flag;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            cases = itemView.findViewById(R.id.cases);
            death = itemView.findViewById(R.id.deaths);
            recover = itemView.findViewById(R.id.recovered);
            active = itemView.findViewById(R.id.active);
            flag = itemView.findViewById(R.id.flag);
            newCases = itemView.findViewById(R.id.casesNew);
            newDeaths = itemView.findViewById(R.id.deathsNew);
            newActive = itemView.findViewById(R.id.activeNew);
            newRecover = itemView.findViewById(R.id.recoveredNew);
        }
    }

}
