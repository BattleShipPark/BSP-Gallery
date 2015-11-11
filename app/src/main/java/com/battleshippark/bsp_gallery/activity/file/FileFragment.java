package com.battleshippark.bsp_gallery.activity.file;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.media.MediaFileModel;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FileFragment extends Fragment {
    private static final String KEY_MODEL = "model";

    private MediaFileModel model;

    @Bind(R.id.image)
    ImageView imageView;

    public FileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = getArguments().getParcelable(KEY_MODEL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_file, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        Log.i("DEBUG", model.getPath());
        Picasso.with(getActivity()).load(new File(model.getPath())).resize(1080, 1920).centerInside().into(imageView);
    }

    public static Fragment newInstance(MediaFileModel mediaFileModel) {
        FileFragment fragment = new FileFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_MODEL, mediaFileModel);
        fragment.setArguments(args);
        return fragment;
    }
}
