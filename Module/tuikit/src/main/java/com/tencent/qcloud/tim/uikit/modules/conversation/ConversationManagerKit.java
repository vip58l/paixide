package com.tencent.qcloud.tim.uikit.modules.conversation;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.imsdk.v2.V2TIMConversationResult;
import com.tencent.imsdk.v2.V2TIMGroupAtInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberFullInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberInfoResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.config.TUIKitConfigs;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfoUtil;
import com.tencent.qcloud.tim.uikit.modules.message.MessageRevokedManager;
import com.tencent.qcloud.tim.uikit.utils.SharedPreferenceUtils;
import com.tencent.qcloud.tim.uikit.utils.TUIKitLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ConversationManagerKit implements MessageRevokedManager.MessageRevokeHandler {

    private final static String TAG = ConversationManagerKit.class.getSimpleName();
    private final static String SP_NAME = "_top_conversion_list";
    private final static String SP_IMAGE = "_conversation_group_face";
    private final static String TOP_LIST = "top_list";

    private static final ConversationManagerKit instance = new ConversationManagerKit();

    private ConversationProvider mProvider;
    private final List<MessageUnreadWatcher> mUnreadWatchers = new ArrayList<>();
    private SharedPreferences mConversationPreferences;
    private LinkedList<ConversationInfo> mTopLinkedList = new LinkedList<>();
    private int mUnreadTotal;

    private ConversationManagerKit() {
        init();
    }

    public static ConversationManagerKit getInstance() {
        return instance;
    }

    private void init() {
        TUIKitLog.i(TAG, "init");
        MessageRevokedManager.getInstance().addHandler(this);
    }

    /**
     * ??????????????????
     *
     * @param callBack
     */
    public void loadConversation(final IUIKitCallBack callBack) {
        TUIKitLog.i(TAG, "loadConversation callBack:" + callBack);
        mConversationPreferences = TUIKit.getAppContext().getSharedPreferences(TUIKitConfigs.getConfigs().getGeneralConfig().getSDKAppId() + "-" + V2TIMManager.getInstance().getLoginUser() + SP_NAME, Context.MODE_PRIVATE);
        mTopLinkedList = SharedPreferenceUtils.getListData(mConversationPreferences, TOP_LIST, ConversationInfo.class);
        //mProvider???????????????null,???????????????????????????????????????????????????????????????
        if (mProvider == null) {
            mProvider = new ConversationProvider();
        }

        //?????????????????? ????????????????????????
        V2TIMManager.getConversationManager().getConversationList(0, 100, new V2TIMValueCallback<V2TIMConversationResult>() {
            @Override
            public void onError(int code, String desc) {
                TUIKitLog.v(TAG, "??????????????????, code = " + code + ", desc = " + desc);
                Log.d(TAG, "??????????????????");
            }

            @Override
            public void onSuccess(V2TIMConversationResult v2TIMConversationResult) {
                ArrayList<ConversationInfo> infos = new ArrayList<>();
                List<V2TIMConversation> v2TIMConversationList = v2TIMConversationResult.getConversationList();
                mUnreadTotal = 0;
                for (V2TIMConversation v2TIMConversation : v2TIMConversationList) {
                    //??? imsdk v2TIMConversation ????????? UIKit ConversationInfo

                    ConversationInfo conversationInfo = TIMConversation2ConversationInfo(v2TIMConversation);
                    if (conversationInfo != null) {
                        //???????????????????????????
                        if (v2TIMConversation.getType() == 2 && TextUtils.isEmpty(v2TIMConversation.getGroupType())) {
                            continue;
                        }
                        mUnreadTotal = mUnreadTotal + conversationInfo.getUnRead();
                        conversationInfo.setType(ConversationInfo.TYPE_COMMON);
                        infos.add(conversationInfo);
                    }

                }

                //?????????imsdk???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????imsdk????????????????????????
                mProvider.setDataSource(sortConversations(infos));
                SharedPreferenceUtils.putListData(mConversationPreferences, TOP_LIST, mTopLinkedList);
                //????????????????????????
                updateUnreadTotal(mUnreadTotal);
                if (callBack != null) {
                    callBack.onSuccess(mProvider);
                }
            }
        });
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param v2TIMConversationList ???????????????????????????
     */
    public void onRefreshConversation(List<V2TIMConversation> v2TIMConversationList) {
        TUIKitLog.v(TAG, "onRefreshConversation conversations:" + v2TIMConversationList);
        if (mProvider == null) {
            return;
        }
        ArrayList<ConversationInfo> infos = new ArrayList<>();
        for (int i = 0; i < v2TIMConversationList.size(); i++) {
            V2TIMConversation v2TIMConversation = v2TIMConversationList.get(i);
            TUIKitLog.v(TAG, "refreshConversation v2TIMConversation " + v2TIMConversation.toString());
            ConversationInfo conversationInfo = TIMConversation2ConversationInfo(v2TIMConversation);
            if (conversationInfo != null && !V2TIMManager.GROUP_TYPE_AVCHATROOM.equals(v2TIMConversation.getGroupType())) {
                infos.add(conversationInfo);
            }
        }
        if (infos.size() == 0) {
            return;
        }
        List<ConversationInfo> dataSource = mProvider.getDataSource();
        ArrayList exists = new ArrayList();
        for (int j = 0; j < infos.size(); j++) {
            ConversationInfo update = infos.get(j);
            boolean exist = false;
            for (int i = 0; i < dataSource.size(); i++) {
                ConversationInfo cacheInfo = dataSource.get(i);
                //???????????????????????????????????????????????????????????????????????????????????????????????????????????????id?????????id??????
                if (cacheInfo.getId().equals(update.getId()) && cacheInfo.isGroup() == update.isGroup()) {
                    dataSource.remove(i);
                    dataSource.add(i, update);
                    exists.add(update);
                    //infos.remove(j);
                    //???????????????????????????
                    mUnreadTotal = mUnreadTotal - cacheInfo.getUnRead() + update.getUnRead();
                    TUIKitLog.v(TAG, "onRefreshConversation after mUnreadTotal = " + mUnreadTotal);
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                mUnreadTotal += update.getUnRead();
                TUIKitLog.i(TAG, "onRefreshConversation exist = " + exist + ", mUnreadTotal = " + mUnreadTotal);
            }
        }
        updateUnreadTotal(mUnreadTotal);
        infos.removeAll(exists);
        if (infos.size() > 0) {
            dataSource.addAll(infos);
        }
        mProvider.setDataSource(sortConversations(dataSource));
        SharedPreferenceUtils.putListData(mConversationPreferences, TOP_LIST, mTopLinkedList);
    }

    /**
     * TIMConversation?????????ConversationInfo
     *
     * @param conversation
     * @return
     */
    private ConversationInfo TIMConversation2ConversationInfo(final V2TIMConversation conversation) {
        if (conversation == null) {
            return null;
        }
        TUIKitLog.i(TAG, "TIMConversation2ConversationInfo id:" + conversation.getConversationID()
                + "|name:" + conversation.getShowName()
                + "|unreadNum:" + conversation.getUnreadCount());
        V2TIMMessage message = conversation.getLastMessage();
        if (message == null) {
            return null;
        }
        final ConversationInfo info = new ConversationInfo();
        int type = conversation.getType();
        if (type != V2TIMConversation.V2TIM_C2C && type != V2TIMConversation.V2TIM_GROUP) {
            return null;
        }

        boolean isGroup = type == V2TIMConversation.V2TIM_GROUP;
        info.setLastMessageTime(message.getTimestamp());
        List<MessageInfo> list = MessageInfoUtil.TIMMessage2MessageInfo(message);
        if (list != null && list.size() > 0) {
            info.setLastMessage(list.get(list.size() - 1));
        }

        int atInfoType = getAtInfoType(conversation);
        switch (atInfoType) {
            case V2TIMGroupAtInfo.TIM_AT_ME:
                info.setAtInfoText(TUIKit.getAppContext().getString(R.string.ui_at_me));
                break;
            case V2TIMGroupAtInfo.TIM_AT_ALL:
                info.setAtInfoText(TUIKit.getAppContext().getString(R.string.ui_at_all));
                break;
            case V2TIMGroupAtInfo.TIM_AT_ALL_AT_ME:
                info.setAtInfoText(TUIKit.getAppContext().getString(R.string.ui_at_all_me));
                break;
            default:
                info.setAtInfoText("");
                break;

        }

        info.setTitle(conversation.getShowName());
        if (isGroup) {
            fillConversationUrlForGroup(conversation, info);
        } else {
            List<Object> faceList = new ArrayList<>();
            if (TextUtils.isEmpty(conversation.getFaceUrl())) {
                faceList.add(R.drawable.default_head);
            } else {
                faceList.add(conversation.getFaceUrl());
            }
            info.setIconUrlList(faceList);
        }
        if (isGroup) {
            info.setId(conversation.getGroupID());
        } else {
            info.setId(conversation.getUserID());
        }
        info.setConversationId(conversation.getConversationID());
        info.setGroup(isGroup);
        // Meeting ??? AVChatRoom ????????????????????????Meeting ??????????????????????????????
        if (!V2TIMManager.GROUP_TYPE_MEETING.equals(conversation.getGroupType())
                && !V2TIMManager.GROUP_TYPE_AVCHATROOM.equals(conversation.getGroupType())) {
            info.setUnRead(conversation.getUnreadCount());
        }
        return info;
    }

    private int getAtInfoType(V2TIMConversation conversation) {
        int atInfoType = 0;
        boolean atMe = false;
        boolean atAll = false;

        List<V2TIMGroupAtInfo> atInfoList = conversation.getGroupAtInfoList();

        if (atInfoList == null || atInfoList.isEmpty()) {
            return V2TIMGroupAtInfo.TIM_AT_UNKNOWN;
        }

        for (V2TIMGroupAtInfo atInfo : atInfoList) {
            if (atInfo.getAtType() == V2TIMGroupAtInfo.TIM_AT_ME) {
                atMe = true;
                continue;
            }
            if (atInfo.getAtType() == V2TIMGroupAtInfo.TIM_AT_ALL) {
                atAll = true;
                continue;
            }
            if (atInfo.getAtType() == V2TIMGroupAtInfo.TIM_AT_ALL_AT_ME) {
                atMe = true;
                atAll = true;
                continue;
            }
        }

        if (atAll && atMe) {
            atInfoType = V2TIMGroupAtInfo.TIM_AT_ALL_AT_ME;
        } else if (atAll) {
            atInfoType = V2TIMGroupAtInfo.TIM_AT_ALL;
        } else if (atMe) {
            atInfoType = V2TIMGroupAtInfo.TIM_AT_ME;
        } else {
            atInfoType = V2TIMGroupAtInfo.TIM_AT_UNKNOWN;
        }

        return atInfoType;
    }

    private void fillConversationUrlForGroup(final V2TIMConversation conversation, final ConversationInfo info) {
        if (TextUtils.isEmpty(conversation.getFaceUrl())) {
            final String savedIcon = getGroupConversationAvatar(conversation.getConversationID());
            if (TextUtils.isEmpty(savedIcon)) {
                fillFaceUrlList(conversation.getGroupID(), info);
            } else {
                List<Object> list = new ArrayList<>();
                list.add(savedIcon);
                info.setIconUrlList(list);
            }
        } else {
            List<Object> list = new ArrayList<>();
            list.add(conversation.getFaceUrl());
            info.setIconUrlList(list);
        }
    }

    private void fillFaceUrlList(final String groupID, final ConversationInfo info) {
        V2TIMManager.getGroupManager().getGroupMemberList(groupID, V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ALL, 0, new V2TIMValueCallback<V2TIMGroupMemberInfoResult>() {
            @Override
            public void onError(int code, String desc) {
                TUIKitLog.e(TAG, "getGroupMemberList failed! groupID:" + groupID + "|code:" + code + "|desc: " + desc);
            }

            @Override
            public void onSuccess(V2TIMGroupMemberInfoResult v2TIMGroupMemberInfoResult) {
                List<V2TIMGroupMemberFullInfo> v2TIMGroupMemberFullInfoList = v2TIMGroupMemberInfoResult.getMemberInfoList();
                int faceSize = v2TIMGroupMemberFullInfoList.size() > 9 ? 9 : v2TIMGroupMemberFullInfoList.size();
                List<Object> urlList = new ArrayList<>();
                for (int i = 0; i < faceSize; i++) {
                    V2TIMGroupMemberFullInfo v2TIMGroupMemberFullInfo = v2TIMGroupMemberFullInfoList.get(i);
                    if (TextUtils.isEmpty(v2TIMGroupMemberFullInfo.getFaceUrl())) {
                        urlList.add(R.drawable.default_head);
                    } else {
                        urlList.add(v2TIMGroupMemberFullInfo.getFaceUrl());
                    }
                }
                info.setIconUrlList(urlList);
                mProvider.updateAdapter(info.getConversationId());
            }
        });
    }

    /**
     * ?????????????????????
     *
     * @param index
     * @param conversation
     */
    public void setConversationTop(int index, ConversationInfo conversation) {
        TUIKitLog.i(TAG, "setConversationTop index:" + index + "|conversation:" + conversation);
        if (!conversation.isTop()) {
            mTopLinkedList.remove(conversation);
            mTopLinkedList.addFirst(conversation);
            conversation.setTop(true);
        } else {
            conversation.setTop(false);
            mTopLinkedList.remove(conversation);
        }
        mProvider.setDataSource(sortConversations(mProvider.getDataSource()));
        SharedPreferenceUtils.putListData(mConversationPreferences, TOP_LIST, mTopLinkedList);
    }

    /**
     * ??????????????????
     *
     * @param id   ??????ID
     * @param flag ????????????
     */
    public void setConversationTop(String id, boolean flag) {
        TUIKitLog.i(TAG, "setConversationTop id:" + id + "|flag:" + flag);
        handleTopData(id, flag);
        mProvider.setDataSource(sortConversations(mProvider.getDataSource()));
        SharedPreferenceUtils.putListData(mConversationPreferences, TOP_LIST, mTopLinkedList);
    }

    private boolean isTop(String id) {
        if (mTopLinkedList == null || mTopLinkedList.size() == 0) {
            return false;
        }
        for (ConversationInfo info : mTopLinkedList) {
            if (TextUtils.equals(info.getId(), id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ?????????????????????????????????????????????SharePreferences????????????????????????
     *
     * @param id
     * @param flag
     */
    private void handleTopData(String id, boolean flag) {

        try {

            ConversationInfo conversation = null;
            List<ConversationInfo> conversationInfos = mProvider.getDataSource();
            for (int i = 0; i < conversationInfos.size(); i++) {
                ConversationInfo info = conversationInfos.get(i);
                if (info.getId().equals(id)) {
                    conversation = info;
                    break;
                }
            }

            if (conversation == null) {
                return;
            }

            if (flag) {
                if (!isTop(conversation.getId())) {
                    mTopLinkedList.remove(conversation);
                    mTopLinkedList.addFirst(conversation);
                    conversation.setTop(true);
                } else {
                    return;
                }
            } else {
                if (isTop(conversation.getId())) {
                    conversation.setTop(false);
                    mTopLinkedList.remove(conversation);
                } else {
                    return;
                }
            }

            SharedPreferenceUtils.putListData(mConversationPreferences, TOP_LIST, mTopLinkedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ??????????????????????????????????????????imsdk?????????
     *
     * @param index        ????????????????????????
     * @param conversation ????????????
     */
    public void deleteConversation(int index, ConversationInfo conversation) {
        TUIKitLog.i(TAG, "deleteConversation index:" + index + "|conversation:" + conversation);
        V2TIMManager.getConversationManager().deleteConversation(conversation.getConversationId(), new V2TIMCallback() {
            @Override
            public void onError(int code, String desc) {
                TUIKitLog.e(TAG, "deleteConversation error:" + code + ", desc:" + desc);
            }

            @Override
            public void onSuccess() {
                TUIKitLog.i(TAG, "deleteConversation success");
            }
        });
        handleTopData(conversation.getId(), false);
        mProvider.deleteConversation(index);
        updateUnreadTotal(mUnreadTotal - conversation.getUnRead());
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param id C2C???????????? userID???Group?????? ID
     */
    public void deleteConversation(String id, boolean isGroup) {
        try {

            TUIKitLog.i(TAG, "deleteConversation id:" + id + "|isGroup:" + isGroup);
            handleTopData(id, false);
            List<ConversationInfo> conversationInfos = mProvider.getDataSource();
            for (int i = 0; i < conversationInfos.size(); i++) {
                ConversationInfo info = conversationInfos.get(i);
                if (info.getId().equals(id)) {
                    updateUnreadTotal(mUnreadTotal - info.getUnRead());
                    break;
                }
            }
            String conversationID = "";
            if (mProvider != null) {
                List<ConversationInfo> conversationInfoList = mProvider.getDataSource();
                for (ConversationInfo conversationInfo : conversationInfoList) {
                    if (isGroup == conversationInfo.isGroup() && conversationInfo.getId().equals(id)) {
                        conversationID = conversationInfo.getConversationId();
                        break;
                    }
                }
                if (!TextUtils.isEmpty(conversationID)) {
                    mProvider.deleteConversation(conversationID);
                }
            }
            if (!TextUtils.isEmpty(conversationID)) {
                V2TIMManager.getConversationManager().deleteConversation(conversationID, new V2TIMCallback() {
                    @Override
                    public void onError(int code, String desc) {
                        TUIKitLog.i(TAG, "deleteConversation error:" + code + ", desc:" + desc);
                    }

                    @Override
                    public void onSuccess() {
                        TUIKitLog.i(TAG, "deleteConversation success");
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????
     *
     * @param conversationInfo
     * @return
     */
    public boolean addConversation(ConversationInfo conversationInfo) {
        List<ConversationInfo> conversationInfos = new ArrayList<>();
        conversationInfos.add(conversationInfo);
        return mProvider.addConversations(conversationInfos);
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param sources
     * @return
     */
    private List<ConversationInfo> sortConversations(List<ConversationInfo> sources) {
        ArrayList<ConversationInfo> conversationInfos = new ArrayList<>();
        List<ConversationInfo> normalConversations = new ArrayList<>();
        List<ConversationInfo> topConversations = new ArrayList<>();

        for (int i = 0; i <= sources.size() - 1; i++) {
            ConversationInfo conversation = sources.get(i);
            if (isTop(conversation.getId())) {
                conversation.setTop(true);
                topConversations.add(conversation);
            } else {
                normalConversations.add(conversation);
            }
        }

        mTopLinkedList.clear();
        mTopLinkedList.addAll(topConversations);
        if (topConversations != null && topConversations.size() > 1) {
            Collections.sort(topConversations); // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        }
        conversationInfos.addAll(topConversations);
        if (normalConversations != null && normalConversations.size() > 1) {
            Collections.sort(normalConversations); // ?????????????????????????????????????????????????????????????????????
        }
        conversationInfos.addAll(normalConversations);
        return conversationInfos;
    }

    /**
     * ????????????????????????
     *
     * @param unreadTotal
     */
    public void updateUnreadTotal(int unreadTotal) {
        TUIKitLog.i(TAG, "updateUnreadTotal:" + unreadTotal);
        mUnreadTotal = unreadTotal;
        for (int i = 0; i < mUnreadWatchers.size(); i++) {
            mUnreadWatchers.get(i).updateUnread(mUnreadTotal);
        }
    }

    /**
     * ???????????????????????????
     *
     * @return ????????????
     */
    public int getUnreadTotal() {
        return mUnreadTotal;
    }

    public boolean isTopConversation(String groupId) {
        TUIKitLog.i(TAG, "isTopConversation:" + groupId);
        return isTop(groupId);
    }

    /**
     * ??????????????????
     *
     * @param msgID
     */
    @Override
    public void handleInvoke(String msgID) {
        if (mProvider != null) {
            loadConversation(null);
        }
    }


    /**
     * ???????????????????????????
     *
     * @param messageUnreadWatcher
     */
    public void addUnreadWatcher(MessageUnreadWatcher messageUnreadWatcher) {
        if (!mUnreadWatchers.contains(messageUnreadWatcher)) {
            mUnreadWatchers.add(messageUnreadWatcher);
            messageUnreadWatcher.updateUnread(mUnreadTotal);

        }
    }

    /**
     * ???????????????????????????
     *
     * @param messageUnreadWatcher
     */
    public void removeUnreadWatcher(MessageUnreadWatcher messageUnreadWatcher) {
        TUIKitLog.i(TAG, "removeUnreadWatcher:" + messageUnreadWatcher);
        if (messageUnreadWatcher == null) {
            mUnreadWatchers.clear();
        } else {
            mUnreadWatchers.remove(messageUnreadWatcher);
        }
    }

    /**
     * ???UI????????????????????????????????????
     */
    public void destroyConversation() {
        if (mProvider != null) {
            mProvider.attachAdapter(null);
        }
        if (mUnreadWatchers != null) {
            mUnreadWatchers.clear();
        }
    }

    public String getGroupConversationAvatar(String groupId) {
        SharedPreferences sp = TUIKit.getAppContext().getSharedPreferences(TUIKitConfigs.getConfigs().getGeneralConfig().getSDKAppId() + SP_IMAGE, Context.MODE_PRIVATE);
        final String savedIcon = sp.getString(groupId, "");
        if (!TextUtils.isEmpty(savedIcon) && new File(savedIcon).isFile() && new File(savedIcon).exists()) {
            return savedIcon;
        }
        return "";
    }

    public void setGroupConversationAvatar(String groupId, String url) {
        SharedPreferences sp = TUIKit.getAppContext().getSharedPreferences(TUIKitConfigs.getConfigs().getGeneralConfig().getSDKAppId() + SP_IMAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(groupId, url);
        editor.apply();
    }

    /**
     * ?????????????????????????????????
     */
    public interface MessageUnreadWatcher {
        void updateUnread(int count);
    }

}