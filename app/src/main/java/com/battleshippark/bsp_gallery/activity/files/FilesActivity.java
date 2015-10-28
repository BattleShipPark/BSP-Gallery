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

import com.battleshippark.bsp_gallery.Consts;
import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.activity.folders.FoldersModel;
import com.battleshippark.bsp_gallery.media.MediaController;
import com.battleshippark.bsp_gallery.media.MediaDirectoryModel;
import com.battleshippark.bsp_gallery.media.MediaMode;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FilesActivity extends AppCompatActivity {
    /* */
    private FilesModel model;

    /* View */
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.listview)
    RecyclerView listview;

    /* Controller */
    private MediaController mediaController;

    /* */
    private FilesAdapter adapter;
    private FilesItemDecoration decoration;
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
        Log.d("", "OnMediaFileListUpdated()");
        adapter.refresh();
    }

    public static Intent createIntent(Context context, FoldersModel foldersModel, MediaDirectoryModel mediaDirectoryModel) {
        Intent i = new Intent(context, FilesActivity.class);
        i.putExtra(Consts.KEY_DIR_ID, mediaDirectoryModel.getId());
        i.putExtra(Consts.KEY_MEDIA_MODE, foldersModel.getMediaMode().name());

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
        decoration = new FilesItemDecoration(model);

        mediaController = new MediaController(this);
    }

    private FilesModel parseBundle(Bundle bundle) {
        FilesModel model = new FilesModel();

        model.setDirId(bundle.getInt(Consts.KEY_DIR_ID, 0));

        String mode = bundle.getString(Consts.KEY_MEDIA_MODE);
        model.setMediaMode(MediaMode.valueOf(mode));

        return model;
    }

    private FilesModel parseIntent() {
        FilesModel model = new FilesModel();

        model.setDirId(getIntent().getIntExtra(Consts.KEY_DIR_ID, 0));

        String mode = getIntent().getStringExtra(Consts.KEY_MEDIA_MODE);
        model.setMediaMode(MediaMode.valueOf(mode));

        return model;
    }


    private void initUI() {
        setSupportActionBar(toolbar);

        listview.setAdapter(adapter);
        listview.setLayoutManager(new GridLayoutManager(this, 3));
        listview.addItemDecoration(decoration);
    }
}
