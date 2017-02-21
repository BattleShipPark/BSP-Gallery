package com.battleshippark.bsp_gallery.presentation.files;

import com.battleshippark.bsp_gallery.media.MediaFileModel;

import java.util.List;

/**
 */

interface FilesView {
    void showProgress();

    void hideProgress();

    void refreshList();

    void refreshList(List<MediaFileModel> mediaFileModels);
}
