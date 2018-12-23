package com.housedevelop.housedevelop;

import android.view.LayoutInflater;
import android.util.TypedValue;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.IOException;
import android.content.Intent;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import java.io.File;
import java.text.SimpleDateFormat;
import android.os.Environment;
import java.util.Date;
import android.net.Uri;
import android.graphics.BitmapFactory;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.app.ProgressDialog;
import android.view.MenuInflater;
import android.support.v7.app.ActionBarActivity;
import android.widget.LinearLayout;
import android.media.ExifInterface;
import 	android.graphics.Matrix;
//import 	android.graphics.Bitmap;
import 	java.net.URLEncoder;
/*import java.io.FileNotFoundException;
import  	java.io.FileOutputStream;
import android.content.Context;*/
import android.app.Activity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;


public class HouseAddComment extends ActionBarActivity {
    String User_uid;
    String marker_id;
    LayoutInflater inflater;
    EditText im_new_foto_comment;
    ImageView im_new_foto;
    String serverResponseMessage;
    LinearLayout.LayoutParams lParams1;
    private static int LOAD_IMAGE_RESULTS = 2;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;
    File FilePhoto;
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    Boolean photoyes = false;

    Intent mResultIntent;
    private File mPrivateRootDir;
    // The path to the "images" subdirectory
    private File mImagesDir;
    // Array of files in the images subdirectory
    File[] mImageFiles;
    // Array of filenames corresponding to mImageFiles
    String[] mImageFilenames;
    //----------------------------------------------------------------------------------------------


    public int uploadFile(String sourceFileUri, String comment) {
        String upLoadServerUri = AppConfig.URL_UPLOAD_PHOTO;
        String fileName ="none";
        File sourceFile = null;
        HttpURLConnection conn;
        DataOutputStream dos;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        FileInputStream fileInputStream = null;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        if (photoyes) {
            fileName = FilePhoto.getPath();
            sourceFile = new File(sourceFileUri);
            if (!sourceFile.isFile()) {
                Log.e("uploadFile", "Source File Does not exist");
                return 0;
            }
        }
        try { // open a URL connection to the Servlet
            if (photoyes) {
                fileInputStream = new FileInputStream(sourceFile);
            }
            URL url = new URL(upLoadServerUri);
            conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("Userid", User_uid);
            conn.setRequestProperty("uploaded_file", fileName);
            conn.setRequestProperty("filename", fileName);
            //Toast.makeText(HouseAddComment.this, User_uid , Toast.LENGTH_SHORT).show();

            conn.setRequestProperty("Comment", URLEncoder.encode(comment, "UTF-8"));
            conn.setRequestProperty("Tag", "upload_photo");
            conn.setRequestProperty("Marker", marker_id);
            conn.setRequestProperty("Empty", " ");
            //Log.d("uid " , User_uid);

            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes("Content-Disposition: form-data;name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            if (photoyes) {

            bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            }

            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();
            serverResponseMessage = conn.getResponseMessage();

            Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
            if(serverResponseCode == 200){
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("File Upload"," Completed.");
                        Toast.makeText(HouseAddComment.this, (getResources().getString(R.string.upload_complete))+ serverResponseMessage , Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.putExtra("OK", "OK");
                        setResult(RESULT_OK, intent);

                        finish();

                    }
                });
            }

            //close the streams //
            if (photoyes) {
                fileInputStream.close();
            }
            dos.flush();
            dos.close();

        } catch (MalformedURLException ex) {
            dialog.dismiss();
            ex.printStackTrace();
            Toast.makeText(HouseAddComment.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {
            dialog.dismiss();
            e.printStackTrace();
            Toast.makeText(HouseAddComment.this, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Upload file to server", "Exception : " + e.getMessage(), e);
        }
        dialog.dismiss();
        return serverResponseCode;
    }

    //----------------------------------------------------------------------------------------------

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    //----------------------------------------------------------------------------------------------

    private void setPic() {
        // Get the dimensions of the View

        Matrix matrix = new Matrix();
        matrix.postRotate(getImageOrientation(FilePhoto.getPath()));

        int targetW = im_new_foto.getWidth();
        int targetH = im_new_foto.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(FilePhoto.getPath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        //bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(FilePhoto.getPath(), bmOptions);

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);

        im_new_foto.setImageBitmap(rotatedBitmap);

    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            if(FilePhoto.exists()){

                lParams1 = (LinearLayout.LayoutParams) im_new_foto.getLayoutParams();
                lParams1.height=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
                im_new_foto.setLayoutParams(lParams1);
                setPic();
                photoyes = true;

            }
        }
        if (requestCode == LOAD_IMAGE_RESULTS && resultCode == RESULT_OK) {

            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            Log.d("fileget", imagePath);
            FilePhoto = new File(imagePath);
            lParams1 = (LinearLayout.LayoutParams) im_new_foto.getLayoutParams();
            lParams1.height=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
            im_new_foto.setLayoutParams(lParams1);
            setPic();
            photoyes = true;

            }
    }

    //----------------------------------------------------------------------------------------------

    public static int getImageOrientation(String imagePath){
        int rotate = 0;
        try {

            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

    //----------------------------------------------------------------------------------------------

    private void dispatchTakePictureIntent() {
/*        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }*/

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ignored) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                FilePhoto = photoFile;

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    //----------------------------------------------------------------------------------------------

    private void dispatchLoadPictureIntent() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Start new activity with the LOAD_IMAGE_RESULTS to handle back the results when image is picked from the Image Gallery.
        startActivityForResult(i, LOAD_IMAGE_RESULTS);
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcomment);

        Bundle bundle = getIntent().getParcelableExtra("bundle");
        User_uid = bundle.getString("user_uid");
        marker_id = bundle.getString("marker_id");
      //  Log.e("User_uid comment ",User_uid);
        inflater = getLayoutInflater();

        im_new_foto = (ImageView) findViewById(R.id.im_new_foto);
        im_new_foto_comment = (EditText) findViewById(R.id.im_new_foto_comment);
        lParams1 = (LinearLayout.LayoutParams) im_new_foto.getLayoutParams();
        lParams1.height=1;
        im_new_foto.setLayoutParams(lParams1);

    }

    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addcomment_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.bt_send_load_photo) {
            dispatchLoadPictureIntent();
        }

        if (id == R.id.bt_send_make_photo) {
            dispatchTakePictureIntent();
        }

        // Upload file to server
        if (id == R.id.bt_send_comment) {
            dialog = ProgressDialog.show(HouseAddComment.this, "", (getResources().getString(R.string.sending))+"...", true);
            new Thread(new Runnable() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {

                        }
                    });
                    if (photoyes) {
                        int response = uploadFile(FilePhoto.getPath(), im_new_foto_comment.getText().toString());
                        Log.d("RES" , " + " + String.valueOf(response));
                    }
                    if (!photoyes) {
                        int response = uploadFile(null, im_new_foto_comment.getText().toString());
                        Log.d("RES" , " + " + String.valueOf(response));
                    }

                }
            }).start();
        }
        if (id == R.id.bt_send_cancel) {
            Intent intent = new Intent();
            intent.putExtra("OK", "cancel");
            setResult(RESULT_OK, intent);

            finish();

        }
        return super.onOptionsItemSelected(item);
    }
}

