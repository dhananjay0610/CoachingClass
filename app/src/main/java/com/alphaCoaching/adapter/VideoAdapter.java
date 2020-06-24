package com.alphaCoaching.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.Model.VideoModel;
import com.alphaCoaching.R;
import com.alphaCoaching.Utils.UserSharedPreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static com.alphaCoaching.AlphaApplication.getAppContext;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    public FirebaseFirestore mFireBaseDB = FirebaseFirestore.getInstance();
    private OnVideoItemClick onVideoItemClick;
    private ArrayList<VideoModel> videoList;

    public VideoAdapter(@NonNull ArrayList<VideoModel> options, OnVideoItemClick onVideoItemClick) {
        videoList = options;
        this.onVideoItemClick = onVideoItemClick;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_item, parent, false);
        return new VideoAdapter.VideoViewHolder(view, onVideoItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoModel model = videoList.get(position);
        holder.pdfName.setText(model.getName());
        holder.pdfSubject.setText(UserSharedPreferenceManager.getUserSubject(getAppContext(), model.getSubject()));
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        private TextView pdfName;
        private TextView pdfSubject;
        private TextView tagName;
        private ImageView itemImage;
        OnVideoItemClick onVideoItemClick;

        public VideoViewHolder(@NonNull View itemView, OnVideoItemClick onVideoItemClick) {
            super(itemView);
            pdfName = itemView.findViewById(R.id.pdfName);
            pdfSubject = itemView.findViewById(R.id.pdfSubject);
            tagName = itemView.findViewById(R.id.tagName);
            itemImage = itemView.findViewById(R.id.imagePdf);
            tagName.setText("Video");
            itemImage.setImageResource(R.drawable.ic_video);
            this.onVideoItemClick = onVideoItemClick;
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onVideoItemClick != null) {
                    onVideoItemClick.onItemClick(videoList.get(position), position);
                }
            });

        }

    }

    public interface OnVideoItemClick {
        void onItemClick(VideoModel snapshot, int position);
    }


}
