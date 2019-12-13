package com.brightfuture.eduquiz.adapter;
import android.content.Context;
import android.os.Parcelable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.brightfuture.eduquiz.R;
import com.brightfuture.eduquiz.model.ImageModel;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;


public class SlidingImage_Adapter extends PagerAdapter {


    private ArrayList<ImageModel> urls;
    private LayoutInflater inflater;
    private Context context;


    public SlidingImage_Adapter(Context context, ArrayList<ImageModel> urls) {
        this.context = context;
        this.urls = urls;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.image);
        TextView tv = imageLayout.findViewById(R.id.tv_tittle);
        tv.setText(urls.get(position).getPost());

        String uri=urls.get(position).getUrl();

      //  http://192.168.1.154/quiz.edu-quiz.live/post/1567586013.7191.jpg
        //http://quiz.edu-quiz.live/

        Glide.with(context)
                .load("http://quiz.edu-quiz.live/post/"+uri)
                .into(imageView);

        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}