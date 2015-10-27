package com.battleshippark.bsp_gallery.activity.folders;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.media.MediaController;
import com.battleshippark.bsp_gallery.pref.SharedPreferenceController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FoldersActivity extends AppCompatActivity {
    /* */

    /* View */

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.listview)
    RecyclerView listview;

    /* Controller */
    private MediaController mediaController;

    /* */
    private FoldersAdapter adapter;
    private FoldersItemDecoration decoration;
    private FoldersModel foldersModel;
    private Bus eventBus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initData();
        initUI();

        eventBus.post(Events.OnActivityCreated.EVENT);

//        mediaController.refreshDirListAsync();
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
    public void OnMediaDirectoryListUpdated(Events.OnMediaDirectoryListUpdated event) {
        Log.d("", "OnMediaDirectoryListUpdated()");
        adapter.refresh();
    }

    @Subscribe
    public void OnMediaModeUpdated(Events.OnMediaModeUpdated event) {
        Log.d("", getClass().getSimpleName() + ".OnMediaModeUpdated()");

        TextView tv = (TextView) toolbar.findViewById(R.id.media);
        switch (foldersModel.getMediaMode()) {
            case ALL:
                tv.setText(R.string.media_mode_all);
                break;
            case IMAGE:
                tv.setText(R.string.media_mode_image);
                break;
            case VIDEO:
                tv.setText(R.string.media_mode_video);
                break;
        }
        adapter.refresh();
    }

    @Subscribe
    public void OnSharedPreferenceRead(Events.OnSharedPreferenceRead event) {
        Log.d("", "OnSharedPreferenceRead()");
        foldersModel.setMediaMode(event.getModel().getMediaMode());
        mediaController.refreshDirListAsync();
    }

    private void initData() {
        eventBus = new Bus();
        eventBus.register(this);

        foldersModel = new FoldersModel(eventBus);

        adapter = new FoldersAdapter(this, foldersModel);
        decoration = new FoldersItemDecoration(foldersModel);

        mediaController = new MediaController(this, foldersModel);

        new SharedPreferenceController(this, foldersModel);
    }

    private void initUI() {
        setSupportActionBar(toolbar);
        toolbar.findViewById(R.id.media).setOnClickListener(this::showMediaPopup);

        listview.setAdapter(adapter);
        listview.setLayoutManager(new LinearLayoutManager(this));
        listview.addItemDecoration(decoration);
    }

    private void showMediaPopup(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.inflate(R.menu.menu_main_media_popup);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_media_all:
                    foldersModel.setMediaMode(FoldersModel.MEDIA_MODE.ALL);
                    break;
                case R.id.action_media_image:
                    foldersModel.setMediaMode(FoldersModel.MEDIA_MODE.IMAGE);
                    break;
                case R.id.action_media_video:
                    foldersModel.setMediaMode(FoldersModel.MEDIA_MODE.VIDEO);
                    break;
            }

            mediaController.refreshDirListAsync();
            return true;
        });
        popupMenu.show();
    }
}
