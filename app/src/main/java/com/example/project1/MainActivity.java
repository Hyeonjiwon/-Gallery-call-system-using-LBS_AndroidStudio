package com.example.project1;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "SDK_VERSION_CHECK";
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("wldnjs_Main", "MainActivity : 현재 단말의 SDK : " + Build.VERSION.SDK_INT);

        checkTedPermission();
    }

    private void checkTedPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    public void myListener(View target) {
        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
        Toast.makeText(MainActivity.this, "Camera", Toast.LENGTH_SHORT).show();
        //checkTedPermission();
        startActivity(intent);
    }

    public void myListener2(View target) {
        Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
        Toast.makeText(MainActivity.this, "Gallery", Toast.LENGTH_SHORT).show();
        //checkTedPermission();
        startActivity(intent);
    }

    public void myListener3(View target) {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        Toast.makeText(MainActivity.this, "Map", Toast.LENGTH_SHORT).show();
        //checkTedPermission();
        startActivity(intent);
    }
}
