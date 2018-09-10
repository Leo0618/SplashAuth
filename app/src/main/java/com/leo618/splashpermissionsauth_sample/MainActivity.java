package com.leo618.splashpermissionsauth_sample;

import android.Manifest;
import android.content.Intent;
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

    private String[] perms = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
    };

    //1.校验是否授权
    public void check(View view) {
        boolean checkResult = SplashAuthUI.check(this, perms);
        Toast.makeText(this, checkResult ? "全部已授权" : "包含未授权", Toast.LENGTH_SHORT).show();
    }

    //2.请求授权，在onActivityResult中处理结果
    public void request(View view) {
        SplashAuthUI.launch(this, 100, perms);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100) {
            String result = resultCode == RESULT_OK ? "授权成功" : "授权失败";
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        }
    }

    //3.校验并请求授权，若存在未授权则启动申请授权，无法处理结果,需要用户再次点击操作触发已授权后的操作
    public void checkAndRequest(View view) {
        boolean checkResult = SplashAuthUI.checkAndLaunch(this, perms, true);
        Toast.makeText(this, checkResult ? "全部已授权" : "包含未授权", Toast.LENGTH_SHORT).show();
    }
}
