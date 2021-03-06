package com.paixide.activity.Aboutus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.paixide.BasActivity.BasActivity2;
import com.paixide.R;
import com.paixide.Util.KeyboardUtil;
import com.paixide.Util.Toashow;
import com.paixide.listener.Paymnets;
import com.paixide.widget.Backtitle;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * https://www.cnblogs.com/lxjhoney/p/6535283.html
 */
public class activity_joinin extends BasActivity2 {
    @BindView(R.id.backtitle)
    Backtitle backtitle;
    @BindView(R.id.gettitle)
    TextView gettitle;
    @BindView(R.id.imagego)
    ImageView imagego;
    @BindView(R.id.pulldele)
    ImageView pulldele;
    @BindView(R.id.pulldele1)
    ImageView pulldele1;
    @BindView(R.id.editquery)
    EditText editquery;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.tv_search_tag2)
    TextView tv_search_tag2;
    String[] item;
    int index;
    int textlength = 200;
    boolean[] bools = {false, false, false, false, false};

    public static void starAction(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, activity_joinin.class);
        context.startActivity(intent);
    }

    @Override
    protected int getview() {
        return R.layout.activity_joinin;
    }

    @Override
    public void iniview() {
        backtitle.setconter(getString(R.string.activity_a1));
        item = getResources().getStringArray(R.array.tabs9);
        editquery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > textlength) { //??????EditText??????????????????????????????????????????6
                    editquery.setText(s.toString().substring(0, textlength)); //??????EditText???????????????6?????????
                    editquery.setSelection(textlength);//?????????????????????
                    Toashow.show(getString(R.string.dialog_a6));
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                tv_search_tag2.setText(String.format("%s/%s", s.length(), textlength));
                pulldele.setVisibility(s.length() == 0 ? View.GONE : View.VISIBLE);
            }

        });
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pulldele1.setVisibility(s.length() == 0 ? View.GONE : View.VISIBLE);
            }

        });
    }

    @Override
    public void initData() {

    }

    @Override
    @OnClick({R.id.gettitle, R.id.send, R.id.pulldele, R.id.pulldele1})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.gettitle:
                dialog_2(v);
                break;
            case R.id.send:
                String msg = gettext();
                if (TYPE == 0) {
                    Toashow.show(getString(R.string.dialog_a5));
                    return;
                }
                if (TextUtils.isEmpty(msg)) {
                    Toashow.show(getString(R.string.dialog_a4));
                    return;
                }
                KeyboardUtil.hideSoftKeyboard(activity);
                String myphone = phone.getText().toString();
                datamodule.cooperationmode(TYPE, msg, myphone, new Paymnets() {
                    @Override
                    public void onSuccess(String msg) {
                        Toashow.show(msg);
                        editquery.setText(null);
                    }

                    @Override
                    public void onFail(String msg) {
                        Toashow.show(msg);
                    }
                });
                break;
            case R.id.pulldele:
                editquery.setText(null);
                pulldele.setVisibility(View.GONE);
                break;
            case R.id.pulldele1:
                phone.setText(null);
                pulldele1.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void OnEorr() {

    }

    public void dialog_1(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("?????????");
        builder.setMessage("??????????????????????????????");
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }

        });
        builder.setNegativeButton("??????", null);
        builder.setNeutralButton("??????", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    public void dialog_2(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.didalog_a2);
        builder.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == item.length - 1) {
                    TYPE = 0;
                    gettitle.setText(R.string.didalog_a2);
                    imagego.setVisibility(View.VISIBLE);
                } else {
                    gettitle.setText(item[which]);
                    imagego.setVisibility(View.GONE);
                    TYPE = (which + 1);
                }


            }
        });
        // ?????????????????????
        //builder.setNegativeButton("??????",null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void dialog_3(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("?????????");
        builder.setSingleChoiceItems(item, index, new DialogInterface.OnClickListener() {
            @Override


            public void onClick(DialogInterface dialog, int which) {
                index = which;

            }

        });
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override


            public void onClick(DialogInterface dialog, int which) {

            }

        });
        builder.setNegativeButton("??????", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void dialog_4(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("?????????");

        builder.setMultiChoiceItems(item, bools, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                bools[which] = isChecked;

            }

        });
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override


            public void onClick(DialogInterface dialog, int which) {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < item.length; i++) {
                    if (bools[i]) {
                        sb.append(item[i] + " ");

                    }

                }


            }

        });
        builder.setNegativeButton("??????", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void dialog_5(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("??????wifi");
        final EditText et = new EditText(this);
        et.setHint("???????????????");
        et.setSingleLine(true);
        builder.setView(et);
        builder.setNegativeButton("??????", null);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = et.getText().toString();
                if (password.equals("123456")) {
                    Toast.makeText(context, "????????????", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "????????????", Toast.LENGTH_SHORT).show();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void dialog_6(View v) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.didalog_a2);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == item.length - 1) {
                    gettitle.setText(R.string.didalog_a2);
                    imagego.setVisibility(View.VISIBLE);
                } else {
                    gettitle.setText(item[which]);
                    imagego.setVisibility(View.GONE);
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public String gettext() {
        return editquery.getText().toString();
    }
}