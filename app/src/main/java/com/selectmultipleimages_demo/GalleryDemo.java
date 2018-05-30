package com.selectmultipleimages_demo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class GalleryDemo extends AppCompatActivity {

    RecyclerView recyclerview;
    static Button btnnext;
    Button btnselect,btnaudio;
    Uri audioFileUri = null;
    Dialog dialog;
    private static final int AudioFromSDCard = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_demo);

        btnselect = (Button)findViewById(R.id.btnselect);
        btnaudio = (Button)findViewById(R.id.btnaudio);

        dialog = new Dialog(GalleryDemo.this);
        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        recyclerview = (RecyclerView) dialog.findViewById(R.id.recyclerview);
        btnnext = (Button)dialog.findViewById(R.id.btnnext);
        verifyStoragePermissions();
        recyclerview.addItemDecoration(new EqualSpacingItemDecoration(16)); // 16px. In practice, you'll want to use getDimensionPixelSize
        // LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //  layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //  recyclerview.setLayoutManager(layoutManager);

        final MyAdapter adapter = new MyAdapter(getAllShownImagesPath(GalleryDemo.this), GalleryDemo.this);
        GridLayoutManager glm = new GridLayoutManager(this, 2);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case 0:
                        return 2;
                    case 1:
                        return 1;
                    case 2:
                        return 2;
                    default:
                        return 1;
                }
            }
        });
        glm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerview.setLayoutManager(glm);
        recyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MyAdapter.arrayList.size()>0) {
                    Intent intent = new Intent(GalleryDemo.this, VideoActivity.class);
                    intent.putStringArrayListExtra("images_list", (ArrayList<String>) MyAdapter.arrayList);
                    intent.putExtra("uri", audioFileUri + "");
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(GalleryDemo.this,"Please select images",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        btnaudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/mpeg");
                startActivityForResult(Intent.createChooser(intent, "Select Audio"), AudioFromSDCard);
            }
        });
    }

    public static ArrayList<DataClass> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        ArrayList<DataClass> DatalistOfAllImages = new ArrayList<DataClass>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            DataClass data = new DataClass();
            data.setUri(absolutePathOfImage);
            listOfAllImages.add(absolutePathOfImage);
            DatalistOfAllImages.add(data);
        }
        return DatalistOfAllImages;
    }
    protected void onActivityResult(int requestcode, int resultcode,
                                    Intent imagereturnintent) {
        super.onActivityResult(requestcode, resultcode, imagereturnintent);
        switch (requestcode) {
            case AudioFromSDCard:
                if (resultcode == RESULT_OK) {
                    if ((imagereturnintent != null) && (imagereturnintent.getData() != null)) {
                        audioFileUri = imagereturnintent.getData();
                        Log.e("audioFileUri", audioFileUri + "");
                        if(!audioFileUri.equals("")){}
                      //      tvaudio.setEnabled(true);
                        // Now you can use that Uri to get the file path, or upload it, ...
                    }
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(dialog!=null)
        {
            dialog.dismiss();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
    /*private class VideoCreate extends AsyncTask<Void, Void, Void> {

        String Videopath;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("VideoCreate onPreExecute call ");
          // dismissProgressDialog();
          //  showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {

            // try {

            Object temp_video_file_name_last = System.currentTimeMillis()
                    + FileUtils.VIDEO_TYPE_MP4;
            // Vidopah = FileUtils.getScreenShotDirectory(getActivity(),
            // FileUtils.ATTACHMENT_TYPE_VIDEO)
            // + File.separator
            // + video_file_name_last;
            Videopath = getTempVideoDirectory() + File.separator
                    + temp_video_file_name_last;

            File pF = new File(tempVideoShotDirecotry);
            if (!pF.exists()) {
                System.out.println("pF not exist");
                pF.mkdirs();
            }
            if (pF.exists()) {
                System.out.println("pF  exist");
            }
            int imgCounter = 0;

            File folder = new File(tempVideoShotDirecotry);

            File[] listOfFiles = folder.listFiles();
            System.out.println("listOfFiles size::" + listOfFiles.length);
            if (listOfFiles.length > 0) {

                for (int j = 0; j < listOfFiles.length; j++) {

                    if (listOfFiles[j].getAbsolutePath().endsWith(".JPEG")
                            || listOfFiles[j].getAbsolutePath().endsWith(
                            ".jpeg")
                            || listOfFiles[j].getAbsolutePath()
                            .endsWith(".JPG")
                            || listOfFiles[j].getAbsolutePath()
                            .endsWith(".jpg")
                            || listOfFiles[j].getAbsolutePath()
                            .endsWith(".png")) {
                        System.out.println(j + " position listOfFiles::"
                                + listOfFiles[j].getAbsolutePath());
                        Bitmap bmp = BitmapFactory.decodeFile(listOfFiles[j]
                                .getAbsolutePath());
                        if (bmp != null) {
                            System.out.println("bmp is not null");
                            ++imgCounter;
                        }

                    }

                }

            }

            Log.e("", "Intial Execute");

            FFmpegFrameRecorder recorder = null;
            recorder = new FFmpegFrameRecorder(Videopath, 100,
                    100);
            Log.d("Display",
                    (new StringBuilder(String.valueOf(100)))
                            .append(":").append(100).toString());

            // working
           // recorder.setVideoCodec(13);
            recorder.setFormat("mp4");
            recorder.setFrameRate(1.0D);
        //    recorder.setVideoQuality(1.0D);
       //     recorder.setVideoBitrate(40000);


            long l = System.currentTimeMillis();
            try {
                recorder.start();
            }*//* catch (org.bytedeco.javacv.FrameRecorder.Exception e1) {
                e1.printStackTrace();
            }*//* catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("imgCounter size::" + imgCounter);

            for (int i = 0; i < imgCounter; i++) {
                opencv_core.IplImage iplimageTemp = opencv_highgui
                        .cvLoadImage(listOfFiles[i].getAbsolutePath());
                long l1 = 1000L * (System.currentTimeMillis() - l);
                if (l1 < recorder.getTimestamp()) {
                    l1 = 1000L + recorder.getTimestamp();
                }
                System.out.println(i + " timestamp:::" + l1);
                recorder.setTimestamp(l1);
                try {
                    recorder.record(iplimageTemp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Log.e("", "End Execute");
            try {
                recorder.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // } catch (Exception e) {
            // e.printStackTrace();
            // }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        //    dismissProgressDialog();
        //    alert.setCustomToast("Video is Generated");

            if (isTempVideoAvailableInStorage()) {

                new MergeAudioAndVideo().execute();
            }
        }
    }*/
    private void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(GalleryDemo.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
// We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    GalleryDemo.this,
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

}
