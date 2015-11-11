package com.battleshippark.bsp_gallery.activity.file;

import android.os.Parcel;
import android.os.Parcelable;

import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.media.MediaFileModel;
import com.battleshippark.bsp_gallery.media.MediaMode;
import com.squareup.otto.Bus;

import java.util.List;

import lombok.Data;

/**
 */
@Data
public final class FileActivityModel implements Parcelable {
    private Bus eventBus;
    private int position;
    private int folderId;
    private String folderName;
    private List<MediaFileModel> mediaFileModelList;
    private MediaMode mediaMode;

    public FileActivityModel() {
    }

    public FileActivityModel(Bus eventBus) {
        this.eventBus = eventBus;
    }

    protected FileActivityModel(Parcel in) {
        position = in.readInt();
        folderId = in.readInt();
        folderName = in.readString();
        mediaMode = MediaMode.valueOf(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(position);
        dest.writeInt(folderId);
        dest.writeString(folderName);
        dest.writeTypedList(mediaFileModelList);
        dest.writeString(mediaMode.name());
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
