package com.alphaCoaching.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.alphaCoaching.Model.PDFModel;
import com.alphaCoaching.Model.SpinnerModel;
import com.alphaCoaching.R;
import com.alphaCoaching.activity.PdfListActivity;
import com.alphaCoaching.activity.PdfViewActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<SpinnerModel> {
    private Context mContext;
    private ArrayList<SpinnerModel> listState;
    private SpinnerAdapter myAdapter;
    private boolean isFromView = false;

    public SpinnerAdapter(Context context, int resource, List<SpinnerModel> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.listState = (ArrayList<SpinnerModel>) objects;
        this.myAdapter = this;
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

//        if ((position == 0)) {
//            holder.mCheckBox.setVisibility(View.INVISIBLE);
//        } else {
//            holder.mCheckBox.setVisibility(View.VISIBLE);
//        }
        holder.mCheckBox.setVisibility(View.VISIBLE);
        holder.mCheckBox.setTag(position);
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int getPosition = (Integer) buttonView.getTag();

                if (!isFromView) {
                    listState.get(position).setSelected(isChecked);
                }

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
                    pdfListActivity.updateAdapter(options);
                } else
                    pdfListActivity.updateAdapter(optionsTemp);
                return;
            }
        });
        return convertView;
    }

    private class ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
    }
}

