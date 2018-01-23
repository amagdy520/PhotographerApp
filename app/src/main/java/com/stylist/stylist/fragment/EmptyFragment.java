package com.stylist.stylist.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.stylist.stylist.Blog;
import com.stylist.stylist.NewPost;
import com.stylist.stylist.NewPrice;
import com.stylist.stylist.R;

import java.util.Calendar;
import java.util.Date;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Ahmed Magdy on 8/24/2017.
 */

public class EmptyFragment extends BaseFragment {

    FirebaseUser user;
    private RecyclerView mBlogList;
    private DatabaseReference mDatabase , mDatabaseLike ;
    private FirebaseAuth mAuth;
    private boolean mProcess=false;
    private TextView count;
    private ImageButton delete , upload;
    private TextView date;
    public static EmptyFragment create(){
        return new EmptyFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_post;
    }

    @Override
    public void inOnCreateView(View root, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabase.keepSynced(true);
        mDatabaseLike.keepSynced(true);
        count = (TextView)root.findViewById(R.id.count);
        mBlogList = (RecyclerView) root.findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mBlogList.setLayoutManager(layoutManager);
        //mBlogList.setLayoutManager(new LinearLayoutManager(getActivity()));
        user = FirebaseAuth.getInstance().getCurrentUser();
        delete = (ImageButton)root.findViewById(R.id.delete_post);
        upload = (ImageButton)root.findViewById(R.id.upload);
        if( user != null )
        {
            String user_email = user.getEmail();
            if(user_email.equals("admin@admin.com"))
            {
                upload.setVisibility(View.VISIBLE);
                upload.setEnabled(true);
            }
            else{
                upload.setVisibility(View.INVISIBLE);
                upload.setEnabled(false);
            }

        }
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NewPost.class));
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Blog, EmptyFragment.BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, EmptyFragment.BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                EmptyFragment.BlogViewHolder.class,
                mDatabase
        ){
            @Override
            protected void populateViewHolder(final EmptyFragment.BlogViewHolder viewHolder, Blog model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setPost_time(model.getPost_time());
                viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());
                viewHolder.set_like_icon(post_key);
                PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(viewHolder.mShow);
                photoViewAttacher.update();
                if(!mAuth.getCurrentUser().getEmail().equals("admin@admin.com")){
                    viewHolder.delete.setVisibility(View.INVISIBLE);
                    viewHolder.delete.setEnabled(false);
                }
                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Are you Sure?")
                                .setNegativeButton("Cancel",null)
                                .setPositiveButton("Yes I'm Sure", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        viewHolder.mDatabase.child(post_key).removeValue();
                                        viewHolder.mDatabaseLike.child(post_key).removeValue();

                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    }
                });
                viewHolder.mDatabaseLike.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.count.setText((String.valueOf(dataSnapshot.child(post_key).getChildrenCount()))+" Likes");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                viewHolder.mLikebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProcess = true;

                            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(mProcess) {
                                        if (dataSnapshot.child(post_key).hasChild((mAuth.getCurrentUser().getUid()))) {
                                            mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                            mProcess = false;
                                        } else {
                                            mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("Random");
                                            mProcess = false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                    }
                });
            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageButton mLikebtn ,delete;
        TextView count;
        DatabaseReference mDatabaseLike , mDatabase;
        FirebaseAuth mAuth;
        ImageView mShow;
        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mShow = (ImageView) mView.findViewById(R.id.PostImage);
            delete = (ImageButton)mView.findViewById(R.id.delete_post);
            mLikebtn = (ImageButton) mView.findViewById(R.id.like_but);
            count = (TextView)mView.findViewById(R.id.count);
            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
            mAuth = FirebaseAuth.getInstance();
            mDatabaseLike.keepSynced(true);
        }
        public void set_like_icon(final String post_key){
            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                        mLikebtn.setImageResource(R.drawable.like2);
                    }else{
                        mLikebtn.setImageResource(R.drawable.like);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        public void setTitle(String title){
            TextView title_post = (TextView) mView.findViewById(R.id.PostTitle);
            title_post.setText(title);
        }
        public void setDescription(String description){
            TextView desc_post = (TextView) mView.findViewById(R.id.PostDesc);
            desc_post.setText(description);
        }
        public void setPost_time(String post_time){
            TextView date_post = (TextView) mView.findViewById(R.id.PostTime);
            date_post.setText(post_time);
        }
        public void setImage(final Context ctx, final String image){
            final ImageView image_post = (ImageView) mView.findViewById(R.id.PostImage);
            //Picasso.with(ctx).load(image).into(image_post);
            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(image_post, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError() {
                    Picasso.with(ctx).load(image).into(image_post);
                }
            });
        }
    }
}
