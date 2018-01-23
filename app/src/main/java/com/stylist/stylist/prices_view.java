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

public class prices_view extends Activity {
    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Prices");
        mDatabase.keepSynced(true);
        mBlogList = (RecyclerView) findViewById(R.id.prices_list);
        mBlogList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(prices_view.this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mBlogList.setLayoutManager(layoutManager);
        //mBlogList.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<offer, prices_view.BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<offer, prices_view.BlogViewHolder>(
                offer.class,
                R.layout.session_row,
                prices_view.BlogViewHolder.class,
                mDatabase
        ){
            @Override
            protected void populateViewHolder(prices_view.BlogViewHolder viewHolder, offer model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setDetails(model.getDetails());
                viewHolder.setPrice(model.getPrice());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());
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
        public void setName(String name){
            TextView name_post = (TextView) mView.findViewById(R.id.SessionTitle);
            name_post.setText(name);
        }
        public void setDetails(String details){
            TextView desc_post = (TextView) mView.findViewById(R.id.SessionDetails);
            desc_post.setText(details);
        }
        public void setPrice(String price){
            TextView desc_post = (TextView) mView.findViewById(R.id.SessionPrice);
            desc_post.setText(price);
        }
        public void setNote(String note){
            TextView desc_post = (TextView) mView.findViewById(R.id.SessionNote);
            desc_post.setText(note);
        }
        public void setDate(String date) {
            TextView offer_date = (TextView) mView.findViewById(R.id.OfferTime);
            offer_date.setText(date);
        }
        public void setImage(final Context ctx, final String image){
            final ImageView image_post = (ImageView) mView.findViewById(R.id.SessionImage);
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
