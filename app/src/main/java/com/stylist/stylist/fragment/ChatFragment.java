package com.stylist.stylist.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.stylist.stylist.Blog;
import com.stylist.stylist.NewPrice;
import com.stylist.stylist.R;
import com.stylist.stylist.offer;
import com.stylist.stylist.posts_view;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Ahmed Magdy on 8/24/2017.
 */

public class ChatFragment extends BaseFragment {
    ImageButton add_but , delete_session;
    private RecyclerView mBlogList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    FirebaseUser user;
    public static ChatFragment create(){
        return new ChatFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_chat;
    }

    @Override
    public void inOnCreateView(View root, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Prices");
        mDatabase.keepSynced(true);
        mBlogList = (RecyclerView) root.findViewById(R.id.prices_list);
        mBlogList.setHasFixedSize(true);
        user = FirebaseAuth.getInstance().getCurrentUser();
        add_but = (ImageButton) root.findViewById(R.id.add_price);
        delete_session = (ImageButton) root.findViewById(R.id.delete_session);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mBlogList.setLayoutManager(layoutManager);
        //mBlogList.setLayoutManager(new LinearLayoutManager(getActivity()));
        if( user != null )
        {
            String user_email = user.getEmail();
            if(user_email.equals("admin@admin.com"))
            {
                add_but.setVisibility(View.VISIBLE);
                add_but.setEnabled(true);
            }
            else{
                add_but.setVisibility(View.INVISIBLE);
                add_but.setEnabled(false);
            }

        }
        add_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NewPrice.class));
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<offer, ChatFragment.BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<offer, ChatFragment.BlogViewHolder>(
                offer.class,
                R.layout.session_row,
                ChatFragment.BlogViewHolder.class,
                mDatabase
        ){
            @Override
            protected void populateViewHolder(final ChatFragment.BlogViewHolder viewHolder, offer model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setName(model.getName());
                viewHolder.setDetails(model.getDetails());
                viewHolder.setPrice(model.getPrice());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());
                viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());
                PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(viewHolder.Session);
                photoViewAttacher.update();
                if(!mAuth.getCurrentUser().getEmail().equals("admin@admin.com")){
                    viewHolder.delete_session.setVisibility(View.INVISIBLE);
                    viewHolder.delete_session.setEnabled(false);
                }
                viewHolder.delete_session.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Are you Sure?")
                                .setNegativeButton("Cancel",null)
                                .setPositiveButton("Yes I'm Sure", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        viewHolder.mDatabase.child(post_key).removeValue();
                                    }
                                });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);

    }
    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;
        FirebaseUser user;
        ImageButton add_but , delete_session;
        DatabaseReference  mDatabase;
        FirebaseAuth mAuth;
        ImageView Session;
        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Prices");
            mAuth = FirebaseAuth.getInstance();
            user = FirebaseAuth.getInstance().getCurrentUser();
            add_but = (ImageButton) mView.findViewById(R.id.add_price);
            delete_session = (ImageButton) mView.findViewById(R.id.delete_session);
            Session = (ImageView) mView.findViewById(R.id.SessionImage);
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
