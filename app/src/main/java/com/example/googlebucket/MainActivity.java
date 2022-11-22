package com.example.googlebucket;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.gax.paging.Page;
import com.google.auth.Credentials;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;*/

import java.util.List;
import java.util.Locale;
import java.util.Map;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.StorageObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;

public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity";
    String projectId = "eordersall";
    //eordersall.appspot.com/testingaudio
    String bucketName = "eordersall.appspot.com";
    String objectName = "testingaudio";
    String gsUtil = "";

    TextView upload_files;

    private static final int SELECT_AUDIO = 2;
    String selectedPath = "", audio;
    ProgressDialog prgDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prgDialog = new ProgressDialog(this);
        prgDialog.setCancelable(false);

        upload_files = findViewById(R.id.upload_files);

        upload_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGalleryAudio();
            }
        });

       /* Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {


                    Date currentTime = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(currentTime);
                    calendar.add(Calendar.DATE, 1);
                    currentTime = calendar.getTime();
                    Log.e(TAG, "msg>>time>>" + currentTime);


                    Storage storage = StorageOptions.newBuilder()
                            .setCredentials(ServiceAccountCredentials.fromStream(getResources().openRawResource(R.raw.server_key)))
                            .build()
                            .getService();


                    //Log.e(TAG, "msg>>storage::>>" + new GsonBuilder().create().toJson(storage));

                    Bucket bucket = storage.get(bucketName);
                    Log.e(TAG, "msg>>getName>>" + bucket.getName());

                    /////////////////////**************   GET BUCKET AND DETAILS **********************+//////////////
                    //ListObjects
                    Page<Blob> blobs = storage.list(bucketName);

                    for (Blob blob : blobs.iterateAll()) {
                        Log.e(TAG, "msg>>blobget>>" + blob.getName());
                        //get download objects get bucket specific details
                        Blob blob1 = storage.get(BlobId.of(bucketName, blob.getName()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Log.e(TAG, "msg>>blob::>>" + new GsonBuilder().create().toJson(blob1));
                            //blob.downloadTo(Paths.get(destFilePath));
                        }
                        //get download objects get bucket specific details end end
                    }
                    //ListObjects end end
                    /////////////////////**************   GET BUCKET AND DETAILS **********************+//////////////

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "msg>>error>>" + e.getMessage());


                }
            }
        });
        thread.start();*/

    }

    public void openGalleryAudio() {

        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Audio "), SELECT_AUDIO);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == SELECT_AUDIO) {
                System.out.println("SELECT_AUDIO");

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {

                            Uri uri = data.getData();
                            Log.e(TAG, "msg>>path>>uri : " + uri);
                            File file = new File(uri.getPath());//create path from uri
                            Log.e(TAG, "msg>>path>>file path: " + file.getPath());
                            String[] str = ((file.getPath()).split("/"));
                            String[] arr = (str[str.length - 1]).split(":");
                            audio = arr[1];

                /*InputStream inputStream = null;
                try {
                     inputStream=this.getContentResolver().openInputStream(uri);
                    Log.e(TAG,"msg>>path>>inputStream: " + inputStream);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e(TAG,"msg>>path>>inputStream:error>> " + e.getMessage());

                }*/
                            //selectedPath =inputStream;


                            Storage storage;
                            File tempFile;
                            try {
                                Storage.BlobTargetOption precondition = Storage.BlobTargetOption.doesNotExist();
                                storage = StorageOptions.newBuilder()
                                        .setCredentials(ServiceAccountCredentials.fromStream(getResources().openRawResource(R.raw.server_key)))
                                        .build()
                                        .getService();
                                String[] arrStudio = (audio).split(("\\."));

                                BucketInfo bucketInfo=BucketInfo.newBuilder(bucketName)
                                        .setLocation(objectName)
                                        .build();
                                Log.e(TAG, "msg>>path>>bucketInfo>>" + bucketInfo);
                                String test=objectName+"/"+audio;
                                BlobId blobId = BlobId.of(bucketName, test);

                                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    Log.e(TAG, "msg>>path>>audio>>" + audio);


                                    //File tempFile = File.createTempFile("file", ".txt");
                                    tempFile = File.createTempFile(arrStudio[0], "." + arrStudio[1]);

                                    selectedPath = tempFile.getPath();
                                    Log.e(TAG, "msg>>path>>selectedPath: " + selectedPath);

                                    storage.create(blobInfo, Files.readAllBytes(Paths.get(selectedPath)));
                                    Log.e(TAG, "msg>>path>>SELECT_AUDIO  : " + tempFile);
                                    Log.e(TAG, "msg>>path>>SELECT_AUDIO getPath : " + tempFile.getPath());
                                    //doFileUpload();
                                    prgDialog.setMessage("Calling Upload");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e(TAG, "msg>>path>>SELECT_AUDIO Path error>>: " + e.getMessage());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "msg>>path>>error>>" + e.getMessage());


                        }
                    }
                });
                thread.start();
            }


        }
    }

    public void dummy() {
        //Storage storage = StorageOptions.getDefaultInstance().getService();
     /*   String accessTokenCrediental="149282824341-45ufhh1du5f5i71vuc694p20nft39hvd.apps.googleusercontent.com";
        Date currentTime = Calendar.getInstance().getTime();
        Credentials credentials = GoogleCredentials.create(new AccessToken(accessTokenCrediental,currentTime));
        Storage storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();*/

                    /*//Using Credentials
                    Credentials credentials = GoogleCredentials.create(new AccessToken(accessToken,currentTime));
                    Storage storage = StorageOptions.newBuilder()
                            .setCredentials(credentials)
                            .build()
                            .getService();*/

        //get object metadata objects
                   /* Blob blob =
                            storage.get(bucketName, objectName, Storage.BlobGetOption.fields(Storage.BlobField.values()));
                    Log.e(TAG, "msg>>blob::>>" + new GsonBuilder().create().toJson(blob));
                    if (blob.getMetadata() != null) {
                        Log.e(TAG,"msg>>User metadata:");
                        for (Map.Entry<String, String> userMetadata : blob.getMetadata().entrySet()) {
                            Log.e(TAG,"msg>>metakey>>"+userMetadata.getKey() + "=" + userMetadata.getValue());
                        }
                    }*/
        //get object metadata objects end end

                  /*  Page<Bucket> buckets = storage.list();
                    List<Acl> bucketAcls = bucket.getAcl();*/

        //Log.e(TAG, "msg>>bucket::>>" + new GsonBuilder().create().toJson(bucket));
                   /* for (Bucket buckets1 : buckets.iterateAll()) {
                        //System.out.println(bucket.getName());


                        Acl userAcl = bucket.getAcl(new Acl.User("aostapharmacy@eordersall.iam.gserviceaccount.com"));

                        String userRole = userAcl.getRole().name();
                        Log.e(TAG, "msg>>Name>>" + buckets1.getName() + ">>" + userRole);

                    }*/

                   /* for (Acl acl : bucketAcls) {

                        // This will give you the role.
                        // See https://cloud.google.com/storage/docs/access-control/lists#permissions
                        String role = acl.getRole().name();

                        // This will give you the Entity type (i.e. User, Group, Project etc.)
                        // See https://cloud.google.com/storage/docs/access-control/lists#scopes
                        String entityType = acl.getEntity().getType().name();

                        TextView textView = findViewById(R.id.text);
                        //textView.setText(entityType+"=="+role);
                    }*/

    }

}