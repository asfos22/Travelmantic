package com.andela.travelmantic.user;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.andela.travelmantic.R;
import com.andela.travelmantic.model.PostModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

import static com.andela.travelmantic.model.DatabaseModel.FB_DATABASE_PATH;
import static com.andela.travelmantic.model.DatabaseModel.FB_LIST_DATABASE_PATH;
import static com.andela.travelmantic.model.DatabaseModel.FB_LIST_STORAGE_PATH;


public class AdminActivity extends AppCompatActivity {


    public static final int REQUEST_CODE = 1000;
    private Uri imageURL;
    private ImageView tripImageView;

    private Button submitButton, selectImageButton;

    private String posterID, userEmail;

    private EditText nameEditText, priceEditText, descriptionEditText;


    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    // Authenticate
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_content);


        // init the database

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();


        // user authenticated email

        auth = FirebaseAuth.getInstance();

        // e-rental user have already sign up

        if (auth.getCurrentUser() != null) {

            userEmail = auth.getCurrentUser().getEmail();
            posterID = auth.getCurrentUser().getUid();

        }


        // get the company  registration credentials


       /* DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(FB_DATABASE_PATH).child(FB_COMPANY_DATABASE_PATH).orderByChild("userID").equalTo(posterID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //                        emergencyContactsList = new ArrayList<>();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {

                        // company model

                        CompanyModel companyModel = postSnapShot.getValue(CompanyModel.class);

                        companyName = companyModel.getCompanyName();
                        companyAddress = companyModel.getCompanyAddress();
                        companyPhoneNumber = companyModel.getCompanyPhoneNumber();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


        submitButton = findViewById(R.id.submitButton);

        nameEditText = findViewById(R.id.nameEditText);
        priceEditText = findViewById(R.id.priceEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);

        tripImageView = findViewById(R.id.tripImageView);

        selectImageButton = findViewById(R.id.selectImageButton);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image"), REQUEST_CODE);

            }
        });


    }

    /***
     *
     * Image
     *
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            try {
                imageURL = data.getData();
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURL);
                tripImageView.setImageBitmap(bm);

                // Visibility
                tripImageView.setVisibility(View.VISIBLE);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // convert to Image Extension;
    public String getImageUrlExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // post request to Database

    public void submitNewItemRequest() {


        if (!imageURL.equals(null) && !nameEditText.getText().toString().equals(null) && !priceEditText.getText().toString().equals(null) && !descriptionEditText.getText().toString().equals(null)) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle(getString(R.string.please_wait));
            dialog.show();

            //Get the storage reference


            final StorageReference imageStorageReference = mStorageRef.child(FB_DATABASE_PATH).child(FB_LIST_STORAGE_PATH + System.currentTimeMillis() + "." + getImageUrlExtension(imageURL));
            imageStorageReference.putFile(imageURL).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imageStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUrl = uri;
                            // I want the url

                            //Dismiss dialog when success
                            dialog.dismiss();
                            //Display success toast msg

                            PostModel postModel = new PostModel(
                                    posterID,
                                    nameEditText.getText().toString(),
                                    priceEditText.getText().toString(),
                                    descriptionEditText.getText().toString(),
                                    downloadUrl.toString()
                            );

                            //Save image info in to travelmantic firebase database
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(FB_DATABASE_PATH).child(FB_LIST_DATABASE_PATH).child(uploadId).setValue(postModel);

                            // intent to List Activity

                            startActivity(new Intent(getApplicationContext(), ListTravelMainActivity.class));

                        }


                    });


                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            //Dismiss dialog when error
                            dialog.dismiss();
                            //Display err toast msg

                            setToast(e.getMessage());

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            //Show upload progress

                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            dialog.setMessage(getString(R.string.uploaded) + (int) progress + "%");

                        }
                    });
        } else {

            setToast(getString(R.string.enter_all_fields));

        }


    }

    // --
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //--
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // submit to firebase
        if (id == R.id.action_new) {

            submitNewItemRequest();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // -- toast

    void setToast(String msg) {

        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
