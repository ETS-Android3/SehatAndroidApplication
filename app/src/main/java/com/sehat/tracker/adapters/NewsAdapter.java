package com.sehat.tracker.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.sehat.tracker.R;
import com.sehat.tracker.Utils;
import com.sehat.tracker.models.Article;

import java.util.List;

public class NewsAdapter extends PagerAdapter {

    private List<Article> articles;
    private Context context;
    private LayoutInflater layoutInflater;
    private TextView title, desc, source, time;
    private ImageView imageView;
    private Button button;
    private int i=0;

    public NewsAdapter(List<Article> articles, Context context) {
        this.articles = articles;
        this.context = context;
        if(context!=null)
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return articles.size();
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
        final View itemView = layoutInflater.inflate(R.layout.item,container,false);

        title = itemView.findViewById(R.id.title);
        desc = itemView.findViewById(R.id.desc);
        time = itemView.findViewById(R.id.time);
        imageView = itemView.findViewById(R.id.img);
        button = itemView.findViewById(R.id.view_button);
        imageView.setClipToOutline(true);

        final Article model = articles.get(position);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(Utils.getRandomDrawbleColor());
        requestOptions.error(R.drawable.news_sample);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();


        Glide.with(context)
                .load(model.getFields().getImgUrl())
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);


        String descText = model.getFields().getText();


        title.setText(model.getTitle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            desc.setText(Html.fromHtml(descText, Html.FROM_HTML_MODE_COMPACT));
        } else {
            desc.setText(Html.fromHtml(descText));
        }
        time.setText(" \u2022 "+ Utils.DateToTimeFormat(model.getPublishedAt()));


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse(model.getUrl()); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
        container.addView(itemView);
        i++;
        return itemView;
    }



}
