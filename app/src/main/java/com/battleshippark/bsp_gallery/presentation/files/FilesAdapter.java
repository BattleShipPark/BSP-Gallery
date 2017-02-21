package com.battleshippark.bsp_gallery.presentation.files;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.media.MediaFileModel;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class FilesAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final FilesActivityModel model;
    private List<MediaFileModel> mediaFileModelList;

    public FilesAdapter(Context context, FilesActivityModel model) {
        this.context = context;
        this.model = model;

        mediaFileModelList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.listitem_files, null);
        return new FilesItemViewHolder(context, view, model);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FilesItemViewHolder vh = (FilesItemViewHolder) holder;
        vh.bind(position, mediaFileModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return mediaFileModelList.size();
    }

    public void refresh() {
        mediaFileModelList.clear();
        mediaFileModelList.addAll(model.getMediaFileModelList());
        notifyDataSetChanged();
    }
}
