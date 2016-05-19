package com.battleshippark.bsp_gallery.activity.file;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.media.MediaFileModel;

import org.parceler.Parcels;

import butterknife.Bind;

public class FileFragment extends Fragment {
    private static final String KEY_MODEL = "model";

    protected MediaFileModel model;

    @Bind(R.id.image)
    ImageView imageView;

    @Bind(R.id.progress)
    ProgressBar progressBar;

    public FileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = getArguments().getParcelable(KEY_MODEL);
    }

    public static Fragment newInstance(MediaFileModel mediaFileModel) {
        FileFragment fragment;
        if (mediaFileModel.getMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            fragment = new ImageFileFragment();
        } else {
            fragment = new VideoFileFragment();
        }
        Bundle args = new Bundle();
        args.putParcelable(KEY_MODEL, Parcels.wrap(mediaFileModel));
        fragment.setArguments(args);
        return fragment;
    }
}
