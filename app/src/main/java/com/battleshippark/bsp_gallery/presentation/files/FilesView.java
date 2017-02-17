package com.battleshippark.bsp_gallery.presentation.files;

import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.util.List;

/**
 */

interface FilesView {
    void showProgress();

    void hideProgress();

    void updateFilterMode(MediaFilterMode mediaFilterMode);

    void refreshList();

    void refreshList(List<MediaFolderModel> mediaFolderModels);
}
