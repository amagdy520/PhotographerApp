package com.stylist.stylist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class Modify extends Activity {

    ImageButton mProfile;
    EditText mPhName,mPhDetails;
    Button mSave;
    private Uri mImageUri = null;
    private ProgressDialog mProgress;
    private final int GALLERY_REQUEST=1;
    private StorageReference mStorage;
    private DatabaseReference mDataBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Admin_Profile");
        mProfile = (ImageButton) findViewById(R.id.add_profile_pic);
        mPhName = (EditText) findViewById(R.id.ph_name_edit);
        mPhDetails = (EditText) findViewById(R.id.ph_details_edit);
        mSave = (Button) findViewById(R.id.save_change);
        mProgress = new ProgressDialog(this);
        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplayChanges();
            }
        });
    }

    @Override
    public void onBackPressed() {
        final String title_val = mPhName.getText().toString().trim();
        final String desc_val = mPhDetails.getText().toString().trim();
        if((!TextUtils.isEmpty(title_val)) || (!TextUtils.isEmpty(desc_val))){
            AlertDialog.Builder builder = new AlertDialog.Builder(Modify.this);
            builder.setMessage("Your post will Dismiss")
                    .setNegativeButton("Cancel",null)
                    .setPositiveButton("Dismiss Post", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Modify.super.onBackPressed();
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else{
            Modify.super.onBackPressed();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
                mProfile.setImageURI(mImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    private void ApplayChanges(){
        mProgress.setMessage("Saving Changes ...");
        final String title_val = mPhName.getText().toString().trim();
        final String desc_val = mPhDetails.getText().toString().trim();
        if((!TextUtils.isEmpty(title_val)) && (!TextUtils.isEmpty(desc_val))) {
            mProgress.show();
            StorageReference filepath = mStorage.child("Admin_Profile_pic").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    DatabaseReference newPost = mDataBase.push();
                    newPost.child("title").setValue(title_val);
                    newPost.child("description").setValue(desc_val);
                    newPost.child("image").setValue(downloadUrl.toString());
                    mProgress.dismiss();
                    startActivity(new Intent(Modify.this, MainActivity.class));
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            });
        }
    }
}
