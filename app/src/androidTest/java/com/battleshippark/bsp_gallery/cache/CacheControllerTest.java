package com.battleshippark.bsp_gallery.cache;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.battleshippark.bsp_gallery.BlankActivity;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

/**
 */
@RunWith(AndroidJUnit4.class)
public class CacheControllerTest {
    @Rule
    public ActivityTestRule<BlankActivity> rule = new ActivityTestRule<>(BlankActivity.class);

    @Test
    public void testReadWriteCache_All() {
        Context context = rule.getActivity();

        MediaFolderModel mediaFolderModel1 = new MediaFolderModel();
        mediaFolderModel1.setId(1);
        mediaFolderModel1.setCoverMediaId(2);
        mediaFolderModel1.setCoverThumbPath("3");
        mediaFolderModel1.setName("4");
        mediaFolderModel1.setCount(5);
        mediaFolderModel1.setCoverMediaType(6);

        MediaFolderModel mediaFolderModel2 = new MediaFolderModel();
        mediaFolderModel2.setId(2);
        mediaFolderModel2.setCoverMediaId(3);
        mediaFolderModel2.setCoverThumbPath("4");
        mediaFolderModel2.setName("5");
        mediaFolderModel2.setCount(6);
        mediaFolderModel2.setCoverMediaType(7);

        List<MediaFolderModel> models = Arrays.asList(mediaFolderModel1, mediaFolderModel2);
        CacheController.writeCache(context, MediaFilterMode.ALL, models);

        List<MediaFolderModel> newModels = CacheController.readCache(context, MediaFilterMode.ALL);

        Assert.assertEquals(models, newModels);
    }

    @Test
    public void testReadWriteCache_Image() {
        Context context = rule.getActivity();

        MediaFolderModel mediaFolderModel1 = new MediaFolderModel();
        mediaFolderModel1.setId(2);
        mediaFolderModel1.setCoverMediaId(3);
        mediaFolderModel1.setCoverThumbPath("4");
        mediaFolderModel1.setName("5");
        mediaFolderModel1.setCount(6);
        mediaFolderModel1.setCoverMediaType(7);

        MediaFolderModel mediaFolderModel2 = new MediaFolderModel();
        mediaFolderModel2.setId(1);
        mediaFolderModel2.setCoverMediaId(2);
        mediaFolderModel2.setCoverThumbPath("3");
        mediaFolderModel2.setName("4");
        mediaFolderModel2.setCount(5);
        mediaFolderModel2.setCoverMediaType(6);

        List<MediaFolderModel> models = Arrays.asList(mediaFolderModel1, mediaFolderModel2);
        CacheController.writeCache(context, MediaFilterMode.IMAGE, models);

        List<MediaFolderModel> newModels = CacheController.readCache(context, MediaFilterMode.IMAGE);

        Assert.assertEquals(models, newModels);
    }

    @Test
    public void testReadWriteCache_Video() {
        Context context = rule.getActivity();

        MediaFolderModel mediaFolderModel1 = new MediaFolderModel();
        mediaFolderModel1.setId(3);
        mediaFolderModel1.setCoverMediaId(4);
        mediaFolderModel1.setCoverThumbPath("5");
        mediaFolderModel1.setName("6");
        mediaFolderModel1.setCount(7);
        mediaFolderModel1.setCoverMediaType(8);

        MediaFolderModel mediaFolderModel2 = new MediaFolderModel();
        mediaFolderModel2.setId(1);
        mediaFolderModel2.setCoverMediaId(2);
        mediaFolderModel2.setCoverThumbPath("3");
        mediaFolderModel2.setName("4");
        mediaFolderModel2.setCount(5);
        mediaFolderModel2.setCoverMediaType(6);

        List<MediaFolderModel> models = Arrays.asList(mediaFolderModel1, mediaFolderModel2);
        CacheController.writeCache(context, MediaFilterMode.VIDEO, models);

        List<MediaFolderModel> newModels = CacheController.readCache(context, MediaFilterMode.VIDEO);

        Assert.assertEquals(models, newModels);
    }
}