package com.battleshippark.bsp_gallery.presentation.folders;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;
import com.battleshippark.bsp_gallery.presentation.files.FilesActivity;
import com.bumptech.glide.Glide;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 */
class FoldersItemViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.image)
    ImageView coverImageView;

    @Bind(R.id.play)
    ImageView playImageView;

    @Bind(R.id.name)
    TextView nameTextView;

    @Bind(R.id.count)
    TextView countTextView;

    private final Context context;
    private final MediaFilterMode filterMode;
    private MediaFolderModel model;

    FoldersItemViewHolder(Context context, View view, MediaFilterMode filterMode) {
        super(view);
        this.context = context;
        this.filterMode = filterMode;

        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.root)
    void onClick() {
        context.startActivity(FilesActivity.createIntent(context, filterMode, model));
    }

    public void bind(MediaFolderModel model) {
        this.model = model;

        if (model.getCoverThumbPath() == null) {
            playImageView.setVisibility(View.GONE);
        } else {
            Glide.with(context).load(new File(model.getCoverThumbPath())).error(R.drawable.error_100).into(coverImageView);

            if (model.getCoverMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                playImageView.setVisibility(View.VISIBLE);
            } else {
                playImageView.setVisibility(View.GONE);
            }
        }

        nameTextView.setText(model.getName());
        countTextView.setText(String.valueOf(model.getCount()));
    }
}
