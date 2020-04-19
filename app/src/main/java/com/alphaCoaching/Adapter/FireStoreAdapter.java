package com.alphaCoaching.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.alphaCoaching.Model.recentLecturesModel;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class FireStoreAdapter extends FirestoreRecyclerAdapter<recentLecturesModel, FireStoreAdapter.productsviewholder> {

    private OnListItemclick onListItemclick;

    public FireStoreAdapter(@NonNull FirestoreRecyclerOptions<recentLecturesModel> options, OnListItemclick onListItemclick) {
        super(options);
        this.onListItemclick = onListItemclick;
    }

    @Override
    protected void onBindViewHolder(@NonNull productsviewholder holder, int position, @NonNull recentLecturesModel model) {
        holder.textViewchaptername.setText(model.getChapterName());
        holder.textViewsubject.setText(model.getSubject());
        holder.textViewdescription.setText(model.getDescription());
        holder.textViewdate.setText(getstring(model.getLectureDate()));
        // Log.d("from string ans date", "" + (model.getLectureDate()));
    }

    //method to convert time to date.
    private String getstring(Date datefromfirestore) {
        SimpleDateFormat dateformate = new SimpleDateFormat("MMM d, yyyy", Locale.US);
        return dateformate.format(datefromfirestore);
    }

    @NonNull
    @Override
    public productsviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new FireStoreAdapter.productsviewholder(view, onListItemclick);

    }

    public class productsviewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textViewchaptername;
        private TextView textViewsubject;
        private TextView textViewdescription;
        private TextView textViewdate;
        OnListItemclick onListItemclick;


        public productsviewholder(@NonNull View itemView, OnListItemclick onListItemclick) {
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
        void onItemclick(recentLecturesModel snapshot, int position);
    }
}
