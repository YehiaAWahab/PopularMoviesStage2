package io.github.yahia_hassan.popularmoviesstage2.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.yahia_hassan.popularmoviesstage2.R;
import io.github.yahia_hassan.popularmoviesstage2.Review;


public class UserReviewAdapter extends RecyclerView.Adapter<UserReviewAdapter.UserReviewViewHolder> {

    private Context mContext;
    private ArrayList<Review> mReviewArrayList;

    public UserReviewAdapter(Context context, ArrayList<Review> reviewArrayList) {
        mContext = context;
        mReviewArrayList = reviewArrayList;
    }


    @Override
    public UserReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_review_list_item, parent, false);

        return new UserReviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserReviewViewHolder holder, int position) {
        holder.mReviewAuthorTextView.setText(mReviewArrayList.get(position).getAuthor() + mContext.getString(R.string.colon));
        holder.mReviewContentTextView.setText(mReviewArrayList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return mReviewArrayList.size();
    }

    public class UserReviewViewHolder extends RecyclerView.ViewHolder {
        TextView mReviewAuthorTextView;
        TextView mReviewContentTextView;
        public UserReviewViewHolder(View itemView) {
            super(itemView);
            mReviewAuthorTextView = itemView.findViewById(R.id.author_tv);
            mReviewContentTextView = itemView.findViewById(R.id.content_tv);
        }
    }
}
