package com.paixide.activity.party.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paixide.BasActivity.BasFragment;
import com.paixide.Fragment.page3.page3;
import com.paixide.R;
import com.paixide.Util.Constants;
import com.paixide.Util.Toashow;
import com.paixide.Util.config;
import com.paixide.activity.party.activity_party;
import com.paixide.activity.party.activity_user_party;
import com.paixide.adapter.Radapter;
import com.paixide.adapter.itemdecoration.MyDecoration;
import com.paixide.dialog.dialog_msg_svip;
import com.paixide.listener.Callback;
import com.paixide.listener.Paymnets;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.opensource.model.Gamefinish;
import com.tencent.opensource.model.Party;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * θδΌεε 
 */
public class party_page1 extends BasFragment {
    String TAG = party_page1.class.getSimpleName();
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;


    public static Fragment show(int type) {
        Bundle args = new Bundle();
        args.putInt(Constants.TYPE, type);
        party_page1 party_page = new party_page1();
        party_page.setArguments(args);
        return party_page;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Bundle arguments = getArguments();
        type = arguments.getInt(Constants.TYPE);
    }

    @Override
    public View getview(LayoutInflater inflater) {
        return inflater.inflate(R.layout.layout_item_page3, null);
    }

    @Override
    public void iniview() {
        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerview.setLayoutManager(manager);
        recyclerview.setAdapter(radapter = new Radapter(context, list2, Radapter.party_item, callback));
        recyclerview.addItemDecoration(new MyDecoration(context, 0));
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                smartRefreshLayout.finishRefresh(100/*,false*/);//δΌ ε₯falseθ‘¨η€Ίε·ζ°ε€±θ΄₯
                loadMoreData();
            }
        });
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                smartRefreshLayout.finishLoadMore(1000/*,false*/);//δΌ ε₯falseθ‘¨η€Ίε θ½½ε€±θ΄₯
                toinitData();
            }
        });
    }

    @Override
    public void initData() {
        toinitData();
    }

    public void loadMoreData() {
        if (config.isNetworkAvailable()) {
            totalPage = 0;
            list2.clear();
            radapter.notifyDataSetChanged();
            toinitData();
        } else {
            Toashow.show(getString(R.string.eorrfali2));
        }
    }

    public void toinitData() {
        totalPage++;
        datamodule.partylist(totalPage, 1, new Paymnets() {
            @Override
            public void isNetworkAvailable() {
                OnEorr();
            }

            @Override
            public void onFail(String msg) {
                if (totalPage > 1) {
                    totalPage--;
                    Toashow.show(msg);
                }
                OnEorr();
            }

            @Override
            public void onSuccess(Object object) {
                List<Party> parties = (List<Party>) object;
                list2.addAll(parties);
                radapter.notifyDataSetChanged();
                OnEorr();
            }

            @Override
            public void onFail() {
                OnEorr();
            }
        });
    }

    @Override
    @OnClick({R.id.eorr})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.eorr:
                loadMoreData();
                break;
        }

    }

    @Override
    public void OnEorr() {
        if (smartRefreshLayout != null) {
            smartRefreshLayout.setVisibility(list2.size() > 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onRefresh() {

    }

    private Paymnets paymnets = new Paymnets() {
        @Override
        public void isNetworkAvailable() {
            try {
                ToastUtil.toastLongMessage(getString(R.string.eorrfali2));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFail() {
            try {
                ToastUtil.toastLongMessage(getString(R.string.eorrfali3));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSuccess(Object object) {
            List<Gamefinish> gamelists = (List<Gamefinish>) object;
            list2.addAll(gamelists);
            radapter.notifyDataSetChanged();
            OnEorr();
        }


        @Override
        public void onSuccess(String msg) {

            if (totalPage > 1) {
                totalPage--;
                Toashow.show(msg);
            }
            OnEorr();
        }


    };

    private Callback callback = new Callback() {
        @Override
        public void LongClickListener(int position) {
            Party party = (Party) list2.get(position);
            if (String.valueOf(party.getUserid()).equals(userInfo.getUserId())) {
                dialog_msg_svip.dialogmsgsvip(context, getString(R.string.t12), getString(R.string.t13), getString(R.string.t14), new Paymnets() {
                    @Override
                    public void onRefresh() {
                        //ζ―ζ¬δΊΊεεΈηζθ½ε ι€
                        datamodule.delpartyl(String.valueOf(party.getId()), new Paymnets() {
                            @Override
                            public void onSuccess() {
                                Toashow.show(getString(R.string.Toaaaaa));
                                list2.remove(party);
                                radapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFail() {
                                Toashow.show(getString(R.string.eorrfali2));
                            }

                            @Override
                            public void onFail(String msg) {
                                Toashow.show(msg);
                            }

                            @Override
                            public void isNetworkAvailable() {
                                Toashow.show(getString(R.string.eorrfali2));
                            }
                        });

                    }

                    @Override
                    public void onSuccess() {


                    }

                });
            }
        }

        @Override
        public void OnClickListener(int position) {
            Party party = (Party) list2.get(position);
            activity_user_party.setAction(context, String.valueOf(party.getId()), 1);
        }
    };

}
