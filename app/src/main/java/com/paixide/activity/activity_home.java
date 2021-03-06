package com.paixide.activity;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;

import com.paixide.activity.Memberverify.activity_livebroadcast;
import com.paixide.activity.Web.DyWebActivity;
import com.paixide.activity.ZYservices.activity_photo_album;
import com.paixide.activity.matching.activity_thesamecity_speed;
import com.paixide.activity.meun.MEUN_MainActivity;
import com.paixide.adapter.Radapter;
import com.paixide.IMtencent.scenes.LiveRoomAnchorActivity;
import com.paixide.utils.AES.AES;
import com.tencent.qcloud.tim.tuikit.live.BuildConfig;
import com.paixide.BasActivity.BasActivity2;
import com.paixide.R;
import com.paixide.Test.live_animation;
import com.paixide.activity.Withdrawal.Withdrawals;
import com.paixide.activity.ZYservices.acitivity_NewsServices;
import com.paixide.activity.ZYservices.activity_categories;
import com.paixide.activity.ZYservices.activity_courses;
import com.paixide.activity.ZYservices.activity_editing_more;
import com.paixide.activity.ZYservices.activity_servicetitle;
import com.paixide.activity.DouYing.activity_jsonvideo;
import com.paixide.activity.edit.activity_nickname2;
import com.paixide.activity.edit.activity_uploadavatar;
import com.paixide.activity.matching.activity_audio_speed;
import com.paixide.Util.Glideload;
import com.paixide.Util.Toashow;
import com.paixide.Util.config;
import com.paixide.listener.Paymnets;
import com.paixide.dialog.dialog_item_show_home;
import com.paixide.dialog.dialog_realname;
import com.paixide.getHandler.JsonUitl;
import com.paixide.getHandler.PostModule;
import com.paixide.utils.Constants;
import com.tencent.opensource.model.Mesresult;
import com.tencent.opensource.model.gethelp;
import com.tencent.opensource.model.navigation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * ??????????????????
 */
public class activity_home extends BasActivity2 {

    @BindView(R.id.bgsimg)
    ImageView bgsimg;
    @BindView(R.id.icon)
    ImageView icon;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.home)
    TextView home;
    @BindView(R.id.a1)
    TextView a1;
    @BindView(R.id.a2)
    TextView a2;
    @BindView(R.id.a3)
    TextView a3;
    @BindView(R.id.a4)
    TextView a4;
    @BindView(R.id.a5)
    TextView a5;
    @BindView(R.id.tv_show)
    TextView tv_show;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    String TAG = "activity_home";
    @Override
    protected int getview() {
        return R.layout.activity_home;
    }

    @Override
    public void iniview() {
        radapter = new Radapter(this, list, Radapter.activity_home);
        radapter.setPaymnets(new Paymnets() {
            @Override
            public void status(int result) {
                startActivitygo(result);
            }
        });
        recyclerview.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerview.setAdapter(radapter);
        recyclerview.setFocusable(false);
        recyclerview.setHasFixedSize(true);
        recyclerview.setNestedScrollingEnabled(false);


        //?????????Activity
        clslist.add(acitivity_NewsServices.class);//????????????
        clslist.add(activity_courses.class);      //????????????
        clslist.add(activity_servicetitle.class); //????????????
        clslist.add(activity_categories.class);   //????????????
        clslist.add(Withdrawals.class);           //????????????
        clslist.add(activity_photo_album.class);  //????????????
        clslist.add(activity_sevaluate.class);    //????????????
        clslist.add(activity_mylikeyou.class);    //???????????????
        clslist.add(activity_nickname2.class);    //????????????
        clslist.add(activity_jsonvideo.class);    //????????????
        clslist.add(activity_audio_speed.class);  //??????1v1??????
        clslist.add(activity_thesamecity_speed.class);     //????????????
        clslist.add(MEUN_MainActivity.class);      //??????????????????
        clslist.add(live_animation.class);        //????????????
        setTextUtilsvide();


    }

    @Override
    public void initData() {
        PostModule.getModule(BuildConfig.HTTP_API + "/homegethelp?userid=" + userInfo.getUserId()+"&token="+userInfo.getToken(), new Paymnets() {
            @Override
            public void success(String date) {
                try {
                    gethelp help = new Gson().fromJson(date, gethelp.class);
                    if (help.getId() > 0) {
                        a1.setText(TextUtils.isEmpty(String.valueOf(help.getDeqy())) ? "0" : String.valueOf(help.getDeqy()));
                        a2.setText(String.valueOf(help.getCertificates()));
                        a3.setText(String.valueOf(help.getPeople()));
                        a4.setText(String.valueOf(help.getDuration()));
                        a5.setText(String.format("%s?????????", help.getEvaluate()));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fall(int code) {

            }
        });
        PostModule.getModule(BuildConfig.HTTP_API + "/navigation?type=1", new Paymnets() {
            @Override
            public void success(String date) {
                try {
                    List<navigation> navigations = JsonUitl.stringToList(date, navigation.class);
                    list.addAll(navigations);
                    radapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fall(int code) {

            }
        });
    }

    @OnClick({R.id.tv_show, R.id.icon, R.id.layout, R.id.layout2, R.id.user_edit, R.id.natitle})
    public void OnClick(View v) {
        if (!config.isNetworkAvailable()) {
            Toashow.show(getString(R.string.eorrfali2));
            return;
        }
        switch (v.getId()) {
            case R.id.tv_show:
                Intent intent = new Intent(this, activity_home_page.class);
                intent.putExtra(Constants.USERID, userInfo.getUserId());
                startActivity(intent);
                break;
            case R.id.icon:
                startActivityForResult(new Intent(this, activity_uploadavatar.class), config.sussess);
                break;
            case R.id.layout:
                startActivityForResult(new Intent(this, activity_sevaluate.class), config.sussess);

//                Intent intent1 = new Intent(this, activity_servicepj.class);
//                intent1.putExtra(Constants.USERID, userInfo.getUserId());
//                intent1.putExtra(Constants.Orderid,config.getRandomFileName());
//                startActivity(intent1);
                break;
            case R.id.layout2:
                initTimePicker(listage(), certificates);
                break;
            case R.id.user_edit:
                startActivityForResult(new Intent(this, activity_editing_more.class), config.sussess);
                break;
            case R.id.natitle:
                dialog_item_show_home.showhome(this);
                break;
        }

    }

    /**
     * ???????????????
     *
     * @param position
     */
    private void startActivitygo(int position) {
        navigation navigation = (navigation) list.get(position);
        int activity = navigation.getActivity();
        if (activity == 0) {
            if (TextUtils.isEmpty(navigation.getPath())) {
                return;
            }
            Intent intent = new Intent(this, DyWebActivity.class);
            intent.putExtra(Constants.VIDEOURL, navigation.getPath());
            startActivity(intent);
        } else {
            if (position > clslist.size() - 1) {
                return;
            }
            Class<?> cls = (Class<?>) clslist.get(position);
            Intent intent = new Intent(this, cls);
            intent.putExtra(Constants.USERID, userInfo.getUserId());
            intent.putExtra(Constants.POSITION, position);
            if (position == 5) {
                //????????????
                LiveRoomAnchorActivity.start(this, "");
            } else {
                startActivity(intent);
            }
        }


    }

    @Override
    public void OnEorr() {

    }

    /**
     * ???????????????
     */
    private void initTimePicker(List<String> mOptionsItems1, int result) {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String age = mOptionsItems1.get(options1);
                FormBody(age);
            }
        }).setSubmitColor(getResources().getColor(R.color.c_fu))
                .setCancelColor(getResources().getColor(R.color.home))
                .setContentTextSize(22)
                .setSelectOptions(result)
                .setTextColorCenter(getResources().getColor(R.color.c_fu))
                .setTitleText("????????????")
                .build();
        pvOptions.setPicker(mOptionsItems1);
        pvOptions.show();
    }

    private List<String> listage() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            list.add(String.valueOf(i));
        }
        return list;
    }

    public void FormBody(String age) {
        String token = AES.getStringkey(userInfo.getUserId());
        RequestBody requestBody = new FormBody.Builder()
                .add("userid", userInfo.getUserId())
                .add("age", age)
                .add("token", userInfo.getToken())
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == config.sussess) {
            setTextUtilsvide();

            list.clear();
            radapter.notifyDataSetChanged();
            initData();
        }
    }

    public void setTextUtilsvide() {
        if (!TextUtils.isEmpty(userInfo.getAvatar())) {
            Glideload.loadImage(icon, userInfo.getAvatar());
        } else {
            Glideload.loadImage(icon, userInfo.getSex().equals("1") ? R.mipmap.ic_man_choose : R.mipmap.icon_woman_choose);
        }
        name.setText(!TextUtils.isEmpty(userInfo.getGivenname()) ? userInfo.getGivenname() : userInfo.getName());
        home.setText(String.format(getString(R.string.tv_msg100), getString(R.string.app_name), userInfo.getUserId()));
        tv_show.setText(String.format(getString(R.string.tv_msg99), getString(R.string.app_name)));
        switch (userInfo.getState()) {
            case 0:
            case 1:
                bgsimg.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog_realname.Listener(activity_home.this, new Paymnets() {
                            @Override
                            public void onError() {
                                Log.d(TAG, "onError: ");
                                finish();
                            }

                            @Override
                            public void onSuccess() {
                                Intent intent = new Intent(activity_home.this, activity_livebroadcast.class);
                                intent.putExtra(com.paixide.Util.Constants.TYPE, 1);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onRefresh() {
                                Log.d(TAG, "onRefresh: ");
                                finish();
                            }
                        });
                    }
                }, 200);
                break;
            case 2:
                break;
            default:
                Toashow.show(getString(R.string.tv_msg21));
                finish();
                break;
        }

    }
}