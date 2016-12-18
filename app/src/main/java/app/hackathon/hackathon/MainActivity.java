package app.hackathon.hackathon;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private Button sendImageData;
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private static final int REQUEST_CLICK_IMG = 1;
    private String mCurrentPhotoPath;

    private String KEY_IMAGE = "image";
    private String UPLOAD_URL ="http://192.168.1.207:8080/upload";
    private Bitmap bitmap = null;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAlbumStorageDirFactory = new AlbumDirFactory();
        sendImageData = (Button) findViewById(R.id.send_image);
        sendImageData.setOnClickListener(this);
        progressDialog = new ProgressDialog(MainActivity.this,R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_image:
                dispatchTakePictureIntent();
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CLICK_IMG) {
                Log.v("wtf","result code was okay: " + resultCode + " data: " + data);
                handleCameraPhoto();
            }
        }
        else {
            Log.v("wtf","result code was not okay: " + resultCode + " data: " + data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onStop () {
        super.onStop();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void handleCameraPhoto() {
        if (mCurrentPhotoPath != null) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
            mCurrentPhotoPath = null;

            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), contentUri);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Error while fetching bitmap" , Toast.LENGTH_LONG).show();

            }
        }
    }

    private void showAlertDialogBox(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog myDialog = builder.create();
        myDialog.show();
    }

    private void uploadImage(){
        if(isNetworkConnected()) {
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
            //Showing the progress dialog
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("wtf", "Uploaded image");
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }

                            Intent intent = new Intent(MainActivity.this,ProductActivity.class);
                            intent.putExtra("name","Iphone");
                            intent.putExtra("screen","1,920 x 1,080 pixels");
                            intent.putExtra("reviews","Expensive, Best Smartphone, Best Camera");
                            intent.putExtra("recommendations","Moto Z, Google Pixel");
                            MainActivity.this.startActivity(intent);

                            //Showing toast message of the response
//                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            //Dismissing the progress dialog
                            //Showing toast
                            Log.v("wtf error", volleyError.toString());
                            showAlertDialogBox("Error", "Something went wrong");
                            Toast.makeText(MainActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    //Converting Bitmap to String
                    String image = getStringImage(bitmap);

                    //Creating parameters
                    Map<String, String> params = new Hashtable<String, String>();

                    //Adding parameters
                    params.put(KEY_IMAGE, image);

                    //returning parameters
                    return params;
                }
            };

            //Creating a Request Queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            //Adding request to the queue
            requestQueue.add(stringRequest);
        }
        else {
            showAlertDialogBox("No Internet","Please check your internet connectivity.");
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile;
            try {
                // Create an image file name
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "HACK_" + timeStamp + "_";
                photoFile = createImageFile(imageFileName);
                mCurrentPhotoPath = photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                Log.v("wtf","Capturing image");
            }
            catch (IOException ex) {
                photoFile = null;
                mCurrentPhotoPath = null;

                Log.v("wtf","Error in camera");
                new android.app.AlertDialog.Builder(MainActivity.this)
                        .setMessage("Couldn't take picture")
                        .setCancelable(false)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
//                Log.v("wtf", "Successfully captured image");
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_CLICK_IMG);
            }
        }
    }

    private File createImageFile(String imageFileName) throws IOException {

        File storageDir = getAlbumDir();
        return File.createTempFile(
                imageFileName,  /* prefix */
                JPEG_FILE_SUFFIX,         /* suffix */
                storageDir      /* directory */
        );
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getString(R.string.app_name));

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("wtf", "Failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v("wtf", "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }
}
