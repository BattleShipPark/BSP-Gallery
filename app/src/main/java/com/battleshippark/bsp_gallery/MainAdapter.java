package com.battleshippark.bsp_gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.battleshippark.bsp_gallery.media.MediaDirectory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public class MainAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final MainModel mainModel;
    private List<MediaDirectory> mediaDirectoryList;

    public MainAdapter(Context context, MainModel mainModel) {
        this.context = context;
        this.mainModel = mainModel;

        mediaDirectoryList = new ArrayList<>();
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
        vh.setModel(mediaDirectoryList.get(position));
    }

    @Override
    public int getItemCount() {
        return mediaDirectoryList.size();
    }

    public void refresh() {
        mediaDirectoryList = Collections.unmodifiableList(mainModel.getMediaDirectoryList());
        notifyDataSetChanged();
    }
}
