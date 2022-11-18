package com.example.googlebucket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.api.gax.paging.Page;
import com.google.auth.Credentials;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.GsonBuilder;

import java.io.FileInputStream;
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

public class MainActivity extends AppCompatActivity {
    String TAG="MainActivity";
    String projectId = "eordersall";
    //eordersall.appspot.com/testingaudio
    String bucketName = "eordersall.appspot.com";
    //String userEmail = "eordersall@appspot.gserviceaccount.com";
    //String userEmail = "aostapharmacy@eordersall.iam.gserviceaccount.com";
    String userEmail = "aostapharmacy@eordersall.iam.gserviceaccount.com";

    String accessTokenCrediental = "149282824341-45ufhh1du5f5i71vuc694p20nft39hvd.apps.googleusercontent.com";


    //get Download object
    String destFilePath = "/local/path/to/file.txt";
    String objectName = "testingaudio";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread thread = new Thread(new Runnable() {

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

                    /////////////////////**************   GET BUCKET AND DETAILS **********************//////////////
                    //ListObjects
                    Page<Blob> blobs = storage.list(bucketName);

                    for (Blob blob : blobs.iterateAll()) {
                        Log.e(TAG,"msg>>blobget>>"+blob.getName());
                        //get download objects get bucket specific details
                        Blob blob1 = storage.get(BlobId.of(bucketName, blob.getName()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Log.e(TAG, "msg>>blob::>>" + new GsonBuilder().create().toJson(blob1));
                            //blob.downloadTo(Paths.get(destFilePath));
                        }
                        //get download objects get bucket specific details end end
                    }
                    //ListObjects end end
                    /////////////////////**************   GET BUCKET AND DETAILS **********************//////////////

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "msg>>error>>" + e.getMessage());


                }
            }
        });
        thread.start();












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