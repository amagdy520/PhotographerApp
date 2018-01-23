package com.stylist.stylist;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.stylist.stylist.adapter.MainPagerAdapter;

public class MainActivity extends AppCompatActivity {

    ImageButton FaceBook , Twitter , Instagram , WhatsApp , Phone , Prices , Info ,Login;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final View background = findViewById(R.id.am_background_view);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.am_view_pager);
        FaceBook = (ImageButton) findViewById(R.id.facebook);
        Twitter = (ImageButton) findViewById(R.id.twitter);
        Instagram = (ImageButton) findViewById(R.id.instagram);
        WhatsApp = (ImageButton) findViewById(R.id.whats);
        Phone = (ImageButton) findViewById(R.id.phone);
        Prices = (ImageButton) findViewById(R.id.prices);
        Info = (ImageButton) findViewById(R.id.info);
        Login = (ImageButton) findViewById(R.id.login);
        String check = getIntent().getStringExtra("1");
        if(check.equals("Admin")){
            Login.setImageDrawable(
                    ContextCompat.getDrawable(getApplicationContext(), R.drawable.openlock));
        }else{
            Login.setImageDrawable(
                    ContextCompat.getDrawable(getApplicationContext(), R.drawable.openlock));
        }
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        final int colorBlue = ContextCompat.getColor(this,R.color.colorPrimary);
        final int colorPurple = ContextCompat.getColor(this,R.color.purple);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if(position == 0){
                    background.setBackgroundColor(colorBlue);
                    background.setAlpha(1-positionOffset);
                }
                else if(position == 1){
                    background.setBackgroundColor(colorPurple);
                    background.setAlpha(positionOffset);
                }

            }
            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        FaceBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = openFacebook(MainActivity.this);
                startActivity(it);
            }
        });

        Twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String twitter_user_name = "amagdy520";
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + twitter_user_name)));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + twitter_user_name)));
                }

            }
        });

        Instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://instagram.com/_u/abdohawilaphotography");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/abdohawilaphotography")));
                }
            }
        });

        WhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
                if (isWhatsappInstalled) {
                    Uri ui = Uri.parse("smsto:01007200017");
                    Intent sendIntent = new Intent(Intent.ACTION_SENDTO, ui);
                    sendIntent.setPackage("com.whatsapp");
                    startActivity(Intent.createChooser(sendIntent, ""));
                } else {
                    Toast.makeText(MainActivity.this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
                    Uri ur = Uri.parse("market://details?id=com.whatsapp");
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, ur);
                    startActivity(goToMarket);
                }
            }
        });

        Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "tel:01007200017";
                Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(call);
            }
        });

        Prices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
            }
        });

        Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(2);
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login.setImageResource(R.drawable.lock);
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }
    public static Intent openFacebook(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/1566035656974503"));

        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/AbdoHawilaphotography"));
        }

    }
    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
}
