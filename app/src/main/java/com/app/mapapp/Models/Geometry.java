package com.app.mapapp.Models;


import android.location.Location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 11/18/2017.
 */

public class Geometry {
    @SerializedName("location")
    private MarkerLocation location;

    public MarkerLocation getLocation() {
        return location;
    }

    public void setLocation(MarkerLocation location) {
        this.location = location;
    }
}
