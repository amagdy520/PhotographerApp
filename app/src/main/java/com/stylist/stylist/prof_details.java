package com.stylist.stylist;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by Ahmed Magdy on 8/28/2017.
 */

public class prof_details extends Activity {

    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_story);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Admin_Profile");
        mDatabase.keepSynced(true);
        mBlogList = (RecyclerView) findViewById(R.id.profile_list);
        mBlogList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(prof_details.this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mBlogList.setLayoutManager(layoutManager);
        //mBlogList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<prof, prof_details.BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<prof, prof_details.BlogViewHolder>(
                prof.class,
                R.layout.prof_row,
                prof_details.BlogViewHolder.class,
                mDatabase
        ){
            @Override
            protected void populateViewHolder(prof_details.BlogViewHolder viewHolder, prof model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(getApplicationContext(), model.getImage());
            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setTitle(String title){
            TextView title_post = (TextView) mView.findViewById(R.id.ph_name);
            title_post.setText(title);
        }
        public void setDescription(String description){
            TextView desc_post = (TextView) mView.findViewById(R.id.ph_details);
            desc_post.setText(description);
        }
        public void setImage(final Context ctx, final String image){
            final ImageView image_post = (ImageView) mView.findViewById(R.id.add_profile);
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
