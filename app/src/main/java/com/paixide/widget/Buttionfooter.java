package com.paixide.widget;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.VIBRATOR_SERVICE;

import static com.tencent.qcloud.costransferpractice.object.ObjectActivity.ACTIVITY_STATE;
import static com.tencent.qcloud.costransferpractice.object.ObjectActivity.ACTIVITY_TOKEN;
import static com.tencent.qcloud.costransferpractice.object.ObjectActivity.ACTIVITY_TYPE;
import static com.tencent.qcloud.costransferpractice.object.ObjectActivity.ACTIVITY_USERID;
import static com.tencent.qcloud.costransferpractice.object.ObjectActivity.ACTIVITY_VIDEO;
import static com.tencent.qcloud.costransferpractice.object.ObjectActivity.ACTIVITY_VIP;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amap.api.location.AMapLocation;
import com.paixide.IMtencent.menu.AddMoreActivity;
import com.paixide.Module.Datamodule;
import com.paixide.Module.api.Config_Msg;
import com.paixide.Module.api.Config_User;
import com.paixide.Module.api.version;
import com.paixide.R;
import com.paixide.Receiver.videoService;
import com.paixide.Util.ActivityLocation;
import com.paixide.Util.StatusBarUtil;
import com.paixide.Util.Toashow;
import com.paixide.Util.config;
import com.paixide.activity.Aboutus.activity_viecode;
import com.paixide.activity.Main.HomeMain;
import com.paixide.amap.lbsamap;
import com.paixide.dialog.dialog_sayhello;
import com.paixide.listener.Callback;
import com.paixide.listener.Paymnets;
import com.paixide.utils.Utils;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.imsdk.ext.message.TIMManagerExt;
import com.tencent.liteav.callService;
import com.tencent.opensource.model.Basicconfig;
import com.tencent.opensource.model.UserInfo;
import com.tencent.qcloud.costransferpractice.object.ObjectActivity;
import com.tencent.qcloud.costransferpractice.transfer.UploadActivity;
import com.tencent.qcloud.tim.uikit.component.NoticeLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.GroupChatManagerKit;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationManagerKit;
import com.tencent.qcloud.tim.uikit.utils.FileUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class Buttionfooter extends FrameLayout {
    private static final String TAG = Buttionfooter.class.getSimpleName();
    @BindView(R.id.tv_1)
    TextView tv_1;
    @BindView(R.id.tv_2)
    TextView tv_2;
    @BindView(R.id.tv_3)
    TextView tv_3;
    @BindView(R.id.tv_4)
    TextView tv_4;
    @BindView(R.id.tv_5)
    TextView tv_5;
    @BindView(R.id.img1)
    ImageView img1;
    @BindView(R.id.img2)
    ImageView img2;
    @BindView(R.id.img3)
    ImageView img3;
    @BindView(R.id.img4)
    ImageView img4;
    @BindView(R.id.img5)
    ImageView img5;
    @BindView(R.id.total_unread)
    TextView total_unread;
    @BindView(R.id.lin_2)
    LinearLayout lin_2;
    @BindView(R.id.home6)
    homefooter home6;
    @BindView(R.id.bootNavigation)
    LinearLayout bootNavigation;
    @BindView(R.id.mBottomLayout)
    LinearLayout mBottomLayout;
    @BindView(R.id.tv2)
    TextView tv2;

    private HomeMain homeMain;
    private Unbinder bind;
    private Datamodule datamodule;
    private Context context;
    private Activity activity;
    private Paymnets paymnets;
    /**
     * ????????????????????????????????????
     */
    private MyIBinder myIBinder = new MyIBinder();
    private AMapLocation mapLocation;

    public void setPaymnets(Paymnets paymnets) {
        this.paymnets = paymnets;
    }

    private int[] drawables = {R.mipmap.a_9, R.mipmap.a_m, R.mipmap.a_a, R.mipmap.a_i, R.mipmap.a_g, R.mipmap.a_e};
    private int[] drawable = {R.mipmap.a_, R.mipmap.a_n, R.mipmap.a_b, R.mipmap.a_j, R.mipmap.a_h, R.mipmap.a_f};

    public Buttionfooter(@NonNull Context context) {
        this(context, null);

    }

    public Buttionfooter(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public Buttionfooter(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.buttonfooter, this);
        bind = ButterKnife.bind(this);
        homeMain = new HomeMain();
        context = getContext();
        activity = (Activity) context;
        datamodule = new Datamodule(context);
        tv2.setText("??????");
        findViewById(R.id.lay3).setOnClickListener(v -> sUploadActivity(ACTIVITY_VIDEO));

        //?????????????????????
        FileUtil.initPath();

        //?????????????????????????????? ?????????????????? N?????????
        ConversationManagerKit.getInstance().addUnreadWatcher(messageUnreadWatcher);
        GroupChatManagerKit.getInstance();

        //??????????????????????????? ?????????????????????
        TIMManager.getInstance().addMessageListener(timMessageListener);

        //???????????????????????????????????????
        addsendchat();

        //????????????????????????20???????????????????????????
        datamodule.activemessage(null);

        if (ActivityLocation.checkpermissions(activity)) {
            lbsamap.getmyLocation(new Callback() {
                @Override
                public void onSuccess(AMapLocation map) {
                    mapLocation = map;
                }
            });
        }

        Config_Msg configMsg = Config_Msg.getInstance();

        if (!configMsg.isOne()) {
            configMsg.setMessage_shock(false); //????????????
            configMsg.setMessage_voice(false); //????????????
            configMsg.setOne(true);           //???????????? true
        }
    }

    public void result(int result) {
        switch (result) {
            case 1:
                findViewById(R.id.lin_1).performClick();
                break;
            case 2:
                findViewById(R.id.lin_2).performClick();
                break;
            case 3:
                findViewById(R.id.lin_3).performClick();
                break;
            case 4:
                findViewById(R.id.lin_4).performClick();
                break;
            case 5:
                findViewById(R.id.lin_5).performClick();
                break;
        }
    }

    /**
     * ???????????????
     */
    private void setdrawable() {
        selectDrawableTop(img1, drawable[0], null);
        selectDrawableTop(img2, drawable[1], null);
        selectDrawableTop(img3, drawable[2], null);
        selectDrawableTop(img4, drawable[3], null);
        selectDrawableTop(img5, drawable[4], null);
        home6.setImage(null, drawable[5], 0);

        //????????????????????????
        lin_2.setVisibility(UserInfo.getInstance().getLive() == 1 ? GONE : VISIBLE);

        //???????????????
        home6.setVisibility(UserInfo.getInstance().getLvideo() == 1 ? GONE : VISIBLE);

        //?????????
        StatusBarUtil.transparencyBar(activity, 2);

        //????????????
        bootNavigation.setBackgroundColor(Color.WHITE);
        StatusBarUtil.setNavigationBarColor(activity, Color.WHITE);
        mBottomLayout.setVisibility(GONE);
    }

    /**
     * ??????????????????
     *
     * @param image
     * @param drawable
     */
    private void selectDrawableTop(ImageView image, int drawable, TextView tv) {
        image.setImageResource(drawable);
        if (tv == null) {
            tv_1.setTextColor(getContext().getResources().getColor(R.color.home));
            tv_2.setTextColor(getContext().getResources().getColor(R.color.home));
            tv_3.setTextColor(getContext().getResources().getColor(R.color.home));
            tv_4.setTextColor(getContext().getResources().getColor(R.color.home));
            tv_5.setTextColor(getContext().getResources().getColor(R.color.home));
        } else {
            tv.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
        }


    }

    /**
     * ??????????????????
     *
     * @param fragment
     */
    private void Fragmentcommit(Fragment fragment) {

        //????????????????????????
        datamodule.getvip(paymnets);

        //???????????????????????????
        datamodule.version(appUpdate);

        //???????????????MainActivity
        paymnets.onSuccess(fragment);

    }

    /**
     * ???????????????????????????
     */
    public ConversationManagerKit.MessageUnreadWatcher messageUnreadWatcher = new ConversationManagerKit.MessageUnreadWatcher() {
        @Override
        public void updateUnread(int count) {
            dealMessageCount(count);
        }

    };

    /**
     * ???????????????????????????
     */
    private TIMMessageListener timMessageListener = new TIMMessageListener() {
        @Override
        public boolean onNewMessages(List<TIMMessage> list) {

            handler.removeCallbacks(messageRun);
            handler.postDelayed(messageRun, 500);

            List<TIMMessage> needMessages = new ArrayList<>();
            boolean hasOtherMessage = false;
            for (TIMMessage timMessage : list) {
                TIMConversation conversation = timMessage.getConversation();
                if (conversation != null && conversation.getType() == TIMConversationType.C2C && !TextUtils.isEmpty(conversation.getPeer()) && TextUtils.isDigitsOnly(conversation.getPeer())) {
                    if (!hasOtherMessage) {
                        hasOtherMessage = !timMessage.isSelf();
                    }
                    needMessages.add(timMessage);
                }
            }
            if (needMessages.size() > 0) {
                try {
                    if (hasOtherMessage) {
                        playMusicAndVibrate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    };

    @OnClick({R.id.lin_1, R.id.lin_2, R.id.lin_3, R.id.lin_4, R.id.lin_5, R.id.home6})
    public void onClick(View v) {
        setdrawable();
        switch (v.getId()) {
            case R.id.lin_1: {
                //??????
                selectDrawableTop(img1, drawables[0], tv_1);
                Fragmentcommit(homeMain.page1);
                dialogsayhello();
                break;
            }
            case R.id.lin_2: {
                //??????
                selectDrawableTop(img2, drawables[1], tv_2);
                Fragmentcommit(homeMain.page2);
                break;
            }
            case R.id.lin_3: {
                //??????
                selectDrawableTop(img3, drawables[2], tv_3);
                Fragmentcommit(homeMain.page3);
                break;
            }
            case R.id.lin_4: {
                //??????
                selectDrawableTop(img4, drawables[3], tv_4);
                Fragmentcommit(homeMain.page4);
                break;
            }
            case R.id.lin_5: {
                //??????
                selectDrawableTop(img5, drawables[4], tv_5);
                Fragmentcommit(homeMain.page5);
                break;
            }
            case R.id.home6: {
                //?????????
                Fragmentcommit(homeMain.page6);
                mBottomLayout.setVisibility(VISIBLE);
                home6.setImage(null, drawables[5], 1);
                //???????????????
                StatusBarUtil.transparencyBar(activity, 1);
                config.AsetctivityBLACK(activity);
                //??????????????????
                bootNavigation.setBackgroundColor(Color.BLACK);
                break;
            }
        }
    }

    /**
     * ???????????????????????????
     */
    private void dialogsayhello() {
        if (!Config_User.getInstance().isSendout() && Utils.checkPermission2(activity)) {
            dialog_sayhello.myshow(context, new Paymnets() {
                @Override
                public void onSuccess() {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.lin_4).performClick();
                        }
                    }, 200);
                }
            });
        }
    }

    /**
     * ??????????????????APP???????????????
     */
    private void addsendchat() {
        Config_User configUser = Config_User.getInstance();
        if (configUser.getKeaddmsg() == 0) {
            //??????????????????????????? ???????????????????????? basiconfig
            datamodule.basicconfig(0, callback);
        } else {
            //????????????????????????????????? ???????????????????????? basiconfig
            datamodule.basicconfig(configUser.getKeaddmsg(), callback);
        }

        UserInfo userInfo = UserInfo.getInstance();
        Config_Msg configMsg = Config_Msg.getInstance();

        //???????????????????????? ??????????????? ??????|??????|??????|??????
        if (userInfo.getSex().equals("2") && userInfo.gettRole() == 1 && userInfo.getState() == 2) {
            Log.d(TAG, "?????????????????????????????????");
            if (configMsg.isIsswitch()) {
                callService.startCall(null);
            }

        }

    }

    /**
     * ????????????ID?????????
     */
    private Paymnets callback = new Paymnets() {
        @Override
        public void onFail() {

        }

        @Override
        public void onSuccess(Object object) {
            Basicconfig basicconfig = (Basicconfig) object;
            String kefu = basicconfig.getKefu();
            //????????????????????????ID
            if (!TextUtils.isEmpty(kefu)) {
                String[] split = kefu.split("\\|");
                AddMoreActivity.checkFriend(Arrays.asList(split));
                if (!TextUtils.isEmpty(split[0])) {
                    Config_User.getInstance().setUserid(split[0]);
                }
            }

            //??????????????????????????????
            Config_User.getInstance().setKeaddmsg(1);
        }
    };

    /**
     * APP???????????????????????????
     */
    private Paymnets appUpdate = new Paymnets() {
        @Override
        public void onSuccess(Object object) {
            version version = (com.paixide.Module.api.version) object;
            int versionCode = config.getVersionCode();
            int verscode = version.getVersion();
            if (version != null) {
                if (verscode > versionCode) {
                    activity_viecode.showUpdaloadDialog(context, version);
                }
            }


        }

        @Override
        public void onFail() {

        }
    };

    public void funbind() {
        if (bind != null) {
            bind.unbind();
        }
        //????????????
        ConversationManagerKit.getInstance().destroyConversation();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d(TAG, "????????????: ");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // ?????????????????????
        Intent intent = new Intent(context, videoService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
        //??????????????????????????????????????????????????????kiis
        context.bindService(intent, myIBinder, BIND_AUTO_CREATE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            Log.d(TAG, "????????????: " + hasWindowFocus);
        } else {
            Log.d(TAG, "????????????: " + hasWindowFocus);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "??????????????????: ");
        //????????????
        context.stopService(new Intent(context, videoService.class));
        //??????????????????
        context.unbindService(myIBinder);

        //??????????????????
        TIMManager.getInstance().removeMessageListener(timMessageListener);

        //????????????
        ConversationManagerKit.getInstance().removeUnreadWatcher(messageUnreadWatcher);
    }

    /**
     * ???????????????????????????
     */
    public class MyIBinder implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            videoService.MyBinder myBinder = (videoService.MyBinder) service;
            myBinder.getdelayMillis();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    /**
     * ??????????????????
     */
    public void sUploadActivity(int TYPE) {
        if (!config.isNetworkAvailable()) {
            Toashow.show(context.getString(R.string.isNetworkAvailable));
            return;
        }
        UserInfo userInfo = UserInfo.getInstance();
        Intent intent = new Intent(context, UploadActivity.class);
        intent.putExtra(ACTIVITY_USERID, userInfo.getUserId());
        intent.putExtra(ACTIVITY_TYPE, TYPE);
        intent.putExtra(ACTIVITY_VIP, userInfo.getVip());
        intent.putExtra(ACTIVITY_STATE, userInfo.getState());
        intent.putExtra(ACTIVITY_TOKEN, userInfo.getToken());
        try {
            //GPS??????
            if (!TextUtils.isEmpty(mapLocation.getProvince())) {
                intent.putExtra(ObjectActivity.province, mapLocation.getProvince());
                intent.putExtra(ObjectActivity.city, mapLocation.getCity());
                intent.putExtra(ObjectActivity.district, mapLocation.getDistrict());
                intent.putExtra(ObjectActivity.address, mapLocation.getAddress());
                intent.putExtra(ObjectActivity.jwd, mapLocation.getLatitude() + "," + mapLocation.getLongitude());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        activity.startActivityForResult(intent, config.sussess);
    }

    /**
     * ???????????? ??????
     */
    private boolean isSoundPlaying;

    //???????????????
    private SoundPool mSoundPool;
    private int mSoundId;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable messageRun = new Runnable() {
        @Override
        public void run() {
            int timUnReadCount = getTIMUnReadCount();
            dealMessageCount(timUnReadCount);
        }
    };

    /**
     * ???????????? ??????
     */
    private void playMusicAndVibrate() {
        if (isSoundPlaying) {
            return;
        }
        isSoundPlaying = true;

        try {
            //????????????????????????
            boolean sound = Config_Msg.getInstance().isMessage_shock();
            if (sound) {
                if (mSoundPool == null) {
                    //?????????SoundPool
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        AudioAttributes aab = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build();
                        mSoundPool = new SoundPool.Builder().setMaxStreams(5).setAudioAttributes(aab).build();
                    } else {
                        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 8);
                    }

                    //????????????????????????
                    mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                        @Override
                        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                            if (mSoundPool != null && mSoundId > 0) {
                                mSoundPool.play(mSoundId, 1, 1, 0, 0, 1);
                            }
                        }
                    });

                    //??????deep ????????????
                    mSoundId = mSoundPool.load(context, R.raw.new_message, 1);
                } else {
                    if (mSoundId > 0) {
                        mSoundPool.play(mSoundId, 1, 1, 0, 0, 1);
                    }
                }
            }

            //??????????????????
            boolean vibrate = Config_Msg.getInstance().isMessage_voice();
            if (vibrate) {
                Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(400);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isSoundPlaying = false;
            }
        }, 1000);
    }

    /**
     * ??????????????????
     */
    private void dealMessageCount(int count) {
        String unreadStr = count > 100 ? "99+" : String.valueOf(count);
        total_unread.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        total_unread.setText(unreadStr);
    }

    /**
     * ??????IM???????????????
     */
    private int getTIMUnReadCount() {
        int count = 0;
        try {
            List<TIMConversation> conversationList = TIMManagerExt.getInstance().getConversationList();
            if (conversationList != null && conversationList.size() > 0) {
                for (TIMConversation timConversation : conversationList) {
                    //??????
                    if (TextUtils.isEmpty(timConversation.getPeer()) || !TextUtils.isDigitsOnly(timConversation.getPeer()) || timConversation.getType() != TIMConversationType.C2C) {
                        continue;
                    }
                    //??????bean
                    TIMConversationExt conExt = new TIMConversationExt(timConversation);
                    count += conExt.getUnreadMessageNum();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }


}
