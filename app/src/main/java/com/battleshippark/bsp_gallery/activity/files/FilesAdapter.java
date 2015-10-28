package com.battleshippark.bsp_gallery.activity.files;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.activity.folders.FoldersItemViewHolder;
import com.battleshippark.bsp_gallery.activity.folders.FoldersModel;
import com.battleshippark.bsp_gallery.media.MediaDirectoryModel;
import com.battleshippark.bsp_gallery.media.MediaFileModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public class FilesAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final FilesModel model;
    private List<MediaFileModel> mediaFileModelList;

    public FilesAdapter(Context context, FilesModel model) {
        this.context = context;
        this.model = model;

        mediaFileModelList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.listitem_files, null);
        RecyclerView.ViewHolder vh = new FilesItemViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FilesItemViewHolder vh = (FilesItemViewHolder) holder;
        vh.setModel(mediaFileModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return mediaFileModelList.size();
    }

    public void refresh() {
        if (model.getMediaFileModelList() != null) {
            mediaFileModelList = Collections.unmodifiableList(model.getMediaFileModelList());
            notifyDataSetChanged();
        }
    }
}
