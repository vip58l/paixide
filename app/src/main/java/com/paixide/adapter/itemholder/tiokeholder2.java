package com.paixide.adapter.itemholder;

import static com.blankj.utilcode.util.ThreadUtils.runOnUiThread;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.paixide.Util.config.getFileName;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.paixide.BasActivity.BaseHolder;
import com.paixide.Fragment.page6.fragment.pagevideo;
import com.paixide.Module.Datamodule;
import com.paixide.Module.api.Share;
import com.paixide.R;
import com.paixide.Util.Constants;
import com.paixide.Util.Glideload;
import com.paixide.Util.Toashow;
import com.paixide.activity.picenter.activity_personalhome;
import com.paixide.activity.picenter.activity_picenter;
import com.paixide.activity.video2.widget.player;
import com.paixide.adapter.Tiktokholder.TiktokAdapter;
import com.paixide.app.DemoApplication;
import com.paixide.dialog.Dialog_fenxing;
import com.paixide.dialog.dialog_Config;
import com.paixide.dialog.dialog_game;
import com.paixide.dialog.dialog_videotitle;
import com.paixide.listener.Callback;
import com.paixide.listener.Paymnets;
import com.paixide.tencent.cos.MySessionCredentialProvider;
import com.tencent.opensource.model.UserInfo;
import com.tencent.opensource.model.item;
import com.tencent.opensource.model.member;
import com.tencent.opensource.model.videolist;
import com.tencent.qcloud.tim.tuikit.live.BuildConfig;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import tv.danmaku.ijk.media.player.IMediaPlayer;

public class tiokeholder2 extends BaseHolder {
    String TAG = tiokeholder2.class.getSimpleName();
    @BindView(R.id.mRootView)
    RelativeLayout mRootView;
    @BindView(R.id.mThumb)
    ImageView mThumb;
    @BindView(R.id.mticon)
    ImageView mticon;
    @BindView(R.id.mPlay)
    ImageView mPlay;
    @BindView(R.id.mTitle)
    TextView mTitle;
    @BindView(R.id.mMarquee)
    TextView mMarquee;
    @BindView(R.id.video_view)
    player player;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tv2)
    TextView tv2;
    @BindView(R.id.tv3)
    TextView tv3;
    @BindView(R.id.relayout1)
    LinearLayout relayout1;
    @BindView(R.id.relayout2)
    LinearLayout relayout2;
    @BindView(R.id.relayout3)
    LinearLayout relayout3;
    @BindView(R.id.circleimageview)
    ImageView circleimageview;
    @BindView(R.id.MYRIGHT)
    View MYRIGHT;
    UserInfo userInfo;

    public tiokeholder2(Context mContext, ViewGroup parent) {
        super(LayoutInflater.from(mContext).inflate(R.layout.item_tiktok_layout2, parent, false));
    }

    @Override
    public void bind(Object object, int position, Callback callback) {
        videolist video = (videolist) object;
        userInfo = UserInfo.getInstance();
        //??????????????????????????????????????????????????????
        member me = video.getMember();
        String bigpicurl = getvideo(video.getBigpicurl(), video.getTencent());
        String picuser = getvideo(video.getPicuser(), video.getTencent());
        String playurl = !TextUtils.isEmpty(video.getPlaytest()) ? getvideo(video.getPlaytest(), video.getTencent()) : getvideo(video.getPlayurl(), video.getTencent());
        String avatar = getvideo(video.getAvatar(), video.getTencent());
        Glideload.loadImage(mticon, me != null ? (TextUtils.isEmpty(me.getPicture()) ? bigpicurl : me.getPicture()) : bigpicurl);
        mticon.setOnClickListener(v -> activity_picenter.open(context, video.getUserid()));
        relayout1.setOnClickListener(v -> ToastUtil.toastShortMessage(context.getString(R.string.tv_msg234)));
        relayout2.setOnClickListener(v -> dialog_Config.Pinglun(context, video.getId()));
        relayout3.setOnClickListener(v -> dialog_Config.fenxing(context));

        //???????????? ???VIP??????????????????
        if (video.getType() == Constants.TENCENT && userInfo.getVip() == Constants.TENCENT0) {
            Glideload.loadImage(mThumb, bigpicurl, 10, 25);
        } else {
            Glideload.loadImage(mThumb, bigpicurl);
        }

        //????????????????????????type ???????????? vip0 ???????????? ?????????????????????
        int vip = UserInfo.getInstance().getVip();
        if (video.getType() == Constants.TENCENT && vip == Constants.TENCENT0 && !UserInfo.getInstance().getUserId().equals(video.getUserid())) {
            //?????????VIP????????????
            Glide.with(context).load(bigpicurl).apply(bitmapTransform(new BlurTransformation(5, 25))).dontAnimate().into(mThumb);
        } else {
            //????????????????????????
            Glideload.loadImage(mThumb, bigpicurl, 5);
        }
        player.setPath(playurl);
        mTitle.setText(video.getTitle());
        mMarquee.setText(video.getAlias());
        mMarquee.setSelected(true);
        tv1.setText(video.getAnum());
        tv2.setText(video.getPnum());
        tv3.setText(video.getFnum());

    }

    @Override
    public void bind(Context context, Object object, int position, Callback callback) {
        videolist video = (videolist) object;
        userInfo = UserInfo.getInstance();
        MYRIGHT.setVisibility(View.GONE);

        //??????????????????????????????????????????????????????
        String bigpicurl = video.getBigpicurl();
        String picuser = video.getPicuser();
        String playurl = video.getPlayurl();
        String avatar = video.getAvatar();
        try {
            bigpicurl = video.getTencent() == Constants.TENCENT ? DemoApplication.presignedURL(video.getBigpicurl()) : video.getBigpicurl();
            picuser = video.getPictencent() == Constants.TENCENT ? DemoApplication.presignedURL(video.getPicuser()) : video.getPicuser();
            if (!TextUtils.isEmpty(video.getPlaytest())) {
                playurl = video.getTencent() == Constants.TENCENT ? DemoApplication.presignedURL(video.getPlaytest()) : video.getPlaytest();
            } else {
                playurl = video.getTencent() == Constants.TENCENT ? DemoApplication.presignedURL(video.getPlayurl()) : video.getPlayurl();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        member me = video.getMember();
        if (me != null) {
            String picicon = TextUtils.isEmpty(me.getPicture()) ? bigpicurl : me.getPicture();
            Glideload.loadImage(mticon, picicon);
            mticon.setOnClickListener(v -> activity_picenter.open(context, video.getUserid()));
            relayout1.setOnClickListener(v -> ToastUtil.toastShortMessage(context.getString(R.string.tv_msg234)));
            relayout2.setOnClickListener(v -> dialog_Config.Pinglun(context, video.getId()));
            relayout3.setOnClickListener(v -> dialog_Config.fenxing(context));
            circleimageview.setOnClickListener(v -> {
            });

        } else {
            Glideload.loadImage(mticon, picuser);
        }

        //???????????? ???VIP??????????????????
        if (video.getType() == Constants.TENCENT && userInfo.getVip() == Constants.TENCENT0) {
            Glideload.loadImage(mThumb, bigpicurl, 10, 25);
        } else {
            Glideload.loadImage(mThumb, bigpicurl);
        }

        //????????????????????????type ???????????? vip0 ???????????? ?????????????????????
        int vip = UserInfo.getInstance().getVip();
        if (video.getType() == Constants.TENCENT && vip == Constants.TENCENT0 && !UserInfo.getInstance().getUserId().equals(video.getUserid())) {
            //?????????VIP????????????
            Glide.with(context).load(bigpicurl).apply(bitmapTransform(new BlurTransformation(5, 25))).dontAnimate().into(mThumb);
        } else {
            //????????????????????????
            Glideload.loadImage(mThumb, bigpicurl, 5);
        }
        player.setTag(video);
        player.setPath(playurl);
        player.setListener(new player.VideoPlayerListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                Log.d(TAG, "????????????: ");
                iMediaPlayer.start();
            }

            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                Log.d(TAG, "???????????????: ");
                return false;
            }

            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                switch (i) {
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: //??????????????????????????????
                        Log.d(TAG, "??????????????????????????????: ");
                        //Android????????????(Animator)
                        //translationX???translationY	x??????y???????????????
                        //rotation???rotationX???rotationY	??????????????????
                        //rotation(360) ??????????????????
                        //scaleX???scaleY	??????
                        //alpha	?????????
                        //x(5000).y(5000)  ??????
                        //xBy(500).xBy(500) ??????
                        //image.animate().alpha(0f).setDuration(1000).y(100).x(310).translationX(500).translationY(500).rotation(360).start();
                        mPlay.animate().alpha(0f).setDuration(500).start();
                        mThumb.animate().alpha(0f).setDuration(500).start();
                        return true;
                    case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:   //??????????????????????????????
                        Log.d(TAG, "??????????????????????????????: ");
                        return true;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:       //????????????????????????
                        Log.d(TAG, " ????????????????????????");
                        return true;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:         //????????????????????????
                        Log.d(TAG, "????????????????????????");
                        return true;
                }
                return false;
            }

            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                Log.d(TAG, "onPrepared: ");

            }
        });
        mTitle.setText(video.getTitle());
        mMarquee.setText(video.getAlias());
        mMarquee.setSelected(true);
        tv1.setText(video.getAnum());
        tv2.setText(video.getPnum());
        tv3.setText(video.getFnum());
    }

    public void bind(Context context, Object object, int position) {
        videolist video = (videolist) object;
        userInfo = UserInfo.getInstance();

        //??????????????????????????????????????????????????????
        member me = video.getMember();
        String bigpicurl = getvideo(video.getBigpicurl(), video.getTencent());
        String picuser = getvideo(video.getPicuser(), video.getTencent());
        String playurl = getvideo(video.getPlayurl(), video.getTencent());
        String avatar = getvideo(video.getAvatar(), video.getTencent());

        if (me != null) {
            String picicon = TextUtils.isEmpty(me.getPicture()) ? bigpicurl : me.getPicture();
            Glideload.loadImage(mticon, picicon);
            mticon.setOnClickListener(v -> activity_picenter.open(context, video.getUserid()));
            relayout1.setOnClickListener(v -> ToastUtil.toastShortMessage(context.getString(R.string.tv_msg234)));
            relayout2.setOnClickListener(v -> dialog_Config.Pinglun(context, video.getId()));
            relayout3.setOnClickListener(v -> dialog_Config.fenxing(context));
        } else {
            Glideload.loadImage(mticon, picuser);
        }

        //???????????? ???VIP??????????????????
        if (video.getType() == Constants.TENCENT && userInfo.getVip() == Constants.TENCENT0) {
            Glideload.loadImage(mThumb, bigpicurl, 10, 25);
        } else {
            Glideload.loadImage(mThumb, bigpicurl);
        }

        //????????????????????????type ???????????? vip0 ???????????? ?????????????????????
        int vip = UserInfo.getInstance().getVip();
        if (video.getType() == Constants.TENCENT && vip == Constants.TENCENT0 && !UserInfo.getInstance().getUserId().equals(video.getUserid())) {
            //?????????VIP????????????
            Glide.with(context).load(bigpicurl).apply(bitmapTransform(new BlurTransformation(5, 25))).dontAnimate().into(mThumb);
        } else {
            //????????????????????????
            Glideload.loadImage(mThumb, bigpicurl, 5);
        }
        player.setPath(playurl);
        mTitle.setText(video.getTitle());
        mMarquee.setText(video.getAlias());
        mMarquee.setSelected(true);
        tv1.setText(video.getAnum());
        tv2.setText(video.getPnum());
        tv3.setText(video.getFnum());
    }

    public void bind8(Context context, videolist video) {
        userInfo = UserInfo.getInstance();
        //??????????????????????????????????????????????????????
        member me = video.getMember();
        String bigpicurl = getvideo(video.getBigpicurl(), video.getTencent());
        String picuser = getvideo(video.getPicuser(), video.getTencent());
        String playurl = getvideo((!TextUtils.isEmpty(video.getPlaytest()) ? video.getPlaytest() : video.getPlayurl()), video.getTencent());
        String avatar = getvideo(video.getAvatar(), video.getTencent());
        Glideload.loadImage(mticon, me != null ? (!TextUtils.isEmpty(me.getPicture()) ? me.getPicture() : bigpicurl) : bigpicurl);
        mticon.setOnClickListener(v -> activity_personalhome.starsetAction(context, video.getUserid()));
        relayout1.setOnClickListener(v -> ToastUtil.toastShortMessage(context.getString(R.string.tv_msg234)));
        relayout2.setOnClickListener(v -> dialog_Config.Pinglun(context, video.getId()));

        Glideload.loadImage(mThumb, bigpicurl);
        player.setTag(video);
        player.setPath(playurl);
        player.setListener(new player.VideoPlayerListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                Datamodule.getInstance().senvideo(video, 1);
                iMediaPlayer.start();
            }

            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                Log.d(TAG, "onError: ");
                return false;
            }

            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                switch (i) {
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: //??????????????????????????????
                        Log.d(TAG, "??????????????????????????????: ");

                        //Android????????????(Animator)
                        //translationX???translationY	x??????y???????????????
                        //rotation???rotationX???rotationY	??????????????????
                        //rotation(360) ??????????????????
                        //scaleX???scaleY	??????
                        //alpha	?????????
                        //x(5000).y(5000)  ??????
                        //xBy(500).xBy(500) ??????
                        //image.animate().alpha(0f).setDuration(1000).y(100).x(310).translationX(500).translationY(500).rotation(360).start();
                        mPlay.animate().alpha(0f).setDuration(500).start();
                        mThumb.animate().alpha(0f).setDuration(500).start();
                        pagevideo.circleimageanimation(context, circleimageview);

                        return true;
                    case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:   //??????????????????????????????
                        Log.d(TAG, "??????????????????????????????: ");
                        return true;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:       //????????????????????????
                        Log.d(TAG, " ????????????????????????");
                        return true;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:         //????????????????????????
                        Log.d(TAG, "????????????????????????");
                        return true;
                }
                return false;
            }

            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                Log.d(TAG, "onPrepared: ");

            }
        });
        mTitle.setText(video.getTitle());
        mMarquee.setText(video.getAlias());
        mMarquee.setSelected(true);
        tv1.setText(video.getAnum());
        tv2.setText(video.getPnum());
        tv3.setText(video.getFnum());


    }

    @Override
    public void OnClick(View v) {

    }

    /**
     * //?????????????????????????????????????????????????????????
     *
     * @param video
     */
    public void getvideo(videolist video) {

        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(video.getPlayurl());
        player.setPath(video.getPlayurl());
        mThumb.setImageBitmap(media.getFrameAtTime());
        mTitle.setText(video.getTitle());
        mMarquee.setText(video.getAlias());
        mMarquee.setSelected(true);
    }

    public static String getvideo(String url, int type) {
        try {
            boolean contains = url.contains("myqcloud.com");
            if (contains) {
                return type == Constants.TENCENT ? DemoApplication.presignedURL(url) : url;
            } else {
                return url;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void fenxing(Context context, videolist video) {
        Share share = new Share();
        share.setH5Url(com.tencent.qcloud.tim.tuikit.live.BuildConfig.HTTP_WEB + "?&userid=" + UserInfo.getInstance().getUserId());
        share.setText(video.getTitle());
        share.setTitle(video.getTitle());
        share.setIcon("");
        String paramStr = new Gson().toJson(share);
        Dialog_fenxing.myshow(context, paramStr);
    }

    public static void fenxing(Context context, videolist video, TiktokAdapter mAdapter, List<Object> list) {
        Share share = new Share();
        share.setH5Url(com.tencent.qcloud.tim.tuikit.live.BuildConfig.HTTP_WEB + "?&userid=" + UserInfo.getInstance().getUserId());
        share.setText(video.getTitle());
        share.setTitle(video.getTitle());
        share.setIcon("");
        share.setUserid(video.getUserid());
        String paramStr = new Gson().toJson(share);
        Dialog_fenxing.myshow(context, paramStr, new Paymnets() {
            @Override
            public void onFail() {

            }

            @Override
            public void onSuccess() {
                deletevideo(context, video, mAdapter, list);

            }
        });
    }

    private static void deletevideo(Context context, videolist video, TiktokAdapter mAdapter, List<Object> list) {
        dialog_game dialogGame = dialog_Config.dialog_game(context);
        dialogGame.setTitle(context.getString(R.string.tv_msg221));
        dialogGame.setKankan(context.getString(R.string.btn_ok));
        dialogGame.setTextColor(context.getResources().getColor(R.color.half_transparent));
        dialogGame.setkankanColor(context.getResources().getColor(R.color.c_fu));
        dialogGame.setTextSize(14);
        dialogGame.setPaymnets(new Paymnets() {
            @Override
            public void activity() {
                deleteitem(context, video, mAdapter, list);
            }
        });
    }

    /**
     * ????????????
     *
     * @param
     */
    private static void deleteitem(Context context, videolist videolist, TiktokAdapter mAdapter, List<Object> list) {
        //??????????????????1???N?????????
        if (videolist.getTencent() == Constants.TENCENT) {
            String videofileName = getFileName(videolist.getPlayurl(), ".com/");
            String imagefileName = getFileName(videolist.getBigpicurl(), ".com/");
            List<String> objectList = Arrays.asList(videofileName, imagefileName);
            MySessionCredentialProvider.DELETEObject(objectList);
            MySessionCredentialProvider.setPaymnets(new Paymnets() {
                @Override
                public void onSuccess() {
                    deletevidel(context, videolist, mAdapter, list);
                }

                @Override
                public void onFail() {

                }
            });
        } else {
            deletevidel(context, videolist, mAdapter, list);
        }
    }

    private static void deletevidel(Context context, videolist videolist, TiktokAdapter mAdapter, List<Object> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //??????????????????
                //??????????????????????????????
                Datamodule.getInstance().delete(videolist, new Paymnets() {
                    @Override
                    public void onSuccess() {
                        list.remove(videolist);
                        mAdapter.notifyDataSetChanged();
                        Toashow.show(String.format(context.getString(R.string.deletetips) + "", videolist.getTitle()));
                    }
                });
            }
        });

    }

}

