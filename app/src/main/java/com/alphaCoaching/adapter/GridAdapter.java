package com.alphaCoaching.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alphaCoaching.R;

public class GridAdapter extends BaseAdapter {
    Context context;
    String[] questionNumber;
    int[] questionBackground;
    LayoutInflater layoutInflater;

    public GridAdapter(Context applicationContext, String[] questionNumber, int[] questionBackground) {
        this.context = applicationContext;
        this.questionNumber = questionNumber;
        this.questionBackground = questionBackground;
        layoutInflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return questionNumber.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(R.layout.single_question_grid_view, null);
        TextView textView = view.findViewById(R.id.QuestionNumber);
//        Button textView = view.findViewById(R.id.QuestionNumber);
        textView.setText(questionNumber[i]);
        //0 means wrong answer  && 1 means correct answer  && -1 means not attempted
        if (questionBackground[i] == 1) {
            textView.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            textView.setBackgroundColor(Color.GREEN);
            textView.setBackgroundResource(R.drawable.rounded_square_corner);
        } else if (questionBackground[i] == 0) {
            textView.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            textView.setBackgroundColor(Color.RED);
            textView.setBackgroundResource(R.drawable.rounded_square_corner);
        } else if (questionBackground[i] == -1) {
            textView.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            textView.setBackgroundColor(Color.GRAY);
            textView.setBackgroundResource(R.drawable.rounded_square_corner);
        }
        return view;
    }
}