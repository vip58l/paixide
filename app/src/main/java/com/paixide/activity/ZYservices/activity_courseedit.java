package com.paixide.activity.ZYservices;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tencent.qcloud.tim.tuikit.live.BuildConfig;
import com.paixide.BasActivity.BasActivity2;
import com.paixide.Module.api.Buckets;
import com.paixide.Module.api.misc;
import com.paixide.Module.api.videotitle;
import com.paixide.R;
import com.paixide.activity.activity_music_play ;
import com.paixide.activity.video.videoijkplayer0;
import com.paixide.Util.Constants;
import com.paixide.Util.Glideload;
import com.paixide.Util.Imagecompressiontool;
import com.paixide.Util.Toashow;
import com.paixide.Util.config;
import com.paixide.listener.Paymnets;
import com.paixide.app.DemoApplication;
import com.paixide.dialog.dialog_item_view_edit;
import com.paixide.dialog.dialog_load;
import com.paixide.getHandler.JsonUitl;
import com.paixide.getHandler.PostModule;
import com.paixide.widget.itembackTopbr;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.cos.xml.transfer.TransferState;
import com.tencent.cos.xml.transfer.TransferStateListener;
import com.tencent.opensource.model.HttpUtils;
import com.tencent.opensource.model.Mesresult;
import com.tencent.opensource.model.UserInfo;
import com.tencent.opensource.model.curriculum;
import com.tencent.qcloud.costransferpractice.CosServiceFactory;
import com.tencent.qcloud.costransferpractice.common.FilePathHelper;
import com.tencent.qcloud.costransferpractice.common.Utils;
import com.tencent.qcloud.costransferpractice.transfer.Api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.paixide.Util.Imagecompressiontool.dataDir;
import static com.paixide.Util.config.getFileName;
import static com.paixide.tencent.cos.MySessionCredentialProvider.DELETEObject;

/**
 * ??????????????????
 */
public class activity_courseedit extends BasActivity2 {
    private static final String TAG = "activity_courseedit";
    @BindView(R.id.itemback)
    itembackTopbr itembackTopbr;
    @BindView(R.id.iv_img1)
    ImageView iv_img1;
    @BindView(R.id.iv_img2)
    ImageView iv_img2;
    @BindView(R.id.iv_img3)
    ImageView iv_img3;
    @BindView(R.id.img_del)
    ImageView img_del;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tv3)
    TextView tv3;
    @BindView(R.id.tv4)
    TextView tv4;
    @BindView(R.id.listener)
    TextView listener;
    @BindView(R.id.content)
    EditText content;
    @BindView(R.id.tv2)
    EditText tv2;
    @BindView(R.id.howtime)
    TextView howtime;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    @BindView(R.id.layout11)
    RelativeLayout layout11;
    @BindView(R.id.layout12)
    RelativeLayout layout12;
    @BindView(R.id.layout13)
    RelativeLayout layout13;
    @BindView(R.id.video_img)
    ImageView video_img;
    @BindView(R.id.audio_img)
    ImageView audio_img;
    @BindView(R.id.tv_delete)
    TextView tv_delete;
    @BindView(R.id.tv_play)
    TextView tv_play;
    dialog_load dialogload;

    private final int OPEN_IMG_CODE = 10001;
    private final int OPEN_VIDEO_CODE = 10002;
    private final int OPEN_Audio_CODE = 10003;
    public final int PERMISSIONS = 10001;
    private final int s0 = 0;
    private final int s1 = 1;
    private final int s2 = 2;
    private final int s3 = 3;
    private final int s4 = 4;
    private curriculum curriculum;
    private UserInfo userInfo;
    private int TYPE;
    private final int upTtype = 0;
    private final int textcount = 120;
    private final String Log_path = "";
    private File file, newfile;

    private CosXmlService cosXmlService;
    private COSXMLUploadTask cosxmlTask;
    private TransferManager transferManager;
    private String currentUploadPath;
    private String dataaccessUrl;
    private String FileName;
    List<String> list;
    private List<String> hlist;
    private List<Boolean> booleanList;
    private final int videozies = 100 * 1024 * 1024; //????????????????????????100M
    private final int audiozies = 20 * 1024 * 1024; //????????????????????????100M
    private activity_courseedit activityCourse;

    @Override
    protected int getview() {
        return R.layout.activity_course;
    }

    @Override
    public void iniview() {
        activityCourse = this;
        requestPermissions();
        itembackTopbr.settitle(getString(R.string.tv_msg4s));
        itembackTopbr.sendright.setVisibility(View.VISIBLE);
        itembackTopbr.setsendright(getString(R.string.tv_msg80));
        relativeLayout.setVisibility(View.GONE);
        audio_img.setVisibility(View.GONE);
        img_del.setVisibility(View.GONE);

        curriculum = (curriculum) getIntent().getSerializableExtra(Constants.ROOM);
        userInfo = UserInfo.getInstance();
        content.setText(curriculum.getTitle());
        String path = curriculum.getTencent() == Constants.TENCENT ? DemoApplication.presignedURL(curriculum.getPic()) : curriculum.getPic();
        String videppath = curriculum.getTencent() == Constants.TENCENT ? DemoApplication.presignedURL(curriculum.getVideo()) : curriculum.getVideo();
        Glideload.loadImage(iv_img1, path);

        tv1.setText(curriculum.getPay() == Constants.TENCENT ? "??????" : "??????");
        tv2.setText(curriculum.getMoney());
        tv3.setText(curriculum.getTag());
        tv4.setText(curriculum.getDownload());
        currentUploadPath = curriculum.getDownload();
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listener.setText(String.format("%s/%s", start, textcount));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > textcount) {
                    content.setText(s.toString().substring(0, textcount)); //??????EditText???????????????6?????????
                    content.setSelection(content.length());                //?????????????????????
                    listener.setText(String.format("%s/", s.length() - 1, textcount));
                    Toashow.show(getString(R.string.Toast_msg4));
                    return;
                }
                curriculum.setTitle(s.toString());
                curriculum.setMsg(s.toString());
            }
        });
        TYPE = curriculum.getType();
        switch (TYPE) {
            case s0: //??????
                layout12.setVisibility(View.VISIBLE);
                layout13.setVisibility(View.GONE);
                Glideload.loadImage(iv_img2, videppath);
                break;
            case s1: //??????
                layout12.setVisibility(View.GONE);
                layout13.setVisibility(View.VISIBLE);
                iv_img3.setImageResource(R.mipmap.ms_play_icon_gif3);
                audio_img.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void initData() {
        if (!config.isNetworkAvailable()) {
            Toashow.show(getString(R.string.eorrfali2));
            return;
        }
    PostModule.getModule(HttpUtils.videotitle, new Paymnets() {
            @Override
            public void success(String date) {
                try {
                    List<videotitle> videotypetitle = JsonUitl.stringToList(date, videotitle.class);
                    hlist = new ArrayList<>();
                    booleanList = new ArrayList<>();
                    for (videotitle videotitle : videotypetitle) {
                        hlist.add(videotitle.getTitle());
                        booleanList.add(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void fall(int code) {

            }
        });

    }

    @OnClick({R.id.sendright, R.id.layout1, R.id.layout2, R.id.layout3, R.id.layout4, R.id.iv_img1, R.id.iv_img2, R.id.video_img, R.id.iv_img3, R.id.audio_img, R.id.tv_play, R.id.tv_delete, R.id.img_del})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.sendright:
                postdata();
                break;
            case R.id.layout1:
                showSingleAlertDialog();
                break;
            case R.id.layout2:
                //sProgressDialog();
                break;
            case R.id.layout3:
                showMutilAlertDialog();
                break;
            case R.id.iv_img1:
                OpenImages();
                break;
            case R.id.iv_img2:
            case R.id.video_img:
                activityvideoijkplayer();
                break;
            case R.id.iv_img3:
            case R.id.audio_img:
                gotartActivity();
                break;
            case R.id.tv_play:
                file = null;
                Glide.with(this).load(R.mipmap.rc_image_error3).into(iv_img3);
                video_img.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.GONE);
                audio_img.setVisibility(View.GONE);
                break;
            case R.id.tv_delete:
            case R.id.img_del:
                file = null;
                Glide.with(this).load(R.mipmap.rc_image_error2).into(iv_img2);
                video_img.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.GONE);
                img_del.setVisibility(View.GONE);
                break;
            case R.id.layout4:
                dialog_item_view_edit.myedit(this, currentUploadPath, new Paymnets() {
                    @Override
                    public void activity(String str) {
                        if (!TextUtils.isEmpty(str)) {
                            tv4.setText(str);
                            currentUploadPath = str;
                            curriculum.setDownload(currentUploadPath);
                        }
                    }
                });
                break;
        }
    }

    /**
     * ?????????????????????
     */
    private void postdata() {
        String sontent = content.getText().toString().trim();
        if (TextUtils.isEmpty(sontent)) {
            Toashow.show(getString(R.string.tv_msg41));
            content.setEnabled(true);
            content.setFocusable(true);
            content.setFocusableInTouchMode(true);
            content.requestFocus();
            content.setSelection(content.length());
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            return;
        }
        curriculum.setTitle(sontent);
        curriculum.setMsg(sontent);
        curriculum.setMoney(tv2.getText().toString().trim());
        curriculum.setPrice(tv2.getText().toString().trim());

        if (sontent.length() < 10) {
            Toashow.show(getString(R.string.Toast_msg12));
            return;
        }
        double price = 0;
        try {
            price = Double.valueOf(curriculum.getPrice());
        } catch (Exception e) {
        }
        if (price <= 0) {
            Toashow.show(getString(R.string.Toast_msg6));
            tv2.setEnabled(true);
            tv2.setFocusable(true);
            tv2.setFocusableInTouchMode(true);
            tv2.requestFocus();
            tv2.setSelection(tv2.length());
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            return;
        }
        if (TextUtils.isEmpty(curriculum.getTag())) {
            Toashow.show(getString(R.string.tv_msg38));
            return;
        }
        if (TextUtils.isEmpty(curriculum.getPic())) {
            Toashow.show(getString(R.string.Toast_msg7));
            return;
        }
        dialogload = dialog_load.myshow(this);
        if (newfile != null) {
            upload(newfile, true);
        } else {
            resupdate();
        }

    }


    /**
     * ??????????????????
     */
    private void resupdate() {
        if (!config.isNetworkAvailable()) {
            Toashow.show(getString(R.string.eorrfali2));
            return;
        }
        RequestBody requestBody = new FormBody.Builder()
                .add("id", String.valueOf(curriculum.getId()))
                .add("userid", curriculum.getUserid())
                .add("title", curriculum.getTitle())
                .add("msg", curriculum.getMsg())
                .add("tag", curriculum.getTag())
                .add("pic", curriculum.getPic())
                .add("money", curriculum.getMoney())
                .add("price", curriculum.getPrice())
                .add("pay", String.valueOf(curriculum.getPay()))
                .add("token", userInfo.getToken())
                .add("download", TextUtils.isEmpty(curriculum.getDownload()) ? "" : curriculum.getDownload())
                .build();
        PostModule.postModule(BuildConfig.HTTP_API + "/curriculumaedit", requestBody, new Paymnets() {
            @Override
            public void success(String date) {
                try {
                    Mesresult mesresult = new Gson().fromJson(date, Mesresult.class);
                    Toashow.show(mesresult.getMsg());
                    if (mesresult.getStatus().equals("1")) {
                        gostartActivity();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toashow.show(getString(R.string.eorrfali3));
                }
                if (dialogload != null) {
                    dialogload.dismiss();
                }
            }

            @Override
            public void fall(int code) {
                Toashow.show(getString(R.string.eorrfali3));
                if (dialogload != null) {
                    dialogload.dismiss();
                }
            }
        });

    }

    /**
     * ????????????????????????
     */
    private void gostartActivity() {
        // startActivity(new Intent(activityCourse, activity_mycurriculum.class));
        finish();
    }

    @Override
    public void OnEorr() {

    }

    /**
     * ??????????????????
     */
    public void OpenImages() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*"); //???????????? ????????????
        startActivityForResult(intent, OPEN_IMG_CODE);
    }

    /**
     * ??????????????????
     */
    public void OpenVideo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("video/*"); //???????????? ????????????
        startActivityForResult(intent, OPEN_VIDEO_CODE);
    }

    /**
     * ??????????????????
     */
    public void OpenAudio() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*"); //???????????? ????????????
        startActivityForResult(intent, OPEN_Audio_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case OPEN_IMG_CODE:
                resultCodeImages(resultCode, data);
                break;
            case OPEN_VIDEO_CODE:
                resultCodevideo(resultCode, data);
                break;
            case OPEN_Audio_CODE:
                resultCodeAudio(resultCode, data);
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS);
            }
        }
    }


    /**
     * ??????????????????
     *
     * @param resultCode
     * @param data
     */
    private void resultCodeImages(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            String path = FilePathHelper.getAbsPathFromUri(DemoApplication.instance(), data.getData());
            if (TextUtils.isEmpty(path)) {
                return;
            }
            File file = new File(path);

            //????????????????????????URI??????????????? ????????????
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (bitmap != null) {
                iv_img1.setImageBitmap(bitmap);
            }
            //??????????????????????????????
            newfile = dataDir();
            //??????????????????
            displayToGallery(this, newfile);

            //??????????????????????????????????????????????????????????????????????????? ????????????500KB
            if (file.length() > 512000) {
                Imagecompressiontool.sizeCompress(bitmap, newfile, 2);
                while (true) {
                    if (newfile.length() <= 512000) {
                        Log.d(TAG, "???????????? ???" + newfile.length());
                        break;
                    }
                    Bitmap bitmap2 = BitmapFactory.decodeFile(newfile.getPath());
                    Imagecompressiontool.sizeCompress(bitmap2, newfile, 2);
                    Log.d(TAG, "???????????????" + newfile.length());
                }

            } else {
                /**
                 * ???????????????????????????
                 * @param bmp
                 * @param file
                 * @return
                 */
                Imagecompressiontool.qualityCompress(bitmap, newfile);
                Log.d(TAG, "myresultCode: ?????????");
            }

            //????????????
            Log.d(TAG, "??????????????????" + file.getPath());
            Log.d(TAG, "??????????????????" + file.length());
            Log.d(TAG, "???????????????" + getFileName(file.getPath()));
            Log.d(TAG, "*********************?????????************************");
            Log.d(TAG, "??????????????? : " + newfile.getPath());
            Log.d(TAG, "??????????????? : " + newfile.length());
            Log.d(TAG, "???????????? ???" + getFileName(newfile.getPath()));
        }

    }

    /**
     * ??????????????????
     *
     * @param resultCode
     * @param data
     */
    private void resultCodevideo(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            String path = FilePathHelper.getAbsPathFromUri(DemoApplication.instance(), data.getData());
            file = new File(path);
            if (!file.exists()) {
                return;
            }

            //????????????????????????URI??????????????? ????????????
            //Bitmap bitmap = BitmapFactory.decodeFile(path);
            //???????????????????????????
            Bitmap bitmap = getVedioThumbnail(file);
            //???????????????????????????
            Long totalTime = getVedioTotalTime(file);
            if (bitmap != null) {
                Glide.with(this).load(bitmap).into(iv_img2);
                Glide.with(this).load(bitmap).into(iv_img1);
                howtime.setText(config.generateTime(totalTime));
                curriculum.setDuration(config.generateTime(totalTime));
                video_img.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
                img_del.setVisibility(View.VISIBLE);
                tv_play.setVisibility(View.GONE);
            }
            Log.d(TAG, String.format("????????????:%sMB", file.length() / 1024 / 1024));
            if (file.length() > videozies) {
                Toashow.show(getString(R.string.Toast_msg13));
            }

            //??????????????????
            newfile = dataDir();
            curriculum.setPic(newfile.getPath());
            curriculum.setVideo(file.getPath());
            //??????????????????
            displayToGallery(this, newfile);
            Imagecompressiontool.sizeCompress(bitmap, newfile, 2);
            while (true) {
                if (newfile.length() <= 512000) {
                    Log.d(TAG, "???????????? ???" + newfile.length());
                    break;
                }
                Imagecompressiontool.sizeCompress(BitmapFactory.decodeFile(newfile.getPath()), newfile, 2);
                Log.d(TAG, "???????????????" + newfile.length());
            }


            //??????????????????
            StringBuilder sb = new StringBuilder();
            sb.append("???????????????" + file.getPath() + "\n");
            sb.append("???????????????" + file.length() + "\n");
            sb.append("???????????????" + file.getName() + "\n");
            sb.append("???????????????" + totalTime + "\n");
            sb.append("???????????????" + totalTime / 1000 + "\n");
            sb.append("???????????????" + config.generateTime(totalTime) + "\n");

            sb.append("*********************?????????************************\n");
            sb.append("????????????: " + newfile.getPath() + "\n");
            sb.append("????????????: " + newfile.length() + "\n");
            sb.append("???????????????" + newfile.getName() + "\n");
            Log.d(TAG, "??????????????????: \n" + sb.toString());
        }

    }


    /**
     * ??????????????????
     *
     * @param resultCode
     * @param data
     */
    private void resultCodeAudio(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            String path = FilePathHelper.getAbsPathFromUri(DemoApplication.instance(), data.getData());
            file = new File(path);
            if (!file.exists()) {
                return;
            }
            Long totalTime = getVedioTotalTime(file);
            iv_img3.setImageResource(R.mipmap.ms_play_icon_gif3);
            howtime.setText(config.generateTime(totalTime) + " " + file.getName());
            curriculum.setDuration(config.generateTime(totalTime));
            curriculum.setVideo(file.getPath());

            relativeLayout.setVisibility(View.VISIBLE);
            audio_img.setVisibility(View.VISIBLE);
            tv_play.setVisibility(View.VISIBLE);
            tv_delete.setVisibility(View.GONE);
            Log.d(TAG, String.format("????????????:%sMB", file.length() / 1024 / 1024));
            if (file.length() > audiozies) {
                Toashow.show(getString(R.string.Toast_msg14));
            }

            //??????????????????
            StringBuilder sb = new StringBuilder();
            sb.append("???????????????" + file.getPath() + "\n");
            sb.append("???????????????" + file.length() + "\n");
            sb.append("???????????????" + file.getName() + "\n");
            sb.append("???????????????" + totalTime + "\n");
            sb.append("???????????????" + totalTime / 1000 + "\n");
            sb.append("???????????????" + config.generateTime(totalTime) + "\n");
            Log.d(TAG, "??????????????????: \n" + sb.toString());
        }

    }


    /**
     * ????????????????????? ???Android????????????????????????????????????MediaMetadataRetriever??????
     *
     * @param file
     * @return
     */
    public static Bitmap getVedioThumbnail(File file) {
        if (!file.exists()) {
            return null;
        }
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(file.getAbsolutePath());
        Bitmap bitmap = retriever.getFrameAtTime();
        //Bitmap bitmap = retriever.getFrameAtTime(1000);//???????????????,???????????????????????????????????????
        return bitmap;
    }

    /**
     * ???????????????????????????
     *
     * @param file
     * @return
     */
    public static Long getVedioTotalTime(File file) {
        if (!file.exists()) {
            return null;
        }
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(file.getAbsolutePath());
        String timeString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        Long time = Long.valueOf(timeString);
        return time;

    }


    /**
     * ?????????????????????????????????
     *
     * @param file
     */
    private static void FileVideo(File file) {
        //??????????????????
        Bitmap bitmap = getVedioThumbnail(file);
        //???????????????????????????
        Long totalTime = getVedioTotalTime(file);

        //??????????????????
        String time = config.generateTime(totalTime);
    }

    /**
     * Glide???????????????????????????
     */
    public void SaveBitmap(String path) throws ExecutionException, InterruptedException {
        //??????Glide????????????
        File file = Glide.with(this).asFile().load(path).submit().get();
        //???????????????
        displayToGallery(this, file);
    }

    /**
     * ??????????????????????????????
     *
     * @param context
     * @param file
     */
    public void displayToGallery(Context context, File file) {
        if (!file.exists()) {
            return;
        }
        String photoPath = file.getAbsolutePath();
        String photoName = file.getName();
        try {
            //??????????????????????????????
            //ContentResolver contentResolver = context.getContentResolver();
            //MediaStore.Images.Media.insertImage(contentResolver, photoPath, photoName, null);

            //??????????????????
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + photoPath)));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * ??????????????????
     */
    public void upload(File file, boolean IsUpFile) {

        Buckets sbuckets = Buckets.getSbuckets();
        cosXmlService = DemoApplication.cosXmlService;
        //???????????????
        if (cosXmlService == null) {
            cosXmlService = CosServiceFactory.getCosXmlService(this, sbuckets.getLocation(), com.tencent.qcloud.costransferpractice.BuildConfig.COS_SECRET_ID, com.tencent.qcloud.costransferpractice.BuildConfig.COS_SECRET_KEY, true);
        }
        transferManager = new TransferManager(this.cosXmlService, new TransferConfig.Builder().build());
        //??????????????????
        cosxmlTask = transferManager.upload(sbuckets.getName(), file.getName(), file.getPath(), null);
        //??????????????????????????????????????????
        cosxmlTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(final TransferState state) {
                Log.d(TAG, "onStateChanged: " + state.toString());
            }
        });
        //???????????????????????? ??????????????????
        cosxmlTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(final long complete, final long total) {
                Log.d(TAG, "onProgress: " + complete + " " + total);
                long progress = 100 * complete / total;
                Log.d(TAG, "onProgress: " + Utils.readableStorageSize(progress) + "/" + Utils.readableStorageSize(total));
            }
        });
        //????????????????????????, ????????????????????????
        cosxmlTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                COSXMLUploadTask.COSXMLUploadTaskResult cOSXMLUploadTaskResult = (COSXMLUploadTask.COSXMLUploadTaskResult) result;
                cosxmlTask = null;
                //???????????????????????????
                final String accessUrl = cOSXMLUploadTaskResult.accessUrl;
                if (accessUrl.toLowerCase().endsWith(".png") || accessUrl.toLowerCase().endsWith(".jpg") || accessUrl.toLowerCase().endsWith(".gif")) {
                    try {
                        if (curriculum.getTencent() == Constants.TENCENT) {
                            //???????????????????????????
                            String fileName = Api.getFileName(curriculum.getPic());
                            DELETEObject(fileName);

                            //??????????????????1???N?????????
                            //List<String> objectList = Arrays.asList(videofileName, imagefileName);
                            //DELETEMultipleObject(objectList);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //????????????
                    curriculum.setPic(accessUrl);
                } else {
                    //?????????????????????
                    curriculum.setVideo(accessUrl);
                }

                if (IsUpFile) {
                    iv_img1.post(new Runnable() {
                        @Override
                        public void run() {
                            resupdate();
                        }
                    });
                }


            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                if (cosxmlTask.getTaskState() != TransferState.PAUSED) {
                    cosxmlTask = null;
                }
                exception.printStackTrace();
                serviceException.printStackTrace();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cosXmlService != null) {
            cosXmlService.release();
            cosxmlTask = null;
            transferManager = null;
        }
    }

    /**
     * ??????????????????
     */
    public void staralertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.alertDialog_title);
        alertDialog.setMessage(R.string.dialog_setMessage);
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        alertDialog.setNegativeButton(R.string.AlertDialog_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //????????????
        AlertDialog dialog = alertDialog.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Log.e(TAG, "??????????????????");
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.e(TAG, "??????????????????");
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return false;
            }
        });
        dialog.show();


    }

    /**
     * ???????????????
     */
    public void showSingleAlertlist() {
        final String[] items = {"??????1", "??????2", "??????3", "??????4"};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("???????????????");
        alertBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertBuilder.show();
    }

    /**
     * ????????????????????????
     */
    public void showSingleAlertDialog() {
        String[] items = {"??????", "??????"};
        String[] textconter = {""};
        int[] is = new int[1];
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("??????????????????");
        alertBuilder.setCancelable(false);
        alertBuilder.setSingleChoiceItems(items, curriculum.getPay(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                textconter[0] = items[i];
                is[0] = i;
            }
        });
        alertBuilder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                curriculum.setPay(is[0] == 0 ? 0 : 1);
                tv1.setText(textconter[0]);
            }
        });
        alertBuilder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertBuilder.show();
    }

    /**
     * ???????????????
     */
    public void showMutilAlertDialog() {
        if (hlist == null || hlist.size() == 0) {
            Toashow.show(getString(R.string.eorrfali3));
            Log.d(TAG, "showMutilAlertDialog: " + hlist.size());
            return;
        }

        String[] items = hlist.toArray(new String[hlist.size()]);
        boolean[] checkedItems = new boolean[booleanList.size()];
        for (int i = 0; i < booleanList.size(); i++) {
            checkedItems[i] = booleanList.get(i);
        }

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(R.string.tv_msg39);
        /**
         *???????????????:???????????????????????????????????????????????????
         * ??????????????????????????????????????????????????????
         * ????????????????????????????????????
         */
        alertBuilder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {
                checkedItems[i] = isChecked;
            }
        });

        alertBuilder.setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < checkedItems.length; i++) {
                    //??????????????????
                    if (checkedItems[i]) {
                        sb.append(items[i] + ",");
                    }
                    if (!TextUtils.isEmpty(sb)) {
                        tv3.setText(sb.toString());
                        curriculum.setTag(sb.toString());

                    } else {
                        tv3.setText("????????????");
                        curriculum.setTag("");
                    }
                    //2.????????????????????????
                    dialogInterface.dismiss();
                }
            }
        });
        alertBuilder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertBuilder.show();

    }

    /**
     * ???????????????
     */
    public void sProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("?????????????????????...");
        //??????????????????????????????
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        //?????????????????????
        new Thread() {
            @Override
            public void run() {
                //???????????????????????????
                dialog.setMax(100);
                //??????????????????
                for (int i = 0; i <= 100; i++) {
                    dialog.setProgress(i);
                    //???????????????
                    SystemClock.sleep(50);
                }
                //???????????????
                dialog.dismiss();
            }
        }.start();
        dialog.show();
    }

    /**
     * ??????????????????
     */
    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS);
        }
    }


    /**
     * ?????????????????????
     */
    private void activityvideoijkplayer() {
        String path = curriculum.getTencent() == 1 ? DemoApplication.presignedURL(curriculum.getVideo()) : curriculum.getVideo();
        Intent intent = new Intent(this, videoijkplayer0.class);
        intent.putExtra(Constants.TITLE, content.getText().toString().trim());
        intent.putExtra(Constants.PATHVIDEO, path);
        intent.putExtra(Constants.PATHIMG, "");
        intent.putExtra(Constants.POSITION, 0);
        startActivity(intent);
    }


    /**
     * ?????????????????????
     */
    public void gotartActivity() {
        String path = curriculum.getTencent() == 1 ? DemoApplication.presignedURL(curriculum.getVideo()) : curriculum.getVideo();
        Log.d(TAG, "gotartActivity: " + path);

        String tag = content.getText().toString().trim();
        misc misc = new misc();
        misc.setId(String.valueOf(curriculum.getId()));
        misc.setUrl(path);
        misc.setTitle(curriculum.getTitle());
        if (!TextUtils.isEmpty(tag)) {
            misc.setTag(tag);
        } else {
            misc.setTag(curriculum.getTag());
        }
        if (newfile != null) {
            misc.setPicture(newfile.getPath());
        } else {
            misc.setPicture(curriculum.getTencent() == 1 ? DemoApplication.presignedURL(curriculum.getPic()) : curriculum.getPic());
        }
        Intent intent = new Intent(this, activity_music_play .class);
        intent.putExtra(Constants.PATHVIDEO, misc);
        startActivity(intent);
    }
}