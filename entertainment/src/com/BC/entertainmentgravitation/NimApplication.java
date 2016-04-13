package com.BC.entertainmentgravitation;

import com.BC.entertainment.config.Cache;
import com.BC.entertainment.config.Preferences;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;

import android.app.Application;
import android.os.Environment;
import android.text.TextUtils;

public class NimApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		Cache.setContext(this);
		NIMClient.init(this, getLoginInfo(), null);
	}

    private LoginInfo getLoginInfo() {
        String account = Preferences.getUserAccount();
        String token = Preferences.getUserToken();

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            Cache.setAccount(account.toLowerCase());
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }
    
//    private SDKOptions getOptions() {
//        SDKOptions options = new SDKOptions();
//
//        // ���������Ϣ֪ͨ�����йܸ�SDK��ɣ���Ҫ����������á�
//        StatusBarNotificationConfig config = UserPreferences.getStatusConfig();
//        if (config == null) {
//            config = new StatusBarNotificationConfig();
//        }
//        // ���֪ͨ��Ҫ��ת���Ľ���
//        config.notificationEntrance = WelcomeActivity.class;
//        config.notificationSmallIconId = R.drawable.ic_stat_notify_msg;
//
//        // ֪ͨ������uri�ַ���
//        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";
//        options.statusBarNotificationConfig = config;
//        DemoCache.setNotificationConfig(config);
//        UserPreferences.setStatusConfig(config);
//
//        // ���ñ���ͼƬ���ļ���log�����ݵ�Ŀ¼
//        String sdkPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim";
//        options.sdkStorageRootPath = sdkPath;
//
//        // �������ݿ������Կ
//        options.databaseEncryptKey = "NETEASE";
//
//        // �����Ƿ���ҪԤ���ظ�������ͼ
//        options.preloadAttach = true;
//
//        // ���ø�������ͼ�ĳߴ��С��
//        options.thumbnailSize = MsgViewHolderThumbBase.getImageMaxEdge();
//
//        // �û���Ϣ�ṩ��
//        options.userInfoProvider = infoProvider;
//
//        // ����֪ͨ�������İ�����ѡ����������ƽ�����SDKĬ���İ���
//        options.messageNotifierCustomization = messageNotifierCustomization;
//
//        return options;
//    }
}
