package com.app.mapapp.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.app.mapapp.Models.PlacesResponse;
import com.app.mapapp.Models.Result;
import com.app.mapapp.R;
import com.app.mapapp.Services.ApiClient;
import com.app.mapapp.Services.ApiInterface;
import com.app.mapapp.Views.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 11/17/2017.
 */

public class FragMap extends Fragment {

    private static final int REQUEST_LOCATION = 1;
    private String placeUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";

    private MapView mMapView;
    private GoogleMap googleMap, parentMap;
    private Location location_address = null;
    private LocationManager location_manager;
    private double main_latitude = 0.0, main_longitude = 0.0;

    private Button btn_search, btn_restaurant, btn_hospitals;
    private EditText edt_location;

    private MarkerOptions markerOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_map, container, false);

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        location_manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Util.alertMessageNoGps(getActivity());
        } else if (location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            location_address = Util.getLocation(getActivity(), location_manager, REQUEST_LOCATION);
        }

        initView(rootView);
        initializeMap(savedInstanceState);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        edt_location.setText("");
    }

    private void initView(View rootView) {
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        edt_location = (EditText) rootView.findViewById(R.id.edt_location);

        btn_search = (Button) rootView.findViewById(R.id.btn_search);
        btn_hospitals = (Button) rootView.findViewById(R.id.btn_hospitals);
        btn_restaurant = (Button) rootView.findViewById(R.id.btn_restaurant);

        btn_hospitals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (main_latitude < 1) {
                    location_address = Util.getLocation(getActivity(), location_manager, REQUEST_LOCATION);
                } else {
                    markerShows("hospital", 0);
                }
            }
        });

        btn_restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (main_latitude < 1) {
                    location_address = Util.getLocation(getActivity(), location_manager, REQUEST_LOCATION);
                } else {
                    markerShows("restaurant", 1);
                }
            }
        });


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = edt_location.getText().toString().trim();
                if (!TextUtils.isEmpty(address)) {
                    getLatLongAddress(address);
                } else {
                    edt_location.setError("Please enter location");
                }
            }
        });
    }

    private void markerShows(String markerField, final int changeTag) {
        //googleMap.clear();
        String url = getUrl(main_latitude, main_longitude, markerField);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = googleMap;
        DataTransfer[1] = url;

        ApiInterface apiService = ApiClient.getClientMap(ApiClient.BASE_URL).create(ApiInterface.class);
        Call<PlacesResponse> call = apiService.getMap(main_latitude + "," + main_longitude, 5000, markerField, true, getActivity().getResources().getString(R.string.google_api_key_place));
        call.enqueue(new Callback<PlacesResponse>() {
            @Override
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                ShowPlaces(response.body().getResults(), changeTag);
            }

            @Override
            public void onFailure(Call<PlacesResponse> call, Throwable t) {
                Util.showLog("CA" + t.toString());
            }
        });

    }

    private void ShowPlaces(List<Result> results, int changeTag) {

        try {

            if (googleMap != null) {
                googleMap.clear();
                googleMap = parentMap;
            }

            // googleMap.clear();
            // This loop will go through all the results and add marker on each location.
            for (int i = 0; i < results.size(); i++) {
                Double lat = results.get(i).getGeometry().getLocation().getLat();
                Double lng = results.get(i).getGeometry().getLocation().getLng();
                String placeName = results.get(i).getName();

                Util.showLog("in" + lat + " , " + lng + " .. " + placeName);

                showMapLocationZoom(main_latitude, main_longitude, placeName, BitmapDescriptorFactory.HUE_RED);
                googleMap.addCircle(new CircleOptions()
                        .center(new LatLng(main_latitude, main_longitude))
                        .radius(5000)
                        .strokeColor(Color.RED));

                if (changeTag == 0) {
                    showMapLocationZoom(lat, lng, placeName, BitmapDescriptorFactory.HUE_ORANGE);
                } else {
                    showMapLocationZoom(lat, lng, placeName, BitmapDescriptorFactory.HUE_BLUE);
                }
            }
        } catch (Exception e) {
            Log.d("onResponse", "There is an error");
            e.printStackTrace();
        }
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googlePlacesUrl = new StringBuilder(placeUrl);
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 5000);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + getActivity().getResources().getString(R.string.google_api_key_place));
        Util.showLog(googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    private void getLatLongAddress(String address) {
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(address, 1);
            if (addresses.size() > 0) {
                main_latitude = addresses.get(0).getLatitude();
                main_longitude = addresses.get(0).getLongitude();
                if (main_latitude < 1 && main_longitude < 1) {
                    Util.showToast(getActivity(), getActivity().getResources().getString(R.string.location_notfound));
                } else {
                    if (googleMap != null) {
                        googleMap.clear();
                        googleMap = parentMap;
                    }
                    showMapLocationZoom(main_latitude, main_longitude, address, BitmapDescriptorFactory.HUE_RED);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeMap(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                parentMap = mMap;
                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //  ActivityCompat#requestPermissions here to request the missing permissions, and then overriding
                    //  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
                    //  to handle the case where the user grants the permission.
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                if (location_address != null) {
                    main_latitude = location_address.getLatitude();
                    main_longitude = location_address.getLongitude();
                    showMapLocationZoom(main_latitude, main_longitude, "My Location", BitmapDescriptorFactory.HUE_RED);
                } else {
                    Util.showToast(getActivity(), getActivity().getResources().getString(R.string.location_notfound));
                }
            }
        });
    }

    private void showMapLocationZoom(double latitude, double longitude, String title, float val) {
        markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng(latitude, longitude);
        // Position of Marker on Map
        markerOptions.position(latLng);
        // Adding Title to the Marker
        markerOptions.title(title);
        // Adding Marker to the Camera.
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(val));
        googleMap.addMarker(markerOptions);
        // move map camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Map");
    }
}
