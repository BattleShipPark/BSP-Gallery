package com.battleshippark.bsp_gallery;

import com.battleshippark.bsp_gallery.pref.SharedPreferenceModel;

import lombok.Value;

/**
 */
public class Events {
    public enum OnActivityCreated {
        EVENT
    }

    public enum OnActivityResumed {
        EVENT
    }

    public enum OnActivityPaused {
        EVENT
    }

    public enum OnActivityDestroyed {
        EVENT
    }

    /**
     * MediaStore에서 디렉토리 구조를 읽어서, 모델을 갱신했다.
     * 화면갱신이나 다른 컨트롤러들의 작업을 할 수 있다
     */
    public enum OnMediaFolderListUpdated {
        UPDATED,
        FINISHED
    }

    /**
     * 툴바에서 미디어 모드를 변경해서 모델을 갱신했다.
     * 화면갱신이나 다른 컨트롤러들의 작업을 할 수 있다
     */
    public enum OnMediaModeUpdated {
        EVENT
    }

    /**
     * MediaStore에서 특정 디렉토리의 파일을 읽어서, 모델을 갱신했다.
     * 화면갱신이나 다른 컨트롤러들의 작업을 할 수 있다
     */
    public enum OnMediaFileListUpdated {
        EVENT
    }

    /**
     * 전체 shared preference를 읽었다
     * 이 이벤트를 받은 후에 화면을 갱신해야 한다
     * 화면갱신이나 다른 컨트롤러들의 작업을 할 수 있다
     */
    @Value
    public static class OnSharedPreferenceRead {
        public SharedPreferenceModel model;
    }
}
