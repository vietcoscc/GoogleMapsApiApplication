package com.example.viet.googlemapsapiapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private static final String TAG = "MapsActivity";
    @BindView(R.id.edtOrigin)
    EditText edtOrigin;
    @BindView(R.id.edtDestination)
    EditText edtDestination;
    @BindView(R.id.btnSearch)
    Button btnSearch;

    private GoogleMap mMap;
    private Marker mMarkerClick;
    private Polyline mPolyline;
    private Marker mSrcMarker;
    private Marker mDesMarker;

    private Handler mDrawingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<LatLng> arrLatLng = (ArrayList<LatLng>) msg.obj;
            if (mPolyline != null) {
                mPolyline.remove();
            }
            if (mSrcMarker != null) {
                mSrcMarker.remove();
            }
            if (mDesMarker != null) {
                mDesMarker.remove();
            }
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.RED);
            polylineOptions.width(10f);
            polylineOptions.addAll(arrLatLng);
            mPolyline = mMap.addPolyline(polylineOptions);
            LatLng srcLatLng = arrLatLng.get(0);
            LatLng desLatLng = arrLatLng.get(arrLatLng.size() - 1);
            mSrcMarker = drawMarker(srcLatLng, BitmapDescriptorFactory.HUE_ORANGE, edtOrigin.getText().toString(), "");
            mDesMarker = drawMarker(desLatLng, BitmapDescriptorFactory.HUE_ORANGE, edtDestination.getText().toString(), "");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btnSearch.setOnClickListener(this);
    }

    private void initUISettings() {
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
    }

    private void requestPermission() {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initUISettings();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permistions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            requestPermissions(permistions, 1);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        LatLng haNoi = new LatLng(21.0227387, 105.8194541);
        mMap.addMarker(new MarkerOptions().position(haNoi).title("Marker in Ha noi"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(haNoi));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mMarkerClick != null) {
                    mMarkerClick.remove();
                }
                mMarkerClick = drawMarker(latLng, BitmapDescriptorFactory.HUE_BLUE, "Marker click !", getLocationName(latLng));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });
    }

    private Marker drawMarker(LatLng latLng, float hue, String title, String snippet) {
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.title(title);
        options.snippet(snippet);
        options.icon(BitmapDescriptorFactory.defaultMarker(hue));
        return mMap.addMarker(options);
    }

    private String getLocationName(LatLng latLng) {
        String name = null;
        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            List<Address> address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (address.size() > 0) {
                name = address.get(0).getAddressLine(0);
                name += "-" + address.get(0).getAddressLine(1);
                name += "-" + address.get(0).getAddressLine(2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }

    @Override
    public void onClick(View view) {
        String src = edtOrigin.getText().toString();
        String des = edtDestination.getText().toString();
        if (TextUtils.isEmpty(src) || TextUtils.isEmpty(des)) {
            Toast.makeText(this, "Search fields must not null ", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG, src);
        Log.i(TAG, des);
        LatLng srcP = getLocationFromName(src);
        LatLng desP = getLocationFromName(des);
        if (srcP == null || desP == null) {
            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG, srcP.latitude + "-" + srcP.longitude);
        Log.i(TAG, desP.latitude + "-" + desP.longitude);
        DirectionRouteAsyncTask routeAsyncTask = new DirectionRouteAsyncTask(srcP, desP, mDrawingHandler);
        routeAsyncTask.execute();
    }

    private LatLng getLocationFromName(String name) {
        LatLng latlng = null;
        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            List<Address> addresses = geocoder.getFromLocationName(name, 1);
            if (addresses.size() > 0) {
                latlng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return latlng;
    }
}
