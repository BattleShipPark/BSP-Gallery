package com.battleshippark.bsp_gallery.activity.file;

import com.battleshippark.bsp_gallery.media.MediaFileModel;
import com.battleshippark.bsp_gallery.media.MediaMode;

import java.util.List;

import lombok.Data;

/**
 */
@Data
public class FileModel {
    private int position;
    private List<MediaFileModel> mediaFileModelList;
}
