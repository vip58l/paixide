package com.paixide.activity.picenter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.zxing.activity.CaptureActivity;
import com.paixide.BasActivity.BasActivity2;
import com.paixide.Fragment.page3.fragment.page3_1;
import com.paixide.IMtencent.chta.ChatActivity;
import com.paixide.R;
import com.paixide.Util.ActivityLocation;
import com.paixide.Util.Constants;
import com.paixide.Util.Glideload;
import com.paixide.Util.StatusBarUtil;
import com.paixide.Util.SystemUtil;
import com.paixide.Util.Toashow;
import com.paixide.Util.config;
import com.paixide.ViewPager.setViewPager;
import com.paixide.activity.Memberverify.activity_Namecenter;
import com.paixide.activity.Web.DyWebActivity;
import com.paixide.activity.activity_album;
import com.paixide.activity.edit.activity_updateedit;
import com.paixide.activity.picenter.fragment.per1;
import com.paixide.activity.picenter.fragment.per3;
import com.paixide.activity.picenter.fragment.per4;
import com.paixide.activity.picenter.widget.widgetactivity;
import com.paixide.dialog.dialog_Blocked;
import com.paixide.dialog.dialog_item_qrcode;
import com.paixide.dialog.dialog_item_rs;
import com.paixide.dialog.dialog_success;
import com.paixide.listener.Paymnets;
import com.paixide.widget.item_chid_play;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.opensource.model.UserInfo;
import com.tencent.opensource.model.personal;
import com.tencent.opensource.model.videolist;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ?????????????????????
 */
public class activity_personalhome extends BasActivity2 {
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.dfdfkldfkl)
    TextView dfdfkldfkl;
    @BindView(R.id.tv_id)
    TextView tv_id;
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.back)
    ImageView authsdk_return_bg;
    @BindView(R.id.circleimageview)
    ImageView circleimageview;
    @BindView(R.id.widet1)
    widgetactivity widet1;
    @BindView(R.id.widet2)
    widgetactivity widet2;
    @BindView(R.id.widet3)
    widgetactivity widet3;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    setViewPager FragmenViewPager;
    String getuserid;

    public static void starsetAction(Context context, String getuserid) {
        Intent starter = new Intent(context, activity_personalhome.class);
        starter.putExtra(Constants.USERID, getuserid);
        context.startActivity(starter);
    }

    @Override
    protected int getview() {
        StatusBarUtil.transparencyBar(activity);
        return R.layout.activity_personalhome;
    }

    @Override
    public void iniview() {
        getuserid = getIntent().getStringExtra(Constants.USERID);
        viewPager.setAdapter(FragmenViewPager = new setViewPager(getSupportFragmentManager(), getFragmentlist(), setViewPager.picenter));
        viewPager.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Tabtext(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setCustomView(null); //?????????????????????
            }

            //???????????????????????????
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //???????????????????????????
            }
        });
        Tabtext(tabLayout.getTabAt(0));
        message.setSelected(true);
        if (!TextUtils.isEmpty(getuserid)) {
            if (getuserid.equals(UserInfo.getInstance().getUserId())) {
                dfdfkldfkl.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void initData() {
        //??????????????????
        datamodule.picenterHashMap(getuserid, paymnets);
        //???????????????????????????logs
        datamodule.addlistviewuser(getuserid, null);

        boolean checkpermissions = ActivityLocation.checkpermissions(activity);//??????????????????
        if (mapLocation == null && checkpermissions) {
            lbsamap.getmyLocation(callback);
        }

        //??????????????????????????????????????????
        switch (userInfo.getState()) {
            case 0:
            case 1:
                //?????????????????????
                if (userInfo.getReale() == 1 && !userInfo.getUserId().equals(getuserid)) {
                    dialog_success.show(context, new Paymnets() {
                        @Override
                        public void onSuccess() {
                            activity_Namecenter.starsetAction(context);
                        }

                        @Override
                        public void onFail() {
                            finish();
                        }
                    });
                }
                break;
            case 3:
                //????????????
                dialog_Blocked.myshow(context);
                break;
        }
    }

    @Override
    @OnClick({R.id.back, R.id.RefreshL, R.id.dfdfkldfkl, R.id.chat0})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.dfdfkldfkl:
                //???????????????????????????
                datamodule.gofollowlist(getuserid, paymnets);
                break;
            case R.id.back:
                onBackPressed();
                break;
            case R.id.RefreshL:
                //????????????
                dialog_item_rs.dialogitemrs(context, paymnets).sethideshow(getuserid.equals(userInfo.getUserId()) ? View.VISIBLE : View.GONE);
                break;
            case R.id.chat0:
                startChatActivity();
                break;
        }

    }

    @Override
    public void OnEorr() {

    }


    /**
     * ????????????????????????
     *
     * @return
     */
    private List<Fragment> getFragmentlist() {
        if (fragments != null) {
            fragments.clear();
        }
        fragments = new ArrayList<>();
        fragments.add(per1.perview(getuserid));                 //??????
        fragments.add(page3_1.showfmessage(2, getuserid)); //??????
        fragments.add(per3.perview(getuserid));                 //??????
        fragments.add(per4.perview(getuserid));                 //??????
        return fragments;
    }

    /**
     * ??????????????????
     */
    private Paymnets paymnets = new Paymnets() {
        @Override
        public void onFail() {

        }

        @Override
        public void onSuccess(Object object) {
            member = (com.tencent.opensource.model.member) object;
            Glideload.loadImage(circleimageview, member.getPicture());
            Glideload.loadImage(image, member.getPicture(), 10, 25);
            widet1.setText(member.getMyconun());
            widet2.setText(member.getTacount());
            widet3.setText(member.getGivecount());
            name.setText(member.getTruename());
            tv_id.setText(String.format(getString(R.string.tm152) + "", member.getId()));
            if (member.getPersonal() != null) {
                personal personal = member.getPersonal();
                message.setText(personal.getPesigntext());

                if (TextUtils.isEmpty(personal.getPree())) {
                    //      telid.setText(String.format("ID:%s", member.getId()));
                } else {
                    // telid.setText(String.format(getString(R.string.tm148) + "", member.getId(), personal.getFeeling()));
                }

            }
            if (member.getFollowlist() != null) {
                dfdfkldfkl.setBackground(context.getDrawable(R.drawable.acitvity011));
                dfdfkldfkl.setText("?????????");
            }

        }

        @Override
        public void isNetworkAvailable() {
            ToastUtil.toastLongMessage(getString(R.string.eorrfali2));
        }

        @Override
        public void ToKen(String msg) {
            activity_personalhome.super.paymnets.ToKen(msg);
        }

        @Override
        public void onSuccess(String msg) {
            Toashow.show(msg);
        }

        @Override
        public void status(int position) {
            switch (position) {
                case 1:
                    //????????????
                    initData();
                    break;
                case 2:
                    //????????????
                    startActivity(new Intent(context, activity_updateedit.class));
                    break;
                case 3:
                    //????????????
                    startActivity(new Intent(context, activity_album.class).putExtra(Constants.USERID, getuserid));
                    break;
                case 4:
                    //????????????
                    reportUser();
                    break;
                case 6:
                    //??????
                    dialog_item_qrcode.qrcodes(context, member);
                    break;
                case 7:
                    //??????
                    if (checkinidate()) {
                        CaptureActivity();
                    }
                    break;
            }
        }

        @Override
        public void payens() {
            member.setTacount(member.getTacount() + 1);
            Toashow.show(getString(R.string.gzok));
            dfdfkldfkl.setText("?????????");
            dfdfkldfkl.setBackground(context.getDrawable(R.drawable.acitvity02));
            widet2.setText(member.getTacount()); //??????
        }

        @Override
        public void onError() {
            couneroo = member.getTacount() - 1;
            couneroo = couneroo <= 0 ? 0 : couneroo;
            member.setTacount(couneroo);
            widet2.setText(member.getTacount()); //??????
            Toashow.show(getString(R.string.gzon));
            dfdfkldfkl.setBackground(context.getDrawable(R.drawable.activity011));
            dfdfkldfkl.setText("??????");
        }

    };

    /**
     * ????????????
     */
    public void reportUser() {
        videolist videolist = new videolist();
        videolist.setUserid(getuserid);
        videolist.setId(getString(R.string.tv_msg237) + getuserid);
        item_chid_play.reportUser(context, videolist, 2);
    }

    /**
     * ???????????????????????????????????????
     */
    private boolean checkinidate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> list = new ArrayList<>();
            list.add(Manifest.permission.CAMERA);                            //????????????
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);            //SD?????????
            for (String permission : list) {
                int star = ContextCompat.checkSelfPermission(context, permission);
                if (star != PackageManager.PERMISSION_GRANTED) {
                    SystemUtil.getPermission(activity, list, Constants.REQUEST_CODE);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * ??????CaptureActivity?????????
     */
    private void CaptureActivity() {
        startActivityForResult(new Intent(context, CaptureActivity.class), Constants.REQUEST_CODE);
    }

    /**
     * ??????????????????
     *
     * @param result
     */
    private void setCapture(String result) {
        boolean b1 = result.toLowerCase().startsWith("http://");
        boolean b2 = result.toLowerCase().startsWith("https://");
        boolean b3 = result.toLowerCase().endsWith(".apk");
        if (b1 || b2) {
            //???????????????
            DyWebActivity.starAction(context, result);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (String permission : permissions) {
            int granted = ContextCompat.checkSelfPermission(context, permission);
            if (granted != PackageManager.PERMISSION_GRANTED) {
                Toashow.show(getString(R.string.ts_dialog_call));
                return;
            }
        }
        switch (requestCode) {
            case Constants.REQUEST_CODE:
                CaptureActivity();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == config.sussess) {
            initData();
        }

        if (Constants.REQUEST_CODE == requestCode && data != null) {
            String result = data.getStringExtra(CaptureActivity.SCAN_QRCODE_RESULT);
            setCapture(result);
        }
    }


    /**
     * ??????????????????????????????????????????
     */
    private void startChatActivity() {
        if (member == null) {
            ToastUtil.toastLongMessage(getString(R.string.eorrfali3));
            return;
        }
        if (!config.isNetworkAvailable()) {
            ToastUtil.toastLongMessage(getString(R.string.eorrfali2));
            return;
        }
        if (userInfo.getUserId().equals(String.valueOf(member.getId()))) {
            ToastUtil.toastShortMessage(getString(R.string.tv_msg132));
            return;
        }
        if (userInfo.getSex().equals(String.valueOf(member.getSex()))) {
            Toashow.show(getString(R.string.tmcaht));
            return;
        }

        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setType(V2TIMConversation.V2TIM_C2C);
        chatInfo.setId(getuserid);
        chatInfo.setChatName(member.getTruename());
        chatInfo.setIconUrlList(TextUtils.isEmpty(member.getPicture()) ? "" : member.getPicture());

        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.CHAT_INFO, chatInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}