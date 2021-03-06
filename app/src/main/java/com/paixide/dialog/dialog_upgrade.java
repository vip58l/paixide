package com.paixide.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.paixide.BasActivity.BaseDialog;
import com.paixide.R;
import com.paixide.activity.Aboutus.activity_viecode;
import com.paixide.listener.Paymnets;

import butterknife.BindView;
import butterknife.OnClick;

public class dialog_upgrade extends BaseDialog {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.contertitle)
    TextView contertitle;
    @BindView(R.id.send1)
    TextView send1;
    com.paixide.Module.api.version version;

    public static void updada_Msg(Context context) {
        dialog_upgrade dialog_upgrade = new dialog_upgrade(context, null);
        dialog_upgrade.setCanceledOnTouchOutside(false);
        dialog_upgrade.show();
    }

    public dialog_upgrade(@NonNull Context context, Paymnets paymnets) {
        super(context, paymnets);
        title.setText(R.string.tm128);
        datamodule.version(new Paymnets() {
            @Override
            public void onSuccess(Object object) {
                version = (com.paixide.Module.api.version) object;
                if (!TextUtils.isEmpty(version.getLoginMsg())) {
                    contertitle.setText(version.getLoginMsg());
                } else if (!TextUtils.isEmpty(version.getTitle())) {
                    contertitle.setText(version.getTitle());
                } else {
                    contertitle.setText(R.string.tm127);
                }
            }

            @Override
            public void isNetworkAvailable() {

            }

            @Override
            public void onFail() {

            }
        });
    }

    @Override
    public int getview() {
        return R.layout.dialogsealprompt;
    }

    @Override
    @OnClick({R.id.send1})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.send1:
                if (version != null) {
                    if (!TextUtils.isEmpty(version.getPath())) {
                        //?????????????????????APK
                        if (version.getPath().toLowerCase().endsWith(".apk")) {
                            //????????????????????????
                            activity_viecode.loadUploadshow(context, version.getPath());
                        } else {
                            //?????????????????????
                            activity_viecode.updatedown(context, version.getPath());
                        }
                    }
                }
                break;
        }
        dismiss();
    }

}
