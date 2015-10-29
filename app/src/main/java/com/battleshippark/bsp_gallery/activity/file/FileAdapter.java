package com.battleshippark.bsp_gallery.activity.file;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.battleshippark.bsp_gallery.activity.file.FileModel;
import com.battleshippark.bsp_gallery.media.MediaFileModel;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class FileAdapter extends FragmentStatePagerAdapter {
    private final List<MediaFileModel> mediaFileModelList;

    public FileAdapter(FragmentManager fm, List<MediaFileModel> mediaFileModelList) {
        super(fm);
        this.mediaFileModelList = mediaFileModelList;
    }

    @Override
    public Fragment getItem(int position) {
        return FileFragment.newInstance(mediaFileModelList.get(position));
    }

    @Override
    public int getCount() {
        return mediaFileModelList.size();
    }
}
