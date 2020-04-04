package com.alphaCoaching.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.R;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends RecyclerView.Adapter<MainFragment.ViewHolder> {


    private ArrayList<ListItem> listItems;
    private Context context;


    public MainFragment(ArrayList<ListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public MainFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new ViewHolder(v);



        }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
     ListItem listItem=listItems.get(position);
        holder.setHead(listItem.getHead());
        holder.setdisc(listItem.getDescription());
        holder.setDate(listItem.getDate());
    }


    @Override
    public int getItemCount() {
        return listItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public  TextView textViewHead;
        public TextView textviewdate;
        public TextView textViewdesc;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHead=(TextView)itemView.findViewById(R.id.textviewhead);
            textviewdate=(TextView)itemView.findViewById(R.id.textviewdate);
            textViewdesc=(TextView)itemView.findViewById(R.id.textviewdiscription);
     }

        public void setDate(String date)
        {
            textviewdate.setText(date);
        }
        public void setHead(String head)
        {
            textViewHead.setText(head);
        }
        public void setdisc(String disc) { textViewdesc.setText(disc); }
    }

}
