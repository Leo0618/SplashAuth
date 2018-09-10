# What #

简单易用的andorid M权限相关处理的库，通过极为简单的调用方式实现授权校验、授权申请，开发者可以根据结果反馈做相应的处理。

# How #

1. 添加依赖 

		implementation 'com.leo618:SplashAuth:0.0.3'

2. 声明权限，在manifest中声明需要授权的权限。 举个栗子如下:
	
	    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
	    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
	    <uses-permission android:name="android.permission.CAMERA" />


3. 项目manifest中添加提示对话框组件

	 	<activity
            android:name="com.leo618.splashpermissionsauth.SplashAuthUI"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:theme="@style/SplashPermissionsAuthThemeForSplash" />

4. 使用栗子如下(共有三种使用姿势)：

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
