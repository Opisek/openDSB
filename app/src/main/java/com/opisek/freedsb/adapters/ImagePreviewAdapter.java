package com.opisek.freedsb.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.opisek.freedsb.R;
import com.opisek.freedsb.database.AppDatabase;
import com.opisek.freedsb.database.Node;
import com.opisek.freedsb.network.ProtocolConstants;
import com.opisek.freedsb.viewmodels.PreviewViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

public class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder> {

    private final AppCompatActivity mContext;

    private final long[] mRootNodeIds;

    private final ItemClickEventListener mItemClickEventListener;

    public ImagePreviewAdapter(AppCompatActivity pContext, long[] rootNodeIds, ItemClickEventListener itemClickEventListener) {
        mContext = pContext;
        mRootNodeIds = rootNodeIds;
        mItemClickEventListener = itemClickEventListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_timetable, parent, false), mItemClickEventListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PreviewViewModel previewViewModel = ViewModelProviders.of(mContext).get(getClass().getCanonicalName() + mRootNodeIds[0] + position, PreviewViewModel.class);

        previewViewModel.getTitle().observe(mContext, holder.mTitleTextView::setText);
        previewViewModel.getSubtitle().observe(mContext, holder.mSubtitleTextView::setText);
        previewViewModel.getProgress().observe(mContext, holder.mProgressBar::setProgress);

        Node node = AppDatabase.getInstance(mContext).getNodeDao().getNodeById(mRootNodeIds[position]);
        holder.mTitleTextView.setText(node.mTitle);
        holder.mSubtitleTextView.setText(ProtocolConstants.DATE_PARSER.format(node.mDate));
    }

    @Override
    public int getItemCount() {
        if (mRootNodeIds != null) {
            return mRootNodeIds.length;
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final LinearLayout mClickableLayout;

        final TextView mTitleTextView;
        final TextView mSubtitleTextView;
        final TextView mErrorTextView;

        final ProgressBar mProgressBar;

        PreviewViewModel.Status mStatus = PreviewViewModel.Status.LOADING;

        ViewHolder(@NonNull View itemView, ItemClickEventListener pItemClickEventListener) {
            super(itemView);
            mClickableLayout = itemView.findViewById(R.id.clickableLayout);
            mClickableLayout.setOnClickListener(v -> {
                if (pItemClickEventListener != null) {
                    pItemClickEventListener.onClick(this, getLayoutPosition());
                }
            });

            mClickableLayout.setOnLongClickListener(v -> pItemClickEventListener != null && pItemClickEventListener.onLongClick(this, getLayoutPosition()));

            mTitleTextView = itemView.findViewById(R.id.textViewTitle);
            mSubtitleTextView = itemView.findViewById(R.id.textViewSubtitle);
            mErrorTextView = itemView.findViewById(R.id.textViewError);

            mProgressBar = itemView.findViewById(R.id.progressBar);

        }

        void setStatus(PreviewViewModel.Status status) {
            mStatus = status;
        }

        public PreviewViewModel.Status getStatus() {
            return mStatus;
        }
    }

    public interface ItemClickEventListener {
        void onClick(ViewHolder holder, int position);

        boolean onLongClick(ViewHolder holder, int position);
    }
}
