package com.battleshippark.bsp_gallery.activity.file;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.VisibleForTesting;

import com.battleshippark.bsp_gallery.EventBusHelper;
import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.media.MediaFileModel;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.squareup.otto.Bus;

import java.util.List;

import lombok.Data;

/**
 */
@Data
public final class FileActivityModel implements Parcelable {
    private final Bus eventBus;
    private int position;
    private int folderId;
    private String folderName;
    private List<MediaFileModel> mediaFileModelList;
    private MediaFilterMode mediaFilterMode;

    public FileActivityModel() {
        this(EventBusHelper.eventBus);
    }

    @VisibleForTesting
    FileActivityModel(Bus eventBus) {
        this.eventBus = eventBus;
    }

    protected FileActivityModel(Parcel in) {
        this();
        position = in.readInt();
        folderId = in.readInt();
        folderName = in.readString();
        mediaFilterMode = MediaFilterMode.valueOf(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(position);
        dest.writeInt(folderId);
        dest.writeString(folderName);
        dest.writeString(mediaFilterMode.name());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setMediaFileModelList(List<MediaFileModel> modelList) {
        mediaFileModelList = modelList;

        eventBus.post(Events.OnMediaFileListUpdated.EVENT);
    }

    public static final Creator<FileActivityModel> CREATOR = new Creator<FileActivityModel>() {
        @Override
        public FileActivityModel createFromParcel(Parcel in) {
            return new FileActivityModel(in);
        }

        @Override
        public FileActivityModel[] newArray(int size) {
            return new FileActivityModel[size];
        }
    };
}
