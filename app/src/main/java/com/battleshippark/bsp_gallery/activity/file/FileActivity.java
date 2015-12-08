package com.battleshippark.bsp_gallery.activity.file;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.activity.files.FilesActivityModel;
import com.battleshippark.bsp_gallery.media.MediaController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FileActivity extends AppCompatActivity implements FragmentAccessible {
    private static final String KEY_POSITION = "position";
    private static final String KEY_FILES_ACTIVITY_MODEL = "filesActivityModel";
    private static final String KEY_MODEL = "model";

    /* */
    private FileActivityModel model;

    /* View */
    @Bind(R.id.viewpager)
    ViewPager viewPager;

    /* Controller */

    /* */
    private FileAdapter adapter;

    private Bus eventBus;
    private MediaController mediaController;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        ButterKnife.bind(this);

        initData(savedInstanceState);
        initUI();

        eventBus.post(Events.OnActivityCreated.EVENT);

        mediaController.refreshFileListAsync(this, model);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        model.setPosition(viewPager.getCurrentItem());
        outState.putParcelable(KEY_MODEL, model);
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
        Log.d("", getClass().getSimpleName() + ".OnMediaFileListUpdated()");

        adapter.refresh();

        viewPager.setCurrentItem(model.getPosition());
    }

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    public static Intent createIntent(Context context, int position, FilesActivityModel filesActivityModel) {
        Intent i = new Intent(context, FileActivity.class);
        i.putExtra(KEY_POSITION, position);
        i.putExtra(KEY_FILES_ACTIVITY_MODEL, filesActivityModel);
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

        adapter = new FileAdapter(getSupportFragmentManager(), model);

        mediaController = new MediaController(this, eventBus);

        executor = Executors.newCachedThreadPool();
    }

    private FileActivityModel parseBundle(Bundle bundle) {
        return bundle.getParcelable(KEY_MODEL);
    }


    private FileActivityModel parseIntent() {
        FileActivityModel model = new FileActivityModel();

        model.setPosition(getIntent().getIntExtra(KEY_POSITION, 0));

        FilesActivityModel filesActivityModel = getIntent().getParcelableExtra(KEY_FILES_ACTIVITY_MODEL);
        model.setFolderId(filesActivityModel.getFolderId());
        model.setFolderName(filesActivityModel.getFolderName());
        model.setMediaFilterMode(filesActivityModel.getMediaFilterMode());

        return model;
    }

    private void initUI() {
        viewPager.setAdapter(adapter);
    }
}
