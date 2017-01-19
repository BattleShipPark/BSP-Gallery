package com.battleshippark.bsp_gallery.data.cache;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.battleshippark.bsp_gallery.BlankActivity;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import junit.framework.Assert;

import org.junit.Before;
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

    private CacheController controller;

    @Before
    public void setUp() {
        controller = new CacheController(rule.getActivity());
        controller.clear();
    }

    @Test
    public void testReadCache_fromEmpty() {
        List<MediaFolderModel> newModels = controller.readCache(MediaFilterMode.ALL);
        Assert.assertTrue(newModels.isEmpty());
    }

    @Test
    public void testReadWriteCache_All() {
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
        controller.writeCache(MediaFilterMode.ALL, models);

        List<MediaFolderModel> newModels = controller.readCache(MediaFilterMode.ALL);

        Assert.assertEquals(models, newModels);
    }

    @Test
    public void testReadWriteCache_Image() {
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
        controller.writeCache(MediaFilterMode.IMAGE, models);

        List<MediaFolderModel> newModels = controller.readCache(MediaFilterMode.IMAGE);

        Assert.assertEquals(models, newModels);
    }

    @Test
    public void testReadWriteCache_Video() {
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
        controller.writeCache(MediaFilterMode.VIDEO, models);

        List<MediaFolderModel> newModels = controller.readCache(MediaFilterMode.VIDEO);

        Assert.assertEquals(models, newModels);
    }

    @Test
    public void testReadWriteCache_together() {
        MediaFolderModel mediaFolderModelAll1 = new MediaFolderModel();
        mediaFolderModelAll1.setId(1);
        mediaFolderModelAll1.setCoverMediaId(2);
        mediaFolderModelAll1.setCoverThumbPath("3");
        mediaFolderModelAll1.setName("4");
        mediaFolderModelAll1.setCount(5);
        mediaFolderModelAll1.setCoverMediaType(6);

        MediaFolderModel mediaFolderModelAll2 = new MediaFolderModel();
        mediaFolderModelAll2.setId(2);
        mediaFolderModelAll2.setCoverMediaId(3);
        mediaFolderModelAll2.setCoverThumbPath("4");
        mediaFolderModelAll2.setName("5");
        mediaFolderModelAll2.setCount(6);
        mediaFolderModelAll2.setCoverMediaType(7);

        List<MediaFolderModel> allModels = Arrays.asList(mediaFolderModelAll1, mediaFolderModelAll2);
        controller.writeCache(MediaFilterMode.ALL, allModels);

        /* */

        MediaFolderModel mediaFolderModelImage1 = new MediaFolderModel();
        mediaFolderModelImage1.setId(2);
        mediaFolderModelImage1.setCoverMediaId(3);
        mediaFolderModelImage1.setCoverThumbPath("4");
        mediaFolderModelImage1.setName("5");
        mediaFolderModelImage1.setCount(6);
        mediaFolderModelImage1.setCoverMediaType(7);

        MediaFolderModel mediaFolderModelImage2 = new MediaFolderModel();
        mediaFolderModelImage2.setId(1);
        mediaFolderModelImage2.setCoverMediaId(2);
        mediaFolderModelImage2.setCoverThumbPath("3");
        mediaFolderModelImage2.setName("4");
        mediaFolderModelImage2.setCount(5);
        mediaFolderModelImage2.setCoverMediaType(6);

        List<MediaFolderModel> imageModels = Arrays.asList(mediaFolderModelImage1, mediaFolderModelImage2);
        controller.writeCache(MediaFilterMode.IMAGE, imageModels);

        /* */

        MediaFolderModel mediaFolderModelVideo1 = new MediaFolderModel();
        mediaFolderModelVideo1.setId(3);
        mediaFolderModelVideo1.setCoverMediaId(4);
        mediaFolderModelVideo1.setCoverThumbPath("5");
        mediaFolderModelVideo1.setName("6");
        mediaFolderModelVideo1.setCount(7);
        mediaFolderModelVideo1.setCoverMediaType(8);

        MediaFolderModel mediaFolderModelVideo2 = new MediaFolderModel();
        mediaFolderModelVideo2.setId(1);
        mediaFolderModelVideo2.setCoverMediaId(2);
        mediaFolderModelVideo2.setCoverThumbPath("3");
        mediaFolderModelVideo2.setName("4");
        mediaFolderModelVideo2.setCount(5);
        mediaFolderModelVideo2.setCoverMediaType(6);

        List<MediaFolderModel> videoModels = Arrays.asList(mediaFolderModelVideo1, mediaFolderModelVideo2);
        controller.writeCache(MediaFilterMode.VIDEO, videoModels);

        /* */

        List<MediaFolderModel> newModels = controller.readCache(MediaFilterMode.ALL);
        Assert.assertEquals(allModels, newModels);

        newModels = controller.readCache(MediaFilterMode.IMAGE);
        Assert.assertEquals(imageModels, newModels);

        newModels = controller.readCache(MediaFilterMode.VIDEO);
        Assert.assertEquals(videoModels, newModels);
    }
}