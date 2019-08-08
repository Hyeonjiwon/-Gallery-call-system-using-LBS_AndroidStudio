package com.example.project1;


import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity_tmp extends AppCompatActivity {
    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mCamera = getCameraInstance();

        Log.d("wldnjs", mCamera + " Connection");

        mPreview = new CameraPreview(this, mCamera);
        //FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        //preview.addView(mPreview);

        //캡쳐 버튼
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mCamera.takePicture(null, null, mPicture);
            }
        });
    }
    
    //사진이 찍히면 콜백 메소드 호출, data에 사진 데이터가 저장되어 있음
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
            File pictureFileDir = getDir();
            if(!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
                Log.d("Camera", pictureFileDir.exists() + " " + pictureFileDir.mkdirs());
                Log.d("Camera", "Can't create directory.");
                Toast.makeText(getApplicationContext(), "Can't create directory.", Toast.LENGTH_LONG).show();
                return;
            }

            //파일 이름에 날짜와 시간을 붙여 생성
            SimpleDateFormat dataFormat = new SimpleDateFormat("yyyymmddhhmmss");

            String date = dataFormat.format(new Date());

            Log.d("Camera info dateFormat", date+" ");

            String photoFile = "MyPic" + date + ".jpg";


            String fileName = pictureFileDir.getPath() + File.separator + photoFile;
            File pictureFile = new File(fileName);

            try {
                //파일에 사진 데이터 기록
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();

                //!!사진을 찍고 저장한 후 갤러리에 보이지 않음
                //인텐트를 통해 해당 파일을 업데이트
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(pictureFile);
                mediaScanIntent.setData(contentUri);
                CameraActivity_tmp.this.sendBroadcast(mediaScanIntent);

                Log.d("wldnjs", "fileName: " + photoFile);
                Toast.makeText(getApplicationContext(), "New Image is saved : " + photoFile, Toast.LENGTH_LONG).show();

            } catch (FileNotFoundException e) {
                Log.d("Camera", "File" + fileName + "Image was not saved: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Image was not saved.", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.d("Camera", e.getMessage());
            }
        }

        //사진에 저장되는 폴더를 생성하여 반환
        private File getDir() {
            File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            return new File(sdDir, "CameraCapture");
        }
    };


    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            Log.d("wldnjs",e.getMessage());
        }
        return c;
    }
}
