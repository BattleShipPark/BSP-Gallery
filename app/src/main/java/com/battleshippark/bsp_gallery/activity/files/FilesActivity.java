package com.battleshippark.bsp_gallery.activity.files;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.battleshippark.bsp_gallery.EventBusHelper;
import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.domain.Loader;
import com.battleshippark.bsp_gallery.domain.MediaControllerFactory;
import com.battleshippark.bsp_gallery.domain.MediaControllerFactoryImpl;
import com.battleshippark.bsp_gallery.media.MediaController;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.ButterKnife;
import lombok.AllArgsConstructor;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FilesActivity extends AppCompatActivity {
    private static final String KEY_PARAM = "param";

    /* */
    private FilesActivityModel model;

    /* View */
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.toolbar_back)
    View toolbarBack;

    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;

    @Bind(R.id.toolbar_progress)
    View toolbarProgress;

    @Bind(R.id.listview)
    RecyclerView listview;

    /* Controller */
    private FilesPresenter presenter;
    private MediaController mediaController;

    /* */
    private FilesAdapter adapter;

    @BindInt(R.integer.files_column_count)
    int FILES_COLUMN_COUNT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        ButterKnife.bind(this);

        initData(savedInstanceState);
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.loadList(model);
//        mediaController.refreshFileListWithThumbAsync(this, model);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Param param = new Param(model.getFolderId(), model.getFolderName(), model.getMediaFilterMode());
        outState.putParcelable(KEY_PARAM, param);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        EventBusHelper.eventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void OnMediaFileListUpdated(Events.OnMediaFileListUpdated event) {
        Log.d("", getClass().getSimpleName() + ".OnMediaFileListUpdated()");

        adapter.refresh();

        toolbarProgress.setVisibility(View.GONE);
    }

    public static Intent createIntent(Context context, MediaFilterMode filterMode, MediaFolderModel mediaFolderModel) {
        Param param = new Param(mediaFolderModel.getId(), mediaFolderModel.getName(), filterMode);
        Intent i = new Intent(context, FilesActivity.class);
        i.putExtra(KEY_PARAM, param);
        return i;
    }

    private void initData(Bundle savedInstanceState) {
        EventBusHelper.eventBus.register(this);

        if (savedInstanceState != null) {
            model = parseBundle(savedInstanceState);
        } else {
            model = parseIntent();
        }

        adapter = new FilesAdapter(this, model);

        MediaControllerFactory mediaControllerFactory = new MediaControllerFactoryImpl(this);
        Scheduler scheduler = Schedulers.io();
        Scheduler postScheduler = AndroidSchedulers.mainThread();

        Loader filesLoader = new FilesLoader(mediaControllerFactory, scheduler, postScheduler);

        presenter = new FilesPresenter(this, filesLoader);
        mediaController = new MediaController(this);
    }

    private FilesActivityModel parseBundle(Bundle bundle) {
        FilesActivityModel model = new FilesActivityModel();

        Param param = bundle.getParcelable(KEY_PARAM);
        model.setFolderId(param.id);
        model.setFolderName(param.name);
        model.setMediaFilterMode(param.mode);

        return model;
    }

    private FilesActivityModel parseIntent() {
        FilesActivityModel model = new FilesActivityModel();

        Param param = getIntent().getParcelableExtra(KEY_PARAM);
        model.setFolderId(param.id);
        model.setFolderName(param.name);
        model.setMediaFilterMode(param.mode);

        return model;
    }


    private void initUI() {
        setSupportActionBar(toolbar);

        toolbarBack.setOnClickListener(v -> onBackPressed());

        toolbarTitle.setText(model.getFolderName());
        toolbarTitle.setOnClickListener(v -> onBackPressed());

        listview.setAdapter(adapter);
        listview.setLayoutManager(new GridLayoutManager(this, FILES_COLUMN_COUNT));
//        listview.addItemDecoration(decoration);
    }

    @AllArgsConstructor
    private static class Param implements Parcelable {
        int id;
        String name;
        MediaFilterMode mode;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.name);
            dest.writeInt(this.mode == null ? -1 : this.mode.ordinal());
        }

        protected Param(Parcel in) {
            this.id = in.readInt();
            this.name = in.readString();
            int tmpMode = in.readInt();
            this.mode = tmpMode == -1 ? null : MediaFilterMode.values()[tmpMode];
        }

        public static final Parcelable.Creator<Param> CREATOR = new Parcelable.Creator<Param>() {
            @Override
            public Param createFromParcel(Parcel source) {
                return new Param(source);
            }

            @Override
            public Param[] newArray(int size) {
                return new Param[size];
            }
        };
    }
}
