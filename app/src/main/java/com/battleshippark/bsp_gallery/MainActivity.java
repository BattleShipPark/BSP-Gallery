package com.battleshippark.bsp_gallery;

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

import com.battleshippark.bsp_gallery.media.MediaController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    /* */

    /* View */

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.listview)
    RecyclerView listview;

    /* Controller */
    private MediaController mediaController;

    /* */
    private MainAdapter adapter;
    private MainItemDecoration decoration;
    private MainModel mainModel;
    private Bus eventBus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initData();
        initUI();

        eventBus.post(Events.OnActivityCreated.EVENT);

        mediaController.refreshDirListAsync();
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
        Log.d("", "OnMediaModeUpdated()");
        adapter.refresh();
    }

    private void initData() {
        eventBus = new Bus();
        eventBus.register(this);

        mainModel = new MainModel(eventBus);

        adapter = new MainAdapter(this, mainModel);
        decoration = new MainItemDecoration(mainModel);

        mediaController = new MediaController(this, mainModel);
    }

    private void initUI() {
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        //		myToolbar.setTitle("Gallery");
        //		myToolbar.setTitleTextColor(0xffffff);
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
                    mainModel.setMediaMode(MainModel.MEDIA_MODE.ALL);
                    break;
                case R.id.action_media_image:
                    mainModel.setMediaMode(MainModel.MEDIA_MODE.IMAGE);
                    break;
                case R.id.action_media_video:
                    mainModel.setMediaMode(MainModel.MEDIA_MODE.VIDEO);
                    break;
            }

            mediaController.refreshDirListAsync();
            return true;
        });
        popupMenu.show();
    }
}
