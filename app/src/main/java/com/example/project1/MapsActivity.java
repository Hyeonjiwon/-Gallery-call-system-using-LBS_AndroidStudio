package com.example.project1;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    //멀티미디어관
    private static final LatLng MULT = new LatLng(36.769015, 126.934817);
    //공대
    private static final LatLng ENGINE = new LatLng(36.769130, 126.931912);
    private Marker mMulti;
    private Marker mEngine;
    private Marker mImg;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /* 지도가 준비되면 호출 */
    @Override
    public void onMapReady(GoogleMap Map) {
        mMap = Map;

        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.test);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);

        /*
        Intent intent = new Intent();
        Bundle bundle = intent.getExtras();

        Double latitude = 0.0;
        Double longitude = 0.0;

        if(bundle != null) {
            latitude = bundle.getDouble("latitude");
            longitude = bundle.getDouble("longitude");
            Log.d("지원_maps", "!!!!!!!!!!!!" + latitude + " " + longitude);
            LatLng my = new LatLng(latitude, longitude);
            mImg = mMap.addMarker(new MarkerOptions().position(my).title("my").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            mImg.setTag(0);
        }
        */

        mMulti = mMap.addMarker(new MarkerOptions().position(MULT).title("멀티미디어관"));
        mMulti.setTag(0);

        mEngine = mMap.addMarker(new MarkerOptions().position(ENGINE).title("공과대학").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        mEngine.setTag(0);

        mMap.setOnMarkerClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(MULT));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(mImg));
    }

    /* 사용자가 마커를 클릭하면 호출 */
    @Override
    public boolean onMarkerClick(Marker marker) {
        Integer clickCount = (Integer) marker.getTag();

        if(clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this, marker.getTitle() + " 가 클릭 되었음, 클리 횟수: " + clickCount, Toast.LENGTH_SHORT).show();
        }
        return  false;
    }
}
