package com.battleshippark.bsp_gallery.activity.files;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.activity.file.FileActivity;
import com.battleshippark.bsp_gallery.media.MediaFileModel;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 */
public class FilesItemViewHolder extends RecyclerView.ViewHolder {
    private final Context context;
    private int position;

    @Bind(R.id.image)
    ImageView imageView;
    @Bind(R.id.play)
    ImageView playImageView;

    public FilesItemViewHolder(Context context, View view, FilesActivityModel filesActivityModel) {
        super(view);
        this.context = context;

        ButterKnife.bind(this, view);

        view.setOnClickListener(v -> context.startActivity(FileActivity.createIntent(context, position, filesActivityModel)));
    }

    public void bind(int position, MediaFileModel model) {
        this.position = position;

        if (model.getThumbPath() == null) {
            Picasso.with(context).load(R.drawable.error_100).into(imageView);
            playImageView.setVisibility(View.GONE);
        } else {
            Picasso.with(context).load(new File(model.getThumbPath())).error(R.drawable.error_100).into(imageView);

            if (model.getMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                playImageView.setVisibility(View.VISIBLE);
            } else {
                playImageView.setVisibility(View.GONE);
            }
        }
    }
}
