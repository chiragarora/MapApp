package com.app.mapapp.Fragments;

import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.mapapp.Models.ImagesList;
import com.app.mapapp.Presenter.CustomGridAdapter;
import com.app.mapapp.R;
import com.app.mapapp.Services.ApiClient;
import com.app.mapapp.Services.ApiInterface;
import com.app.mapapp.Services.ImageClick;
import com.app.mapapp.Views.FullScreenViewActivity;
import com.app.mapapp.Views.RecyclerTouchListener;
import com.app.mapapp.Views.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 11/17/2017.
 */

public class FragGallery extends Fragment implements ImageClick {

    private RecyclerView recyclerView;
    private Response<ImagesList> responseList;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_gallery, container, false);

        progressDialog = new ProgressDialog(getActivity());
        Util.showProgress(progressDialog,0);
        initView(rootView);
        getImagesList();
        return rootView;
    }

    private void initView(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
    }

    private void getImagesList() {
        ApiInterface apiService = ApiClient.getClientMap(ApiClient.BASE_URL_IMAGES).create(ApiInterface.class);
        Call<ImagesList> call = apiService.getImages();
        call.enqueue(new Callback<ImagesList>() {
            @Override
            public void onResponse(Call<ImagesList> call, Response<ImagesList> response) {
                responseList = response;
                CustomGridAdapter customGridAdapter = new CustomGridAdapter(responseList.body().getImageItems(), getActivity());
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 1);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(customGridAdapter);
                recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, FragGallery.this));
            }

            @Override
            public void onFailure(Call<ImagesList> call, Throwable t) {
                Util.showLog("CA" + t.toString());
            }
        });

        Util.showProgress(progressDialog,1);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Gallery");
    }

    @Override
    public void onClick(View view, int position) {
        Intent i = new Intent(getActivity(), FullScreenViewActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("images", responseList.body());
        b.putInt("index", position);
        i.putExtras(b);
        getActivity().startActivity(i);
    }

    @Override
    public void onLongClick(View view, int position) {
        copyToClipBoard(responseList.body().getImageItems().get(position));
        Toast.makeText(getActivity(), "URL Copied", Toast.LENGTH_LONG).show();
    }

    private void copyToClipBoard(String text) {
        ClipboardManager clipMan = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Label", text);
            clipboard.setPrimaryClip(clip);
        }
    }
}

