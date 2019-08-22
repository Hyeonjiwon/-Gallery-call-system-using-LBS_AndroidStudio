package com.example.project1;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
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

import java.io.IOException;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    //멀티미디어관
    private static final LatLng MULT = new LatLng(36.769015, 126.934817);
    //공대
    private static final LatLng ENGINE = new LatLng(36.769130, 126.931912);
    private Marker mMulti;
    private Marker mEngine;
    private GoogleMap mMap;

    private Context mContext;
    ExifInterface exif;
    private String exifAttribute;

    private ArrayList<String> thumbsDataList;
    private ArrayList<String> thumbsIDList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /* 지도가 준비되면 호출 */
    @Override
    public void onMapReady(GoogleMap Map) {
        mMap = Map;

        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.test);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);

        mMulti = mMap.addMarker(new MarkerOptions().position(MULT).title("멀티미디어관"));
        mMulti.setTag(0);

        mEngine = mMap.addMarker(new MarkerOptions().position(ENGINE).title("공과대학").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        mEngine.setTag(0);

        mMap.setOnMarkerClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(MULT));

        drawImage(mMap);
    }

    // 이미지 GPS 좌표로 지도에 띄우기
    public  void drawImage(GoogleMap mMap) {
        final ImageAdapter ia = new ImageAdapter(this);

        for (String imgPath : thumbsDataList) {
            Log.d("wldnjs_Maps!!", "\nMapsActivity : "+ imgPath);
            try {
                exif = new ExifInterface(imgPath);
                //exifAttribute = getExif(exif);

                GeoDegree geoDegree = new GeoDegree(exif);

                //위치 좌표가 있으면
                if (geoDegree.getLatitude() != null && geoDegree.getLongitude() != null) {
                    float latitude = geoDegree.getLatitude();
                    float longitude = geoDegree.getLongitude();

                    // Creating a LatLng object for the current location
                    LatLng latLng = new LatLng(latitude, longitude);

                    Bitmap bm = BitmapFactory.decodeFile(imgPath);
                    Bitmap marker = Bitmap.createScaledBitmap(bm, 100, 100, false);

                    Marker mm = mMap.addMarker(new MarkerOptions().position(latLng).title("!!!").icon(BitmapDescriptorFactory.fromBitmap(marker)));
                    mm.setTag(0);

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    Log.d("wldnjs_Maps!!", "\nMapsActivity : " + latitude + " / "+ longitude);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    /**==========================================
     *              Adapter class
     * ==========================================*/
    public class ImageAdapter extends BaseAdapter {
        private String imgData;
        private String geoData;

        ImageAdapter(Context c){
            mContext = c;
            thumbsDataList = new ArrayList<String>();
            thumbsIDList = new ArrayList<String>();
            getThumbInfo(thumbsIDList, thumbsDataList);
        }

        private String getExif(ExifInterface exif) {
            String myAttribute = "";
            myAttribute += getTagString(ExifInterface.TAG_DATETIME, exif);
            myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE, exif);
            myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE_REF, exif);
            myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif);
            myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE_REF, exif);

            return myAttribute;
        }

        private String getTagString(String tag, ExifInterface exif) {
            Log.d("wldnjs_Maps", "\nMapsActivity : " + tag +  " : " + exif.getAttribute(tag));
            return (tag + " : " + exif.getAttribute(tag) + "\n");
        }

        public int getCount() {
            return thumbsIDList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null){
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(95, 95));
                imageView.setAdjustViewBounds(false);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(2, 2, 2, 2);
            }else{
                imageView = (ImageView) convertView;
            }
            BitmapFactory.Options bo = new BitmapFactory.Options();
            bo.inSampleSize = 8;
            Bitmap bmp = BitmapFactory.decodeFile(thumbsDataList.get(position), bo);
            Bitmap resized = Bitmap.createScaledBitmap(bmp, 95, 95, true);
            imageView.setImageBitmap(resized);

            return imageView;
        }

        private void getThumbInfo(ArrayList<String> thumbsIDs, ArrayList<String> thumbsDatas){
            String[] proj = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE};

            Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj, MediaStore.Images.Media.DATA + " like ? ",
                    new String[] {"%CameraCapture%"},
                    null);

            if (imageCursor != null && imageCursor.moveToFirst()){
                String thumbsID;
                String thumbsImageID;
                String thumbsData;

                int thumbsIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
                int thumbsDataCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int thumbsImageIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                int thumbsSizeCol = imageCursor.getColumnIndex(MediaStore.Images.Media.SIZE);
                int num = 0;
                do {
                    thumbsID = imageCursor.getString(thumbsIDCol);
                    thumbsData = imageCursor.getString(thumbsDataCol);
                    thumbsImageID = imageCursor.getString(thumbsImageIDCol);
                    //imgSize = imageCursor.getString(thumbsSizeCol);
                    num++;
                    if (thumbsImageID != null){
                        thumbsIDs.add(thumbsID);
                        thumbsDatas.add(thumbsData);
                    }
                }while (imageCursor.moveToNext());
            }
            return;
        }
    }
}
