package com.paixide.activity.edit;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.paixide.Util.glide.ImageLoadHelper;
import com.paixide.dialog.dialog_Blocked;
import com.paixide.getHandler.Webrowse;
import com.paixide.BasActivity.BasActivity2;
import com.paixide.Util.Glideload;
import com.paixide.Util.StatusBarUtil;
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
import com.paixide.R;
import com.paixide.activity.sesemys.activity_sesemys;
import com.paixide.Util.Constants;
import com.paixide.Util.Imagecompressiontool;
import com.paixide.Util.Toashow;
import com.paixide.Util.config;
import com.paixide.listener.Paymnets;
import com.paixide.app.DemoApplication;
import com.paixide.getHandler.PostModule;
import com.tencent.opensource.model.UserInfo;
import com.tencent.qcloud.costransferpractice.utils.CacheUtil;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.OnClick;

import static com.paixide.Util.ActivityLocation.OPEN_SET_REQUEST_CODE;
import static com.paixide.Util.config.getFileName;
import static com.paixide.Util.config.setResult;
import static com.paixide.app.DemoApplication.cosXmlService;
import static com.paixide.tencent.cos.MySessionCredentialProvider.DELETEMultipleObject;

/**
 * ????????????
 */
public class activity_uploadavatar extends BasActivity2 {
    private String TAG = activity_uploadavatar.class.getSimpleName();
    @BindView(R.id.itemback)
    itembackTopbr itemback;
    @BindView(R.id.avatar)
    ImageView avatar;
    @BindView(R.id.titlemsg)
    TextView titlemsg;
    @BindView(R.id.avatar_msg)
    TextView avatar_msg;
    private Uri imageUri;

    @Override
    protected int getview() {
        StatusBarUtil.setStatusBar(this, getResources().getColor(R.color.transparent));
        return R.layout.activity_uploadavatar;
    }

    @Override
    public void iniview() {
        context = this;
        itemback.settitle(getString(R.string.picavatar));
        avatar_msg.setText(userInfo.getInreview() == 0 ? getString(R.string.aa1) : getString(R.string.aa2));
        avatar_msg.setTextColor(userInfo.getInreview() == 0 ? getResources().getColor(R.color.homeback) : getResources().getColor(R.color.colorAccent));

        itemback.setHaidtopBackgroundColor(true);
        userInfo = UserInfo.getInstance();
        TYPE = getIntent().getIntExtra(Constants.TYPE, -1);
        if (TextUtils.isEmpty(userInfo.getAvatar())) {
            ImageLoadHelper.glideShowCornerImageWithUrl(context, userInfo.getSex().equals("1") ? R.mipmap.avatar : R.mipmap.avatar2, avatar);
        } else {
            //??????????????????
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            ViewGroup.LayoutParams params = avatar.getLayoutParams();
            params.width = (int) (displayMetrics.widthPixels * 0.8F);
            params.height = (int) (displayMetrics.widthPixels * 0.8F);
            avatar.setLayoutParams(params);
            ImageLoadHelper.glideShowCornerImageWithUrl(context, userInfo.getAvatar(), avatar);
        }
        if (!TextUtils.isEmpty(userInfo.getSex())) {
            titlemsg.setText(String.format(titlemsg.getText().toString(), userInfo.getSex().equals("1") ? getString(R.string.sex2) : getString(R.string.sex3)));
        }
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.upload, R.id.contentcll})
    public void OnClick(View v) {
        if (userInfo.getState() == 3) {
            dialog_Blocked.myshow(context);
            return;
        }
        switch (v.getId()) {
            case R.id.upload:
                if (requestPermissions(1))
                    toPicture();
                break;
            case R.id.contentcll:
                if (requestPermissions(2))
                    toCamera();
                break;
        }
    }

    @Override
    public void OnEorr() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case OPEN_FILE_CODE: //??????????????????
                    startUCrop(data.getData());
                    break;
                case OPEN_CAMERA_CODE://??????????????????
                    // ???????????????uri ??????File
                    starPhotoZoom(imageUri);
                    break;
                case IMAGE_CROP_CODE:
                    SetImageToView(data); //??????????????????
                    break;
                case UCrop.REQUEST_CROP:
                    //????????????????????????
                    startUCrop(data);
                    break;
            }
        }
    }


    /**
     * ????????????????????? ???Android????????????????????????????????????MediaMetadataRetriever??????
     *
     * @param vedioFile
     * @return
     */
    public static Bitmap getVedioThumbnail(File vedioFile) {
        if (!vedioFile.exists()) {
            return null;
        }
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(vedioFile.getAbsolutePath());
        Bitmap bitmap = retriever.getFrameAtTime();
        //Bitmap bitmap = retriever.getFrameAtTime(1000);//???????????????,???????????????????????????????????????
        return bitmap;
    }

    /**
     * ???????????????????????????
     *
     * @param vedioFile
     * @return
     */
    public static Long getVedioTotalTime(File vedioFile) {
        if (!vedioFile.exists()) {
            return null;
        }
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(vedioFile.getAbsolutePath());
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
        Long vedioTotalTime = getVedioTotalTime(file);
        String time = config.generateTime(vedioTotalTime);
    }

    /**
     * ???????????????????????????
     */
    public void saveBitmapfile(String path) throws ExecutionException, InterruptedException {
        //??????Glide????????????
        File file = Glide.with(activity).asFile().load(path).submit().get();
        //???????????????
        displayToGallery(context, file);
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
     * ??????????????????????????????
     */
    private void uploads(File file) {
        if (!config.isNetworkAvailable()) {
            Toashow.show(getResources().getString(R.string.eorrfali2));
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(Constants.PATH, Webrowse.uploads);
        params.put(Constants.USERID, userInfo.getUserId());
        params.put(Constants.Token, userInfo.getToken());
        PostModule.okhttpimage(params, file, new Paymnets() {
            @Override
            public void success(String date) {
                try {
                    Mesresult mesresult = gson.fromJson(date, Mesresult.class);
                    Toashow.show(mesresult.getMsg());
                    if (mesresult.getStatus().equals("1")) {
                        file.delete();
                        if (file != null) {
                            file.delete();
                        }
                        userInfoAvatar(mesresult.getPicture());
                    }
                } catch (Exception e) {
                    Toashow.show(e.getMessage());
                }
            }

            @Override
            public void fall(int code) {
                Log.d(TAG, "fall: ");
            }
        });

       /* Map<String,String> params = new HashMap<>();
        params.put("userid", instance.getUserId());
        params.put("image", file.getPath());
        String posturl = BuildConfig.HTTP_API + "/uploads";
        HttpUtils.RequestPost(posturl, params, new HttpUtils.HttpListener() {
            @Override
            public void success(String response) {
                Log.d(TAG, "RequestPost:" + response);

            }

            @Override
            public void onFailed(String message) {
                Log.d(TAG, "message:" + message);
            }
        });*/
    }

    /**
     * ???????????????????????????
     */
    public void uploadavatar(File file) {
        try {
            //????????? TransferConfig???????????????????????????????????????????????????????????? SDK ????????????
            TransferConfig transferConfig = new TransferConfig.Builder().build();
            //????????? TransferManager
            TransferManager transferManager = new TransferManager(cosXmlService, transferConfig);
            String bucket = DemoApplication.bucket.name;  //?????????????????????BucketName-APPID
            String cosPath = String.format("avatar/%s/%s", config.DateTime(false), file.getName());   //?????????????????????????????????????????????????????????
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
                    if (file != null) {
                        file.delete();

                        try {
                            String avatar = userInfo.getAvatar();
                            if (!TextUtils.isEmpty(avatar)) {
                                String fileName = getFileName(avatar, ".com/");
                                List<String> objectList = Arrays.asList(fileName);
                                DELETEMultipleObject(objectList);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    updateaccessUrl(accessUrl);
                }

                @Override
                public void onFail(CosXmlRequest request, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                    if (clientException != null) {
                        clientException.printStackTrace();
                    } else {
                        serviceException.printStackTrace();
                    }
                    dialogLoadings();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            toastLongMessage(getString(R.string.regs1upate));
            dialogLoadings();
        }
    }

    /**
     * ??????????????????
     */
    private void toCamera() {
        imageUri = createImageFile();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //????????????????????????????????????
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, OPEN_CAMERA_CODE);
        }

    }

    /**
     * ????????????
     */
    private void toPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, OPEN_FILE_CODE);
    }

    /**
     * ?????????????????? ????????????????????????????????????
     *
     * @return
     */
    private Uri createImageFile() {
        String FileName = System.currentTimeMillis() + ".jpg";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            //APP???????????? ??????????????????????????????????????????
            File cropPhoto = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(), FileName);
            return FileProvider.getUriForFile(context, getPackageName() + ".fileprovider", cropPhoto);
        } else {
            //APP???????????? ??????????????????????????????????????????
            File cropPhoto = new File(getExternalCacheDir(), FileName);
            return Uri.fromFile(cropPhoto);
        }
    }

    /**
     * ??????????????????
     *
     * @param uri
     */
    private void starPhotoZoom(Uri uri) {
        if (uri == null) {
            Toashow.show("?????????????????????");
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //???????????????????????????????????????????????????Uri??????????????????
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");  //????????????
        intent.putExtra("aspectX", Build.MANUFACTURER.equals("HUAWEI") ? 1 : 1);
        intent.putExtra("aspectY", Build.MANUFACTURER.equals("HUAWEI") ? 1 : 1);
        intent.putExtra("outputX", 300); //?????????
        intent.putExtra("outputY", 300); //?????????
        intent.putExtra("return-data", true);//???????????????????????? Bitmap
        intent.putExtra("scale ", true);     //??????????????????
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//?????????????????????
        intent.putExtra("noFaceDetection", false);       //????????????
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);          //??????????????????????????????????????????
        startActivityForResult(intent, IMAGE_CROP_CODE);
    }

    /**
     * ??????????????????????????????
     *
     * @param data
     */
    private void SetImageToView(Intent data) {
        Bundle extras = data.getExtras();
        Bitmap bitmap = (data == null || data.getData() == null) ? imageUriimageUri() : extras.getParcelable("data");
        avatar.setImageBitmap(bitmap);
        try {
            //android 11??????????????????/storage/emulated/0/Pictures
            String FilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            Log.d(TAG, "FilePath: " + FilePath);

            //android 10?????? /storage/emulated/0
            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            Log.d(TAG, "absolutePath: " + absolutePath);

            //storage/emulated/0/Android/data/com.paixide/files/Pictures
            String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            Log.d(TAG, "path: " + path);

            //storage/emulated/0/Android/data/com.paixide/cache/1646635494633.jpg
            File cropPhoto = new File(CacheUtil.getCacheDirectory(context, null), System.currentTimeMillis() + ".jpg");
            Log.d(TAG, "cropPhoto0: " + cropPhoto.getAbsolutePath());

            //storage/emulated/0/Android/data/com.paixide/files/Pictures/1646635494633.jpg
            File cropPhoto1 = new File(path + File.separator + System.currentTimeMillis() + ".jpg");
            Log.d(TAG, "cropPhoto1: " + cropPhoto1.getAbsolutePath());

            //?????????cropPhoto??????
            Imagecompressiontool.oNqualityCompress(bitmap, cropPhoto);

            //????????????????????????
            Log.d(TAG, "????????????????????????: ");
            galleryAddPic(cropPhoto.getAbsolutePath());

            Toashow.show(getString(R.string.pics) + cropPhoto.getAbsolutePath());

            setResult(setResult, data);

            //????????????????????????
            //uploads(cropPhoto);

            //?????????????????????????????????
            new Thread(new myThread(cropPhoto)).start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * ????????????????????????
     *
     * @param type
     * @return
     */
    private boolean requestPermissions(int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> list = new ArrayList<>();
            list.add(Manifest.permission.CAMERA);
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            for (String permission : list) {
                int star = ContextCompat.checkSelfPermission(context, permission);
                if (star != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, list.toArray(new String[list.size()]), type == 1 ? OPEN_SET_REQUEST_CODE : OPEN_CAMERA_CODE);
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case OPEN_SET_REQUEST_CODE:
                for (String permission : permissions) {
                    Log.d(TAG, "=====" + permission);
                    int STATE = ContextCompat.checkSelfPermission(context, permission);
                    if (STATE != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "?????????????????? ");
                        return;
                    }
                }
                toPicture();
                break;
            case OPEN_CAMERA_CODE:
                for (String permission : permissions) {
                    Log.d(TAG, "=====" + permission);
                    int STATE = ContextCompat.checkSelfPermission(context, permission);
                    if (STATE != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "?????????????????? ");
                        return;
                    }
                }
                toCamera();
                break;
        }
    }

    /**
     * ???????????????IM??????????????????
     *
     * @param accessUrl
     */
    public void userInfoAvatar(String accessUrl) {
        //????????????????????????
        userInfo.setAvatar(accessUrl);
        //????????????IM????????????
        activity_sesemys.updateProfile(userInfo);

        //???????????????????????????ACTIVITY
        if (TYPE == Constants.TENCENT) {
            startActivity(new Intent(context, activity_updateedit.class));
        }
        dialogLoadings();
        finish();
    }

    /**
     * ????????????????????????
     */
    private Paymnets paymnets = new Paymnets() {
        @Override
        public void isNetworkAvailable() {
            Toashow.show(getString(R.string.eorrfali2));
            dialogLoadings();
        }

        @Override
        public void onFail() {
            Toashow.show(getString(R.string.eorrfali3));
            dialogLoadings();
        }

        @Override
        public void onSuccess(String accessUrl) {
            userInfoAvatar(accessUrl);
        }


    };

    /**
     * ???????????????????????????????????????
     */
    private class myThread extends Thread {
        private File file;

        public myThread(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            super.run();
            try {
                netonk = config.loginByGet();
                uploadavatar(file);//???????????????
            } catch (IOException e) {
                e.printStackTrace();
                dialogLoadings();
            }
        }

    }

    /**
     * ????????????????????????
     *
     * @param accessUrl
     */
    private void updateaccessUrl(String accessUrl) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                datamodule.Postsave(accessUrl, paymnets);
            }
        });
    }

    /**
     * ????????????????????????????????????
     */
    private void startUCrop() {
        //???????????????????????????
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
        Uri destinationUri = Uri.fromFile(new File(CacheUtil.getCacheDirectory(this, null), fileName));
        UCrop uCrop = UCrop.of(imageUri, destinationUri);
        UCrop.Options options = new UCrop.Options();
        //????????????????????????????????????
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        //??????toolbar??????
        options.setToolbarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        //?????????????????????
        options.setStatusBarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        //????????????????????????
        //options.setFreeStyleCropEnabled(true);
        uCrop.withOptions(options);
        uCrop.withAspectRatio(1, 1); //??????
        uCrop.start(this);
    }

    /**
     * ??????????????????????????????
     *
     * @param imageUri
     */
    private void startUCrop(Uri imageUri) {
        //???????????????????????????
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
        Uri destinationUri = Uri.fromFile(new File(CacheUtil.getCacheDirectory(this, null), fileName));
        UCrop uCrop = UCrop.of(imageUri, destinationUri);
        UCrop.Options options = new UCrop.Options();
        //????????????????????????????????????
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        //??????toolbar??????
        options.setToolbarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        //?????????????????????
        options.setStatusBarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        //????????????????????????
//        options.setFreeStyleCropEnabled(true);
        uCrop.withOptions(options);
        uCrop.withAspectRatio(1, 1); //??????
        uCrop.start(this);
    }

    /**
     * ??????????????????????????????
     *
     * @param data
     */
    private void startUCrop(Intent data) {
        //???????????????????????????UI
        setResult(setResult, data);

        final Uri croppedUri = UCrop.getOutput(data);
        try {
            dialogshow(getString(R.string.mbttavity));
            if (croppedUri != null) {
                Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(croppedUri));
                avatar.setImageBitmap(bit);
                File file = new File(new URI(croppedUri.toString()));
                //?????????????????????????????????
                new Thread(new myThread(file)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ??????????????????
     */
    private void toCamera2() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile2();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(context, getPackageName() + ".fileprovider", photoFile);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, OPEN_CAMERA_CODE);

        } else {
            Toashow.show("?????????????????????");
        }
    }

    private File createImageFile2() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //APP????????????
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    /**
     * ????????????????????????
     *
     * @param currentPhotoPath
     */
    private void galleryAddPic(String currentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    /**
     * ?????????????????????????????????
     *
     * @param currentPhotoPath
     */
    private void setPic(String currentPhotoPath) {
        // Get the dimensions of the View
        int targetW = avatar.getWidth();
        int targetH = avatar.getHeight();
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        avatar.setImageBitmap(bitmap);
    }

    public Bitmap imageUriimageUri() {
        try {
            //Bitmap???uri???????????????
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri));

            //Bitmap???uri???????????????
            //Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}