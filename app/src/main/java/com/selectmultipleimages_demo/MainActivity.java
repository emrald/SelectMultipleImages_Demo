package com.selectmultipleimages_demo;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static Button openCustomGallery;
    private static GridView selectedImageGridView;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    TextView tvvideo, tvaudio;
    List<String> selectedImages;
    private static final int CustomGallerySelectId = 1;//Set Intent Id
    private static final int AudioFromSDCard = 2;//Set Intent Id
    public static final String CustomGalleryIntentKey = "ImageArray";//Set Intent Key Value
    ArrayList<String> images_array = new ArrayList<String>();
    Uri audioFileUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions();

        initViews();
        setListeners();
        getSharedImages();
     //   tvaudio.setEnabled(false);
        tvvideo.setEnabled(false);

        tvvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImages.size() > 0) {
                    Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                    intent.putStringArrayListExtra("images_list", (ArrayList<String>) selectedImages);
                    intent.putExtra("uri",audioFileUri+"");
                    startActivity(intent);
                }
                Log.e("Size...", selectedImages.size() + "");
            }
        });
        tvaudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/mpeg");
                startActivityForResult(Intent.createChooser(intent, "Select Audio"), AudioFromSDCard);
            }
        });
      /*  animation = new AnimationDrawable();
        animation.addFrame(Drawable.createFromPath("/storage/emulated/0/DCIM/Screenshots/Screenshot_20180525-103346.png"), 1000);
        animation.addFrame(getDrawable(R.drawable.d_responded), 1000);
        animation.addFrame(getDrawable(R.drawable.deal_selected), 1000);
        animation.setOneShot(false);
        img.setBackgroundDrawable(animation);*/

    }

    //Init all views
    private void initViews() {
        openCustomGallery = (Button) findViewById(R.id.openCustomGallery);
        selectedImageGridView = (GridView) findViewById(R.id.selectedImagesGridView);
        tvvideo = (TextView) findViewById(R.id.tvvideo);
        tvaudio = (TextView) findViewById(R.id.tvaudio);
    }

    //set Listeners
    private void setListeners() {
        openCustomGallery.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.openCustomGallery:
                //Start Custom Gallery Activity by passing intent id
                startActivityForResult(new Intent(MainActivity.this, CustomGallery_Activity.class), CustomGallerySelectId);
                break;
        }
    }

    protected void onActivityResult(int requestcode, int resultcode,
                                    Intent imagereturnintent) {
        super.onActivityResult(requestcode, resultcode, imagereturnintent);
        switch (requestcode) {
            case CustomGallerySelectId:
                if (resultcode == RESULT_OK) {
                    String imagesArray = imagereturnintent.getStringExtra(CustomGalleryIntentKey);//get Intent data
                    //Convert string array into List by splitting by ',' and substring after '[' and before ']'
                    selectedImages = new ArrayList<String>(Arrays.asList(imagesArray.substring(1, imagesArray.length() - 1).split(", ")));
                    //    images_array = (ArrayList<String>) selectedImages;
                    Log.e("selectedImages", selectedImages.get(1) + "");

                    loadGridView(new ArrayList<String>(selectedImages));//call load gridview method by passing converted list into arrayList
                    if(selectedImages.size()>0)
                    {
                        tvvideo.setEnabled(true);
                    }
                }
                break;
            case AudioFromSDCard:
                if (resultcode == RESULT_OK) {
                    if ((imagereturnintent != null) && (imagereturnintent.getData() != null)) {
                        audioFileUri = imagereturnintent.getData();
                        Log.e("audioFileUri", audioFileUri + "");
                        if(!audioFileUri.equals(""))
                            tvaudio.setEnabled(true);
                        // Now you can use that Uri to get the file path, or upload it, ...
                    }
                }
                break;
        }
    }

    //Load GridView
    private void loadGridView(ArrayList<String> imagesArray) {
        GridView_Adapter adapter = new GridView_Adapter(MainActivity.this, imagesArray, false);
        selectedImageGridView.setAdapter(adapter);
    }

    //Read Shared Images
    private void getSharedImages() {

        //If Intent Action equals then proceed
        if (Intent.ACTION_SEND_MULTIPLE.equals(getIntent().getAction())
                && getIntent().hasExtra(Intent.EXTRA_STREAM)) {
            ArrayList<Parcelable> list =
                    getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM);//get Parcelabe list
            ArrayList<String> selectedImages = new ArrayList<>();

            //Loop to all parcelable list
            for (Parcelable parcel : list) {
                Uri uri = (Uri) parcel;//get URI
                String sourcepath = getPath(uri);//Get Path of URI
                selectedImages.add(sourcepath);//add images to arraylist
                Log.e("selectedImages", selectedImages + "");
            }
            loadGridView(selectedImages);//call load gridview
        }
    }


    //get actual path of uri
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
// We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
// we already have permission, lets go ahead and call camera intent
           /* Intent intent = new Intent();
            intent.setType("image*//*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);*/
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
