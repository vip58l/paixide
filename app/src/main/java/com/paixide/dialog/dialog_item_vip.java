/**
 * Description :
 * 开发者 小清新 QQ804031885
 *
 * @author WSoban
 * @date 2021/2/25 0025
 */


package com.paixide.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.paixide.BasActivity.BaseDialog;
import com.paixide.R;
import com.paixide.listener.Paymnets;

import butterknife.BindView;
import butterknife.OnClick;

public class dialog_item_vip extends BaseDialog {
    @BindView(R.id.title)
    TextView title;

    public dialog_item_vip(Context context, int count, Paymnets paymnet) {
        super(context, paymnet);
        title.setText(String.format(title.getText().toString(), count));
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
    }

    @Override
    public int getview() {
        return R.layout.dialog_item_vip;
    }

    @OnClick({R.id.goosvip})
    public void OnClick(View v) {
        if (paymnets!=null){
            paymnets.onClick();
        }

        dismiss();
    }

    public static void dialogitemvip(Context context, int count, Paymnets paymnet) {
        dialog_item_vip dialogItemVip = new dialog_item_vip(context, count, paymnet);
        dialogItemVip.setCanceledOnTouchOutside(false);
        dialogItemVip.show();
    }


}
