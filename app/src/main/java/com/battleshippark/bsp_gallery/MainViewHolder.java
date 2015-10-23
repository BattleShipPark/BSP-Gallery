package com.battleshippark.bsp_gallery;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.battleshippark.bsp_gallery.media.MediaDirectory;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 */
public class MainViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.image)
    ImageView coverImageView;

    @Bind(R.id.name)
    TextView nameTextView;

    @Bind(R.id.count)
    TextView countTextView;

    public MainViewHolder(View view) {
        super(view);

        ButterKnife.bind(this, view);
    }

    public void setModel(MediaDirectory mediaDirectory) {
        if (mediaDirectory.getCoverThumbPath() != null)
            coverImageView.setImageBitmap(BitmapFactory.decodeFile(mediaDirectory.getCoverThumbPath()));
        
        nameTextView.setText(mediaDirectory.getName());
        countTextView.setText(String.valueOf(mediaDirectory.getCount()));
    }
}
