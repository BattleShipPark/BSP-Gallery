package com.battleshippark.bsp_gallery.presentation.file;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.media.MediaFileModel;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.file.MediaFileController;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VideoFileFragment extends FileFragment {
    @Bind(R.id.play)
    ImageView playImageView;

    public VideoFileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_video_file, container, false);
        ButterKnife.bind(this, v);
        v.setOnClickListener(v1 -> {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setDataAndType(Uri.fromFile(new File(model.getPath())), "video/*");
            getActivity().startActivity(sendIntent);
        });
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressBar.setVisibility(View.VISIBLE);

        ExecutorService executor = ((FragmentAccessible) getActivity()).getExecutor();

        new AsyncTask<Void, Void, MediaFileModel>() {
            @Override
            protected MediaFileModel doInBackground(Void... params) {
                MediaFileController mediaFileController = MediaFileController.create(getActivity(), 0, MediaFilterMode.ALL);

                List<MediaFileModel> mediaFileModelList = new ArrayList<>();
                mediaFileModelList.add(model);
                mediaFileModelList = mediaFileController.addMediaThumbPath(mediaFileModelList);

                return mediaFileModelList.get(0);
            }

            @Override
            protected void onPostExecute(MediaFileModel mediaFileModel) {
                model = mediaFileModel;

                if (TextUtils.isEmpty(model.getThumbPath())) {
                    Glide.with(getActivity()).load(R.drawable.error_100)
                            .fitCenter().into(imageView);
                    progressBar.setVisibility(View.GONE);
                    playImageView.setVisibility(View.GONE);
                } else {
                    Glide.with(getActivity()).load(new File(model.getThumbPath()))
                            .fitCenter().error(R.drawable.error_100)
                            .listener(new RequestListener<File, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    e.printStackTrace();
                                    progressBar.setVisibility(View.GONE);
                                    playImageView.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    progressBar.setVisibility(View.GONE);
                                    playImageView.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(imageView);
                }
            }
        }.executeOnExecutor(executor);
    }
}
