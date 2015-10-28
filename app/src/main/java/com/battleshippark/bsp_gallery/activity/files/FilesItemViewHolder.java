package com.battleshippark.bsp_gallery.activity.files;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.media.MediaFileModel;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 */
public class FilesItemViewHolder extends RecyclerView.ViewHolder {
    private final Context context;

    @Bind(R.id.image)
    ImageView imageView;

    @Bind(R.id.play)
    ImageView playImageView;

    public FilesItemViewHolder(Context context, View view) {
        super(view);
        this.context = context;

        ButterKnife.bind(this, view);
    }

    public void setModel(MediaFileModel model) {
        if (model.getThumbPath() != null) {
//            imageView.setImageBitmap(BitmapFactory.decodeFile(model.getThumbPath()));
            Picasso.with(context).load(new File(model.getThumbPath())).error(R.drawable.ic_launcher).into(imageView);

            if (model.getMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                playImageView.setVisibility(View.VISIBLE);
            } else {
                playImageView.setVisibility(View.GONE);
            }
        }
    }
}
