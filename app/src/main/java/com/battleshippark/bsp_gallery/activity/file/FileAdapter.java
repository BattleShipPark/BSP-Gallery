package com.battleshippark.bsp_gallery.activity.file;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.battleshippark.bsp_gallery.media.MediaFileModel;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class FileAdapter extends FragmentStatePagerAdapter {
    private final FileActivityModel fileActivityModel;
    private List<MediaFileModel> mediaFileModelList;

    public FileAdapter(FragmentManager fm, FileActivityModel fileActivityModel) {
        super(fm);
        this.fileActivityModel = fileActivityModel;

        mediaFileModelList = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return FileFragment.newInstance(mediaFileModelList.get(position));
    }

    @Override
    public int getCount() {
        return mediaFileModelList.size();
    }

    public void refresh() {
//        mediaFileModelList.clear();
//        mediaFileModelList.addAll(fileActivityModel.getMediaFileModelList());
        mediaFileModelList = fileActivityModel.getMediaFileModelList();
        notifyDataSetChanged();
    }
}
