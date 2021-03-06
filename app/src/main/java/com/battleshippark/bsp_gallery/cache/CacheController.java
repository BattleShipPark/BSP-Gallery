package com.battleshippark.bsp_gallery.cache;

import android.content.Context;

import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * 폴더 목록에서 사용하는 캐시를 관리한다
 */
public class CacheController {
    private final Context context;

    public CacheController(Context context) {
        this.context = context;
    }

    public void writeCache(MediaFilterMode mediaFilterMode, List<MediaFolderModel> models) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(_realm -> {
            _realm.where(FoldersCacheModel.class).equalTo("mediaFilterMode", mediaFilterMode.name()).findAll().deleteAllFromRealm();

            FoldersCacheModel cacheModel = _realm.createObject(FoldersCacheModel.class);
            cacheModel.setMediaFilterMode(mediaFilterMode.name());
            for (MediaFolderModel model : models) {
                cacheModel.getFolderModels().add(model);
            }
        });
        realm.close();
    }

    public List<MediaFolderModel> readCache(MediaFilterMode mediaFilterMode) {
        Realm realm = Realm.getDefaultInstance();

        RealmQuery<FoldersCacheModel> query = realm.where(FoldersCacheModel.class).equalTo("mediaFilterMode", mediaFilterMode.name());
        FoldersCacheModel foldersCacheModel = query.findFirst();

        List<MediaFolderModel> results = new ArrayList<>();
        if (query.count() != 0) {
            //noinspection Convert2streamapi
            for (MediaFolderModel model : foldersCacheModel.getFolderModels())
                results.add(model.copy());
        }

        realm.close();

        return results;
    }

    public void clear() {
        Realm r = Realm.getDefaultInstance();
        r.executeTransaction(realm -> realm.delete(FoldersCacheModel.class));
        r.close();
    }
}
