package com.battleshippark.bsp_gallery.presentation.folders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public class FoldersAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final FoldersActivityModel foldersActivityModel;
    private List<MediaFolderModel> mediaFolderModelList;

    public FoldersAdapter(Context context, FoldersActivityModel foldersActivityModel) {
        this.context = context;
        this.foldersActivityModel = foldersActivityModel;

        mediaFolderModelList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.listitem_folders, null);
        RecyclerView.ViewHolder vh = new FoldersItemViewHolder(context, view, foldersActivityModel);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FoldersItemViewHolder vh = (FoldersItemViewHolder) holder;
        vh.setModel(mediaFolderModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return mediaFolderModelList.size();
    }

    public void refresh() {
        if (foldersActivityModel.getMediaFolderModelList() != null) {
            mediaFolderModelList = Collections.unmodifiableList(foldersActivityModel.getMediaFolderModelList());
            notifyDataSetChanged();
        }
    }
}
