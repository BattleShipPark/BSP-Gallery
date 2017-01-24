package com.battleshippark.bsp_gallery.presentation.folders;

import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.util.List;

/**
 */

interface FoldersView {
    void showProgress();

    void hideProgress();

    void refreshList(List<MediaFolderModel> mediaFolderModels);
}
