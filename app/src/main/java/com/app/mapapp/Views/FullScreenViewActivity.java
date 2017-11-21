package com.app.mapapp.Views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.app.mapapp.Models.ImagesList;
import com.app.mapapp.Presenter.FullScreenImageAdapter;
import com.app.mapapp.R;

import java.util.ArrayList;

/**
 * Created by user on 11/19/2017.
 */

public class FullScreenViewActivity extends Activity {

    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;
    private ImagesList body;
    private int currentPosition = 0;
    private Bundle bundle;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreenview);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            currentPosition = bundle.getInt("index");
            body = bundle.getParcelable("images");
        }
        progressDialog = new ProgressDialog(this);
        Util.showProgress(progressDialog, 0);
        initView();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new FullScreenImageAdapter(FullScreenViewActivity.this, body.getImageItems());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPosition);
        Util.showProgress(progressDialog, 1);
    }
}