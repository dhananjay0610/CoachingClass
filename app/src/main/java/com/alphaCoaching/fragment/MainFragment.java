package com.alphaCoaching.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.R;

import java.util.List;

public class MainFragment extends RecyclerView.Adapter<MainFragment.ViewHolder> {


    private List<ListItem> listItems;
    private Context context;


    public MainFragment(List<ListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListItem listItem=listItems.get(position);
        holder.textViewHead.setText(listItem.getHead());
        holder.textViewdesc.setText(listItem.getDescription());
        holder.date.setText(listItem.getDate());
    }


    @Override
    public int getItemCount() {
        return listItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public  TextView textViewHead;
        public TextView date;
        public TextView textViewdesc;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHead=(TextView)itemView.findViewById(R.id.textviewhead);
            date=(TextView)itemView.findViewById(R.id.date);

            textViewdesc=(TextView)itemView.findViewById(R.id.textviewdiscription);
        }
    }

}
