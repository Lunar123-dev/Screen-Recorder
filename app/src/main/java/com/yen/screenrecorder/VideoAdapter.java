package com.yen.screenrecorder;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    public interface OnVideoActionListener {
        void onPlayVideo(VideoModel video);
        void onRenameVideo(VideoModel video, int position);
        void onDeleteVideo(VideoModel video, int position);
    }

    private final Context context;
    private final List<VideoModel> videoList;
    private final OnVideoActionListener listener;
    private final ExecutorService thumbnailExecutor = Executors.newFixedThreadPool(3);

    public VideoAdapter(Context context, List<VideoModel> videoList, OnVideoActionListener listener) {
        this.context = context;
        this.videoList = videoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoModel video = videoList.get(position);
        holder.tvFilename.setText(video.getFilename());

        // Format duration
        long seconds = video.getDurationMs() / 1000;
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = seconds / (60 * 60);
        String durStr = h > 0 ? String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
                : String.format(Locale.getDefault(), "%02d:%02d", m, s);
        holder.tvDuration.setText(durStr);

        // Format details: date & size
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String dateStr = sdf.format(new Date(video.getLastModified()));
        String sizeStr = Formatter.formatFileSize(context, video.getSizeBytes());
        holder.tvDetails.setText(dateStr + " • " + sizeStr);

        // Load thumbnail asynchronously
        holder.ivThumbnail.setImageResource(android.R.drawable.ic_menu_gallery);
        holder.currentPath = video.getPath();
        thumbnailExecutor.execute(() -> {
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(video.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
            if (thumb != null && video.getPath().equals(holder.currentPath)) {
                holder.ivThumbnail.post(() -> holder.ivThumbnail.setImageBitmap(thumb));
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPlayVideo(video);
        });

        holder.btnMore.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.btnMore);
            popup.getMenu().add(0, 1, 0, "Đổi tên");
            popup.getMenu().add(0, 2, 1, "Xóa video");
            popup.setOnMenuItemClickListener(item -> {
                int pos = holder.getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return true;
                if (item.getItemId() == 1) {
                    if (listener != null) listener.onRenameVideo(videoList.get(pos), pos);
                    return true;
                } else if (item.getItemId() == 2) {
                    if (listener != null) listener.onDeleteVideo(videoList.get(pos), pos);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvDuration, tvFilename, tvDetails;
        ImageButton btnMore;
        String currentPath;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvFilename = itemView.findViewById(R.id.tv_filename);
            tvDetails = itemView.findViewById(R.id.tv_details);
            btnMore = itemView.findViewById(R.id.btn_more);
        }
    }
}
