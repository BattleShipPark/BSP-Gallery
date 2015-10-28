package com.battleshippark.bsp_gallery.activity.folders;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.activity.files.FilesActivity;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 */
public class FoldersItemViewHolder extends RecyclerView.ViewHolder {
    private final Context context;
    private final FoldersModel foldersModel;

    @Bind(R.id.image)
    ImageView coverImageView;

    @Bind(R.id.play)
    ImageView playImageView;

    @Bind(R.id.name)
    TextView nameTextView;

    @Bind(R.id.count)
    TextView countTextView;

    private MediaFolderModel model;

    public FoldersItemViewHolder(Context context, View view, FoldersModel foldersModel) {
        super(view);
        this.context = context;
        this.foldersModel = foldersModel;

        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.root)
    public void onClick() {
        context.startActivity(FilesActivity.createIntent(context, foldersModel, model));
    }

    public void setModel(MediaFolderModel mediaFolderModel) {
        this.model = mediaFolderModel;

        if (mediaFolderModel.getCoverThumbPath() != null) {
            coverImageView.setImageBitmap(BitmapFactory.decodeFile(mediaFolderModel.getCoverThumbPath()));
            if (mediaFolderModel.getCoverMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                playImageView.setVisibility(View.VISIBLE);
            } else {
                playImageView.setVisibility(View.GONE);
            }
        }

        nameTextView.setText(mediaFolderModel.getName());
        countTextView.setText(String.valueOf(mediaFolderModel.getCount()));
    }
}
