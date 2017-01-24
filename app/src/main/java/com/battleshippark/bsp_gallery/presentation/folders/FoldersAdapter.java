package com.battleshippark.bsp_gallery.presentation.folders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.util.ArrayList;
import java.util.List;

/**
 */
class FoldersAdapter extends RecyclerView.Adapter {
    private final Context context;
    private List<MediaFolderModel> items = new ArrayList<>();
    private MediaFilterMode filterMode;

    FoldersAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.listitem_folders, null);
        RecyclerView.ViewHolder vh = new FoldersItemViewHolder(context, view, filterMode);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FoldersItemViewHolder vh = (FoldersItemViewHolder) holder;
        vh.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void setItems(List<MediaFolderModel> items) {
        this.items = items;
    }

    void setFilterMode(MediaFilterMode filterMode) {
        this.filterMode = filterMode;
    }
}
