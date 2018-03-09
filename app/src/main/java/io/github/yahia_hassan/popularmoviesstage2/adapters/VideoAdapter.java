package io.github.yahia_hassan.popularmoviesstage2.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.yahia_hassan.popularmoviesstage2.R;
import io.github.yahia_hassan.popularmoviesstage2.Video;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private ArrayList<Video> mVideoArrayList;
    private VideoAdapterOnClickListener mVideoAdapterOnClickListener;

    public interface VideoAdapterOnClickListener {
        void OnClick(Video video);
    }

    public VideoAdapter(ArrayList<Video> videoArrayList, VideoAdapterOnClickListener videoAdapterOnClickListener) {
        mVideoArrayList = videoArrayList;
        mVideoAdapterOnClickListener = videoAdapterOnClickListener;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.videos_list_item, parent, false);

        return new VideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.mVideoTitleTextView.setText(mVideoArrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mVideoArrayList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mVideoTitleTextView;

        public VideoViewHolder(View itemView) {
            super(itemView);

            mVideoTitleTextView = itemView.findViewById(R.id.video_title_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Video clickedVideo = mVideoArrayList.get(position);
            mVideoAdapterOnClickListener.OnClick(clickedVideo);
        }
    }
}
