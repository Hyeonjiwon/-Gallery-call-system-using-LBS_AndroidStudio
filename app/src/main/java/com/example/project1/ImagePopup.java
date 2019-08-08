package com.example.project1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class ImagePopup extends Activity implements View.OnClickListener {
    private Context mContext = null;
    private final int imgWidth = 320;
    private final int imgHeight = 372;
    String imgPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_popup);
        mContext = this;

        /** 전송메시지 */
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        imgPath = extras.getString("filename");

        /** 완성된 이미지 보여주기  */
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 2;
        ImageView iv = (ImageView)findViewById(R.id.imageView);
        Bitmap bm = BitmapFactory.decodeFile(imgPath, bfo);
        Bitmap resized = Bitmap.createScaledBitmap(bm, imgWidth, imgHeight, true);
        iv.setImageBitmap(resized);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Back button pressed.", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Toast.makeText(this, "onPause()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Toast.makeText(this, "onStop()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "onDestroy()", Toast.LENGTH_SHORT).show();
    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    public void onClick(View v) {

    }

    public void Details(View target) {
        Intent intent = new Intent(getApplicationContext(), Details.class);
        intent.putExtra("filePath", imgPath);
        Toast.makeText(ImagePopup.this, "Details", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}