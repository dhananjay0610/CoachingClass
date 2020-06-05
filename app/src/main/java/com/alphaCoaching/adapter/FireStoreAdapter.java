package com.alphaCoaching.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.Model.RecentLecturesModel;
import com.alphaCoaching.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class FireStoreAdapter extends FirestoreRecyclerAdapter<RecentLecturesModel, FireStoreAdapter.ProductsViewHolder> {

    private OnListItemclick onListItemclick;

    public FireStoreAdapter(@NonNull FirestoreRecyclerOptions<RecentLecturesModel> options, OnListItemclick onListItemclick) {
        super(options);
        this.onListItemclick = onListItemclick;
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull RecentLecturesModel model) {
        holder.textViewchaptername.setText(model.getChapterName());
        holder.textViewsubject.setText(model.getSubject());
        holder.textViewdescription.setText(model.getDescription());
        holder.textViewdate.setText(getstring(model.getLectureDate()));
    }


    //method to convert time to date.
    private String getstring(Date datefromfirestore) {
        SimpleDateFormat dateformate = new SimpleDateFormat("MMM d, yyyy", Locale.US);
        return dateformate.format(datefromfirestore);
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new FireStoreAdapter.ProductsViewHolder(view, onListItemclick);

    }

    public class ProductsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textViewchaptername;
        private TextView textViewsubject;
        private TextView textViewdescription;
        private TextView textViewdate;
        OnListItemclick onListItemclick;


        ProductsViewHolder(@NonNull View itemView, OnListItemclick onListItemclick) {
            super(itemView);

            textViewchaptername = itemView.findViewById(R.id.textviewchaptername);
            textViewdescription = itemView.findViewById(R.id.textviewdiscription);
            textViewsubject = itemView.findViewById(R.id.textviewsubject);
            textViewdate = itemView.findViewById(R.id.textviewdate);
            this.onListItemclick = onListItemclick;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onListItemclick.onItemclick(getItem(getAdapterPosition()), getAdapterPosition());
        }
    }

    public interface OnListItemclick {
        void onItemclick(RecentLecturesModel snapshot, int position);
    }
}
