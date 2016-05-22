package com.battleshippark.bsp_gallery;

/**
 */
public class Events {
    /**
     * MediaStore에서 디렉토리 구조를 읽어서, 모델을 갱신했다.
     * 화면갱신이나 다른 컨트롤러들의 작업을 할 수 있다
     */
    public enum OnMediaFolderListUpdated {
        EVENT
    }

    /**
     * MediaStore에서 특정 디렉토리의 파일을 읽어서, 모델을 갱신했다.
     * 화면갱신이나 다른 컨트롤러들의 작업을 할 수 있다
     */
    public enum OnMediaFileListUpdated {
        EVENT
    }
}
