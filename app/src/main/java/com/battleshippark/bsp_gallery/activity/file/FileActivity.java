package com.battleshippark.bsp_gallery.activity.file;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.battleshippark.bsp_gallery.BspApplication;
import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.media.MediaFileModel;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FileActivity extends AppCompatActivity {
    private static final String KEY_POSITION = "position";
    private static final String KEY_LIST = "list";

    /* */
    private FileModel model;

    /* View */
    @Bind(R.id.viewpager)
    ViewPager viewPager;

    /* Controller */

    /* */
    private FileAdapter adapter;

    private Bus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        ButterKnife.bind(this);

        initData(savedInstanceState);
        initUI();

        eventBus.post(Events.OnActivityCreated.EVENT);

        viewPager.setCurrentItem(model.getPosition());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_POSITION, model.getPosition());

        outState.putParcelableArrayList(KEY_LIST, (ArrayList<? extends Parcelable>) model.getMediaFileModelList());
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

    public static Intent createIntent(Context context, int position, List<MediaFileModel> mediaFileModelList) {
        Intent i = new Intent(context, FileActivity.class);
        i.putExtra(KEY_POSITION, position);
        BspApplication.TempStorage.mediaFileModelList = mediaFileModelList;
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

        adapter = new FileAdapter(getSupportFragmentManager(), model.getMediaFileModelList());
//        decoration = new FilesItemDecoration(model);

//        mediaController = new MediaController(this);
    }

    private FileModel parseBundle(Bundle bundle) {
        FileModel model = new FileModel();

        model.setPosition(bundle.getInt(KEY_POSITION, 0));
        model.setMediaFileModelList(bundle.getParcelableArrayList(KEY_LIST));

        return model;
    }

    private FileModel parseIntent() {
        FileModel model = new FileModel();

        model.setPosition(getIntent().getIntExtra(KEY_POSITION, 0));
        model.setMediaFileModelList(BspApplication.TempStorage.mediaFileModelList);

        return model;
    }


    private void initUI() {
        viewPager.setAdapter(adapter);
    }
}
