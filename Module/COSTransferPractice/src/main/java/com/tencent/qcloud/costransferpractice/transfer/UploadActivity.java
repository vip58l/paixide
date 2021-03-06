package com.tencent.qcloud.costransferpractice.transfer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.service.GetServiceRequest;
import com.tencent.cos.xml.model.service.GetServiceResult;
import com.tencent.cos.xml.model.tag.ListAllMyBuckets;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.cos.xml.transfer.TransferState;
import com.tencent.cos.xml.transfer.TransferStateListener;
import com.tencent.qcloud.costransferpractice.BuildConfig;
import com.tencent.qcloud.costransferpractice.CosServiceFactory;
import com.tencent.qcloud.costransferpractice.common.base.BaseModeul;
import com.tencent.qcloud.costransferpractice.dialog.Callback;
import com.tencent.qcloud.costransferpractice.dialog.dialog_show2;
import com.tencent.qcloud.costransferpractice.object.ObjectActivity;
import com.tencent.qcloud.costransferpractice.utils.Constants;
import com.tencent.qcloud.costransferpractice.utils.FileUtils;
import com.tencent.qcloud.costransferpractice.utils.Imagecompressiontool;
import com.tencent.qcloud.costransferpractice.R;
import com.tencent.qcloud.costransferpractice.common.FilePathHelper;
import com.tencent.qcloud.costransferpractice.common.Utils;
import com.tencent.qcloud.costransferpractice.common.base.BaseActivity;
import com.tencent.qcloud.costransferpractice.utils.StatusBarUtil;

import java.io.File;
import java.util.List;

import static com.tencent.qcloud.costransferpractice.object.ObjectActivity.ACTIVITY_TOKEN;
import static com.tencent.qcloud.costransferpractice.utils.Constants.REQ_PERMISSION_CODE;
import static com.tencent.qcloud.costransferpractice.utils.FileUtils.isNetworkAvailable;
import static com.tencent.qcloud.costransferpractice.object.ObjectActivity.ACTIVITY_EXTRA_BUCKET_NAME;
import static com.tencent.qcloud.costransferpractice.object.ObjectActivity.ACTIVITY_EXTRA_FOLDER_NAME;
import static com.tencent.qcloud.costransferpractice.object.ObjectActivity.ACTIVITY_EXTRA_REGION;
import static com.tencent.qcloud.costransferpractice.object.ObjectActivity.ACTIVITY_IMG;
import static com.tencent.qcloud.costransferpractice.object.ObjectActivity.ACTIVITY_STATE;
import static com.tencent.qcloud.costransferpractice.object.ObjectActivity.ACTIVITY_TYPE;
import static com.tencent.qcloud.costransferpractice.object.ObjectActivity.ACTIVITY_USERID;
import static com.tencent.qcloud.costransferpractice.object.ObjectActivity.ACTIVITY_VIDEO;
import static com.tencent.qcloud.costransferpractice.object.ObjectActivity.ACTIVITY_VIP;
import static com.tencent.qcloud.costransferpractice.transfer.Api.getFileName;

/**
 * Created by jordanqin on 2020/6/18.
 * ??????????????????
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class UploadActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = UploadActivity.class.getSimpleName();
    private int PERMISSIONS = 10001;
    private int OPEN_FILE_CODE = 10001;
    private long feiliength = 1024 * 1024 * 50;    //????????????????????????
    private long sfeiliength1 = 1024 * 1024 * 12;  //????????????10M??????
    private long sfeiliength2 = 1024 * 1024;       //????????????1M??????
    private int uptime = 60;                       //????????????????????????2??????
    private String UP_VIDEO = "video";
    private String UP_PICTURE = "picture";

    private RelativeLayout supdate;
    //????????????????????????
    private ImageView iv_image;
    //????????????
    private TextView tv_state;
    //????????????
    private TextView tv_progress;
    //???????????????
    private ProgressBar pb_upload;
    //?????????????????????????????????
    private TextView btn_left;
    public String bucketName;
    private String bucketRegion;
    /**
     * {@link CosXmlService} ???????????? COS ??????????????????????????????????????? COS ??????????????? API ?????????
     * <p>
     * ?????????{@link CosXmlService} ???????????????????????? region???????????????????????????????????? region ???
     * Bucket????????????????????? {@link CosXmlService} ?????????
     */
    public CosXmlService cosXmlService;
    /**
     * {@link TransferManager} ?????????????????? {@link CosXmlService} ???????????????????????????????????????
     * ??????????????? COS ????????? COS ?????????????????????????????????????????????
     */
    private TransferManager transferManager;
    private COSXMLUploadTask cosxmlTask;
    /**
     * ?????????????????????COS ??????
     */
    private String currentUploadPath;
    /**
     * ???????????????????????????????????????
     **/
    private String imgCurrentUploadPath;
    private TextView tev_show;
    private TextView hideshow;
    private boolean upfile = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.transparencyBar(this);
        setContentView(R.layout.upload_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        bucketName = getIntent().getStringExtra(ACTIVITY_EXTRA_BUCKET_NAME);
        bucketRegion = getIntent().getStringExtra(ACTIVITY_EXTRA_REGION);
        baseModeul.setFolderName(getIntent().getStringExtra(ACTIVITY_EXTRA_FOLDER_NAME));

        baseModeul.setUserid(getIntent().getStringExtra(ACTIVITY_USERID));
        baseModeul.setTYPE(getIntent().getIntExtra(ACTIVITY_TYPE, -1));
        baseModeul.setUserVIP(getIntent().getIntExtra(ACTIVITY_VIP, -1));
        baseModeul.setUserSTATE(getIntent().getIntExtra(ACTIVITY_STATE, -1));
        baseModeul.setTOKEN(getIntent().getStringExtra(ACTIVITY_TOKEN));

        //GPS??????
        baseModeul.setProvince(getIntent().getStringExtra(ObjectActivity.province));
        baseModeul.setCity(getIntent().getStringExtra(ObjectActivity.city));
        baseModeul.setDistrict(getIntent().getStringExtra(ObjectActivity.district));
        baseModeul.setAddress(getIntent().getStringExtra(ObjectActivity.address));
        baseModeul.setJwd(getIntent().getStringExtra(ObjectActivity.jwd));

        baseModeul.getsetTransferManager();//????????????
        baseModeul.inidateRequestPost();   //????????????????????????????????????

        getSupportActionBar().setTitle(baseModeul.getTYPE() == 2 ? getString(R.string.img_call_1) : getString(R.string.videl_2));

        if (TextUtils.isEmpty(BuildConfig.COS_SECRET_ID) || TextUtils.isEmpty(BuildConfig.COS_SECRET_KEY)) {
            finish();
        }
        if (TextUtils.isEmpty(bucketName) || TextUtils.isEmpty(bucketRegion)) {
            getBuckets1();
        } else {
            getBuckets2();
        }

        iniview();
    }

    private void iniview() {
        iv_image = findViewById(R.id.iv_image);
        tv_state = findViewById(R.id.tv_state);
        tv_progress = findViewById(R.id.tv_progress);
        pb_upload = findViewById(R.id.pb_upload);
        btn_left = findViewById(R.id.btn_left);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        supdate = findViewById(R.id.supdate);
        hideshow = findViewById(R.id.hideshow);
        tev_show = findViewById(R.id.tev_show);
        supdate.setOnClickListener(this);
        btn_left.setOnClickListener(this);
        findViewById(R.id.r1).setOnClickListener(v -> baseModeul.sAlertDialog(tv1));
        findViewById(R.id.r2).setOnClickListener(v -> baseModeul.sAlertDialogjinbi(tv2));
        findViewById(R.id.r3).setOnClickListener(v -> baseModeul.sAlertDialogpgs(tv3));
        btn_left.setVisibility(View.GONE);
        hideshow.setText(baseModeul.getTYPE() == 2 ? getString(R.string.img_call_1) : getString(R.string.videl_2));
        tev_show.setVisibility(baseModeul.getTYPE() == 2 ? View.GONE : View.VISIBLE);
        tv3.setText(!TextUtils.isEmpty(baseModeul.getProvince()) ? baseModeul.getProvince() + "." + baseModeul.getCity() : getString(R.string.gps_msg));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.choose_photo) {
            Intent intent = new Intent();
            intent.setAction("com.mywebview.activity.jsoup");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * ??????????????????
     *
     * @param state ?????? {@link TransferState}
     */
    private void refreshUploadState(final TransferState state) {
        uiAction(new Runnable() {
            @Override
            public void run() {
                String s = state.toString();
                tv_state.setText(s);
            }
        });

    }

    /**
     * ??????????????????
     *
     * @param progress ?????????????????????
     * @param total    ???????????????
     */
    private void refreshUploadProgress(final long progress, final long total) {
        uiAction(new Runnable() {
            @Override
            public void run() {
                pb_upload.setProgress((int) (100 * progress / total));
                tv_progress.setText(Utils.readableStorageSize(progress) + "/" + Utils.readableStorageSize(total));
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_left) {
            if ("??????".contentEquals(btn_left.getText())) {
                if (cosxmlTask != null && cosxmlTask.getTaskState() == TransferState.IN_PROGRESS) {
                    cosxmlTask.pause();
                    btn_left.setText("??????");
                } else {
                    toastMessage("????????????");
                }
            }
            if ("??????".contentEquals(btn_left.getText())) {
                if (cosxmlTask != null && cosxmlTask.getTaskState() == TransferState.PAUSED) {
                    cosxmlTask.resume();
                    btn_left.setText("??????");
                } else {
                    toastMessage("????????????");
                }
            }
            if ("??????".contentEquals(btn_left.getText())) {
                if (cosxmlTask != null) {
                    cosxmlTask.cancel();
                    finish();
                } else {
                    toastMessage("????????????");
                }
            }

            //???????????????????????????????????????????????????
            switch (baseModeul.getUserSTATE()) {
                case 0:
                    dialog_show2.mshow(context, getString(R.string.t1), callback);
                    break;
                case 1:
                    dialog_show2.mshow(context, getString(R.string.t4), getString(R.string.t8), 1, callback);
                    break;
                case 2:
                    upload();
                    break;
                case 3:
                    dialog_show2.mshow(context, getString(R.string.t5), getString(R.string.t7), 3, callback);
                    break;
                default:
                    dialog_show2.mshow(context, getString(R.string.t6), callback);
                    break;
            }
        }

        /**
         * ??????????????????
         */
        if (v.getId() == R.id.supdate) {
            if (Constants.checkPermission(activity)) {
                if (cosxmlTask == null) {
                    sACTION_GET_CONTENT();
                } else {
                    toastMessage(getString(R.string.tv_msg_update));
                }
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * ???????????????????????????
         */
        myonActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSION_CODE) {
            for (String permission : permissions) {
                int granted = ContextCompat.checkSelfPermission(context, permission);
                if (granted != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "?????????????????? ???????????????", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            //??????????????????????????????????????????
            sACTION_GET_CONTENT();
        }

    }


    /**
     * ??????????????????????????????
     */
    public void upload() {
        if (!isNetworkAvailable(context)) {
            Toast.makeText(this, getString(R.string.tv1), Toast.LENGTH_LONG).show();
            return;
        }
        if (baseModeul.getbUserinfo() == null) {
            Toast.makeText(this, getString(R.string.tv12), Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(currentUploadPath)) {
            toastMessage(getString(R.string.tv2));
            return;
        }
        if (cosxmlTask == null) {
            //??????????????????
            File file = new File(currentUploadPath);
            //????????????100M
            if (file.length() > feiliength) {
                toastMessage(String.format(getString(R.string.tv3) + "", (feiliength / 1024 / 1024)));
                tv_state.setText(String.format(getString(R.string.tv3) + "", feiliength / 1024 / 1024));
                return;
            }
            //????????????
            boolean vedioFile = FileUtils.isVedioFile(file.getPath());
            //????????????
            boolean img = FileUtils.getFileType(file.getPath());
            //***??????????????????****/

            if (!vedioFile && !img) {
                toastMessage(getString(R.string.tv5));
                return;
            }
            if (!vedioFile && baseModeul.getTYPE() == ACTIVITY_VIDEO) {
                toastMessage(getString(R.string.tv6));
                return;
            }
            if (!img && baseModeul.getTYPE() == ACTIVITY_IMG) {
                toastMessage(getString(R.string.tv7));
                return;
            }

            //????????????????????????10M
            if (baseModeul.getTYPE() == ACTIVITY_IMG && file.length() > sfeiliength1) {
                toastMessage(getString(R.string.tv8));
                return;
            }

            //??????????????????
            if (TextUtils.isEmpty(baseModeul.getTitle())) {
                baseModeul.dialogshow(this);
                return;
            }

            //***??????????????????****/
            String cosPath = "";
            Bitmap bitmap = null;
            switch (baseModeul.getTYPE()) {
                case ACTIVITY_VIDEO:
                    if (baseModeul.getSvideo() == null) {
                        toastMessage("???????????????????????????");
                        baseModeul.dialogshow(this);
                        return;
                    }
                    bitmap = baseModeul.getVedioThumbnail(file);                          //?????????????????????
                    baseModeul.setVedioTotalTime(baseModeul.getVedioTotalTime(file));    //???????????????????????????
                    baseModeul.setVideoTime(FileUtils.generateTime(baseModeul.getVedioTotalTime()));
                    if (baseModeul.getVedioTotalTime() / 1000 > uptime) {
                        String msg = String.format("????????????????????????%ss???", uptime);
                        toastMessage(msg);
                        tv_state.setText(msg);
                        hideshow.setVisibility(View.GONE);
                        return;
                    }
                    cosPath = String.format("%s/%s/%s.mp4", UP_VIDEO, FileUtils.DateTime(false), !TextUtils.isEmpty(baseModeul.getUserid()) ? baseModeul.getUserid() + "_" + baseModeul.getRandomFileName() : baseModeul.getRandomFileName());
                    String newfileName = getFileName(String.format("%s.png", getFileName(cosPath, "\\.")), "/", true);
                    File videoImg = Imagecompressiontool.dataDir(newfileName);
                    Imagecompressiontool.qualityCompress(bitmap, videoImg); //Y??????????????????
                    imgCurrentUploadPath = videoupdate(videoImg);           //???????????????????????????????????????????????????
                    break;
                case ACTIVITY_IMG:
                    bitmap = BitmapFactory.decodeFile(currentUploadPath);  //?????????????????????
                    cosPath = String.format("%s/%s/%s.png", UP_PICTURE, FileUtils.DateTime(false), !TextUtils.isEmpty(baseModeul.getUserid()) ? baseModeul.getUserid() + "_" + baseModeul.getRandomFileName() : baseModeul.getRandomFileName());
                    //????????????1M????????????
                    if (file.length() > sfeiliength2) {
                        File fileImg = Imagecompressiontool.dataDir(file.getName());
                        Imagecompressiontool.qualityCompress(bitmap, fileImg);
                        currentUploadPath = fileImg.getPath();
                        baseModeul.setIsvupcuimg(true);
                    }
                    break;
            }

            try {
                if (transferManager != null) {
                    //??????????????????
                    cosxmlTask = transferManager.upload(bucketName, cosPath, currentUploadPath, null);
                    //??????????????????????????????????????????
                    cosxmlTask.setTransferStateListener(new TransferStateListener() {
                        @Override
                        public void onStateChanged(final TransferState state) {
                            refreshUploadState(state);
                        }
                    });
                    //???????????????????????? ??????????????????
                    cosxmlTask.setCosXmlProgressListener(new CosXmlProgressListener() {
                        @Override
                        public void onProgress(final long complete, final long target) {
                            refreshUploadProgress(complete, target);
                        }
                    });
                    //????????????????????????, ????????????????????????
                    cosxmlTask.setCosXmlResultListener(new CosXmlResultListener() {
                        @Override
                        public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                            COSXMLUploadTask.COSXMLUploadTaskResult cOSXMLUploadTaskResult = (COSXMLUploadTask.COSXMLUploadTaskResult) result;
                            cosxmlTask = null;
                            //???????????????????????????
                            String accessUrl = cOSXMLUploadTaskResult.accessUrl;
                            setResult(RESULT_OK); //???????????????????????????
                            uiAction(new Runnable() {
                                @Override
                                public void run() {
                                    toastMessage(getString(R.string.tv_udate_ok));
                                    btn_left.setVisibility(View.GONE);
                                    if (baseModeul.isIsvupcuimg()) {
                                        new File(currentUploadPath).delete(); //????????????New????????????
                                    }

                                    baseModeul.RequestPost(accessUrl, imgCurrentUploadPath);
                                }
                            });
                        }

                        @Override
                        public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                            if (cosxmlTask.getTaskState() != TransferState.PAUSED) {
                                cosxmlTask = null;
                                uiAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        pb_upload.setProgress(0);
                                        tv_progress.setText("");
                                        tv_state.setText("???");
                                    }
                                });
                            }
                            exception.printStackTrace();
                            serviceException.printStackTrace();
                        }
                    });
                    btn_left.setText("??????");
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toastMessage("??????????????????");
                            finish();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toastMessage("??????????????????");
                        finish();
                    }
                });
            }
        }
    }

    /**
     * ????????? TransferConfig???????????????????????????????????????????????????????????? SDK ????????????
     */
    public void getBuckets2() {
        cosXmlService = CosServiceFactory.getCosXmlService(this, bucketRegion, BuildConfig.COS_SECRET_ID, BuildConfig.COS_SECRET_KEY, true);
        // ????????? TransferConfig???????????????????????????????????????????????????????????? SDK ????????????
        TransferConfig transferConfig = new TransferConfig.Builder().build();
        //????????? TransferManager
        transferManager = new TransferManager(cosXmlService, transferConfig);
    }

    /**
     * ??????????????????????????????
     */
    public void getBuckets1() {
        CosXmlService cosXmlService = CosServiceFactory.getCosXmlService(this, BuildConfig.COS_SECRET_ID, BuildConfig.COS_SECRET_KEY, false);
        cosXmlService.getServiceAsync(new GetServiceRequest(), new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, final CosXmlResult result) {
                uiAction(new Runnable() {
                    @Override
                    public void run() {
                        List<ListAllMyBuckets.Bucket> buckets = ((GetServiceResult) result).listAllMyBuckets.buckets;
                        ListAllMyBuckets.Bucket bucket = buckets.get(buckets.size() - 1);
                        //??????????????????????????????
                        bucketName = bucket.name;
                        bucketRegion = bucket.location;
                        baseModeul.setFolderName(null);
                        getBuckets2();
                    }
                });
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                setLoading(false);
                toastMessage("???????????????????????????");
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
     * ????????????????????????????????????
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    private void myonActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OPEN_FILE_CODE && resultCode == Activity.RESULT_OK && data != null) {
            String path = FilePathHelper.getAbsPathFromUri(this, data.getData());
            File file = new File(path);
            if (file.length() > feiliength) {
                toastMessage(String.format(getString(R.string.tv3) + "", (feiliength / 1024 / 1024)));
                tv_state.setText(String.format(getString(R.string.tv3) + "", feiliength / 1024 / 1024));
                return;
            }
            if (TextUtils.isEmpty(path)) {
                iv_image.setImageBitmap(null);
            } else {
                //??????????????????????????????????????????????????????
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    if (bitmap != null) {
                        iv_image.setImageBitmap(bitmap);
                    } else {
                        //??????????????????->????????????
                        Bitmap vedioThumbnail = baseModeul.getVedioThumbnail(file);
                        if (vedioThumbnail != null) {
                            iv_image.setImageBitmap(vedioThumbnail);
                            //????????????????????????
                            baseModeul.setVedioTotalTime(baseModeul.getVedioTotalTime(file));
                            //????????????????????????XX???
                            if (baseModeul.getVedioTotalTime() / 1000 > uptime) {
                                String msg = String.format(getString(R.string.tv_time) + "", uptime);
                                toastMessage(msg);
                                tv_state.setText(msg);
                                hideshow.setVisibility(View.GONE);
                                return;
                            }
                        } else {
                            //?????????????????????????????????
                            iv_image.setImageResource(R.drawable.file);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            setdeftopenpath(path);
        }
    }

    /**
     * ??????????????????????????????????????????
     */
    private void setdeftopenpath(String path) {
        currentUploadPath = path;
        pb_upload.setProgress(0);
        tv_progress.setText("");
        tv_state.setText("????????????");
        hideshow.setVisibility(View.GONE);
        baseModeul.setTitle("");
        baseModeul.setIsvupcuimg(false);
        upfile = true;
        btn_left.setVisibility(View.VISIBLE);
    }

    /**
     * ????????????????????????
     */
    private String videoupdate(File file) {
        if (transferManager != null) {
            cosxmlTask = transferManager.upload(bucketName, UP_VIDEO + "/" + FileUtils.DateTime(false) + "/" + file.getName(), file.getPath(), null);
            //??????????????????????????????????????????
            cosxmlTask.setTransferStateListener(new TransferStateListener() {
                @Override
                public void onStateChanged(final TransferState state) {

                }
            });
            //???????????????????????? ??????????????????
            cosxmlTask.setCosXmlProgressListener(new CosXmlProgressListener() {
                @Override
                public void onProgress(final long complete, final long target) {

                }
            });
            //????????????????????????, ????????????????????????
            cosxmlTask.setCosXmlResultListener(new CosXmlResultListener() {
                @Override
                public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                    COSXMLUploadTask.COSXMLUploadTaskResult cOSXMLUploadTaskResult = (COSXMLUploadTask.COSXMLUploadTaskResult) result;
                    cosxmlTask = null;
                    //???????????????COS?????????????????????
                    imgCurrentUploadPath = cOSXMLUploadTaskResult.accessUrl;
                    //??????????????????????????????????????????
                    file.delete();

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
        } else {
            toastMessage("??????????????????");
            finish();
        }
        return imgCurrentUploadPath;
    }

    /**
     * ???????????????
     */
    private Callback callback = new Callback() {
        @Override
        public void onSuccess() {
            //???????????????
            Intent intent = new Intent();
            intent.setAction("livebroadcastActivity");
            intent.addCategory("android.intent.category.DEFAULT");
            //???????????????
            //intent.setData(Uri.parse("http://www.baidu.com"));
            startActivity(intent);
        }

        @Override
        public void onappeal() {
            /**
             * ??????????????????
             */
            Intent intent = new Intent();
            intent.setAction("com.paixide.activity.edit.activity_nickname3");
            intent.addCategory("android.intent.category.DEFAULT");
            startActivity(intent);
        }

        @Override
        public void onFall() {

        }
    };

    /**
     * ????????????????????????
     */
    public void sACTION_GET_CONTENT() {
        if (cosxmlTask == null) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            switch (baseModeul.getTYPE()) {
                case ACTIVITY_VIDEO:
                    intent.setType("video/*");
                    break;
                case ACTIVITY_IMG:
                    intent.setType("image/*");
                    break;
            }
            startActivityForResult(intent, OPEN_FILE_CODE);
        }
    }

}

