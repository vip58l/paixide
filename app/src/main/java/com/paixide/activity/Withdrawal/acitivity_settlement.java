package com.paixide.activity.Withdrawal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.paixide.Module.api.qrinfo;
import com.paixide.BasActivity.BasActivity2;
import com.paixide.Util.StatusBarUtil;
import com.paixide.activity.edit.activity_updateedit;
import com.paixide.app.DemoApplication;
import com.paixide.dialog.dialog_game;
import com.paixide.dialog.dialog_Config;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.paixide.Module.api.qrcode;
import com.paixide.widget.itembackTopbr;
import com.paixide.R;
import com.paixide.Util.Imagecompressiontool;
import com.paixide.Util.Toashow;
import com.paixide.Util.config;
import com.paixide.listener.Paymnets;
import com.tencent.qcloud.costransferpractice.common.FilePathHelper;
import com.tencent.qcloud.tim.tuikit.live.utils.GlideEngine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.paixide.Util.ActivityLocation.OPEN_SET_REQUEST_CODE;
import static com.paixide.Util.Imagecompressiontool.dataDir;
import static com.paixide.Util.config.getFileName;
import static com.paixide.Util.config.setResult;
import static com.paixide.tencent.cos.MySessionCredentialProvider.DELETEMultipleObject;

/**
 * ??????????????????
 */
public class acitivity_settlement extends BasActivity2 {
    private static final String TAG = acitivity_settlement.class.getSimpleName();
    @BindView(R.id.itemback)
    itembackTopbr itemback;
    @BindView(R.id.qrcode)
    ImageView qrcode;
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.accounts)
    EditText accounts;
    @BindView(R.id.send)
    TextView send;

    private boolean myqrqr = false;
    public final int OPEN_FILE_CODE = 10001;
    public final int OPEN_CAMERA = 10002;
    public final int IMAGE_CROP_CODE = 10003;
    private File dataDirfile;
    private qrinfo info = new qrinfo();
    private qrcode qrcode1;
    public static void starsetAction(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, acitivity_settlement.class);
        context.startActivity(intent);
    }
    @Override
    protected int getview() {
        StatusBarUtil.setStatusBar(activity, getResources().getColor(R.color.home3));
        return R.layout.activity_acitivity_settlement;
    }

    @Override
    public void iniview() {
        itemback.settitle(getString(R.string.tv_msg40));
        itemback.setHaidtopBackgroundColor(true);
    }

    @Override
    public void initData() {
        datamodule.qrcode(paymnets);
    }

    @OnClick({R.id.send, R.id.qrcode})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                if (myqrqr) {
                    //???????????????
                    deletevideo();
                } else {
                    //????????????
                    upsend();
                }
                break;
            case R.id.qrcode:
                activity_photo_album(qrcode1.getQrcode());
                break;
        }
    }

    @Override
    public void OnEorr() {

    }


    /**
     * ????????????
     */
    private void upsend() {
        if (isusername() && checkPermissions()) {
            OpenFile();
        }

    }

    /**
     * ????????????
     */
    private void OpenFile() {
        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*"); //??????????????????
        startActivityForResult(intent, OPEN_FILE_CODE);
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

    //????????????
    private void SetImageToView(Intent data) {
        if (data == null) {
            return;
        }
        Bundle extras = data.getExtras();
        if (extras == null) {
            return;
        }
        Bitmap bitmap = extras.getParcelable("data");
        qrcode.setImageBitmap(bitmap);
        qrcode.setVisibility(View.VISIBLE);
        File dataDir = dataDir();
        Imagecompressiontool.oNqualityCompress(bitmap, dataDir);
        setResult(setResult, data);

        //???????????????
        qrcodeupload();

    }

    //????????????????????????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case OPEN_FILE_CODE:
                    setIntent(data);
                    break;
            }

        }
    }


    /**
     * ????????????????????? ??????????????????
     *
     * @param data
     */
    public void setIntent(Intent data) {
        String path = FilePathHelper.getAbsPathFromUri(this, data.getData());
        File file = new File(path);
        if (dataDirfile != null) {
            dataDirfile.delete();
        }

        dataDirfile = dataDir();
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap != null) {
            qrcode.setImageBitmap(bitmap);
            qrcode.setVisibility(View.VISIBLE);
            send.setText("????????????");
            myqrqr = true;

            //????????????
            if (file.length() > 1024 * 500) {
                //????????????
                Imagecompressiontool.sizeCompress(bitmap, dataDirfile, 2);
            } else {
                //?????????
                Imagecompressiontool.oNqualityCompress(bitmap, dataDirfile);
            }
            Bitmap bitmap1 = null;
            while (dataDirfile.length() > 1024 * 500) {
                if (bitmap1 == null) {
                    bitmap1 = bitmap;
                }
                bitmap1 = Imagecompressiontool.sizeCompress(bitmap1, dataDirfile, 2);
            }
        }
    }

    /**
     * ??????????????????????????????
     */
    private void qrcodeupload() {
        if (!config.isNetworkAvailable()) {
            Toashow.show(getResources().getString(R.string.eorrfali2));
            return;
        }
        if (!isusername()) {
            Log.d(TAG, "??????????????????????????????");
            return;
        }
        if (userInfo.getState() != 2) {
            Toashow.show(getString(R.string.tv_settlenent));
            return;
        }
        showDialog();
        new Thread(runnable).start();


    }


    /**
     * ???????????????????????????
     */
    public void uploadavatar(File file) {
        TransferConfig transferConfig = new TransferConfig.Builder().build();
        TransferManager transferManager = new TransferManager(DemoApplication.cosXmlService, transferConfig);
        String bucket = DemoApplication.bucket.name;  //?????????????????????BucketName-APPID
        String cosPath = String.format("qrcode/%s/%s", config.DateTime(false), file.getName());   //?????????????????????????????????????????????????????????
        String srcPath = file.getPath(); //???????????????????????????
        //????????????????????????????????? UploadId????????????????????? uploadId ????????????????????????????????? null
        String uploadId = null;
        // ????????????
        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucket, cosPath, srcPath, uploadId);
        //????????????????????????
        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                COSXMLUploadTask.COSXMLUploadTaskResult cOSXMLUploadTaskResult = (COSXMLUploadTask.COSXMLUploadTaskResult) result;
                String accessUrl = cOSXMLUploadTaskResult.accessUrl;
                info.setQrcode(accessUrl);
                if (file != null) {

                    //??????????????????
                    file.delete();

                    //???????????????
                    if (qrcode1 != null) {
                        String qrcode = qrcode1.getQrcode();
                        String fileName = getFileName(qrcode, ".com/");
                        List<String> objectList = Arrays.asList(fileName);
                        DELETEMultipleObject(objectList);
                    }
                }
                handler.postDelayed(runnable2, 200);
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissDialog();
                    }
                });
                if (clientException != null) {
                    clientException.printStackTrace();
                } else {
                    serviceException.printStackTrace();
                }
            }
        });


    }


    /**
     * ??????????????????
     */
    private void Enabled() {
        send.setText(R.string.tv_msg45);
        itemback.settitle(getString(R.string.tv_msg44));
        send.setVisibility(View.GONE);
        send.setEnabled(false);
        username.setEnabled(false);
        accounts.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataDirfile != null) {
            deleteFolderFile(dataDirfile.getAbsolutePath(), true);
        }
    }

    /**
     * ???????????????????????????????????????
     */
    public void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) { //??????
                    File[] files = file.listFiles();
                    for (File file1 : files) {
                        deleteFolderFile(file1.getAbsolutePath(), true);
                    }
                }

                if (deleteThisPath) {
                    if (!file.isDirectory()) { //????????????????????????
                        file.delete();
                    } else { //??????
                        if (file.listFiles().length == 0) { //??????????????????????????????????????????
                            file.delete();
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //????????????????????????
    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> list = new ArrayList<>();
            list.add(Manifest.permission.CAMERA);
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            for (String permission : list) {
                int star = ContextCompat.checkSelfPermission(context, permission);
                if (star != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, list.toArray(new String[list.size()]), OPEN_SET_REQUEST_CODE);
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == OPEN_SET_REQUEST_CODE) {
            for (String permission : permissions) {
                int STATE = ContextCompat.checkSelfPermission(context, permission);
                if (STATE != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, OPEN_SET_REQUEST_CODE);
                    return;
                }
            }
        }

    }

    /**
     * ????????????
     */
    private void deletevideo() {
        if (dataDirfile == null || !myqrqr) {
            Toashow.show("?????????????????????????????????");
            return;
        }
        dialog_game dialogGame = dialog_Config.dialog_game(context);
        dialogGame.setTitle(getString(R.string.qr_qrcode_msg));
        dialogGame.setKankan(getString(R.string.btn_ok));
        dialogGame.setTextColor(getResources().getColor(R.color.half_transparent));
        dialogGame.setkankanColor(getResources().getColor(R.color.c_fu));
        dialogGame.setTextSize(14);
        dialogGame.setPaymnets(paymnets);
    }

    /**
     * ???????????????
     */
    private Paymnets paymnets = new Paymnets() {

        @Override
        public void onSuccess(Object object) {
            qrcode1 = (qrcode) object;
            username.setText(qrcode1.getName());
            accounts.setText(qrcode1.getAccount());
            if (!TextUtils.isEmpty(qrcode1.getQrcode())) {
                qrcode.setVisibility(View.VISIBLE);
                GlideEngine.loadImage(qrcode, qrcode1.getQrcode());
            }
            Enabled();
        }

        @Override
        public void activity() {
            qrcodeupload();
        }
    };

    private Paymnets paymnets2 = new Paymnets() {

        @Override
        public void onSuccess() {
            dismissDialog();
            if (!TextUtils.isEmpty(info.getQrcode())) {
                qrcode.setVisibility(View.VISIBLE);
                GlideEngine.loadImage(qrcode, info.getQrcode());
                if (qrcode1 == null) {
                    qrcode1 = new qrcode();

                }
                qrcode1.setQrcode(info.getQrcode());
            }
            Enabled();
        }

        @Override
        public void isNetworkAvailable() {
            dismissDialog();
        }

        @Override
        public void onFail() {
            dismissDialog();
        }

        @Override
        public void onSuccess(String msg) {
            Toashow.show(msg);
            dismissDialog();
        }
    };

    /**
     * ????????????????????????
     */
    private boolean isusername() {
        String user = username.getText().toString().trim();
        String account = accounts.getText().toString().trim();
        info.setUserid(userInfo.getUserId());
        info.setName(user);
        info.setAccount(account);
        info.setType(1);
        if (TextUtils.isEmpty(info.getName())) {
            Toashow.show(getString(R.string.tv_msg42));
            return false;
        }
        if (TextUtils.isEmpty(info.getAccount())) {
            Toashow.show(getString(R.string.tv_msg43));
            return false;
        }
        return true;
    }

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            boolean isboobogin = false;
            try {
                isboobogin = config.loginByGet();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (isboobogin) {
                uploadavatar(dataDirfile);
            }
        }
    };

    /**
     * ??????????????????
     */
    private Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            datamodule.postsetQrcode(info, paymnets2);
        }
    };

}