package com.paixide.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.paixide.Module.Datamodule;
import com.paixide.Module.McallBack;
import com.paixide.Module.api.Config_Msg;
import com.paixide.Module.api.present;
import com.paixide.R;
import com.paixide.Util.Toashow;
import com.paixide.activity.Withdrawal.Detailedlist;
import com.paixide.activity.video.live.adapter.MessageAdapter;
import com.paixide.app.DemoApplication;
import com.paixide.dialog.dialog_item_gift;
import com.paixide.dialog.dialog_msg_svip;
import com.paixide.getlist.HotRooms;
import com.paixide.listener.Paymnets;
import com.pili.pldroid.player.widget.PLVideoView;
import com.tencent.opensource.model.UserInfo;
import com.tencent.qcloud.tim.tuikit.live.component.gift.GiftPanelDelegate;
import com.tencent.qcloud.tim.tuikit.live.component.gift.imp.GiftInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lib.homhomlib.view2.DivergeView;

/**
 * ?????????????????????
 */
public class Movie_zbativity extends FrameLayout implements View.OnClickListener {
    public TextView title, conum, tvgz, tv_chat;
    public ListView lv_message;
    public LinearLayout line1, linmsess;
    public ImageView circular, home1, home2, home3, home4;
    public EditText Mesges;
    public Button senbnt;

    private PLVideoView plVideoView;      //??????????????????????????????
    private boolean getsetVolume = false; //?????????????????????

    private NumberAnim giftNumberAnim;  //????????????
    private LinearLayout ll_gift_group; //????????????
    private TranslateAnimation outAnim; //????????????
    private TranslateAnimation inAnim;  //????????????
    public Timer timer;
    public TimerTask task;
    private Paymnets paymnets;
    private Context context;


    //??????????????????
    private List<String> messageData;
    private MessageAdapter messageAdapter;
    //????????????
    private Config_Msg instance;
    private UserInfo userInfo;
    //????????????
    public SVGAParser parser;
    public SVGAImageView svgaImageView;
    private static final String TAG = Movie_zbativity.class.getSimpleName();

    private HotRooms hotRooms;
    private DivergeView mDivergeView; //??????
    private ArrayList<Bitmap> mList;  //??????
    private int mIndex = 0;


    public Movie_zbativity(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public Movie_zbativity(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Movie_zbativity(Context context) {
        super(context);
        init();
    }

    public void setPaymnets(Paymnets paymnets) {
        this.paymnets = paymnets;
    }

    public void init() {
        inflate(getContext(), R.layout.activity_movie_zbativity2, this);
        iniview();
        ividate();
        Listener();

    }

    private void ividate() {
        userInfo = UserInfo.getInstance();
        instance = Config_Msg.getInstance();
        timer = new Timer();

        Log.d(TAG, "user_save_update_Profile: "+ UserInfo.getInstance().toString());

    }

    private void iniview() {
        context = getContext();
        line1 = findViewById(R.id.line1);
        title = findViewById(R.id.title);
        conum = findViewById(R.id.conum);
        lv_message = findViewById(R.id.lv_message);
        ll_gift_group = findViewById(R.id.ll_gift_group);
        circular = findViewById(R.id.circular);
        tv_chat = findViewById(R.id.tv_chat);
        tvgz = findViewById(R.id.tvgz);
        home1 = findViewById(R.id.home1);
        home2 = findViewById(R.id.home2);
        home3 = findViewById(R.id.home3);
        home4 = findViewById(R.id.home4);
        linmsess = findViewById(R.id.linmsess);
        Mesges = findViewById(R.id.Mesges);
        senbnt = findViewById(R.id.senbnt);

        //????????????
        lottie_view = findViewById(R.id.lottie_view);
        svgaImageView = findViewById(R.id.svgaImage);
        mDivergeView = findViewById(R.id.divergeView);
    }

    private void Listener() {
        tv_chat.setOnClickListener(this::onClick);
        senbnt.setOnClickListener(this::onClick);
        circular.setOnClickListener(this::onClick);
        tvgz.setOnClickListener(this::onClick);
        home1.setOnClickListener(this::onClick);
        home2.setOnClickListener(this::onClick);
        home3.setOnClickListener(this::onClick);
        home4.setOnClickListener(this::onClick);
        lv_message.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                linmsess.setVisibility(GONE);
                return false;
            }
        });
    }


    public void Movie(HotRooms hotRooms) {
        this.hotRooms = hotRooms;
        title.setText(hotRooms.nickName);
        int random = (int) (Math.random() * 1000 + 1);
        conum.setText(String.valueOf(random));
        Glide.with(DemoApplication.instance()).load(hotRooms.headerImageOriginal).override(50, 50).into(circular);
        initMessage(hotRooms.getNickName());//?????????????????????
        initAnim();                         //???????????????
        clearTiming();                      //??????????????????????????????

    }

    public boolean isGetsetVolume() {
        return getsetVolume;
    }

    public void setGetsetVolume(boolean getsetVolume) {
        this.getsetVolume = getsetVolume;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_chat: {
                //????????????
                linmsess.setVisibility(VISIBLE);
                Mesges.setFocusable(true);
                break;
            }

            case R.id.senbnt: {
                //????????????
                String trim = Mesges.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    ToastUtils.showLong(R.string.tv_msg170);
                    return;
                }
                messageData.add(getMesges());
                messageAdapter.NotifyAdapter(messageData);
                lv_message.setSelection(messageData.size());
                break;
            }

            case R.id.circular: {
                //????????????
                Toashow.show(getContext(), getContext().getString(R.string.tv_msg171));
                break;
            }

            case R.id.tvgz: {
                //????????????
                Toashow.show(getContext(), getContext().getString(R.string.tv_msg172));
                break;
            }

            case R.id.home1: {
                //???????????? 0f?????????????????????????????????????????? 1f?????????????????????????????????????????????
                if (!getsetVolume) {
                    getsetVolume = true;
                    plVideoView.setVolume(0f, 0f);
                    Toast.makeText(getContext(), getContext().getString(R.string.tv_msg173), Toast.LENGTH_SHORT).show();
                } else {
                    getsetVolume = false;
                    plVideoView.setVolume(1f, 100f);
                    Toast.makeText(getContext(), getContext().getString(R.string.tv_msg174), Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case R.id.home2: {
                //????????????
                if (userInfo.getJinbi() > 0 || userInfo.getVip() == 1) {
                    mDivergeViewstart();
                } else {
                    dialog_msg_svip.dialogmsgsvip(getContext(), context.getString(R.string.dialog_tv_msg224), context.getString(R.string.tv_msg228), context.getString(R.string.tv_msg154), new Paymnets() {
                        @Override
                        public void onSuccess() {
                            //???????????????????????????
                            Detailedlist.starsetAction(context);
                        }

                        @Override
                        public void onRefresh() {
                            //?????????????????????
                            McallBack.starsetAction(context);
                        }
                    });
                }
                break;
            }

            case R.id.home3: {
                //????????????????????????
                present present = new present();
                present.setTYPE(3);                       //???????????????3
                present.setTouserid("");                  //?????????ID
                present.setUserid(userInfo.getUserId());  //?????????????????????
                present.setName(hotRooms.nickName);       //?????????????????????
                //?????????????????????????????? LiveVideo->paymnets
                dialog_item_gift.dialogitemgift(context, present, giftPanelDelegate, paymnets);
                break;
            }

            case R.id.home4: {
                //????????????
                Activity activity = (Activity) context;
                activity.finish();
                break;
            }

        }
    }


    /**
     * ?????????????????????
     */
    private void initMessage(String nickName) {
        messageData = new LinkedList<>();
        try {
            messageData.add("????????????:" + (TextUtils.isEmpty(instance.getMsg()) ? "" : instance.getMsg().replace("{username}", TextUtils.isEmpty(nickName) ? "" : nickName)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        messageData.add((TextUtils.isEmpty(userInfo.getName()) ? userInfo.getUserId() : userInfo.getName()) + " ????????????");
        messageAdapter = new MessageAdapter(getContext(), messageData);
        lv_message.setAdapter(messageAdapter);
        lv_message.setSelection(messageData.size());
    }

    /**
     * ????????????
     *
     * @return
     */
    private String getMesges() {
        String Mesgesstr = Mesges.getText().toString().trim();
        Mesges.setText(null);
        return Mesgesstr;
    }


    /**
     * ??????????????????????????????
     */
    private void clearTiming() {
        task = new TimerTask() {
            @Override
            public void run() {
                int childCount = ll_gift_group.getChildCount();
                long nowTime = System.currentTimeMillis();
                for (int i = 0; i < childCount; i++) {
                    View childView = ll_gift_group.getChildAt(i);
                    ImageView iv_gift = childView.findViewById(R.id.iv_gift);
                    long lastUpdateTime = (long) iv_gift.getTag();
                    if (nowTime - lastUpdateTime >= 3000) {
                        RemoveGiftView(i); //????????????
                    }
                }
            }
        };

        timer.schedule(task, 0, 3000);
    }

    /**
     * ???????????????
     */
    private void initAnim() {
        giftNumberAnim = new NumberAnim(); // ?????????????????????
        inAnim = (TranslateAnimation) AnimationUtils.loadAnimation(getContext(), R.anim.gift_in); // ?????????????????????
        outAnim = (TranslateAnimation) AnimationUtils.loadAnimation(getContext(), R.anim.gift_out); // ?????????????????????
    }

    /**
     * ?????????????????????
     *
     * @param plVideoView
     */
    public void setPlay(PLVideoView plVideoView) {
        this.plVideoView = plVideoView;
    }

    /**
     * ?????????????????????????????????
     */
    public class NumberAnim {
        private Animator lastAnimator;

        public void showAnimator(View v) {
            if (lastAnimator != null) {
                lastAnimator.removeAllListeners();
                lastAnimator.cancel();
                lastAnimator.end();
            }
            ObjectAnimator animScaleX = ObjectAnimator.ofFloat(v, "scaleX", 2.5f, 1.3f);
            ObjectAnimator animScaleY = ObjectAnimator.ofFloat(v, "scaleY", 2.5f, 1.3f);
            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(animScaleX, animScaleY);
            animSet.setDuration(200);
            lastAnimator = animSet;
            animSet.start();
        }
    }

    /**
     * ????????? ????????????????????????
     *
     * @param giftInfo
     */
    private void showGift(GiftInfo giftInfo) {
        //?????????????????????????????? ????????????ID???
        View newGiftView = ll_gift_group.findViewWithTag(giftInfo.giftId);
        // ????????????tag???????????????
        if (newGiftView == null) {
            // ?????????????????????????????????3??????????????????????????????????????????????????????, ??????????????????????????????????????????????????????3???
            if (ll_gift_group.getChildCount() >= 3) {
                // ?????????2??????????????????????????????1
                View giftView01 = ll_gift_group.getChildAt(0);
                ImageView iv_gift01 = giftView01.findViewById(R.id.iv_gift);
                long lastTime1 = (long) iv_gift01.getTag();

                // ?????????2??????????????????????????????2
                View giftView02 = ll_gift_group.getChildAt(1);
                ImageView iv_gift02 = giftView02.findViewById(R.id.iv_gift);
                long lastTime2 = (long) iv_gift02.getTag();

                //???????????????????????????????????????
                RemoveGiftView(lastTime1 > lastTime2 ? 1 : 0);
            }
            //????????????VIEW????????????
            newGiftView = getNewGiftView(giftInfo);
            //?????????ll_gift_group
            ll_gift_group.addView(newGiftView);

            // ???????????????????????????
            newGiftView.startAnimation(inAnim);
            TextView mtv_giftNum = newGiftView.findViewById(R.id.mtv_giftNum);
            inAnim.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mtv_giftNum != null) {
                        //??????????????????
                        giftNumberAnim.showAnimator(mtv_giftNum);
                    }
                }

            });

        } else {
            // ??????????????????????????????????????????
            ImageView iv_gift = newGiftView.findViewById(R.id.iv_gift);
            iv_gift.setTag(System.currentTimeMillis());

            // ????????????????????????
            TextView mtv_giftNum = newGiftView.findViewById(R.id.mtv_giftNum);
            int giftCount = (int) mtv_giftNum.getTag() + 1; // ??????
            mtv_giftNum.setTag(giftCount);
            mtv_giftNum.setText("x" + giftCount);

            //???????????????????????????????????????
            giftNumberAnim.showAnimator(mtv_giftNum);
        }

        //??????????????????
        if (giftInfo.type == 1 && !TextUtils.isEmpty(giftInfo.lottieUrl)) {
            //??????????????????  ????????????????????????
            boolean json = giftInfo.lottieUrl.toLowerCase().endsWith(".json");
            boolean svga = giftInfo.lottieUrl.toLowerCase().endsWith(".svga");

            //??????????????????SVGAPlayer
            if (svga) {
                mAnimationUrlList2.add(giftInfo.lottieUrl); //??????????????????
                if (!mIsPlaying2) {
                    paySVGAParser();
                }
            }

            //??????????????????lottie
            if (json) {
                mAnimationUrlList.add(giftInfo.lottieUrl);  //??????????????????
                if (!mIsPlaying) {
                    playLottieAnimation();
                }
            }

        }
    }

    /**
     * ????????????????????????
     */
    private View getNewGiftView(GiftInfo giftInfo) {
        // ????????????, ???view??????layout????????????????????????????????????findViewWithTag?????????????????????
        View giftView = LayoutInflater.from(getContext()).inflate(R.layout.item_gift, null);
        giftView.setTag(giftInfo.giftId);
        // ?????????????????????????????????
        TextView mtv_giftNum = giftView.findViewById(R.id.mtv_giftNum);
        mtv_giftNum.setTag(1);
        mtv_giftNum.setText("x1");

        ImageView iv_gift = giftView.findViewById(R.id.iv_gift);
        ImageView icon = giftView.findViewById(R.id.cv_send_gift_userIcon);
        TextView tag_name = giftView.findViewById(R.id.tag_name);
        TextView gifname = giftView.findViewById(R.id.gifname);

        //???????????????????????????????????????  ??????(???????????????????????????????????????)
        String avatar = UserInfo.getInstance().getAvatar();
        String Name = UserInfo.getInstance().getName();
        String sex = UserInfo.getInstance().getSex();
        //????????????????????????
        if (!TextUtils.isEmpty(avatar)) {
            Glide.with(DemoApplication.instance()).load(avatar).into(icon);
        } else {
            icon.setImageResource(sex.equals("1") ? R.mipmap.ic_man_choose : R.mipmap.icon_woman_choose);
        }

        tag_name.setText(Name);
        gifname.setText("??????" + giftInfo.title);
        Glide.with(DemoApplication.instance()).load(giftInfo.giftPicUrl).into(iv_gift);
        //????????????, ??????????????????????????????????????????????????????????????????????????????
        iv_gift.setTag(System.currentTimeMillis());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 10;
        giftView.setLayoutParams(params);
        return giftView;
    }


    /**
     * ????????????????????????giftView
     */
    private void RemoveGiftView(int index) {
        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                int childCount = ll_gift_group.getChildCount();
                if (childCount > 0) {
                    ll_gift_group.removeViewAt(index);
                }
            }
        });
        final View removeGiftView = ll_gift_group.getChildAt(index);
        removeGiftView.startAnimation(outAnim);

        // ???????????????????????????????????????????????????????????????
        Activity activity = (Activity) getContext();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeGiftView.startAnimation(outAnim);
            }
        });

    }

    private LottieAnimationView lottie_view;
    public LinkedList<String> mAnimationUrlList = new LinkedList<>();
    public LinkedList<String> mAnimationUrlList2 = new LinkedList<>();
    private boolean mIsPlaying = false;
    private boolean mIsPlaying2 = false;

    /**
     * ???????????????
     */
    private void paySVGAParser() {
        try {
            String lottieUrl = mAnimationUrlList2.getFirst();
            if (!TextUtils.isEmpty(lottieUrl)) {
                mAnimationUrlList2.removeFirst();  //?????????????????????
            }
            if (parser == null) {
                parser = new SVGAParser(DemoApplication.instance());
            }
            parser.decodeFromURL(new URL(lottieUrl), new SVGAParser.ParseCompletion() {
                @Override
                public void onError() {
                }

                @Override
                public void onComplete(SVGAVideoEntity svgaVideoEntity) {
                    SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
                    if (svgaImageView != null) {
                        svgaImageView.setImageDrawable(drawable);
                        svgaImageView.startAnimation();
                        mIsPlaying2 = true;
                        svgaImageView.setCallback(new SVGACallback() {
                            @Override
                            public void onPause() {

                            }

                            @Override
                            public void onFinished() {
                                if (mAnimationUrlList2.isEmpty()) {
                                    if (svgaImageView != null) {
                                        svgaImageView.stopAnimation();
                                        mIsPlaying2 = false;
                                    }
                                } else {
                                    paySVGAParser();
                                }
                            }

                            @Override
                            public void onRepeat() {

                            }

                            @Override
                            public void onStep(int i, double v) {


                            }
                        });
                    }
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????????????????
     */
    private void playLottieAnimation() {
        String lottieUrl = mAnimationUrlList.getFirst();
        if (!TextUtils.isEmpty(lottieUrl)) {
            mAnimationUrlList.removeFirst();
            lottie_view.setVisibility(VISIBLE);
            lottie_view.setAnimationFromUrl(lottieUrl);
            lottie_view.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    // ????????????????????????
                    if (valueAnimator.getAnimatedFraction() == 1f) {
                        if (mAnimationUrlList.isEmpty()) {
                            lottie_view.clearAnimation();
                            lottie_view.setVisibility(GONE);
                            mIsPlaying = false;
                        } else {
                            playLottieAnimation();
                        }
                    }
                }
            });
            lottie_view.playAnimation();
            mIsPlaying = true;
        }
    }

    private ArrayList<Bitmap> getBitmapDrawable() {
        ArrayList<Bitmap> mList = new ArrayList<>();
        mList.add(((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_praise_sm1, null)).getBitmap());
        mList.add(((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_praise_sm2, null)).getBitmap());
        mList.add(((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_praise_sm3, null)).getBitmap());
        mList.add(((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_praise_sm4, null)).getBitmap());
        mList.add(((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_praise_sm5, null)).getBitmap());
        mList.add(((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_praise_sm6, null)).getBitmap());
        return mList;
    }

    /********************** ???????????? ****************************/
    /**
     * ????????????1
     */
    public void mDivergeViewstart() {
        mList = getBitmapDrawable();
        if (mIndex == 5) {
            mIndex = 0;
        }
        mDivergeView.startDiverges(mIndex);
        mIndex++;
        /*      if (mDivergeView.isRunning()) {
                    mDivergeView.stop();
                }*/
        post(new Runnable() {
            @Override
            public void run() {
                mDivergeView.setEndPoint(new PointF(mDivergeView.getMeasuredWidth() / 2, 0));
                mDivergeView.setDivergeViewProvider(new Provider());
            }
        });

        if (userInfo.getVip() == 0) {
            //?????????????????????????????????????????????
            Datamodule.getInstance().chatjinbi("", getContext().getString(R.string.tm99) + hotRooms.nickName, null);
        }
    }

    /**
     * ????????????2
     */
    private class Provider implements DivergeView.DivergeViewProvider {

        @Override
        public Bitmap getBitmap(Object obj) {
            return mList == null ? null : mList.get((int) obj);
        }
    }

    private GiftPanelDelegate giftPanelDelegate = new GiftPanelDelegate() {
        @Override
        public void onGiftItemClick(GiftInfo giftInfo) {
            showGift(giftInfo);//??????????????????
        }

        @Override
        public void onChargeClick() {
            //????????????
        }
    };
}
