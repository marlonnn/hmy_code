package com.BC.entertainmentgravitation;

import java.util.ArrayList;
import java.util.List;

import com.BC.entertainment.config.Cache;
import com.BC.entertainment.config.Preferences;
import com.BC.entertainmentgravitation.util.SystemUtil;
import com.netease.nim.uikit.ImageLoaderKit;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.contact.ContactProvider;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider.UserInfo;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;

public class NimApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		Cache.setContext(this);
		NIMClient.init(this, getLoginInfo(), getOptions());
		
		if (inMainProcess())
		{
            // ��ʼ��UIKitģ��
            initUIKit();
		}
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
    
    public boolean inMainProcess() {
        String packageName = getPackageName();
        String processName = SystemUtil.getProcessName(this);
        return packageName.equals(processName);
    }
    
    private SDKOptions getOptions() {
        SDKOptions options = new SDKOptions();
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
        // ���ñ���ͼƬ���ļ���log�����ݵ�Ŀ¼
        String sdkPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim";
        options.sdkStorageRootPath = sdkPath;
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
        // �û���Ϣ�ṩ��
        options.userInfoProvider = infoProvider;
//
//        // ����֪ͨ�������İ�����ѡ����������ƽ�����SDKĬ���İ���
//        options.messageNotifierCustomization = messageNotifierCustomization;
//
        return options;
    }
    
    
    private void initUIKit() {
        // ��ʼ������Ҫ�����û���Ϣ�ṩ��
        NimUIKit.init(this, infoProvider, null);
    }
    
    private UserInfoProvider infoProvider = new UserInfoProvider() {
        @Override
        public UserInfo getUserInfo(String account) {
        	//��������
//        	NimUserInfoCache.getInstance().buildCache();
            UserInfo user = NimUserInfoCache.getInstance().getUserInfo(account);
            if (user == null) {
                NimUserInfoCache.getInstance().getUserInfoFromRemote(account, null);
            }

            return user;
        }

        @Override
        public int getDefaultIconResId() {
            return R.drawable.avatar_def;
        }

        @Override
        public Bitmap getTeamIcon(String teamId) {
            Drawable drawable = getResources().getDrawable(R.drawable.nim_avatar_group);
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }

            return null;
        }

        @Override
        public Bitmap getAvatarForMessageNotifier(String account) {
            /**
             * ע�⣺������ôӻ������ã������ȡ����ͷ����ܵ���UI��������������֪ͨ��������ʱ������
             */
            UserInfo user = getUserInfo(account);
            return (user != null) ? ImageLoaderKit.getNotificationBitmapFromCache(user) : null;
        }

        @Override
        public String getDisplayNameForMessageNotifier(String account, String sessionId, SessionTypeEnum sessionType) {
            String nick = null;
            if (sessionType == SessionTypeEnum.P2P) {
                nick = NimUserInfoCache.getInstance().getAlias(account);
            } else if (sessionType == SessionTypeEnum.Team) {
                nick = TeamDataCache.getInstance().getTeamNick(sessionId, account);
                if (TextUtils.isEmpty(nick)) {
                    nick = NimUserInfoCache.getInstance().getAlias(account);
                }
            }
            // ����null������sdk��������Է�������nick��sdk����ʾnick
            if (TextUtils.isEmpty(nick)) {
                return null;
            }

            return nick;
        }
    };

    private ContactProvider contactProvider = new ContactProvider() {
        @Override
        public List<UserInfoProvider.UserInfo> getUserInfoOfMyFriends() {
            List<NimUserInfo> nimUsers = NimUserInfoCache.getInstance().getAllUsersOfMyFriend();
            List<UserInfoProvider.UserInfo> users = new ArrayList<>(nimUsers.size());
            if (!nimUsers.isEmpty()) {
                users.addAll(nimUsers);
            }

            return users;
        }

        @Override
        public int getMyFriendsCount() {
            return FriendDataCache.getInstance().getMyFriendCounts();
        }

        @Override
        public String getUserDisplayName(String account) {
            return NimUserInfoCache.getInstance().getUserDisplayName(account);
        }
    };
}
