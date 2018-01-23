package com.stylist.stylist.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.stylist.stylist.LoginActivity;
import com.stylist.stylist.MainActivity;
import com.stylist.stylist.Modify;
import com.stylist.stylist.R;
import com.stylist.stylist.posts_view;
import com.stylist.stylist.prof;
import com.stylist.stylist.prof_details;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Ahmed Magdy on 8/24/2017.
 */

public class StoryFragment extends BaseFragment {

    FirebaseUser user;
    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    ImageButton mModify , mDeleteProfile ;
    public static StoryFragment create(){
        return new StoryFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_story;
    }

    @Override
    public void inOnCreateView(View root, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Admin_Profile");
        mDatabase.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mBlogList = (RecyclerView) root.findViewById(R.id.profile_list);
        mBlogList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mBlogList.setLayoutManager(layoutManager);
        //mBlogList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mModify = (ImageButton) root.findViewById(R.id.modify);
        mDeleteProfile = (ImageButton) root.findViewById(R.id.delete_prof);
        if(  user != null )
        {
            String user_email = user.getEmail();
            if(user_email.equals("admin@admin.com"))
            {
                mModify.setVisibility(View.VISIBLE);
                mModify.setEnabled(true);
            }
            else{
                mModify.setVisibility(View.INVISIBLE);
                mModify.setEnabled(false);
            }

        }

        mModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Modify.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<prof, StoryFragment.BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<prof, StoryFragment.BlogViewHolder>(
                prof.class,
                R.layout.prof_row,
                StoryFragment.BlogViewHolder.class,
                mDatabase
        ){
            @Override
            protected void populateViewHolder(final StoryFragment.BlogViewHolder viewHolder, prof model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(getActivity(), model.getImage());
                PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(viewHolder.profile);
                photoViewAttacher.update();
                if(!mAuth.getCurrentUser().getEmail().equals("admin@admin.com")){
                    viewHolder.mDeleteProfile.setVisibility(View.INVISIBLE);
                    viewHolder.mDeleteProfile.setEnabled(false);
                }
                viewHolder.mDeleteProfile.setOnClickListener(new View.OnClickListener() {
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
        ImageButton mDeleteProfile ;
        FirebaseUser user;
        DatabaseReference mDatabase;
        ImageView profile;
        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mDeleteProfile = (ImageButton) mView.findViewById(R.id.delete_prof);
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Admin_Profile");
            mDatabase.keepSynced(true);
            profile = (ImageView) mView.findViewById(R.id.add_profile);
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
