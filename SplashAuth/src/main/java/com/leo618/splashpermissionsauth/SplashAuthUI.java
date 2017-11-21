package com.leo618.splashpermissionsauth;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.leo618.mpermission.AfterPermissionGranted;
import com.leo618.mpermission.MPermission;
import com.leo618.mpermission.MPermissionSettingsDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * function:权限校验启动页
 * <p>1.调用launch启动检查
 * <p>2.复写onActivityResult方法，根据resultCode获取结果状态
 * <p>  {@link android.app.Activity#RESULT_OK} :全部授权成功；{@link android.app.Activity#RESULT_CANCELED} :全部授权未成功；
 *
 * <p>
 * Created by Leo on 2017/11/21.
 */
public class SplashAuthUI extends AppCompatActivity implements MPermission.PermissionCallbacks {
    /** a message explaining why the application needs this set of permissions */
    public static String MSG_HINT_FUNCTIONS     = "请授权以获取更完善的体验";
    /** go settings and open permissions manually */
    public static String MSG_BUTTON_TXT_GO_OPEN = "去开启";
    /** quit current permissions auth. */
    public static String MSG_BUTTON_TXT_GO_QUIT = "退出";

    /**
     * 启动授权操作
     *
     * @param activity    上下文
     * @param requestCode 请求码
     * @param permissions 需要授权的权限集合
     */
    public static void launch(Activity activity, int requestCode, String[] permissions) {
        Intent            intent         = new Intent(activity, SplashAuthUI.class);
        ArrayList<String> permissionList = new ArrayList<>();
        Collections.addAll(permissionList, permissions);
        intent.putStringArrayListExtra("permissions", permissionList);
        activity.startActivityForResult(intent, requestCode);
    }

    private static final int CODE_REQ_INIT_PERS = 0x101;
    private String[] mPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(0, 0);
        getWindow().setDimAmount(0f);
        ArrayList<String> permissions = getIntent().getStringArrayListExtra("permissions");
        mPermissions = permissions.toArray(new String[permissions.size()]);
        checkInitPermissions();
    }

    /**
     * 在启动页添加进去app必要的权限确认
     */
    @AfterPermissionGranted(CODE_REQ_INIT_PERS)
    private void checkInitPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (MPermission.hasPermissions(this, mPermissions)) {
                handleAfterPermissions(true);
            } else {
                MPermission.requestPermissions(this, SplashAuthUI.MSG_HINT_FUNCTIONS, CODE_REQ_INIT_PERS, mPermissions);
            }
        } else {
            handleAfterPermissions(true);
        }
    }

    private void handleAfterPermissions(boolean success) {
        setResult(success ? RESULT_OK : RESULT_CANCELED, getIntent());
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {}

    @SuppressWarnings("deprecation")
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        new MPermissionSettingsDialog.Builder(this)
                .setPositiveButton(SplashAuthUI.MSG_BUTTON_TXT_GO_OPEN)
                .setNegativeButton(SplashAuthUI.MSG_BUTTON_TXT_GO_QUIT, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        handleAfterPermissions(false);
                    }
                }).build().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //从设置应用详情页返回
        if (requestCode == MPermissionSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            checkInitPermissions();
        }
    }

    @Override
    public void onBackPressed() {}

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(0, 0);
    }
}
