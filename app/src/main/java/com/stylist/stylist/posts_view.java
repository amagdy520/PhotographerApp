package com.stylist.stylist;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

public class posts_view extends Activity {

    private RecyclerView mBlogList;
    private DatabaseReference mDatabase , mDatabaseLike;
    private FirebaseAuth mAuth;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_post);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabase.keepSynced(true);
        mDatabaseLike.keepSynced(true);
        mBlogList = (RecyclerView) findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(posts_view.this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mBlogList.setLayoutManager(layoutManager);
        //mBlogList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mDatabase
        ){
            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, Blog model, int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setPost_time(model.getPost_time());
                viewHolder.setImage(getApplicationContext(), model.getImage());
            }
            };
        mBlogList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageButton mLikebtn;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mLikebtn = (ImageButton) mView.findViewById(R.id.like_but);

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
