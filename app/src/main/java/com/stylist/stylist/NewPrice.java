package com.stylist.stylist;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewPrice extends AppCompatActivity {

    private ImageButton mSelectImage;
    private EditText mSessionTitle, mSessionDetail , mSessionPrice , mSessionNote;
    private Button mSubmit;
    private Uri mImageUri = null;
    private ProgressDialog mProgress;
    private final int GALLERY_REQUEST=1;
    private StorageReference mStorage;
    private DatabaseReference mDataBase;
    private boolean check = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_price);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Prices");
        mSelectImage = (ImageButton) findViewById(R.id.session_image);
        mSessionTitle = (EditText) findViewById(R.id.session_name);
        mSessionDetail = (EditText) findViewById(R.id.details);
        mSessionPrice = (EditText) findViewById(R.id.session_price);
        mSessionNote = (EditText) findViewById(R.id.session_note);
        mSubmit =(Button) findViewById(R.id.add_session);
        mProgress = new ProgressDialog(this);

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
                check = true;
            }
        });
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPostNew();
            }
        });
    }

    @Override
    public void onBackPressed() {
        final String session_name = mSessionTitle.getText().toString().trim();
        final String session_detail = mSessionDetail.getText().toString().trim();
        final String session_price = mSessionPrice.getText().toString().trim();
        final String session_note = mSessionNote.getText().toString().trim();
        if((!TextUtils.isEmpty(session_name)) || (!TextUtils.isEmpty(session_detail)) ||
                (!TextUtils.isEmpty(session_price)) || (!TextUtils.isEmpty(session_note)) ){
            AlertDialog.Builder builder = new AlertDialog.Builder(NewPrice.this);
            builder.setMessage("Your post will Dismiss")
                    .setNegativeButton("Cancel",null)
                    .setPositiveButton("Dismiss Post", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NewPrice.super.onBackPressed();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else {
            NewPrice.super.onBackPressed();
        }
    }

    private void startPostNew(){
        mProgress.setMessage("Posting New Session...");
        final String session_name = mSessionTitle.getText().toString().trim();
        final String session_detail = mSessionDetail.getText().toString().trim();
        final String session_price = mSessionPrice.getText().toString().trim();
        final String session_note = mSessionNote.getText().toString().trim();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH:mm");
        final String date2 = sdf.format(new Date());
        if((!TextUtils.isEmpty(session_name)) && (!TextUtils.isEmpty(session_detail)) &&
                (!TextUtils.isEmpty(session_price)) && (!TextUtils.isEmpty(session_note)) ) {
            if(check) {
                mProgress.show();
                StorageReference filepath = mStorage.child("Prices_Images").child(mImageUri.getLastPathSegment());
                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        DatabaseReference newPost = mDataBase.push();
                        newPost.child("image").setValue(downloadUrl.toString());
                        newPost.child("name").setValue(session_name);
                        newPost.child("details").setValue(session_detail);
                        newPost.child("price").setValue(session_price);
                        newPost.child("note").setValue(session_note);
                        newPost.child("date").setValue(date2);
                        mProgress.dismiss();
                        startActivity(new Intent(NewPrice.this, MainActivity.class));
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                });
            }else{
                mProgress.show();
                DatabaseReference newPost = mDataBase.push();
                newPost.child("image").setValue("default");
                newPost.child("name").setValue(session_name);
                newPost.child("details").setValue(session_detail);
                newPost.child("price").setValue(session_price);
                newPost.child("note").setValue(session_note);
                newPost.child("date").setValue(date2);
                mProgress.dismiss();
                startActivity(new Intent(NewPrice.this, MainActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        }else{
            Toast.makeText(NewPrice.this,"Please Complete Data.",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            CropImage.activity(mImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                mSelectImage.setImageURI(mImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
