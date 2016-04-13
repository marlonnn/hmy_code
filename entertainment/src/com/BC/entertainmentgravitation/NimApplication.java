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
//        // 如果将新消息通知提醒托管给SDK完成，需要添加以下配置。
//        StatusBarNotificationConfig config = UserPreferences.getStatusConfig();
//        if (config == null) {
//            config = new StatusBarNotificationConfig();
//        }
//        // 点击通知需要跳转到的界面
//        config.notificationEntrance = WelcomeActivity.class;
//        config.notificationSmallIconId = R.drawable.ic_stat_notify_msg;
//
//        // 通知铃声的uri字符串
//        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";
//        options.statusBarNotificationConfig = config;
//        DemoCache.setNotificationConfig(config);
//        UserPreferences.setStatusConfig(config);
//
//        // 配置保存图片，文件，log等数据的目录
//        String sdkPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim";
//        options.sdkStorageRootPath = sdkPath;
//
//        // 配置数据库加密秘钥
//        options.databaseEncryptKey = "NETEASE";
//
//        // 配置是否需要预下载附件缩略图
//        options.preloadAttach = true;
//
//        // 配置附件缩略图的尺寸大小，
//        options.thumbnailSize = MsgViewHolderThumbBase.getImageMaxEdge();
//
//        // 用户信息提供者
//        options.userInfoProvider = infoProvider;
//
//        // 定制通知栏提醒文案（可选，如果不定制将采用SDK默认文案）
//        options.messageNotifierCustomization = messageNotifierCustomization;
//
//        return options;
//    }
}
