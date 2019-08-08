package com.example.project1;

import android.app.Dialog;
import android.app.DirectAction;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class Details extends AppCompatActivity {
    private static final int ID_JPGDIALOG = 0;
    private String exifAttribute;
    private Dialog dlg;
    private TextView geoPoint;
    ExifInterface exif;
    private DirectAction data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.dialog_select_image_view);

        Intent intent = getIntent();


        if (intent != null) {
            String path = intent.getStringExtra("filePath");

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            //Bitmap bm = BitmapFactory.decodeFile(path, options);
            //detailsBtn.setImageBitmap(bm);

            try {
                exif = new ExifInterface(path);
                exifAttribute = getExif(exif);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        createdDialog(ID_JPGDIALOG).show(); // Instead of showDialog(0);
        GeoDegree geoDegree = new GeoDegree(exif);
        geoPoint = (TextView) dlg.findViewById(R.id.dlgGeoPoint);
        geoPoint.setText(geoDegree.toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "onPause()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(this, "onStop()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "onDestroy()", Toast.LENGTH_SHORT).show();
    }


    protected Dialog createdDialog(int id) {
        dlg = null;
        TextView content;

        switch (id) {
            case ID_JPGDIALOG:
                Context mContext = this;
                dlg = new Dialog(mContext);

                dlg.setContentView(R.layout.dialog_select_image_view);
                content = (TextView) dlg.findViewById(R.id.dlgImageName);
                content.setText(exifAttribute);

                Button okDialogButton = (Button) dlg.findViewById(R.id.btnOk);
                okDialogButton.setOnClickListener(okDialogButtonOnClickListener);

                break;
            default:
                break;
        }
        return dlg;
    }

    private Button.OnClickListener okDialogButtonOnClickListener =
            new Button.OnClickListener() {
                public void onClick(View v) {
                    dlg.dismiss();
                    finish();
                }
            };

    private String getExif(ExifInterface exif) {
        String myAttribute = "";
        myAttribute += getTagString(ExifInterface.TAG_DATETIME, exif);
        myAttribute += getTagString(ExifInterface.TAG_FLASH, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE_REF, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE_REF, exif);
        myAttribute += getTagString(ExifInterface.TAG_IMAGE_LENGTH, exif);
        myAttribute += getTagString(ExifInterface.TAG_IMAGE_WIDTH, exif);
        myAttribute += getTagString(ExifInterface.TAG_MAKE, exif);
        myAttribute += getTagString(ExifInterface.TAG_MODEL, exif);
        myAttribute += getTagString(ExifInterface.TAG_ORIENTATION, exif);
        myAttribute += getTagString(ExifInterface.TAG_WHITE_BALANCE, exif);
        return myAttribute;
    }

    private String getTagString(String tag, ExifInterface exif) {
        Log.d("지원", "!!!!! " + tag +  " : " + exif.getAttribute(tag));
        return (tag + " : " + exif.getAttribute(tag) + "\n");
    }
}
