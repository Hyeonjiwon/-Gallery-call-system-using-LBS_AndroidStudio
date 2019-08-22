package com.example.project1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraActivity extends Activity {

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static String timeTmp;
    Context context = null;

    // directory name to store captured images
    private static final String IMAGE_DIRECTORY_NAME = "CameraCapture";

    private Uri fileUri; // file url to store image

    private ImageView imgPreview;
    private Button btnCapturePicture;

    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        context = getApplicationContext();
        imgPreview = (ImageView) findViewById(R.id.camera_preview);
        btnCapturePicture = (Button) findViewById(R.id.button_capture);

        /**
         * Capture image button click event
         *
        btnCapturePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // capture picture
                captureImage();
            }
        });
         */
        captureImage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            gps = new GPSTracker(CameraActivity.this);
            Log.d("wldnjs_camera", "CaemeraActivity : " + gps);

            if (resultCode == RESULT_OK)
            {
                previewCapturedImage();
                Log.d("wldnjs_camera", "CaemeraActivity : " + gps.canGetLocation());
                if(gps.canGetLocation())
                {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    // \n is for new line
                    Log.d("wldnjs_camera", "CaemeraActivity : " + latitude + " / " + longitude);
                    Log.d("wldnjs_camera", "CaemeraActivity : " + fileUri);

                    Toast.makeText(getApplicationContext(),"Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

                    // exif 파일에 위치 집어넣기
                    setExifInfo(fileUri, latitude, longitude);

                } else {
                    // Can't get location.
                    // GPS or network is not enabled.
                    // Ask user to enable GPS/network in settings.
                    Toast.makeText(getApplicationContext(), "CaemeraActivity : Can't get location", Toast.LENGTH_LONG).show();
                }

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Error!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //위치 정보를 저장해주는 메소드
    public void setExifInfo(Uri ImageUri, double lat, double lon) {
        if (ImageUri != null && lat != 0.0 & lon != 0.0) {
            String strlatitude = convertTagGPSFormat(lat);
            String strlongitude = convertTagGPSFormat(lon);

            try {
                ExifInterface exif = new ExifInterface(ImageUri.getPath());
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, strlatitude);
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, latitudeRef(lat));
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, strlongitude);
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, longitudeRef(lon));
                exif.saveAttributes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //방향
    /**
     * returns ref for latitude which is S or N.
     * @param latitude
     * @return S or N
     */
    public static String latitudeRef(double latitude) {
        return latitude<0.0d?"S":"N";
    }

    /**
     * returns ref for longitude which is W or E.
     * @param longitude
     * @return W or E
     */
    public static String longitudeRef(double longitude) {
        return longitude<0.0d?"W":"E";
    }

    //Exif 인코딩
    private String convertTagGPSFormat(double coordinate) {
        String strlatitude = Location.convert(coordinate, Location.FORMAT_SECONDS);
        String[] arrlatitude = strlatitude.split(":");
        StringBuilder sb = new StringBuilder();
        sb.append(arrlatitude[0]);
        sb.append("/1,");
        sb.append(arrlatitude[1]);
        sb.append("/1,");
        sb.append(arrlatitude[2]);
        sb.append("/1,");

        Log.d("wldnjs_camera","CaemeraActivity : "+ sb.toString());

        return sb.toString();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        //ExifInterface exif = new ExifInterface(fileUri);


        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    /*
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("wldnjs_camera", "CaemeraActivity :  " + IMAGE_DIRECTORY_NAME + " !!!");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        timeTmp = timeStamp;
        File mediaFileName;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFileName = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFileName;
    }

    private void previewCapturedImage() {
        try {
            imgPreview.setVisibility(View.VISIBLE);

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);

            imgPreview.setImageBitmap(bitmap);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}