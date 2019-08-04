package com.andela.travelmantic.user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.andela.travelmantic.R;
import com.andela.travelmantic.model.PostModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.andela.travelmantic.model.DatabaseModel.FB_DATABASE_PATH;
import static com.andela.travelmantic.model.DatabaseModel.FB_LIST_DATABASE_PATH;


public class ListTravelMainActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;
    private List<PostModel> imgList;
    private ListView itemListView;
    private rentListAdapter adapter;
    private ProgressDialog progressDialog;

    // Authenticate
    private FirebaseAuth auth;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item_activity_main);
        imgList = new ArrayList<>();
        itemListView = findViewById(R.id.listViewImage);


        //Show progress dialog during list data loading
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");
        progressDialog.show();


        // check for authentication

        // user authenticated email

        auth = FirebaseAuth.getInstance();

        // e-rental user have already sign up

        if (auth.getCurrentUser() != null) {
            userID = auth.getCurrentUser().getUid();

        }


        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);

        mDatabaseRef.child(FB_LIST_DATABASE_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                //Fetch image data from firebase database
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //ImageUpload class require default constructor
                    PostModel img = snapshot.getValue(PostModel.class);
                    imgList.add(img);


                }


                //Init adapter for e-rental
                adapter = new rentListAdapter(ListTravelMainActivity.this, R.layout.row_item, imgList);
                //Set adapter for list view
                itemListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                progressDialog.dismiss();
            }
        });

    }


    /* travelmantic list adapter*/


    public class rentListAdapter extends ArrayAdapter<PostModel> {


        private Activity context;
        private int resource;
        private List<PostModel> listImage;

        private ImageView itemImageView;


        private TextView priceTextView, descriptionTextView;


        public rentListAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<PostModel> objects) {
            super(context, resource, objects);
            this.context = context;
            this.resource = resource;
            listImage = objects;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();

            View view = inflater.inflate(resource, null);


            TextView itemTitleTextView = view.findViewById(R.id.itemTitleTextView);

            priceTextView = view.findViewById(R.id.priceTextView);
            descriptionTextView = view.findViewById(R.id.descriptionTextView);


            // image gallery
            itemImageView = view.findViewById(R.id.itemImageView);

            itemTitleTextView.setText(listImage.get(position).getName());


            priceTextView.setText(listImage.get(position).getItemPrice());
            descriptionTextView.setText(listImage.get(position).itemDescription);

            Glide.with(context).load(listImage.get(position).getItemImageUrl()).into(itemImageView);


            return view;

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

        // new list
        if (id == R.id.action_new) {

            startActivity(new Intent(getApplicationContext(), AdminActivity.class));


            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
