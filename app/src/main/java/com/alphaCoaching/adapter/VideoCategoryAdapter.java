package com.alphaCoaching.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.Model.VideoCategoryModel;
import com.alphaCoaching.Model.VideoModel;
import com.alphaCoaching.R;
import com.alphaCoaching.Utils.UserSharedPreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static com.alphaCoaching.AlphaApplication.getAppContext;

public class VideoCategoryAdapter extends RecyclerView.Adapter<VideoCategoryAdapter.VideoViewHolder> {

    public FirebaseFirestore mFireBaseDB = FirebaseFirestore.getInstance();
    private VideoCategoryAdapter.OnVideoItemClick onVideoItemClick;
    private ArrayList<VideoCategoryModel> mVideosCategoryList;

    public VideoCategoryAdapter(@NonNull ArrayList<VideoCategoryModel> options, VideoCategoryAdapter.OnVideoItemClick onVideoItemClick) {
        mVideosCategoryList = options;
        this.onVideoItemClick = onVideoItemClick;
    }

    @NonNull
    @Override
    public VideoCategoryAdapter.VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_item, parent, false);
        return new VideoCategoryAdapter.VideoViewHolder(view, onVideoItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoCategoryAdapter.VideoViewHolder holder, int position) {
        VideoCategoryModel model = mVideosCategoryList.get(position);
        holder.pdfName.setText(model.getName());
        holder.pdfSubject.setText(UserSharedPreferenceManager.getUserSubject(getAppContext(), model.getSubject()));
    }

    @Override
    public int getItemCount() {
        return mVideosCategoryList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        private TextView pdfName;
        private TextView pdfSubject;
        private TextView tagName;
        private ImageView itemImage;
        VideoCategoryAdapter.OnVideoItemClick onVideoItemClick;

        public VideoViewHolder(@NonNull View itemView, VideoCategoryAdapter.OnVideoItemClick onVideoItemClick) {
            super(itemView);
            pdfName = itemView.findViewById(R.id.pdfName);
            pdfSubject = itemView.findViewById(R.id.pdfSubject);
            tagName = itemView.findViewById(R.id.tagName);
            itemImage = itemView.findViewById(R.id.imagePdf);
            tagName.setText("Chapters");
            itemImage.setVisibility(View.GONE);
//            itemImage.setImageResource(R.drawable.ic_video);
            this.onVideoItemClick = onVideoItemClick;
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onVideoItemClick != null) {
                    onVideoItemClick.onItemClick(mVideosCategoryList.get(position), position);
                }
            });

        }

    }

    public interface OnVideoItemClick {
        void onItemClick(VideoCategoryModel snapshot, int position);
    }
}
