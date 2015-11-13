package com.battleshippark.bsp_gallery.activity.files;

import android.os.Parcel;
import android.os.Parcelable;

import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.media.MediaFileModel;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import lombok.Data;

/**
 */
@Data
public final class FilesActivityModel implements Parcelable {
    private Bus eventBus;
    private int folderId;
    private String folderName;
    private List<MediaFileModel> mediaFileModelList;
    private MediaFilterMode mediaFilterMode;

    public FilesActivityModel() {
    }

    public FilesActivityModel(Bus eventBus) {
        this.eventBus = eventBus;
    }

    protected FilesActivityModel(Parcel in) {
        folderId = in.readInt();
        folderName = in.readString();
        /* not save mediaFileModelList because I don't use it through Parcel*/
        mediaFilterMode = MediaFilterMode.valueOf(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(folderId);
        dest.writeString(folderName);
        dest.writeString(mediaFilterMode.name());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Subscribe
    public void OnActivityCreated(Events.OnActivityCreated event) {
//        eventBus.register(this);
    }

    @Subscribe
    public void OnActivityDestroyed(Events.OnActivityDestroyed event) {
//        eventBus.unregister(this);
    }

    public void setMediaFileModelList(List<MediaFileModel> modelList) {
        mediaFileModelList = modelList;

        eventBus.post(Events.OnMediaFileListUpdated.EVENT);
    }

    public static final Creator<FilesActivityModel> CREATOR = new Creator<FilesActivityModel>() {
        @Override
        public FilesActivityModel createFromParcel(Parcel in) {
            return new FilesActivityModel(in);
        }

        @Override
        public FilesActivityModel[] newArray(int size) {
            return new FilesActivityModel[size];
        }
    };
}
