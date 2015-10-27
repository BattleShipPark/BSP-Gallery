package com.battleshippark.bsp_gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.battleshippark.bsp_gallery.media.MediaDirectoryModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public class MainAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final MainModel mainModel;
    private List<MediaDirectoryModel> mediaDirectoryModelList;

    public MainAdapter(Context context, MainModel mainModel) {
        this.context = context;
        this.mainModel = mainModel;

        mediaDirectoryModelList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.listitem_main, null);
        RecyclerView.ViewHolder vh = new MainViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MainViewHolder vh = (MainViewHolder) holder;
        vh.setModel(mediaDirectoryModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return mediaDirectoryModelList.size();
    }

    public void refresh() {
        mediaDirectoryModelList = Collections.unmodifiableList(mainModel.getMediaDirectoryModelList());
        notifyDataSetChanged();
    }
}
