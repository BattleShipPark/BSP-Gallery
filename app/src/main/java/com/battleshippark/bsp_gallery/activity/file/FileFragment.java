package com.battleshippark.bsp_gallery.activity.file;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.media.MediaFileModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FileFragment extends Fragment {
    private static final String KEY_MODEL = "model";

    private MediaFileModel model;

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
        progressBar.setVisibility(View.VISIBLE);

        Picasso.with(getActivity()).load(new File(model.getPath()))
                .resize(getContext().getResources().getDisplayMetrics().widthPixels,
                        getContext().getResources().getDisplayMetrics().heightPixels)
                .centerInside().error(R.drawable.error_100).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public static Fragment newInstance(MediaFileModel mediaFileModel) {
        FileFragment fragment = new FileFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_MODEL, mediaFileModel);
        fragment.setArguments(args);
        return fragment;
    }
}
