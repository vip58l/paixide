package com.paixide.activity;

import android.view.View;
import android.widget.ListView;

import com.paixide.BasActivity.BasActivity2;
import com.paixide.adapter.setAdapter;
import com.paixide.R;
import com.tencent.opensource.model.item;

import java.util.List;

import butterknife.BindView;

/**
 * 阅读列表
 */
public class MhReadActivity extends BasActivity2 {
    @BindView(R.id.listview)
    ListView listview;


    @Override
    protected int getview() {
        return R.layout.activity_mhread;
    }

    @Override
    public void iniview() {
        List<item> items = (List<item>) getIntent().getSerializableExtra("readmh");
        listview.setAdapter(adappter = new setAdapter(context, items));
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

}
