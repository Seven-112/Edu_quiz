package com.brightfuture.eduquiz.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.brightfuture.eduquiz.fragment.AllQuestionsFragment;
import com.brightfuture.eduquiz.fragment.MyQuestionsFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {


    private String title[] = {"ALL", "MINE"};
    private Context context;

    public ViewPagerAdapter(FragmentManager manager, Context context)
    {
        super(manager);
        this.context=context;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return AllQuestionsFragment.newInstance(""+position,context);
            case 1:
                return MyQuestionsFragment.newInstance(""+position,context);

                default:
                    return AllQuestionsFragment.newInstance(""+position,context);
        }



    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
