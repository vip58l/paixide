package com.paixide.activity.sesemys;

import android.Manifest;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ToastUtils;
import com.paixide.BasActivity.BasActivity2;
import com.paixide.IMtencent.BaseActivity;
import com.paixide.Module.api.Config_Msg;
import com.paixide.Module.api.reguserinfo;
import com.paixide.Util.Toashow;
import com.paixide.Util.Utils;
import com.paixide.activity.Aboutus.activity_viecode;
import com.paixide.utils.MySocket;
import com.paixide.activity.Withdrawal.acitivity_settlement;
import com.paixide.Util.StatusBarUtil;
import com.paixide.widget.itembackTopbr;
import com.paixide.R;
import com.paixide.activity.edit.activity_edit;
import com.paixide.activity.edit.activity_updateedit;
import com.paixide.Util.DataCleanManager;
import com.paixide.Util.config;
import com.paixide.listener.Paymnets;
import com.paixide.app.DemoApplication;
import com.paixide.dialog.Dialog_Exit;
import com.paixide.dialog.dialog_load;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMUserFullInfo;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.liteav.callService;
import com.tencent.opensource.model.Mesresult;
import com.tencent.opensource.model.UserInfo;
import com.tencent.opensource.model.member;
import com.tencent.qcloud.tim.tuikit.live.TUIKitLive;
import com.tencent.qcloud.tim.tuikit.live.base.TUILiveRequestCallback;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.config.TUIKitConfigs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ??????
 */
public class activity_sesemys extends BasActivity2 {
    @BindView(R.id.VersionName)
    TextView VersionName;
    @BindView(R.id.delete)
    TextView delete;
    @BindView(R.id.itemback)
    itembackTopbr itemback;
    @BindView(R.id.anchor)
    LinearLayout anchor;
    private static final String TAG = activity_sesemys.class.getSimpleName();
    UserInfo userInfo;
    @BindView(R.id.s_v)
    Switch aSwitch;


    @Override
    protected int getview() {

        return R.layout.activity_sesemys;
    }

    @Override
    public void iniview() {
        itemback.settitle(getString(R.string.tv_msg105));
        StatusBarUtil.setStatusBar(activity, Color.TRANSPARENT);
        VersionName.setText(config.getVersionName(context));
        delete.setText(DataCleanManager.getTotalCacheSize());
        userInfo = UserInfo.getInstance();
    }

    @Override
    public void initData() {
        //????????? ???????????? ????????? ?????????
        anchor.setVisibility(userInfo.gettRole() == 1 && userInfo.getSex().equals("2") && userInfo.getState() == 2 ? View.VISIBLE : View.GONE);
        aSwitch.setChecked(Config_Msg.getInstance().isIsswitch());
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (Utils.checkPermission(activity)) {
                    //????????????????????????
                    if (isChecked) {
                        aSwitch.setSwitchTextAppearance(context, R.style.s_true);
                        Config_Msg.getInstance().setIsswitch(true);
                        //???????????????????????????????????????
                        callService.startCall(null);
                    } else {
                        aSwitch.setSwitchTextAppearance(context, R.style.s_false);
                        Config_Msg.getInstance().setIsswitch(false);
                        //???????????????????????????????????????
                        callService.callstopService();
                    }
                }
            }

        });
    }

    @OnClick({R.id.layout0, R.id.layout1, R.id.layout2, R.id.layout3, R.id.layout4, R.id.exit, R.id.layout01, R.id.layout02})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.layout0: {
                //??????????????????
                acitivity_settlement.starsetAction(context);
                break;
            }
            case R.id.layout1: {
                activity_updateedit.starsetAction(context);
                break;
            }
            case R.id.layout2: {
                activity_edit.starsetAction(context);
                break;
            }
            case R.id.layout3: {
                dialog_load dialogLoad = new dialog_load(context);
                dialogLoad.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogLoad.dismiss();
                        DataCleanManager.clearAllCache(activity_sesemys.this);
                        delete.setText(DataCleanManager.getTotalCacheSize());
                        ToastUtils.showShort(getString(R.string.text22));
                    }
                }, 1000);
                break;
            }
            case R.id.layout4: {//????????????
                activity_viecode.starsetAction(context);
                break;
            }
            case R.id.exit: {
                Dialog_Exit dialogExit = new Dialog_Exit(context);
                dialogExit.setdialogTitle(getString(R.string.tvmsgexit));
                dialogExit.show();
                dialogExit.setPaymnets(new Paymnets() {
                    @Override
                    public void activity() {

                    }

                    @Override
                    public void payens() {
                        config.DeLeteall(DemoApplication.instance());
                        config.DeLeteUserinfo(DemoApplication.instance());
                        BaseActivity.logout(DemoApplication.instance());
                        //config.DeLeteUserinfo(DemoApplication.instance(), "per_user_model");
                        TUIKit.unInit();//????????????????????????????????????????????????????????????

                        //????????????????????????????????????
                        MySocket.unInit();
                    }
                });
                break;
            }
            case R.id.layout01: {
                //????????????
                actrivity_sesemy_notice.starsetAction(context);
                break;
            }
            case R.id.layout02: {
                //????????????
                activity_privacy_settings.starsetAction(context);
                break;
            }
        }
    }

    @Override
    public void OnEorr() {

    }

    /**
     * ????????????????????????
     *
     * @param mesresult
     */
    public static void user_save_update_Profile(Mesresult mesresult) {
        UserInfo userInfo = UserInfo.getInstance();
        userInfo.setUserId(mesresult.getUsreid().trim());              //??????UseID
        userInfo.setAutoLogin(true);                                   //????????????
        userInfo.setUsername(mesresult.getUsername().trim());         //???????????????
        userInfo.setMobile(mesresult.getMobile().trim());             //???????????????
        userInfo.setPhone(mesresult.getMobile().trim());              //???????????????
        userInfo.setToken(mesresult.getToken().trim());                //????????????
        userInfo.setRefresh(mesresult.getRefresh().trim());            //????????????
        userInfo.setAvatar(mesresult.getPicture().trim());             //???????????? ??????IM????????????
        userInfo.setPicture(mesresult.getAvatar().trim());             //????????????
        userInfo.setState(mesresult.getMember());                      //????????????
        userInfo.setVip(mesresult.getVip());                           //VIP????????????
        userInfo.setSex(mesresult.getSex());                           //??????
        userInfo.setName(mesresult.getAlias().trim());                 //??????
        userInfo.setGivenname(mesresult.getName().trim());             //??????
        userInfo.setUserSig(mesresult.getUserSig().trim());            //????????????
        userInfo.setLive(mesresult.getLive());                         //????????????
        userInfo.setLvideo(mesresult.getLvideo());                     //???????????????
        userInfo.setRemarks1(mesresult.getRemarks1().trim());          //????????????
        userInfo.setAllow(mesresult.getAllow());                       //????????????
        userInfo.setReale(mesresult.getReale());                       //??????????????????
        userInfo.settRole(mesresult.gettRole());                       //?????????1?????????
        userInfo.setInreview(mesresult.getInreview());                 //???????????????
        updateProfile(userInfo);                                       //IM????????????
    }

    /**
     * ????????????????????????
     *
     * @param mesresult
     */
    public static void user_save_update(Mesresult mesresult) {
        UserInfo userInfo = UserInfo.getInstance();
        userInfo.setVip(mesresult.getVip());
        userInfo.setMobile(mesresult.getMobile());
        userInfo.setUsername(mesresult.getUsername());
        userInfo.setState(Integer.parseInt(mesresult.getStatus()));
        userInfo.setSex(mesresult.getSex());
        userInfo.setPhone(mesresult.getMobile());
        userInfo.setVideotype(mesresult.getVideotype());
        userInfo.setDuedate(mesresult.getDuedate());
        userInfo.setLive(mesresult.getLive());
        userInfo.setLevel(mesresult.getLevel());
        userInfo.setRemarks1(mesresult.getRemarks1());
        userInfo.setGame(mesresult.getGame());
        userInfo.setAvatar(mesresult.getPicture());
        userInfo.setZfboff(mesresult.getZfboff());
        userInfo.setWxoff(mesresult.getWxoff());
        userInfo.setJinbi(mesresult.getJinbi());
        userInfo.setAllow(mesresult.getAllow());
        userInfo.setReale(mesresult.getReale());
        userInfo.settRole(mesresult.gettRole());
        userInfo.setName(mesresult.getTruename());
        userInfo.setGivenname(mesresult.getName());
        userInfo.setLvideo(mesresult.getLvideo());
        userInfo.setInreview(mesresult.getInreview());
    }

    /**
     * ????????????????????????
     *
     * @param
     */
    public static void user_save_update1(reguserinfo info) {
        UserInfo userInfo = UserInfo.getInstance();
        userInfo.setGivenname(info.getTvname());
        userInfo.setName(info.getTruename());
        userInfo.setPesigntext(info.getPesigntext());
        userInfo.setQq(info.getQq());
        userInfo.setWx(info.getWx());
        userInfo.setSex(String.valueOf(info.getSex()));
        userInfo.setVip(info.getVip());
        userInfo.settRole(info.gettRole());
        userInfo.setAvatar(info.getPicture());
        userInfo.setAllow(info.getAllow());
        userInfo.setReale(info.getReale());
        userInfo.setInreview(info.getInreview());
    }

    /**
     * ????????????????????????
     *
     * @param member member
     */
    public static void user_save_update2(com.tencent.opensource.model.member member) {
        UserInfo userInfo = UserInfo.getInstance();
        userInfo.setState(member.getStatus());
        userInfo.setLevel(member.getLevel());
        userInfo.setAvatar(member.getPicture());
        userInfo.setVip(member.getVip());
        userInfo.setAllow(member.getAllow());
        userInfo.setDuedate(member.getDuedate());
        userInfo.setUserId(String.valueOf(member.getId()));
        userInfo.setReale(member.getReale());
        userInfo.setInreview(member.getInreview());
    }

    /**
     * ???????????????IM??????????????????
     *
     * @param userInfo
     */
    public static void updateProfile(UserInfo userInfo) {
        int loginStatus = V2TIMManager.getInstance().getLoginStatus();
        String selfUserID = V2TIMManager.getInstance().getLoginUser();
        String avatar = TextUtils.isEmpty(userInfo.getAvatar()) ? userInfo.getPicture() : userInfo.getAvatar(); //????????????????????????????????????
        String mIconUrl = TextUtils.isEmpty(avatar) ? picpath(selfUserID) : avatar; //????????????????????????????????????

        if (TextUtils.isEmpty(selfUserID) || loginStatus == 0) {
            return;
        }
        V2TIMUserFullInfo v2TIMUserFullInfo = new V2TIMUserFullInfo();

        //????????????
        if (!TextUtils.isEmpty(mIconUrl)) {
            v2TIMUserFullInfo.setFaceUrl(mIconUrl);
            userInfo.setAvatar(mIconUrl);
        }

        //????????????
        String nickName = userInfo.getName();
        v2TIMUserFullInfo.setNickname(nickName);

        // ??????????????????
        String signature = userInfo.getName();
        v2TIMUserFullInfo.setSelfSignature(signature);

        //??????????????????
        int allowType = 0;
        v2TIMUserFullInfo.setAllowType(allowType);

        //???????????????
        v2TIMUserFullInfo.setGender(userInfo.getSex().equals("1") ? 1 : 2);

        //?????????????????????IM
        V2TIMManager.getInstance().setSelfInfo(v2TIMUserFullInfo, new V2TIMCallback() {
            @Override
            public void onError(int code, String desc) {
            }

            @Override
            public void onSuccess() {
                TUIKitConfigs.getConfigs().getGeneralConfig().setUserFaceUrl(mIconUrl);
                TUIKitConfigs.getConfigs().getGeneralConfig().setUserNickname(nickName);
                TUIKitLive.refreshLoginUserInfo(new TUILiveRequestCallback() {
                    @Override
                    public void onError(int code, String desc) {
                    }

                    @Override
                    public void onSuccess(Object o) {

                    }
                });
            }
        });
    }

    /**
     * ??????????????????
     *
     * @return
     */
    public static String picpath(String selfUserID) {
        byte[] bytes = selfUserID.getBytes();
        int index = bytes[bytes.length - 1] % 10;
        String avatarName = "avatar" + index + "_100";
        String mIconUrl = "https://imgcache.qq.com/qcloud/public/static/" + avatarName + ".20191230.png";
        return mIconUrl;
    }

    /**
     * ?????????????????????
     *
     * @param userid
     */
    public static void getUsersInfo(String userid) {
        V2TIMManager.getInstance().getUsersInfo(Arrays.asList(userid), new V2TIMValueCallback<List<V2TIMUserFullInfo>>() {
            @Override
            public void onError(int code, String desc) {

            }

            @Override
            public void onSuccess(List<V2TIMUserFullInfo> v2TIMUserFullInfos) {
                //????????????
                V2TIMUserFullInfo v2TIMUserFullInfo = v2TIMUserFullInfos.get(0);
                String faceUrl = v2TIMUserFullInfo.getFaceUrl();
                UserInfo.getInstance().setAvatar(faceUrl);
            }
        });
    }


    /**
     * ???????????????????????????????????????
     */
    public static void checkTIMInfo(final String nick, final String faceUrl) {
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                if (timUserProfile != null) {
                    //?????????????????????  ??????????????????
                    String timNick = timUserProfile.getNickName();
                    String timFaceUrl = timUserProfile.getFaceUrl();
                    if (!timNick.equals(nick) || !timFaceUrl.equals(faceUrl)) {
                        updateTIMInfo(nick, faceUrl);
                    }
                }
            }
        });
    }

    /**
     * ??????TIM??????
     */
    private static void updateTIMInfo(String nick, String faceUrl) {
        HashMap<String, Object> profileMap = new HashMap<>();
        if (!TextUtils.isEmpty(nick)) {
            profileMap.put(TIMUserProfile.TIM_PROFILE_TYPE_KEY_NICK, nick);
        }
        if (!TextUtils.isEmpty(faceUrl)) {
            profileMap.put(TIMUserProfile.TIM_PROFILE_TYPE_KEY_FACEURL, faceUrl);
        }
        if (!profileMap.isEmpty()) {
            TIMFriendshipManager.getInstance().modifySelfProfile(profileMap, new TIMCallBack() {
                @Override
                public void onError(int code, String desc) {

                }

                @Override
                public void onSuccess() {

                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Utils.REQ_PERMISSION_CODE) {
            if (Utils.checkPermission(activity)) {
                aSwitch.setSwitchTextAppearance(context, R.style.s_true);
                Config_Msg.getInstance().setIsswitch(true);
                //???????????????????????????????????????
                callService.startCall(null);
            } else {
                aSwitch.setSwitchTextAppearance(context, R.style.s_false);
                Config_Msg.getInstance().setIsswitch(false);
                Toashow.show("?????????????????????");
            }
        }


    }
}