package com.app.mapapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by user on 11/19/2017.
 */

public class ImagesList implements Parcelable {

    @SerializedName("pugs")
    private ArrayList<String> imageItems;

    public ArrayList<String> getImageItems() {
        return imageItems;
    }

    public void setImageItems(ArrayList<String> imageItems) {
        this.imageItems = imageItems;
    }

    public static Creator<ImagesList> getCREATOR() {
        return CREATOR;
    }

    protected ImagesList(Parcel in) {
        imageItems = in.createStringArrayList();
    }

    public static final Creator<ImagesList> CREATOR = new Creator<ImagesList>() {
        @Override
        public ImagesList createFromParcel(Parcel in) {
            return new ImagesList(in);
        }

        @Override
        public ImagesList[] newArray(int size) {
            return new ImagesList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringList(imageItems);
    }
}
