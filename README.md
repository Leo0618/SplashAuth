# What #

简单的一个无视图UI实现Android权限授权问题，通过极为简单的调用方式实现授权操作，开发者可以根据结果反馈做相应的处理。基于权限处理库[MPermission](https://github.com/Leo0618/MPermission "MPermission")

# How #

1. 添加依赖 

		compile 'com.leo618:SplashAuth:0.0.1'

2. 声明权限，在manifest中声明需要授权的权限。 eg:
	
	    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
	    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
	    <uses-permission android:name="android.permission.CAMERA" />


3. 项目中添加UI组件

	 	<activity
            android:name="com.leo618.splashpermissionsauth.SplashAuthUI"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:theme="@style/SplashPermissionsAuthThemeForSplash" />

4. 发起请求并处理结果

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
