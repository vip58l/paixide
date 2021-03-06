package com.tencent.liteav.trtcvideocalldemo.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;

import com.google.gson.Gson;
import com.savglotti.activity.PlaySavgLotti;
import com.tencent.Camera.MediaPlayHelper;
import com.tencent.Camera.My_Camera;
import com.tencent.Camera.camera_item;
import com.tencent.liteav.BaseAppCompatActivity;
import com.tencent.liteav.FloatingService;
import com.tencent.liteav.callService;
import com.tencent.liteav.login.ProfileManager;
import com.tencent.liteav.login.UserModel;
import com.tencent.liteav.model.ITRTCAVCall;
import com.tencent.liteav.model.IntentParams;
import com.tencent.liteav.model.TRTCAVCallImpl;
import com.tencent.liteav.model.TRTCAVCallListener;
import com.tencent.liteav.trtcvideocalldemo.ui.videolayout.TRTCVideoLayout;
import com.tencent.liteav.trtcvideocalldemo.ui.videolayout.TRTCVideoLayoutManager;
import com.tencent.opensource.model.Mesresult;
import com.tencent.opensource.model.UserInfo;
import com.tencent.qcloud.tim.tuikit.live.base.Constants;
import com.tencent.qcloud.tim.tuikit.live.component.gift.GiftPanelDelegate;
import com.tencent.qcloud.tim.tuikit.live.component.gift.IGiftPanelView;
import com.tencent.qcloud.tim.tuikit.live.component.gift.imp.DefaultGiftAdapterImp;
import com.tencent.qcloud.tim.tuikit.live.component.gift.imp.GiftInfo;
import com.tencent.qcloud.tim.tuikit.live.component.gift.imp.GiftInfoDataHandler;
import com.tencent.qcloud.tim.tuikit.live.component.gift.imp.GiftPanelViewImp;
import com.tencent.qcloud.tim.tuikit.live.modules.liveroom.model.TRTCLiveRoomDef;
import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.base.DialogcallBack;
import com.tencent.qcloud.tim.uikit.component.picture.imageEngine.impl.GlideEngine;
import com.tencent.qcloud.tim.uikit.modules.group.info.GroupInfo;
import com.tencent.qcloud.tim.uikit.modules.group.info.GroupUpdate;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfoUtil;
import com.tencent.qcloud.tim.uikit.utils.PermissionUtils;
import com.tencent.qcloud.tim.uikit.utils.TUIKitLog;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.tencent.qcloud.tim.uikit.utils.accordingdate;
import com.tencent.qcloud.tim.uikit.utils.goldcoin;
import com.tencent.qcloud.tim.uikit.utilsdialog.Postdeduction;
import com.tencent.qcloud.tim.uikit.utilsdialog.dialogsvip;
import com.tencent.qcloud.tim.uikit.utilsdialog.play;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ???????????????????????????????????????????????????????????????????????????????????????????????????
 *
 * @author guanyifeng
 */
public class TRTCVideoCallActivity extends BaseAppCompatActivity {
    private static final String TAG = TRTCVideoCallActivity.class.getName();
    public static final int TYPE_BEING_CALLED = 1; // ?????????
    public static final int TYPE_CALL = 2; // ?????????
    public static final String PARAM_GROUP_ID = "group_id";
    public static final String PARAM_TYPE = "type";
    public static final String PARAM_USER = "user_model";
    public static final String PARAM_BEINGCALL_USER = "beingcall_user_model";
    public static final String PARAM_OTHER_INVITING_USER = "other_inviting_user_model";
    private static final int MAX_SHOW_INVITING_USER = 4;
    private static final int RADIUS = 30;

    /**
     * ??????????????????
     */
    private ImageView mMuteImg;
    private LinearLayout mMuteLl;
    private ImageView mHangupImg;
    private LinearLayout mHangupLl;
    private ImageView mHandsfreeImg;
    private LinearLayout mHandsfreeLl;
    private ImageView mDialingImg;
    private LinearLayout mDialingLl;
    private LinearLayout mDialingLl1;
    private PlaySavgLotti playsavglotti;
    private TRTCVideoLayoutManager mLayoutManagerTrtc;
    private Group mInvitingGroup;
    private LinearLayout mImgContainerLl;
    private TextView mTimeTv;
    private Runnable mTimeRunnable;
    /**
     * ???????????????
     */
    private int mTimeCount;
    private camera_item camraitem;
    private ImageButton imageButton;
    private ImageView icon_groups;
    private My_Camera mycamera;

    /**
     * ?????????????????????
     */
    private boolean callingparty = false;
    private accordingdate accordingdate;
    private UserInfo userInfo;
    private Handler mTimeHandler;
    private HandlerThread mTimeHandlerThread;

    /**
     * ????????????????????????
     */
    private List<UserModel> mCallUserModelList = new ArrayList<>();             // ????????????????????????
    private final Map<String, UserModel> mCallUserModelMap = new HashMap<>();   // ????????????????????????
    private UserModel mSelfModel;                                               // ???????????????
    private UserModel mSponsorUserModel;                                        // ??????????????????
    private List<UserModel> mOtherInvitingUserModelList;                        // ??????????????????

    private int mCallType;
    private ITRTCAVCall mITRTCAVCall;
    private boolean isHandsFree = true;
    private boolean isMuteMic = false;
    private boolean isFrontCamera = true; //?????????????????????????????????????????????
    private boolean iscallrontCamera = false; //???????????????
    private String mGroupId;          //??????ID
    private Vibrator mVibrator;       //Vibrator(?????????)??????????????????????????????

    private MediaPlayHelper mMediaPlayHelper;
    private Ringtone mRingtone;      //??????
    private play plays;//????????????
    private boolean mEnableMuteMode = false;  // ????????????????????????
    private String mCallingBellPath = "";     // ??????????????????

    /**
     * ???????????????
     */
    private final TRTCAVCallListener mTRTCAVCallListener = new TRTCAVCallListener() {
        @Override
        public void onError(int code, String msg) {
            //??????????????????????????????????????????
            finishActivity();
        }

        @Override
        public void onInvited(String sponsor, List<String> userIdList, boolean isFromGroup, int callType) {
            Log.d(TAG, "?????????????????????: " + sponsor + " " + userIdList + " " + isFromGroup + " " + callType);
        }

        @Override
        public void onGroupCallInviteeListUpdate(List<String> userIdList) {
            Log.d(TAG, "??????IM????????????????????????????????????????????????????????????????????????: ");
        }

        @Override
        public void onUserEnter(final String userId) {
            Log.d(TAG, "onUserEnter: ?????????????????????" + userId);
            mSponsorUserModel = new UserModel();
            mSponsorUserModel.userId = userId;
            iscallrontCamera = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showCallingView();
                    //1.?????????????????????????????????????????????
                    UserModel model = new UserModel();
                    model.userId = userId;
                    model.phone = "";
                    model.userName = userId;
                    model.userAvatar = "";
                    mCallUserModelList.add(model);
                    mCallUserModelMap.put(model.userId, model);
                    TRTCVideoLayout videoLayout = addUserToManager(model);
                    if (videoLayout == null) {
                        return;
                    }
                    videoLayout.setVideoAvailable(false);

                    //2. ???????????????????????????????????????????????????????????????????????????
                    ProfileManager.getInstance().getUserInfoByUserId(userId, new ProfileManager.GetUserInfoCallback() {
                        @Override
                        public void onSuccess(UserModel model) {
                            UserModel oldModel = mCallUserModelMap.get(model.userId);
                            if (oldModel != null) {
                                //??????????????????
                                oldModel.userName = model.userName;
                                oldModel.userAvatar = model.userAvatar;
                                oldModel.phone = model.phone;
                                TRTCVideoLayout videoLayout = mLayoutManagerTrtc.findCloudViewView(model.userId);
                                if (videoLayout != null) {
                                    GlideEngine.loadCornerImage(videoLayout.getHeadImg(), oldModel.userAvatar, null, RADIUS);
                                    videoLayout.getUserNameTv().setText(oldModel.userName);
                                }
                            }
                        }

                        @Override
                        public void onFailed(int code, String msg) {
                            ToastUtil.toastLongMessage(getString(R.string.get_user_info_tips_before) + userId + getString(R.string.get_user_info_tips_after));
                        }
                    });
                }
            });


        }

        @Override
        public void onUserLeave(final String userId) {
            Log.d(TAG, "onUserLeave: ???????????????????????????" + userId);
            mSponsorUserModel = new UserModel();
            mSponsorUserModel.userId = userId;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //1. ??????????????????
                    mLayoutManagerTrtc.recyclerCloudViewView(userId);
                    //2. ????????????model
                    UserModel userModel = mCallUserModelMap.remove(userId);
                    if (userModel != null) {
                        mCallUserModelList.remove(userModel);
                    }
                }
            });
        }

        @Override
        public void onReject(final String userId) {
            Log.d(TAG, "onReject: ?????????????????????" + userId);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCallUserModelMap.containsKey(userId)) {
                        // ??????????????????
                        //1. ??????????????????
                        mLayoutManagerTrtc.recyclerCloudViewView(userId);
                        //2. ????????????model
                        UserModel userModel = mCallUserModelMap.remove(userId);
                        if (userModel != null) {
                            mCallUserModelList.remove(userModel);
                            ToastUtil.toastLongMessage(userModel.userName + getString(R.string.reject_calls));
                        }
                    }
                }
            });
        }

        @Override
        public void onNoResp(final String userId) {
            Log.d(TAG, "onNoResp: ?????????????????????" + userId);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCallUserModelMap.containsKey(userId)) {
                        // ?????????????????????
                        //1. ??????????????????
                        mLayoutManagerTrtc.recyclerCloudViewView(userId);
                        //2. ????????????model
                        UserModel userModel = mCallUserModelMap.remove(userId);
                        if (userModel != null) {
                            mCallUserModelList.remove(userModel);
                            ToastUtil.toastLongMessage(userModel.userName + getString(R.string.no_response));
                        }
                    }
                }
            });
        }

        @Override
        public void onLineBusy(String userId) {
            Log.d(TAG, "onLineBusy: ???????????????");
            if (mCallUserModelMap.containsKey(userId)) {
                // ?????????????????????
                //1. ??????????????????
                mLayoutManagerTrtc.recyclerCloudViewView(userId);
                //2. ????????????model
                UserModel userModel = mCallUserModelMap.remove(userId);
                if (userModel != null) {
                    mCallUserModelList.remove(userModel);
                    ToastUtil.toastLongMessage(userModel.userName + getString(R.string.line_busy));
                }
            }
        }

        @Override
        public void onCallingCancel() {
            Log.d(TAG, "???????????????????????????????????????????????????????????????????????????");
            if (mSponsorUserModel != null) {
                ToastUtil.toastLongMessage(mSponsorUserModel.userName + getString(R.string.cancle_calling));
            }
            finishActivity();
        }

        @Override
        public void onCallingTimeout() {
            Log.d(TAG, "??????????????????????????????????????????????????????????????????????????????");
            if (mSponsorUserModel != null) {
                ToastUtil.toastLongMessage(mSponsorUserModel.userName + getString(R.string.call_time_out));
            }
            finishActivity();
        }

        @Override
        public void onCallEnd() {
            Log.d(TAG, "???????????????????????????");
            deduction();
            finishActivity();

        }

        @Override
        public void onUserVideoAvailable(final String userId, final boolean isVideoAvailable) {
            Log.d(TAG, "true:???????????????????????????  false:???????????????????????????");
            TRTCVideoLayout layout = mLayoutManagerTrtc.findCloudViewView(userId);
            if (layout != null) {
                layout.setVideoAvailable(isVideoAvailable);
                if (isVideoAvailable) {
                    mITRTCAVCall.startRemoteView(userId, layout.getVideoView());
                } else {
                    mITRTCAVCall.stopRemoteView(userId);
                }
            }
        }

        @Override
        public void onUserAudioAvailable(String userId, boolean isVideoAvailable) {
            Log.d(TAG, " true:???????????????????????????  false:???????????????????????????");
        }

        @Override
        public void onUserVoiceVolume(Map<String, Integer> volumeMap) {
            for (Map.Entry<String, Integer> entry : volumeMap.entrySet()) {
                String userId = entry.getKey();
                TRTCVideoLayout layout = mLayoutManagerTrtc.findCloudViewView(userId);
                if (layout != null) {
                    layout.setAudioVolumeProgress(entry.getValue());
                }
            }
        }

        @Override
        public void getgiftInfo(GiftInfo giftInfo) {
            //???????????????????????????
            if (!TextUtils.isEmpty(giftInfo.giftPicUrl)) {
                playsavglotti.showGift(giftInfo);
            }

        }

        @Override
        public void longrangecall(String json) {

        }
    };

    private Group mSponsorGroup;
    private DefaultGiftAdapterImp mGiftAdapter;
    private GiftInfoDataHandler mGiftInfoDataHandler;
    private TRTCLiveRoomDef.LiveAnchorInfo mAnchorInfo;
    private String mSelfUserId;
    private TRTCVideoLayout videoLayout;

    /**
     * ???????????????????????????
     *
     * @param context
     * @param models
     */
    public static void startCallSomeone(Context context, List<UserModel> models) {
        ((TRTCAVCallImpl) TRTCAVCallImpl.sharedInstance(context)).setWaitingLastActivityFinished(false);
        Intent starter = new Intent(context, TRTCVideoCallActivity.class);
        starter.putExtra(PARAM_TYPE, TYPE_CALL);
        starter.putExtra(PARAM_USER, new IntentParams(models));
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

    /**
     * ???????????????????????????
     * ????????????
     *
     * @param context
     * @param models
     */
    public static void startCallSomePeople(Context context, List<UserModel> models, String groupId) {
        TUIKitLog.i(TAG, "startCallSomePeople");
        ((TRTCAVCallImpl) TRTCAVCallImpl.sharedInstance(context)).setWaitingLastActivityFinished(false);
        Intent starter = new Intent(context, TRTCVideoCallActivity.class);
        starter.putExtra(PARAM_GROUP_ID, groupId);
        starter.putExtra(PARAM_TYPE, TYPE_CALL);
        starter.putExtra(PARAM_USER, new IntentParams(models));
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);

        Log.d(TAG, "startCallSomePeople: " + groupId);
    }

    /**
     * ??????????????????
     *
     * @param context
     * @param beingCallUserModel
     */
    public static void startBeingCall(Context context, UserModel beingCallUserModel, List<UserModel> otherInvitingUserModel) {
        ((TRTCAVCallImpl) TRTCAVCallImpl.sharedInstance(context)).setWaitingLastActivityFinished(false);
        Intent starter = new Intent(context, TRTCVideoCallActivity.class);
        starter.putExtra(PARAM_TYPE, TYPE_BEING_CALLED);
        starter.putExtra(PARAM_BEINGCALL_USER, beingCallUserModel);
        starter.putExtra(PARAM_OTHER_INVITING_USER, new IntentParams(otherInvitingUserModel));
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfo = UserInfo.getInstance();
        accordingdate = new accordingdate(80, 80, TYPE_CALL); //??????80??????1?????? ???????????????VIP??????

        mCallType = getIntent().getIntExtra(PARAM_TYPE, TYPE_BEING_CALLED);
        if (mCallType == TYPE_BEING_CALLED && ((TRTCAVCallImpl) TRTCAVCallImpl.sharedInstance(this)).isWaitingLastActivityFinished()) {
            // ??????????????????????????????Activity????????????bug???????????????????????????????????????????????????????????????????????????????????????????????????????????????
            // ????????????????????????????????????Activity??????finish?????????????????????????????????Activity???????????????????????????Activity?????????????????????
            finishActivity();
            return;
        }

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancelAll();
        }

        // ?????????????????????????????????????????????
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE); //???????????????
        setContentView(R.layout.videocall_activity_online_call);

        //PermissionUtils.checkPermission(this, Manifest.permission.CAMERA);      //??????????????????
        //PermissionUtils.checkPermission(this, Manifest.permission.RECORD_AUDIO); //??????????????????

        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);//?????????
        //mRingtone = RingtoneManager.getRingtone(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)); //??????
        //?????????????????????

        //??????????????????
        plays = play.cerplay(TUIKit.getAppContext());
        //??????????????????
        mMediaPlayHelper = new MediaPlayHelper(TUIKit.getAppContext());

        initView();
        initData();
        initListener();
    }

    private void finishActivity() {
        ((TRTCAVCallImpl) TRTCAVCallImpl.sharedInstance(this)).setWaitingLastActivityFinished(true);
        play.cerplay(TUIKit.getAppContext()).btnDestroy();
        playHangupMusic();
        stopMusic();
        finish();

    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ??????????????????");
        deduction();
        if (mITRTCAVCall != null) {
            if (iscallrontCamera) {
                mITRTCAVCall.hangup();
            } else {
                mITRTCAVCall.reject();
            }
        }
        super.onBackPressed();
        finishActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        callService.isboocall = false; //????????????????????????
        if (mVibrator != null) {
            mVibrator.cancel();
        }
        if (mRingtone != null) {
            mRingtone.stop();
        }
        if (mITRTCAVCall != null) {
            mITRTCAVCall.closeCamera();
            mITRTCAVCall.removeListener(mTRTCAVCallListener);
        }
        stopTimeCount();
        if (mTimeHandlerThread != null) {
            mTimeHandlerThread.quit();
        }

    }

    /**
     * ??????????????????
     */
    private void initListener() {
        mMuteLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMuteMic = !isMuteMic;
                mITRTCAVCall.setMicMute(isMuteMic);
                mMuteImg.setActivated(isMuteMic);
                ToastUtil.toastLongMessage(isMuteMic ? getString(R.string.open_silent) : getString(R.string.close_silent));
            }
        });

        //????????????
        mHandsfreeLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHandsFree = !isHandsFree;
                mITRTCAVCall.setHandsFree(isHandsFree);
                mHandsfreeImg.setActivated(isHandsFree);
                ToastUtil.toastLongMessage(isHandsFree ? getString(R.string.use_speakers) : getString(R.string.use_handset));
            }
        });
        mMuteImg.setActivated(isMuteMic);
        mHandsfreeImg.setActivated(isHandsFree);

        //????????????????????????????????????
        imageButton.setOnClickListener(v -> {
            isFrontCamera = isFrontCamera ? false : true;
            mITRTCAVCall.switchCamera(isFrontCamera);
        });
    }

    /**
     * ????????????????????????
     */
    private void startInviting() {
        List<String> list = new ArrayList<>();
        for (UserModel userModel : mCallUserModelList) {
            list.add(userModel.userId);
        }
        //??????????????????userModel.userId????????????????????????
        Log.d(TAG, "startInviting: " + mGroupId);
        mITRTCAVCall.groupCall(list, ITRTCAVCall.TYPE_VIDEO_CALL, mGroupId);
    }

    /**
     * ???????????????
     */
    private void initView() {
        mMuteImg = (ImageView) findViewById(R.id.img_mute);
        mMuteLl = (LinearLayout) findViewById(R.id.ll_mute);
        mHangupImg = (ImageView) findViewById(R.id.img_hangup);
        mHangupLl = (LinearLayout) findViewById(R.id.ll_hangup);
        mHandsfreeImg = (ImageView) findViewById(R.id.img_handsfree);
        mHandsfreeLl = (LinearLayout) findViewById(R.id.ll_handsfree);
        mDialingImg = (ImageView) findViewById(R.id.img_dialing);
        mDialingLl = (LinearLayout) findViewById(R.id.ll_dialing);
        mDialingLl1 = (LinearLayout) findViewById(R.id.ll_dialing1);
        playsavglotti = (PlaySavgLotti) findViewById(R.id.playsavglotti);
        mLayoutManagerTrtc = (TRTCVideoLayoutManager) findViewById(R.id.trtc_layout_manager);
        mInvitingGroup = (Group) findViewById(R.id.group_inviting);
        mImgContainerLl = (LinearLayout) findViewById(R.id.ll_img_container);
        mTimeTv = (TextView) findViewById(R.id.tv_time);
        mSponsorGroup = (Group) findViewById(R.id.group_sponsor);
        camraitem = findViewById(R.id.camraitem);
        mycamera = findViewById(R.id.mycamera);
        imageButton = findViewById(R.id.btn_switch_cam);
        icon_groups = findViewById(R.id.icon_groups);


    }

    /**
     * ?????????????????????
     */
    private void initData() {
        mITRTCAVCall = TRTCAVCallImpl.sharedInstance(this);
        mITRTCAVCall.addListener(mTRTCAVCallListener);
        mTimeHandlerThread = new HandlerThread("time-count-thread");
        mTimeHandlerThread.start();
        mTimeHandler = new Handler(mTimeHandlerThread.getLooper());

        //???????????????????????????
        Intent intent = getIntent();
        mSelfModel = ProfileManager.getInstance().getUserModel();
        mCallType = intent.getIntExtra(PARAM_TYPE, TYPE_BEING_CALLED);
        mGroupId = intent.getStringExtra(PARAM_GROUP_ID);
        mAnchorInfo = new TRTCLiveRoomDef.LiveAnchorInfo();

        if (mCallType == TYPE_BEING_CALLED) {
            //??????????????????(?????????????????????)
            callingparty = false;
            mSponsorUserModel = (UserModel) intent.getSerializableExtra(PARAM_BEINGCALL_USER);
            mAnchorInfo.userId = mSponsorUserModel.userId;
            mSelfUserId = UserInfo.getInstance().getUserId();
            IntentParams params = (IntentParams) intent.getSerializableExtra(PARAM_OTHER_INVITING_USER);
            if (params != null) {
                mOtherInvitingUserModelList = params.mUserModels;
            }
            showWaitingResponseView();

            //?????????????????????
            accordingdate.dateinit(mSelfModel); //????????????ID

            //Vibrator?????????
            //Vibrator.vibrate(300);
            mVibrator.vibrate(new long[]{0, 1000, 1000}, 0); //?????????
            //mRingtone.play();                                   //????????????

            //??????????????????
            if (mSponsorUserModel.floating) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDialingLl.performClick();
                    }
                }, 200);
            }

        } else {
            //???????????????????????????????????????????????????
            callingparty = true;
            IntentParams params = (IntentParams) intent.getSerializableExtra(PARAM_USER);
            if (params != null) {
                mCallUserModelList = params.mUserModels;
                for (UserModel userModel : mCallUserModelList) {
                    mCallUserModelMap.put(userModel.userId, userModel);
                    mAnchorInfo.userId = userModel.userId;
                    mSelfUserId = UserInfo.getInstance().getUserId();

                    //?????????????????????
                    accordingdate.dateinit(userModel); //???????????????ID
                }
                startInviting();
                showInvitingView();
            }
        }

        //????????????mGiftAdapter??????
        mGiftAdapter = new DefaultGiftAdapterImp();
        mGiftInfoDataHandler = new GiftInfoDataHandler();
        mGiftInfoDataHandler.setGiftAdapter(mGiftAdapter);
    }

    /**
     * ??????????????????
     */
    public void showWaitingResponseView() {
        //2. ??????????????????????????????
        mSponsorGroup.setVisibility(View.VISIBLE);
        mycamera.setPath(mSponsorUserModel.userAvatar);
        mycamera.setname(TextUtils.isEmpty(mSponsorUserModel.userName) ? mSponsorUserModel.userId : mSponsorUserModel.userName);

        //3. ????????????????????????
        mHangupLl.setVisibility(View.VISIBLE);
        mDialingLl.setVisibility(View.VISIBLE);
        mHandsfreeLl.setVisibility(View.GONE);
        mMuteLl.setVisibility(View.GONE);
        camraitem.setVisibility(View.GONE);
        mDialingLl1.setVisibility(View.GONE);

        //3. ???????????????listener ????????????
        mHangupLl.setOnClickListener(v -> videoCallcancel());
        //????????????
        mDialingLl.setOnClickListener(v -> {
            //??????????????????|??????????????????
            if (PermissionUtils.checkPermission(this)) {
                checkAnswer();
            }
        });

        //4.????????????????????????
        showOtherInvitingUserView();

        //????????????
        startDialingMusic();

        //?????????????????????
        icon_groups.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(TUIKit.getAppContext())) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, RADIUS);
                } else {
                    //???????????????
                    ToastUtil.toastLongMessage("?????????????????????");
                    FloatingService.startBeingCall(TUIKit.getAppContext(), mSponsorUserModel);
                    smMediaPlayHelper();
                }
            }

        });

    }

    /**
     * ????????????????????????
     */
    public void showInvitingView() {
        //1. ???????????????????????????
        openCamera();

        //???????????????????????????
        for (UserModel userModel : mCallUserModelList) {
            camraitem.setVisibility(View.VISIBLE);
            camraitem.setImage(userModel.userAvatar);
            camraitem.setVideo_name(TextUtils.isEmpty(userModel.userName) ? userModel.userId : userModel.userName);
            mycamera.setVisibility(View.GONE);
        }


      /*  for (UserModel userModel : mCallUserModelList) {
            TRTCVideoLayout layout = addUserToManager(userModel);
            layout.getHeadImg().setVisibility(View.VISIBLE);
        }
*/

        //2. ???????????????
        mHangupLl.setVisibility(View.VISIBLE);
        mHangupLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mITRTCAVCall.hangup();
                finishActivity();
            }
        });
        mDialingLl.setVisibility(View.GONE);

        mHandsfreeLl.setVisibility(View.GONE);
        mMuteLl.setVisibility(View.GONE);
        mDialingLl1.setVisibility(View.GONE);

        //3. ??????????????????????????????
        hideOtherInvitingUserView();
        //4. sponsor???????????????
        mSponsorGroup.setVisibility(View.GONE);

        //5.????????????????????????
        if (plays != null) {
            try {
                plays.initMediaPlayer();
                plays.btnPlay();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ????????????????????????
     */
    public void showCallingView() {
        //1. ????????????
        mSponsorGroup.setVisibility(View.GONE);
        camraitem.setVisibility(View.GONE);
        mycamera.setVisibility(View.GONE);
        mDialingLl.setVisibility(View.GONE);

        //2. ???????????????
        mHangupLl.setVisibility(View.VISIBLE);
        mDialingLl1.setVisibility(View.VISIBLE);
        mHandsfreeLl.setVisibility(View.VISIBLE);
        mMuteLl.setVisibility(View.VISIBLE);
        imageButton.setVisibility(View.VISIBLE);
        playsavglotti.setVisibility(View.VISIBLE);
        mHangupLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deduction();
                Log.d(TAG, "onClick: ????????????");
                mITRTCAVCall.hangup();
                finishActivity();
            }
        });
        mDialingLl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfo.gettRole() == 1) {
                    showGiftPanel();  //????????????
                } else {

                    //???????????????????????????????????????????????????????????????
                    accordingdate.goldcoincallback(new accordingdate.Callback() {
                        @Override
                        public void OnSuccess() {
                            //??????????????????5??????????????????????????????
                            if (accordingdate.getTimeCount() <= 5) {
                                ToastUtil.toastLongMessage(getString(R.string.tv_msg_call_video));
                            } else {
                                showGiftPanel();//????????????
                            }
                        }

                        @Override
                        public void onFall() {

                        }
                    });
                }


            }
        });
        showTimeCount();
        hideOtherInvitingUserView();

        //????????????????????????
        if (plays != null) {
            plays.btnStop();
            stopMusic();
        }
    }

    /**
     * ????????????????????????
     */
    private void showTimeCount() {
        if (mTimeRunnable != null) {
            return;
        }
        mTimeCount = 0;
        mTimeTv.setText(getShowTime(mTimeCount));
        if (mTimeRunnable == null) {
            mTimeRunnable = new Runnable() {
                @Override
                public void run() {
                    mTimeCount++;
                    TimeCount(mTimeCount);
                }
            };
        }
        mTimeHandler.postDelayed(mTimeRunnable, 1000);
    }

    /**
     * ????????????UI????????????
     */
    private void TimeCount(int mTimeCount) {

        boolean iscall = (callingparty && accordingdate.timeout(mTimeCount) && userInfo.gettRole() == 0);

        if (mTimeTv != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //??????????????? ??????????????????2?????? callingparty=true;
                    if (iscall) {
                        mTimeTv.setText(String.format(getString(R.string.tv_call_video), accordingdate.PshowTime(mTimeCount)));  //??????????????????
                    } else {
                        mTimeTv.setText(getShowTime(mTimeCount));  //??????????????????
                    }
                }
            });
        }

        //????????????????????????
        if (iscall) {
            Log.d(TAG, "????????????????????????");
            deduction();
            finishhangup();
            mTimeHandler.removeCallbacks(mTimeRunnable);
            mTimeHandler.removeCallbacksAndMessages(null);
            return;
        }
        mTimeHandler.postDelayed(mTimeRunnable, 1000);

    }

    /**
     * ????????????
     */
    private void stopTimeCount() {
        if (mTimeHandler != null) {
            mTimeHandler.removeCallbacks(mTimeRunnable);
        }
        mTimeRunnable = null;
    }

    /**
     * ???????????????????????????
     *
     * @param count
     * @return
     */
    private String getShowTime(int count) {
        return String.format("%02d:%02d", count / 60, count % 60);
    }

    private void showOtherInvitingUserView() {
        if (mOtherInvitingUserModelList == null || mOtherInvitingUserModelList.size() == 0) {
            return;
        }
        mInvitingGroup.setVisibility(View.VISIBLE);
        int squareWidth = getResources().getDimensionPixelOffset(R.dimen.small_image_size);
        int leftMargin = getResources().getDimensionPixelOffset(R.dimen.small_image_left_margin);
        for (int index = 0; index < mOtherInvitingUserModelList.size() && index < MAX_SHOW_INVITING_USER; index++) {
            UserModel userModel = mOtherInvitingUserModelList.get(index);
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(squareWidth, squareWidth);
            if (index != 0) {
                layoutParams.leftMargin = leftMargin;
            }
            imageView.setLayoutParams(layoutParams);
            GlideEngine.loadCornerImage(imageView, userModel.userAvatar, null, RADIUS);
            mImgContainerLl.addView(imageView);
        }
    }

    private void hideOtherInvitingUserView() {
        mInvitingGroup.setVisibility(View.GONE);
    }

    private TRTCVideoLayout addUserToManager(UserModel userModel) {
        TRTCVideoLayout layout = mLayoutManagerTrtc.allocCloudVideoView(userModel.userId);
        if (layout == null) {
            return null;
        }
        layout.getUserNameTv().setText(userModel.userName);
        if (!TextUtils.isEmpty(userModel.userAvatar)) {
            GlideEngine.loadCornerImage(layout.getHeadImg(), userModel.userAvatar, null, RADIUS);
        }
        return layout;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.requestCode) {
            if (PermissionUtils.checkPermission(TUIKit.getAppContext(), Constants.requestCode)) {
                checkAnswer();
            } else {
                ToastUtil.toastLongMessage(getString(R.string.ts_call_msg));
            }

        }

    }

    /**
     * ?????????????????????????????????
     */
    private void finishhangup() {
        if (mITRTCAVCall != null) {
            mITRTCAVCall.hangup();
        }
        finishActivity();
    }

    /**
     * ????????????
     */
    private void videoCallcancel() {

        if (mVibrator != null) {
            mVibrator.cancel();    //?????????
        }
        if (mRingtone != null) {
            mRingtone.stop();      //??????
        }
        mITRTCAVCall.reject(); //?????????????????????????????? {@link TRTCAVCallListener#onInvited } ????????????????????????????????????????????????
        finishActivity();      //??????activity
    }

    /**
     * ??????????????????
     */
    private void checkAnswer() {
        //???????????????VIP???????????? ????????????????????????
        if (userInfo.getSex().equals("1") && userInfo.getVip() == 0 && userInfo.gettRole() == 0) {
            dialogsvip.getdialogsvip(this, mSponsorUserModel, dialogcallBack);
            return;
        }
        stopMusic();
        stopmusicplay();
        mITRTCAVCall.accept(); //????????????

        //??????????????????
        openCamera();

        //3????????????????????????
        showCallingView();

        //4????????????????????????????????????
        Postdeduction.setcallvideo(TYPE_CALL, mSelfModel.userId, mAnchorInfo.userId);

    }

    /**
     * ????????????????????????????????????
     */
    public void deduction() {
        if (mTimeCount > 0) {
            double kfmoney = accordingdate.kfmoney(mTimeCount);
            Log.d(TAG, "????????????:" + kfmoney + "TYPE:" + TYPE_CALL);
            //??????????????????
            Postdeduction.deduction(callingparty, mSelfModel.userId, mSponsorUserModel.userId, String.valueOf(kfmoney), TYPE_CALL);
        }
    }

    private DialogcallBack dialogcallBack = new DialogcallBack() {
        @Override
        public void onSuccess() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    videoCallcancel();
                }
            });
        }

        @Override
        public void onError() {

        }
    };

    /**
     * ?????????????????? ????????????
     */
    private void showGiftPanel() {
        //?????????????????????ID???????????????id?????????????????? ????????????????????????dialog
        IGiftPanelView giftPanelView = new GiftPanelViewImp(this);
        giftPanelView.init(mGiftInfoDataHandler); //??????????????????
        giftPanelView.setGiftPanelUser(mAnchorInfo, mSelfUserId); // mAnchorInfo//???????????? //??????mSelfUserId
        //???????????????????????????????????????
        giftPanelView.setGiftPanelDelegate(new GiftPanelDelegate() {
            @Override
            public void onGiftItemClick(GiftInfo giftInfo) {
                ToastUtil.toastLongMessage(giftInfo.title);
                giftInfo.sendUser = userInfo.getName();
                giftInfo.sendUserHeadIcon = userInfo.getAvatar();
                giftInfo.userid = userInfo.getUserId();
                mITRTCAVCall.invitereceiver(mAnchorInfo.userId, new Gson().toJson(giftInfo));
                //????????????????????????
                if (!TextUtils.isEmpty(giftInfo.giftPicUrl)) {
                    //??????????????????
                    playsavglotti.showGift(giftInfo);
                }
            }

            @Override
            public void onChargeClick() {
                //????????????????????????

            }

            @Override
            public void myoney(Object obj) {
                //?????????????????????????????????
                double momey = (double) obj;
                if (momey > 0) {
                    accordingdate.setMomey(momey);
                }
            }

        });
        giftPanelView.show();
    }


    /**
     * 1. ???????????????????????????
     */
    public void openCamera() {
        mLayoutManagerTrtc.setMySelfUserId(mSelfModel.userId);
        videoLayout = addUserToManager(mSelfModel);
        if (videoLayout == null) {
            return;
        }
        videoLayout.setVideoAvailable(true);
        //???????????????
        mITRTCAVCall.openCamera(true, videoLayout.getVideoView());

        //???????????????????????????????????????
        //mITRTCAVCall.closeCamera();
        //?????????????????????????????????????????????
        //mITRTCAVCall.switchCamera(true);
    }

    /****************************************************************************************/
    /**
     * ??????????????????
     */
    private void startDialingMusic() {
        if (null == mMediaPlayHelper) {
            return;
        }
        mMediaPlayHelper.start(R.raw.ring_tone, 30000);
    }

    /**
     * ????????????????????????
     */
    private void stopMusic() {
        if (null == mMediaPlayHelper) {
            return;
        }
        final int resId = mMediaPlayHelper.getResId();
        // ???????????????????????????????????????????????????????????????????????????stop
        if (resId != R.raw.hang_up) {
            mMediaPlayHelper.stop();
        }
    }

    /**
     * ???????????????????????????2???
     */
    private void playHangupMusic() {
        if (null == mMediaPlayHelper) {
            return;
        }
        mMediaPlayHelper.start(R.raw.hang_up, 2000);
    }

    private void stopmusicplay() {
        if (mVibrator != null) {
            mVibrator.cancel();    //?????????
        }
        if (mRingtone != null) {
            mRingtone.stop();      //??????
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RADIUS:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(TUIKit.getAppContext())) {
                        ToastUtil.toastLongMessage(getString(R.string.call_a3));
                    } else {
                        ToastUtil.toastLongMessage(getString(R.string.call_a4));
                    }
                }
                break;

        }
    }

    /**
     * ???????????????
     */
    public void smMediaPlayHelper() {
        if (mMediaPlayHelper != null) {
            mMediaPlayHelper.stop();
            mMediaPlayHelper = null;
            play.cerplay(TUIKit.getAppContext()).btnDestroy();
        }
        finish();
    }

}
