package com.paixide.activity;

import androidx.annotation.Nullable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import com.paixide.utils.AES.AES;
import com.tencent.qcloud.tim.tuikit.live.BuildConfig;
import com.paixide.BasActivity.BasActivity2;
import com.paixide.R;
import com.paixide.activity.ZYservices.activity_photo_album;
import com.paixide.Util.Constants;
import com.paixide.Util.Glideload;
import com.paixide.Util.Imagecompressiontool;
import com.paixide.Util.Toashow;
import com.paixide.Util.config;
import com.paixide.listener.Paymnets;
import com.paixide.app.DemoApplication;
import com.paixide.dialog.Dialog_Exit;
import com.paixide.getHandler.PostModule;
import com.paixide.widget.itembackTopbr;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.opensource.model.Mesresult;
import com.tencent.opensource.model.UserInfo;
import com.tencent.opensource.model.gethelp;
import com.tencent.qcloud.costransferpractice.transfer.Api;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.paixide.Util.Imagecompressiontool.dataDir;
import static com.paixide.Util.config.getFileName;
import static com.paixide.Util.config.setResult;
import static com.paixide.app.DemoApplication.cosXmlService;
import static com.paixide.tencent.cos.MySessionCredentialProvider.DELETEMultipleObject;

/**
 * ??????????????????
 */
public class activit_Invitefriends extends BasActivity2 {
    private final String TAG = "activit_Invitefriends";
    @BindView(R.id.itemback)
    itembackTopbr itemback;
    @BindView(R.id.icon)
    ImageView icon;
    @BindView(R.id.name1)
    TextView name1;
    @BindView(R.id.senbnt)
    TextView senbnt;
    @BindView(R.id.tv_type)
    TextView tv_type;
    @BindView(R.id.rlayout)
    RelativeLayout rlayout;

    gethelp g;
    public final int OPEN_FILE_CODE = 10001;
    public final int OPEN_CAMERA = 10002;
    public final int IMAGE_CROP_CODE = 10003;
    private File dataDir;
    private boolean isdatadirboot = false;

    @Override
    protected int getview() {
        return R.layout.activity_invitefriends;
    }

    @Override
    public void iniview() {
        itemback.setHaidtopBackgroundColor(true);
        itemback.settitle("??????????????????");
        userInfo = UserInfo.getInstance();

    }

    @Override
    public void initData() {
    PostModule.getModule(BuildConfig.HTTP_API + "/homegethelp?userid=" + userInfo.getUserId()+"&token="+userInfo.getToken(), new Paymnets() {
            @Override
            public void success(String date) {
                try {
                    g = new Gson().fromJson(date, gethelp.class);
                    name1.setText(TextUtils.isEmpty(g.getNametitle()) ? "" : g.getNametitle());
                    if (!TextUtils.isEmpty(g.getPic())) {
                        senbnt.setEnabled(false);
                        Glideload.loadImage(icon, g.getPic(), 6);
                        rlayout.setVisibility(View.VISIBLE);
                        switch (g.getType()) {
                            case 0:
                                senbnt.setText(R.string.livebr_tv3);
                                senbnt.setVisibility(View.GONE);
                                return;
                            case 1:
                                senbnt.setText(R.string.tv_msg8);
                                return;
                            case 2:
                                senbnt.setText(R.string.tv_msg126);
                                return;
                            case 3:
                            default:
                                break;

                        }

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

    @Override
    @OnClick({R.id.icon, R.id.senbnt})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.senbnt:
                if (dataDir != null && isdatadirboot) {
                    upatefile();
                } else {
                    toPicture();
                }
                break;
            case R.id.icon:
                if (g != null) {
                    Intent intent = new Intent(this, activity_photo_album.class);
                    intent.putExtra(Constants.PATHVIDEO, g.getPic());
                    startActivity(intent);
                }
                break;
        }

    }

    @Override
    public void OnEorr() {



    }


    //????????????
    private void toPicture() {
        isdatadirboot = false;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, OPEN_FILE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case OPEN_FILE_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    starPhotoZoom(data.getData());
                }
                break;
            case IMAGE_CROP_CODE:
                //??????????????????
                SetImageToView(data);
                break;

        }
    }

    //??????????????????
    private void starPhotoZoom(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //????????????
        intent.putExtra("crop", "true");
        //??????????????????
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //??????????????????
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        intent.putExtra("scale ", true);  //??????????????????
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());//?????????????????????
        intent.putExtra("noFaceDetection", false);  //??????????????????
        //?????????????????????????????????
        //Uri cropUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath() + "/paixide/" + System.currentTimeMillis() + ".png"));
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        startActivityForResult(intent, IMAGE_CROP_CODE);
    }

    //??????????????????
    private void SetImageToView(Intent data) {
        if (data == null || data.getExtras() == null) {
            return;
        }
        Bundle extras = data.getExtras();
        Bitmap bitmap = extras.getParcelable("data");
        if (bitmap != null) {
            rlayout.setVisibility(View.VISIBLE);
        }
        icon.setImageBitmap(bitmap);
        dataDir = dataDir();
        Imagecompressiontool.oNqualityCompress(bitmap, dataDir);
        setResult(setResult, data);
        isdatadirboot = true;

    }

    /**
     * ???????????????????????????
     */
    public void uploadavatar(Context context, File file) {
        if (!config.isNetworkAvailable()) {
            Toashow.show(context.getResources().getString(R.string.eorrfali2));
            return;
        }
        //????????? TransferConfig???????????????????????????????????????????????????????????? SDK ????????????
        TransferConfig transferConfig = new TransferConfig.Builder().build();
        //????????? TransferManager
        TransferManager transferManager = new TransferManager(cosXmlService, transferConfig);
        String bucket = DemoApplication.bucket.name;  //?????????????????????BucketName-APPID
        String cosPath = String.format("card/%s/%s", config.DateTime(false), file.getName());   //?????????????????????????????????????????????????????????
        String srcPath = file.getPath(); //???????????????????????????
        //????????????????????????????????? UploadId????????????????????? uploadId ????????????????????????????????? null
        String uploadId = null;
        // ????????????
        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucket, cosPath, srcPath, uploadId);
        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                COSXMLUploadTask.COSXMLUploadTaskResult cOSXMLUploadTaskResult = (COSXMLUploadTask.COSXMLUploadTaskResult) result;
                String accessUrl = cOSXMLUploadTaskResult.accessUrl;
                Log.d(TAG, "accessUrl: " + accessUrl);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FormBody(accessUrl, 4);
                    }
                });

                if (file != null) {
                    file.delete();
                    try {
                        String pic = g.getPic();
                        if (!TextUtils.isEmpty(pic)) {
                            List<String> objectList = Arrays.asList(Api.getFileName(pic));
                            DELETEMultipleObject(objectList);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                if (clientException != null) {
                    clientException.printStackTrace();
                } else {
                    serviceException.printStackTrace();
                }
            }
        });

    }

    /**
     * ????????????
     *
     * @param obj
     * @param i
     */
    public void FormBody(String obj, int i) {

        String token = AES.getStringkey(userInfo.getUserId());
        RequestBody requestBody = new FormBody.Builder()
                .add("userid", userInfo.getUserId())
                .add("nametitle", obj)
                .add("price", obj)
                .add("pic", obj)
                .add("age", obj)
                .add("token", userInfo.getToken())
                .add("type", String.valueOf(i))
                .build();
        PostModule.postModule(BuildConfig.HTTP_API + "/addhelp", requestBody, new Paymnets() {
            @Override
            public void success(String date) {

                try {
                    Mesresult mesresult = new Gson().fromJson(date, Mesresult.class);
                    Toashow.show(mesresult.getMsg());
                    if (mesresult.getStatus().equals("1")) {
                        initData();
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

    public void upatefile() {
        Dialog_Exit.dialogshow(this, new Paymnets() {
            @Override
            public void activity() {

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            boolean netonk = config.loginByGet();
                            //??????????????????
                            uploadavatar(activit_Invitefriends.this, dataDir);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toashow.show(getString(R.string.eorrfali3));
                        }
                    }
                }.start();

            }

            @Override
            public void payens() {
            }
        });
    }

}