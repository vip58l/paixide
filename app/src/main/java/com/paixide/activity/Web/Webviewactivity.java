package com.paixide.activity.Web;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.paixide.BasActivity.BasActivity2;
import com.paixide.R;
import com.paixide.Util.Constants;
import com.paixide.Util.StatusBarUtil;
import com.paixide.Util.config;
import com.paixide.widget.itembackTopbr;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;

import butterknife.BindView;

public class Webviewactivity extends BasActivity2 {
    private String TAG = Webviewactivity.class.getSimpleName();
    @BindView(R.id.dy_webview)
    WebView webView;
    @BindView(R.id.itemback)
    itembackTopbr itemback;

    public static void setAction(Context context, String url) {
        Intent intent = new Intent();
        intent.setClass(context, Webviewactivity.class);
        intent.putExtra(Constants.VIDEOURL, url);
        context.startActivity(intent);
    }

    @Override
    protected int getview() {
        StatusBarUtil.transparencyBar(activity);
        StatusBarUtil.setNavigationBarColor(activity);
        return R.layout.itemwebview;
    }

    @Override
    public void iniview() {
        videoUrl = getIntent().getStringExtra(Constants.VIDEOURL);
        JSON = getIntent().getStringExtra(Constants.JSON);
        itemback.setTRANSPARENT();
        webView.loadUrl(videoUrl);
        basestartActivity.WebSettings(webView);
        webView.setWebViewClient(new webviewClient());
        webView.setWebChromeClient(new myWebChromeClient());
        webView.setVisibility(View.INVISIBLE);
        webView.setOnLongClickListener(new setOnLongClickListener());
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

                //?????????????????????????????????
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

                //????????????
                //if (url.endsWith(".jpg") || url.endsWith(".png") || url.endsWith(".gif") || url.endsWith(".apk") || url.endsWith(".mp4") || url.endsWith(".mp3"))
                //new mydowon(url).start();

            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void OnClick(View v) {

    }

    @Override
    public void OnEorr() {

    }

    private class webviewClient extends WebViewClient {

        @Override
        public void onFormResubmission(WebView view, Message dontResend, Message resend) {
            resend.sendToTarget();
            Log.d(TAG, "onFormResubmission: ");
        }

        public void onPageFinished(WebView view, String paramString) {
            if (!view.getSettings().getLoadsImagesAutomatically()) {
                //???????????????????????????????????????
                view.getSettings().setLoadsImagesAutomatically(true);
                webView.getSettings().setBlockNetworkImage(false);
            }
            super.onPageFinished(view, paramString);
            webView.setVisibility(View.VISIBLE);

            //??????JS??????
            //view.loadUrl("javascript:function setTop(){document.querySelector('div.appbanner').style.display=\"none\";}setTop();");


        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            webView.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            webView.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            //???????????????????????????????????????
            //??????????????? & ??????????????? true ????????????????????? false ???????????????????????????
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                return shouldOverrideUrlLoadingByApp(view, request.toString());
            } else {
                return shouldOverrideUrlLoadingByApp(view, request.getUrl().toString());
            }
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "??????Url??????:2 ");
            view.loadUrl(url);
            return true;
        }
    }

    private class myWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("")
                    .setMessage(message)
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("")
                    .setMessage(message)
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    })
                    .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {

            final EditText et = new EditText(view.getContext());
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("")
                    .setMessage(message)
                    .setView(et)
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm(et.getText().toString());
                        }
                    })
                    .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
            return true;
        }

    }


    /**
     * ??????url???scheme?????????????????????app?????????
     */
    public boolean shouldOverrideUrlLoadingByApp(WebView view, String url) {
        if (url.startsWith("http") || url.startsWith("https") || url.startsWith("ftp")) {
            //?????????http, https, ftp?????????
            return false;
        }
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            intent.setComponent(null);
            if (isInstall(intent)) {
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * ??????app???????????????
     *
     * @param intent
     * @return
     */
    public boolean isInstall(Intent intent) {
        return getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    /**
     * ebView ????????????????????????
     */
    public class setOnLongClickListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            WebView.HitTestResult result = webView.getHitTestResult();
            if (result == null) {
                return false;
            }

            //????????????
            int TYPE = result.getType();
            switch (TYPE) {
                case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE: // ???????????????????????????
                case WebView.HitTestResult.IMAGE_TYPE: // ??????????????????????????????
                case WebView.HitTestResult.IMAGE_ANCHOR_TYPE: // ??????????????????????????????

                    //Saveurlpath(result.getExtra()); //??????????????????

                    //?????????????????????
                    new Thread(new renrqcode(result.getExtra())).start();

                    return true;
                case WebView.HitTestResult.EDIT_TEXT_TYPE: // ?????????????????????
                    break;
                case WebView.HitTestResult.PHONE_TYPE: // ????????????
                    break;
                case WebView.HitTestResult.EMAIL_TYPE: // ??????Email
                    break;
                case WebView.HitTestResult.GEO_TYPE:  //????????????
                    break;
                case WebView.HitTestResult.UNKNOWN_TYPE: //??????
                    break;
                case WebView.HitTestResult.SRC_ANCHOR_TYPE: // ?????????
                    Toast.makeText(context, "?????????", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    public class renrqcode implements Runnable {
        private final String url;

        public renrqcode(String str) {
            this.url = str;
        }

        @Override
        public void run() {
            try {
                //1??????????????? Bitmap
                Bitmap myBitmap = Glide.with(getBaseContext()).asBitmap().load(url).submit().get();

                //????????????????????????
                Result result = parsePic(myBitmap);
                if (result == null) {
                    //????????????
                    doaloggetqrcode(url, 0);
                } else {
                    //?????????
                    doaloggetqrcode(result.getText(), 1);
                }
            } catch (Exception e) {
                handler.sendEmptyMessage(config.fail);
                e.printStackTrace();

            }
        }
    }

    /**
     * ?????????????????????
     * ????????????????????????
     *
     * @return
     */
    public Result parsePic(Bitmap bitmap) {
        //??????????????????UTF-8
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        //????????????RGBLuminanceSource????????????bitmap?????????????????????
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] data = new int[width * height];
        bitmap.getPixels(data, 0, width, 0, 0, width, height);
        RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(width, height, data);
        //?????????????????????????????????
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
        //?????????????????????
        QRCodeReader reader = new QRCodeReader();
        //????????????
        Result result = null;
        try {
            result = reader.decode(binaryBitmap, hints);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * ?????????????????????
     *
     * @param path
     */
    public void doaloggetqrcode(String path, int TYPE) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                String[] Title = TYPE == 0 ? new String[]{getString(R.string.tv_msg218)} : new String[]{getString(R.string.tv_msg218), getString(R.string.tv_msg219)};

                if (TYPE == 0) {
                    builder.setTitle(getString(R.string.alertDialog_title));
                    builder.setMessage(getString(R.string.tv_msg218));
                    builder.setPositiveButton(getString(R.string.btn_ok), (dialogInterface, i) -> sendbutton(path));
                    builder.setNegativeButton(getString(R.string.btn_cancel), (dialogInterface, i) -> dialogInterface.dismiss());
                }

                builder.setItems(Title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                //?????????????????????????????????
                                new Thread(new run(path)).start();
                                break;
                            case 1:
                                startActivity(new Intent(getBaseContext(), DyWebActivity.class).putExtra(Constants.VIDEOURL, path));
                                break;

                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });

    }

    /**
     * ?????????????????????
     */
    public class run implements Runnable {
        String url;

        public run(String str) {
            this.url = str;
        }

        @Override
        public void run() {
            try {
                File file = Glide.with(activity).asFile().load(url).submit().get();
                displayToGallery(context, file);
            } catch (Exception e) {
                handler.sendEmptyMessage(config.fail);
                e.printStackTrace();

            }
        }
    }

    /**
     * ????????????????????????
     *
     * @param context
     * @param file
     */
    public void displayToGallery(Context context, File file) {
        if (file == null || !file.exists()) {
            handler.sendEmptyMessage(config.fail);
            return;
        }
        String photoPath = file.getAbsolutePath();
        String photoName = file.getName();

        // ??????????????????????????????
        try {
            ContentResolver contentResolver = context.getContentResolver();
            MediaStore.Images.Media.insertImage(contentResolver, photoPath, photoName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // ????????????????????????
        //sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + photoPath)));
        handler.sendEmptyMessage(config.sussess);

    }

    private void sendbutton(String path) {
        new Thread(new run(path)).start();
    }
}
