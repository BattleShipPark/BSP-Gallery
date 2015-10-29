package com.battleshippark.bsp_gallery.media;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

/**
 */
@Data
public class MediaFileModel implements Parcelable {
    private int id;
    private String name;
    private int mediaType; /* MediaStore.File.FileColumns.MEDIA_TYPE_? */
    private String path;
    private String thumbPath;

    public MediaFileModel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        mediaType = in.readInt();
        path = in.readString();
        thumbPath = in.readString();
    }

    public MediaFileModel() {
    }

    public MediaFileModel copy() {
        MediaFileModel result = new MediaFileModel();
        result.setId(id);
        result.setName(name);
        result.setMediaType(mediaType);
        result.setPath(path);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(mediaType);
        dest.writeString(path);
        dest.writeString(thumbPath);
    }

    public static final Creator<MediaFileModel> CREATOR = new Creator<MediaFileModel>() {
        @Override
        public MediaFileModel createFromParcel(Parcel in) {
            return new MediaFileModel(in);
        }

        @Override
        public MediaFileModel[] newArray(int size) {
            return new MediaFileModel[size];
        }
    };
}
