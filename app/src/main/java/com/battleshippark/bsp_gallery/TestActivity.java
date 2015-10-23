package com.battleshippark.bsp_gallery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.battleshippark.bsp_gallery.test.MediaControllerTest;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 */
public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.mediaControllerTest)
    public void onClickMediaControllerTest(View v) {
        MediaControllerTest.디렉토리_목록(this);
    }
}
