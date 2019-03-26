package vip.okfood.permission;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * function:权限校验
 * <p></p>
 * <ol>
 * <li>调用launch启动检查</li>
 * <li>复写onActivityResult方法，根据resultCode获取结果状态<br/>
 * {@link android.app.Activity#RESULT_OK} :全部授权成功；{@link android.app.Activity#RESULT_CANCELED} :全部授权未成功；
 * </li>
 * <li>对于对话框按钮文字可以使用public静态变量修改
 * <ul>
 * <li>{@link SplashAuthUI#MSG_HINT_FUNCTIONS}  未授权对话框提示内容</li>
 * <li>{@link SplashAuthUI#MSG_BUTTON_TXT_GO_OPEN}  去开启按钮文案</li>
 * <li>{@link SplashAuthUI#MSG_BUTTON_TXT_GO_QUIT}  退出授权按钮文案</li>
 * </ul>
 * </ol>
 * Created by Leo on 2017/11/21.
 */
@SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
public class SplashAuthUI extends AppCompatActivity implements MPermission.PermissionCallbacks {

    /**
     * 校验授权状态
     *
     * @param activity activity
     * @param perms    权限列表数组
     *
     * @return 权限列表数组均已获取返回true, 否则返回false
     */
    public static boolean check(@NonNull Activity activity, @NonNull String[] perms) {
        return checkAndLaunch(activity, perms, false);
    }

    /**
     * 启动授权操作,可以在onActivityResult中处理结果
     * <ul>
     * <li>复写onActivityResult方法，根据resultCode获取结果状态<br/>
     * {@link android.app.Activity#RESULT_OK} :全部授权成功；{@link android.app.Activity#RESULT_CANCELED} :全部授权未成功；
     * </li>
     * </ul>
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

    /**
     * 校验授权状态
     *
     * @param activity   activity
     * @param perms      权限列表数组
     * @param needLaunch 是否需要启动申请提示，true:申请提示，false:不提示
     *
     * @return 权限列表数组均已获取返回true, 否则返回false
     */
    public static boolean checkAndLaunch(@NonNull Activity activity, @NonNull String[] perms, boolean needLaunch) {
        boolean result = true;
        if(Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            for(String perm : perms) {
                if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(activity, perm)) {
                    permissions.add(perm);
                }
            }
            if(permissions.size() > 0) {
                result = false;
                if(needLaunch) {
                    ActivityCompat.requestPermissions(activity, permissions.toArray(new String[permissions.size()]), 10000);
                }
            }
        }
        return result;
    }

    private static final int      CODE_REQ_INIT_PERS = 1001;
    private              String[] mPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(0, 0);
        getWindow().setDimAmount(0f);
        ArrayList<String> permissions = getIntent().getStringArrayListExtra("permissions");
        mPermissions = permissions.toArray(new String[permissions.size()]);
        checkInitPermissions();
    }

    @AfterPermissionGranted(CODE_REQ_INIT_PERS)
    private void checkInitPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(MPermission.hasPermissions(this, mPermissions)) {
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
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //已经使用注解处理
    }

    public static final String TXT_Title              = "提示";
    public static final String TXT_MSG_DENIED         = "请进入设置修改应用权限，以保证正常使用";
    /** a message explaining why the application needs this set of permissions */
    public static       String MSG_HINT_FUNCTIONS     = "请授权以获取更完善的体验";
    /** go settings and open permissions manually */
    public static       String MSG_BUTTON_TXT_GO_OPEN = "去开启";
    /** quit current permissions auth. */
    public static       String MSG_BUTTON_TXT_GO_QUIT = "退出";

    private static final int CODE_GO_SETTING = 1000;

    @SuppressWarnings("deprecation")
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        new AlertDialog.Builder(this)
                .setTitle(TXT_Title)
                .setMessage(TXT_MSG_DENIED)
                .setPositiveButton(SplashAuthUI.MSG_BUTTON_TXT_GO_OPEN, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, CODE_GO_SETTING);
                    }
                })
                .setNegativeButton(SplashAuthUI.MSG_BUTTON_TXT_GO_QUIT, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        handleAfterPermissions(false);
                    }
                }).create().show();
        //                new MPermissionSettingsDialog.Builder(this)
        //                        .setPositiveButton(SplashAuthUI.MSG_BUTTON_TXT_GO_OPEN)
        //                        .setNegativeButton(SplashAuthUI.MSG_BUTTON_TXT_GO_QUIT, new DialogInterface.OnClickListener() {
        //                            @Override
        //                            public void onClick(DialogInterface dialog, int which) {
        //                                dialog.dismiss();
        //                                handleAfterPermissions(false);
        //                            }
        //                        }).build().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //从设置应用详情页返回
        if(requestCode == CODE_GO_SETTING) {
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
