package com.battleshippark.bsp_gallery.activity.files;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.activity.folders.FoldersModel;
import com.battleshippark.bsp_gallery.media.MediaController;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;
import com.battleshippark.bsp_gallery.media.MediaMode;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.ButterKnife;

public class FilesActivity extends AppCompatActivity {
    private static final String KEY_MEDIA_MODE = "mediaMode";
    private static final String KEY_FOLDER_NAME = "folderName";
    private static final String KEY_FOLDER_ID = "folderId";

    /* */
    private FilesModel model;

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
    private MediaController mediaController;

    /* */
    private FilesAdapter adapter;
//    private FilesItemDecoration decoration;

    @BindInt(R.integer.files_column_count)
    int FILES_COLUMN_COUNT;


    private Bus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        ButterKnife.bind(this);

        initData(savedInstanceState);
        initUI();

        eventBus.post(Events.OnActivityCreated.EVENT);

        mediaController.refreshFileListAsync(model);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_FOLDER_ID, model.getFolderId());

        outState.putString(KEY_FOLDER_NAME, model.getFolderName());

        outState.putString(KEY_MEDIA_MODE, model.getMediaMode().name());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        eventBus.post(Events.OnActivityDestroyed.EVENT);
        eventBus.unregister(this);
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
        Log.d("DEBUG", "OnMediaFileListUpdated()");

        GridLayoutManager layoutManager = (GridLayoutManager) listview.getLayoutManager();

        if (layoutManager.findFirstVisibleItemPosition() == RecyclerView.NO_POSITION ||
                (layoutManager.findFirstVisibleItemPosition() <= adapter.getItemCount() && adapter.getItemCount() <= layoutManager.findLastVisibleItemPosition()))
            adapter.refresh();

        toolbarProgress.setVisibility(View.GONE);
    }

    public static Intent createIntent(Context context, FoldersModel foldersModel, MediaFolderModel mediaFolderModel) {
        Intent i = new Intent(context, FilesActivity.class);
        i.putExtra(KEY_FOLDER_ID, mediaFolderModel.getId());
        i.putExtra(KEY_FOLDER_NAME, mediaFolderModel.getName());
        i.putExtra(KEY_MEDIA_MODE, foldersModel.getMediaMode().name());

        return i;
    }

    private void initData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            model = parseBundle(savedInstanceState);
        } else {
            model = parseIntent();
        }

        eventBus = new Bus();
        eventBus.register(this);

        model.setEventBus(eventBus);

        adapter = new FilesAdapter(this, model);
//        decoration = new FilesItemDecoration(model);

        mediaController = new MediaController(this);
    }

    private FilesModel parseBundle(Bundle bundle) {
        FilesModel model = new FilesModel();

        model.setFolderId(bundle.getInt(KEY_FOLDER_ID, 0));

        model.setFolderName(bundle.getString(KEY_FOLDER_NAME));

        String mode = bundle.getString(KEY_MEDIA_MODE);
        model.setMediaMode(MediaMode.valueOf(mode));

        return model;
    }

    private FilesModel parseIntent() {
        FilesModel model = new FilesModel();

        model.setFolderId(getIntent().getIntExtra(KEY_FOLDER_ID, 0));

        model.setFolderName(getIntent().getStringExtra(KEY_FOLDER_NAME));

        String mode = getIntent().getStringExtra(KEY_MEDIA_MODE);
        model.setMediaMode(MediaMode.valueOf(mode));

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
}
