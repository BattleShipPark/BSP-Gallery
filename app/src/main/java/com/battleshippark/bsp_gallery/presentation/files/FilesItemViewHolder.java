package com.battleshippark.bsp_gallery.presentation.files;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.media.MediaFileModel;
import com.battleshippark.bsp_gallery.presentation.file.FileActivity;
import com.bumptech.glide.Glide;

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

        view.setOnClickListener(v -> startActivity(context, filesActivityModel));
    }

    public void bind(int position, MediaFileModel model) {
        this.position = position;

        if (model.getThumbPath() == null) {
            Glide.with(context).load(R.drawable.error_100).into(imageView);
            playImageView.setVisibility(View.GONE);
        } else {
            Glide.with(context).load(new File(model.getThumbPath())).error(R.drawable.error_100).into(imageView);

            if (model.getMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                playImageView.setVisibility(View.VISIBLE);
            } else {
                playImageView.setVisibility(View.GONE);
            }
        }
    }

    private void startActivity(Context context, FilesActivityModel filesActivityModel) {
        MediaFileModel mediaFileModel = filesActivityModel.getMediaFileModelList().get(position);

        if (mediaFileModel.getMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
            context.startActivity(FileActivity.createIntent(context, position, filesActivityModel));
        else {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_VIEW);
            sendIntent.setDataAndType(Uri.fromFile(new File(mediaFileModel.getPath())), "video/*");
            context.startActivity(sendIntent);
        }
    }
}
