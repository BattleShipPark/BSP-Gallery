package com.battleshippark.bsp_gallery.presentation.folders;

import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.util.List;

/**
 */

interface FoldersView {
    void showProgress();

    void hideProgress();

    void updateFilterMode(MediaFilterMode mediaFilterMode);

    void refreshList();

    void refreshList(List<MediaFolderModel> mediaFolderModels);
}
