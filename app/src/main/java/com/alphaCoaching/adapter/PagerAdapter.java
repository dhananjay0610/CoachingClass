package com.alphaCoaching.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.alphaCoaching.fragment.QuizAnalysis;
import com.alphaCoaching.fragment.QuizQuestions;


public class PagerAdapter extends FragmentPagerAdapter {

private int noOfTabs;

    public PagerAdapter(@NonNull FragmentManager fm,int noOfTabs) {
        super(fm);
        this.noOfTabs=noOfTabs;

    }
    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return  new QuizAnalysis();
            case 1:
                return  new QuizQuestions();
            default:
                return  null;
        }

    }

    @Override
    public int getCount() {
        return noOfTabs;
    }
}
