package com.tencent.qcloud.tim.uikit.modules.chat.base;

import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.imsdk.v2.V2TIMGroupAtInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 聊天信息基本类
 */
public class ChatInfo implements Serializable {

    private String chatName;    //名称
    private String iconUrlList; //聊天头像
    private int type = V2TIMConversation.V2TIM_C2C;
    private String id;
    private boolean isTopChat;
    private boolean kefu; //用来判断对方是否为客服
    private static List<V2TIMGroupAtInfo> atInfoList;

    /**
     * 聊天头像
     *
     * @return
     */
    public String getIconUrlList() {
        return iconUrlList;
    }

    /**
     * 聊天头像
     *
     * @param iconUrlList
     */
    public void setIconUrlList(String iconUrlList) {
        this.iconUrlList = iconUrlList;
    }

    public boolean isKefu() {
        return kefu;
    }

    public void setKefu(boolean kefu) {
        this.kefu = kefu;
    }

    /**
     * 获取聊天的标题，单聊一般为对方名称，群聊为群名字
     *
     * @return
     */
    public String getChatName() {
        return chatName;
    }

    /**
     * 设置聊天的标题，单聊一般为对方名称，群聊为群名字
     *
     * @param chatName
     */
    public void setChatName(String chatName) {
        this.chatName = chatName;

    }

    /**
     * 获取聊天类型，C2C为单聊，Group为群聊
     *
     * @return
     */
    public int getType() {
        return type;
    }

    /**
     * 设置聊天类型，C2C为单聊，Group为群聊
     *
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * 获取聊天唯一标识
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * 设置聊天唯一标识
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }


    /**
     * 是否为置顶的会话
     *
     * @return
     */
    public boolean isTopChat() {
        return isTopChat;
    }

    /**
     * 设置会话是否置顶
     *
     * @param topChat
     */
    public void setTopChat(boolean topChat) {
        isTopChat = topChat;
    }

    public List<V2TIMGroupAtInfo> getAtInfoList() {
        return atInfoList;
    }

    /**
     * 群主聊天
     *
     * @param atInfoList
     */
    public void setAtInfoList(List<V2TIMGroupAtInfo> atInfoList) {
        ChatInfo.atInfoList = atInfoList;
    }
}
