package com.battleshippark.bsp_gallery.presentation.folders;

import android.Manifest;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.battleshippark.bsp_gallery.EventBusHelper;
import com.battleshippark.bsp_gallery.R;
import com.battleshippark.bsp_gallery.data.cache.CacheControllerFactory;
import com.battleshippark.bsp_gallery.data.mode.MediaFilterModeRepositoryImpl;
import com.battleshippark.bsp_gallery.domain.MediaControllerFactoryImpl;
import com.battleshippark.bsp_gallery.domain.UseCase;
import com.battleshippark.bsp_gallery.domain.folders.FilterModeLoader;
import com.battleshippark.bsp_gallery.domain.folders.FilterModeSaver;
import com.battleshippark.bsp_gallery.domain.folders.FoldersLoader;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;
import com.battleshippark.bsp_gallery.pref.SharedPreferenceController;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FoldersActivity extends AppCompatActivity implements FoldersView {
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.toolbar_progress)
    View progress;

    @Bind(R.id.listview)
    RecyclerView listview;

    private FoldersPresenter presenter;

    private FoldersAdapter adapter;
    private FoldersItemDecoration decoration;
    private MediaFilterMode mediaFilterMode;


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
        presenter.loadList(mediaFilterMode);
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

    private void initData() {
        EventBusHelper.eventBus.register(this);

        adapter = new FoldersAdapter(this);
        decoration = new FoldersItemDecoration();

        MediaFilterModeRepositoryImpl mediaFilterModeRepository = new MediaFilterModeRepositoryImpl(SharedPreferenceController.instance());
        MediaControllerFactoryImpl mediaControllerFactory = new MediaControllerFactoryImpl(this);
        CacheControllerFactory cacheControllerFactory = new CacheControllerFactory();
        Scheduler scheduler = Schedulers.io();
        Scheduler postScheduler = AndroidSchedulers.mainThread();

        UseCase<Void, MediaFilterMode> filerModeLoader = new FilterModeLoader(mediaFilterModeRepository, scheduler, postScheduler);
        UseCase<MediaFilterMode, MediaFilterMode> filerModeSaver = new FilterModeSaver(mediaFilterModeRepository, scheduler, postScheduler);
        UseCase<MediaFilterMode, List<MediaFolderModel>> foldersLoader = new FoldersLoader(mediaControllerFactory,
                cacheControllerFactory, scheduler, postScheduler);

        presenter = new FoldersPresenter(this, filerModeLoader, filerModeSaver, foldersLoader);

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        presenter.loadFilterMode();
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
            MediaFilterMode mode = null;
            switch (item.getItemId()) {
                case R.id.action_media_all:
                    mode = MediaFilterMode.ALL;
                    break;
                case R.id.action_media_image:
                    mode = MediaFilterMode.IMAGE;
                    break;
                case R.id.action_media_video:
                    mode = MediaFilterMode.VIDEO;
                    break;
            }

            if (mode != mediaFilterMode) {
                presenter.changeFilterMode(mode);
            }
            return true;
        });
        popupMenu.show();
    }

    @Override
    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void updateFilterMode(MediaFilterMode mediaFilterMode) {
        TextView tv = (TextView) toolbar.findViewById(R.id.media);
        switch (mediaFilterMode) {
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

        this.mediaFilterMode = mediaFilterMode;
    }

    @Override
    public void refreshList() {
        presenter.loadList(mediaFilterMode);
    }

    @Override
    public void refreshList(List<MediaFolderModel> mediaFolderModels) {
        adapter.setItems(mediaFolderModels);
        adapter.notifyDataSetChanged();
    }
}
