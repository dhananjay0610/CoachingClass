package com.alphaCoaching.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.alphaCoaching.Model.PDFModel;
import com.alphaCoaching.Model.SpinnerModel;
import com.alphaCoaching.Model.VideoCategoryModel;
import com.alphaCoaching.R;
import com.alphaCoaching.activity.PdfListActivity;
import com.alphaCoaching.activity.VideoCategoryListActivity;

import java.util.ArrayList;
import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<SpinnerModel> {
    private Context mContext;
    private ArrayList<SpinnerModel> listState;
    private SpinnerAdapter myAdapter;
    private boolean isFromView = false;
    public static final int PDF_TYPE = 0;
    public static final int VIDEO_TYPE = 1;
    private int currentFilterType;

    public SpinnerAdapter(Context context, int resource, List<SpinnerModel> objects, int filterType) {
        super(context, resource, objects);
        this.mContext = context;
        this.listState = (ArrayList<SpinnerModel>) objects;
        this.myAdapter = this;
        this.currentFilterType = filterType;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.spinner_item, null);
            holder = new ViewHolder();
            holder.mTextView = convertView.findViewById(R.id.text);
            holder.mCheckBox = convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTextView.setText(listState.get(position).getTitle());

        // To check weather checked event fire from getview() or user input
        isFromView = true;
        holder.mCheckBox.setChecked(listState.get(position).isSelected());
        isFromView = false;
        holder.mCheckBox.setVisibility(View.VISIBLE);
        holder.mCheckBox.setTag(position);
        holder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isFromView) {
                listState.get(position).setSelected(isChecked);

                if (currentFilterType == PDF_TYPE) {
                    filterPdfList();
                } else if (currentFilterType == VIDEO_TYPE) {
                    filterVideoList();
                }
            }
        });
        return convertView;
    }

    private void filterPdfList() {
        PdfListActivity pdfListActivity = new PdfListActivity();
        ArrayList<PDFModel> options = pdfListActivity.getOptions();
        ArrayList<PDFModel> optionsTemp = new ArrayList<>();
        boolean isAnyOneSelected = false;
        for (int i = 0; i < listState.size(); i++) {
            for (int j = 0; j < options.size(); j++) {
                if (listState.get(i).isSelected() && listState.get(i).getId().equals(options.get(j).getSubject())) {
                    optionsTemp.add(options.get(j));
                    isAnyOneSelected = true;
                }
            }
        }
        if (!isAnyOneSelected) {
            pdfListActivity.updateAdapter(mContext, options);
        } else {
            pdfListActivity.updateAdapter(mContext, optionsTemp);
        }
    }

    private void filterVideoList() {
        VideoCategoryListActivity categoryList = new VideoCategoryListActivity();
        ArrayList<VideoCategoryModel> options = categoryList.getmVideosCategoryList();
        ArrayList<VideoCategoryModel> optionsTemp = new ArrayList<>();
        boolean isAnyOneSelected = false;
        for (int i = 0; i < listState.size(); i++) {
            for (int j = 0; j < options.size(); j++) {
                if (listState.get(i).isSelected() && listState.get(i).getId().equals(options.get(j).getSubject())) {
                    optionsTemp.add(options.get(j));
                    isAnyOneSelected = true;
                }
            }
        }
        if (!isAnyOneSelected) {
            categoryList.updateAdapter(options);
            return;
        } else {
            categoryList.updateAdapter(optionsTemp);
            return;
        }
    }

    private static class ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
    }
}