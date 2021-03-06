package com.battleshippark.bsp_gallery.activity.folders;

import android.Manifest;
import android.app.AlertDialog;
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

import com.battleshippark.bsp_gallery.EventBusHelper;
import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.media.MediaController;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.pref.SharedPreferenceController;
import com.battleshippark.bsp_gallery.pref.SharedPreferenceModel;
import com.squareup.otto.Subscribe;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FoldersActivity extends AppCompatActivity {
    /* */

    /* View */
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.toolbar_progress)
    View progress;

    @Bind(R.id.listview)
    RecyclerView listview;

    /* Controller */
    private MediaController mediaController;

    /* */
    private FoldersAdapter adapter;
    private FoldersItemDecoration decoration;
    private FoldersActivityModel model;
    private RxPermissions rxPermissions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders);
        ButterKnife.bind(this);

        initData();
        initUI();
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
    public void OnMediaFolderListUpdated(Events.OnMediaFolderListUpdated event) {
        Log.d("", "OnMediaFolderListUpdated(): " + event);

        TextView tv = (TextView) toolbar.findViewById(R.id.media);
        switch (model.getMediaFilterMode()) {
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
        progress.setVisibility(View.GONE);
    }

    private void initData() {
        EventBusHelper.eventBus.register(this);

        model = new FoldersActivityModel();

        adapter = new FoldersAdapter(this, model);
        decoration = new FoldersItemDecoration(model);

        mediaController = new MediaController(this);

        rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            MediaFilterMode mode = SharedPreferenceController.instance()
                                    .readMediaMode();
                            model.setMediaFilterMode(mode);
                            mediaController.refreshFolderListAsync(model);
                            progress.post(() -> progress.setVisibility(View.VISIBLE));

                            SharedPreferenceController.instance().writeMediaMode(mode);
                        });
                    } else {
                        new AlertDialog.Builder(FoldersActivity.this)
                                .setMessage("You should grant READ_EXTERNAL_STORAGE").show();
                    }
                });
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
                    model.setMediaFilterMode(MediaFilterMode.ALL);
                    break;
                case R.id.action_media_image:
                    model.setMediaFilterMode(MediaFilterMode.IMAGE);
                    break;
                case R.id.action_media_video:
                    model.setMediaFilterMode(MediaFilterMode.VIDEO);
                    break;
            }

            mediaController.refreshFolderListAsync(model);
            return true;
        });
        popupMenu.show();
    }
}
