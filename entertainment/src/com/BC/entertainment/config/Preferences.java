package com.BC.entertainment.config;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private static Context context;
    private static final String KEY_USER_CLIENT_ID = "clientID";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_PASSWORD = "password";
    private static final String KEY_USER_NICK_NAME = "nickName";
    private static final String KEY_USER_PERSMISSION = "permission";
    private static final String KEY_USER_CHECK_TYPE = "checkType";
    private static final String KEY_USER_IMAGE = "image";
    private static final String KEY_USER_TOKEN = "token";
    private static final String KEY_USER_PUSH_URL = "pushUrl";
    private static final String KEY_USER_SHARE_CODE = "shareCode";
    private static final String KEY_USER_AUTO_LOGIN = "autoLogin";
    
    public static void setContext(Context context) {
    	Preferences.context = context.getApplicationContext();
    }
    
    public static Context getContext() {
        return context;
    }
    
    public static void saveUserId(String clientID)
    {
    	 saveString(KEY_USER_CLIENT_ID, clientID);
    }
    
    public static String getUserId()
    {
    	return getString(KEY_USER_CLIENT_ID);
    }
    
    public static void saveUserName(String userName)
    {
    	saveString(KEY_USER_NAME, userName);
    }
    
    public static String getUserName()
    {
    	return getString(KEY_USER_NAME);
    }
    
    public static void saveUserPassword(String password)
    {
    	saveString(KEY_USER_PASSWORD, password);
    }
    
    public static String getUserPassword()
    {
    	return getString(KEY_USER_PASSWORD);
    }
    
    public static void saveUserNickName(String nickName)
    {
    	saveString(KEY_USER_NICK_NAME, nickName);
    }
    
    public static String getUserNickName()
    {
    	return getString(KEY_USER_NICK_NAME);
    }
    
    public static void saveUserPermission(String permission)
    {
    	saveString(KEY_USER_PERSMISSION, permission);
    }
    
    public static String getUserPermission()
    {
    	return getString(KEY_USER_PERSMISSION);
    }
    
    public static void saveUserCheckType(String checkType)
    {
    	saveString(KEY_USER_CHECK_TYPE, checkType);
    }
    
    public static String getUserCheckType()
    {
    	return getString(KEY_USER_CHECK_TYPE);
    }
    
    public static void saveUserImage(String image)
    {
    	saveString(KEY_USER_IMAGE, image);
    }
    
    public static String getUserImage()
    {
    	return getString(KEY_USER_IMAGE);
    }
    
    public static void saveUserToken(String token) {
        saveString(KEY_USER_TOKEN, token);
    }

    public static String getUserToken() {
        return getString(KEY_USER_TOKEN);
    }
    
    public static void saveUserPushUrl(String pushUrl)
    {
    	saveString(KEY_USER_PUSH_URL, pushUrl);
    }
    
    public static String getUserPushUrl()
    {
    	return getString(KEY_USER_PUSH_URL);
    }
    
    public static void saveUserShareCode(String shareCode)
    {
    	saveString(KEY_USER_SHARE_CODE, shareCode);
    }
    
    public static String getUserShareCode()
    {
    	return getString(KEY_USER_SHARE_CODE);
    }
    
    public static void saveUserAutoLogin(String autoLogin)
    {
    	saveString(KEY_USER_AUTO_LOGIN, autoLogin);
    }
    
    public static String getUserAutoLogin()
    {
    	return getString(KEY_USER_AUTO_LOGIN);
    }
    
    static SharedPreferences getSharedPreferences() {
        return Preferences.getContext().getSharedPreferences("userConfig", Context.MODE_PRIVATE);
    }
    
    private static void saveString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static String getString(String key) {
        return getSharedPreferences().getString(key, null);
    }
}
