package com.BC.entertainmentgravitation;

import android.app.Application;
import android.os.Environment;
import android.text.TextUtils;

import com.BC.entertainment.chatroom.extension.CustomAttachParser;
import com.BC.entertainmentgravitation.util.SystemUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MsgService;
import com.summer.config.Config;
import com.summer.logger.XLog;

public class NimApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Config.maincontext = this.getApplicationContext();
		NIMClient.init(this, getLoginInfo(), getOptions());

//		XLog.allowI = XLog.allowD = XLog.allowE = XLog.allowV = XLog.allowW = false;
		if (inMainProcess())
		{
            NIMClient.getService(MsgService.class).registerCustomAttachmentParser(new CustomAttachParser());
		}
	}

    private LoginInfo getLoginInfo() {
		Config.LoadUser();
		String account = null;
		String token = null;
		if ( Config.User != null)
		{
	        account = Config.User.getUserName();
	        token = Config.User.getToken();
		}

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }
    
    public boolean inMainProcess() {
        String packageName = getPackageName();
        String processName = SystemUtil.getProcessName(this);
        return packageName.equals(processName);
    }
    
    private SDKOptions getOptions() {
        SDKOptions options = new SDKOptions();

        // 配置保存图片，文件，log等数据的目录
        String sdkPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim";
        options.sdkStorageRootPath = sdkPath;

        return options;
    }
}
