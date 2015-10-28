package com.battleshippark.bsp_gallery.activity.files;

import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.media.MediaFileModel;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 */
public class FilesItemViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.image)
    ImageView imageView;

    @Bind(R.id.play)
    ImageView playImageView;

    public FilesItemViewHolder(View view) {
        super(view);

        ButterKnife.bind(this, view);
    }

    public void setModel(MediaFileModel mediaDirectoryModel) {
        if (mediaDirectoryModel.getThumbPath() != null) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(mediaDirectoryModel.getThumbPath()));
            if (mediaDirectoryModel.getMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                playImageView.setVisibility(View.VISIBLE);
            } else {
                playImageView.setVisibility(View.GONE);
            }
        }
    }
}
