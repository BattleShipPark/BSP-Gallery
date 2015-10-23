package com.battleshippark.bsp_gallery;

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
     * 이 이벤트를 받은 후에 화면을 갱신해야 한다
     */
    public enum OnMediaDirectoryListUpdated {
        EVENT
    }
}
