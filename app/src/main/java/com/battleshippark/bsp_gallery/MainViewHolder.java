package com.battleshippark.bsp_gallery;

import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.battleshippark.bsp_gallery.media.MediaDirectoryModel;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 */
public class MainViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.image)
    ImageView coverImageView;

    @Bind(R.id.play)
    ImageView playImageView;

    @Bind(R.id.name)
    TextView nameTextView;

    @Bind(R.id.count)
    TextView countTextView;

    public MainViewHolder(View view) {
        super(view);

        ButterKnife.bind(this, view);
    }

    public void setModel(MediaDirectoryModel mediaDirectoryModel) {
        if (mediaDirectoryModel.getCoverThumbPath() != null) {
            coverImageView.setImageBitmap(BitmapFactory.decodeFile(mediaDirectoryModel.getCoverThumbPath()));
            if (mediaDirectoryModel.getCoverMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                playImageView.setVisibility(View.VISIBLE);
            } else {
                playImageView.setVisibility(View.GONE);
            }
        }

        nameTextView.setText(mediaDirectoryModel.getName());
        countTextView.setText(String.valueOf(mediaDirectoryModel.getCount()));
    }
}
