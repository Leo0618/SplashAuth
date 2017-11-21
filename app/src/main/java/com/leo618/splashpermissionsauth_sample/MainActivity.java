package com.leo618.splashpermissionsauth_sample;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.leo618.splashpermissionsauth.SplashAuthUI;

/**
 * function: 测试授权页面
 * <p>
 * Created by Leo on 2017/11/21.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void auth(View view) {
        SplashAuthUI.launch(MainActivity.this, 100, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA,
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            String result = resultCode == RESULT_OK ? "授权成功" : "授权失败";
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        }
    }
}
