package com.battleshippark.bsp_gallery.activity.folders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.media.MediaDirectoryModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public class FoldersAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final FoldersModel foldersModel;
    private List<MediaDirectoryModel> mediaDirectoryModelList;

    public FoldersAdapter(Context context, FoldersModel foldersModel) {
        this.context = context;
        this.foldersModel = foldersModel;

        mediaDirectoryModelList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.listitem_main, null);
        RecyclerView.ViewHolder vh = new FoldersItemViewHolder(context, view, foldersModel);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FoldersItemViewHolder vh = (FoldersItemViewHolder) holder;
        vh.setModel(mediaDirectoryModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return mediaDirectoryModelList.size();
    }

    public void refresh() {
        if (foldersModel.getMediaDirectoryModelList() != null) {
            mediaDirectoryModelList = Collections.unmodifiableList(foldersModel.getMediaDirectoryModelList());
            notifyDataSetChanged();
        }
    }
}
