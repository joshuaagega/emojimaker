package com.emojimaker.creator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.emojimaker.creator.adapter.AdapterPreViewAlbum;
import com.emojimaker.creator.adapter.PhotoShowPager;
import com.emojimaker.creator.item.ItemPhoto;
import com.emojimaker.creator.ultis.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class ActivityShowAlbum extends AppCompatActivity implements View.OnClickListener, MaxAdListener, MaxAdViewAdListener {
    private AdapterPreViewAlbum adapterPreViewAlbum;
    private ArrayList<ItemPhoto> arrPhoto;
    PhotoShowPager photoShowPager;
    private int pos;
    private RecyclerView rvPreview;
    private TextView textViewTitle;
    private ViewPager viewPager;
    private MaxInterstitialAd interstitialAd;
    private int retryAttempt;

//    private MaxAdView adView;
//    void createBannerAd()
//    {
//        adView = new MaxAdView( "02525f2102dcb937", this );
//        adView.setListener( this );
//
//        // Stretch to the width of the screen for banners to be fully functional
//        int width = ViewGroup.LayoutParams.MATCH_PARENT;
//
//        // Banner height on phones and tablets is 50 and 90, respectively
//        int heightPx = getResources().getDimensionPixelSize( R.dimen.banner_height );
//
//        adView.setLayoutParams( new FrameLayout.LayoutParams( width, heightPx ) );
//
//        // Set background or background color for banners to be fully functional
//        adView.setBackgroundColor(Color.TRANSPARENT);
//
//        ViewGroup rootView = findViewById( android.R.id.content );
//        rootView.addView( adView );
//
//        // Load the ad
//        adView.loadAd();
//    }


    @Override

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        supportRequestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_show_album);




        Intent intent = getIntent();
        if (intent != null) {
            this.arrPhoto = (ArrayList) intent.getSerializableExtra(Constant.ARR);
            this.pos = intent.getIntExtra(Constant.POS, 0);
            if (this.arrPhoto == null) {
                finish();
                return;
            }
            this.arrPhoto.get(this.pos).setChoose(true);
            initView();
            return;
        }
        finish();
//        createBannerAd();
    }

    private void initView() {
        findViewById(R.id.imgDelete).setOnClickListener(this);
        findViewById(R.id.imgShare).setOnClickListener(this);
        this.textViewTitle = (TextView) findViewById(R.id.textTitle);
        this.viewPager = (ViewPager) findViewById(R.id.homepage_card_view_pager);
        this.photoShowPager = new PhotoShowPager(this, this.arrPhoto);
        this.viewPager.setAdapter(this.photoShowPager);
        this.viewPager.setCurrentItem(this.pos);
        this.viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityShowAlbum.this.rvPreview.getVisibility() == View.GONE) {
                    ActivityShowAlbum.this.rvPreview.setVisibility(View.VISIBLE);
                } else {
                    ActivityShowAlbum.this.rvPreview.setVisibility(View.GONE);
                }
                if ( interstitialAd.isReady() )
                {
                    interstitialAd.showAd();
                }
            }

        });
        this.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int i) {
            }

            @Override
            public void onPageScrolled(int i, float f, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                ActivityShowAlbum.this.changeImageChoose(i);
            }
        });
        this.rvPreview = (RecyclerView) findViewById(R.id.rv_show_album);
        this.rvPreview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.adapterPreViewAlbum = new AdapterPreViewAlbum(new AdapterPreViewAlbum.PreViewCallBack() {
            @Override
            public void callBack(int i) {
                ActivityShowAlbum.this.changeImageChoose(i);
            }
        }, this.arrPhoto, this);
        this.rvPreview.setAdapter(this.adapterPreViewAlbum);
        TextView textView = this.textViewTitle;
        textView.setText((this.viewPager.getCurrentItem() + 1) + " / " + this.arrPhoto.size());
    }


    public void changeImageChoose(int i) {
        int i2 = 0;
        while (true) {
            if (i2 >= this.arrPhoto.size()) {
                break;
            } else if (this.arrPhoto.get(i2).isChoose()) {
                this.arrPhoto.get(i2).setChoose(false);
                break;
            } else {
                i2++;
            }
        }
        this.arrPhoto.get(i).setChoose(true);
        this.viewPager.setCurrentItem(i);
        this.adapterPreViewAlbum.notifyDataSetChanged();
        this.textViewTitle.setText((this.viewPager.getCurrentItem() + 1) + " / " + this.arrPhoto.size());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imgDelete) {
            showConfirmRemoveEmoji(this.viewPager.getCurrentItem());
        } else if (id == R.id.imgShare) {
            sharePhoto(this.viewPager.getCurrentItem());
        }
        if ( interstitialAd.isReady() )
        {
            interstitialAd.showAd();
        }
    }

    void showConfirmRemoveEmoji(final int i) {
        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.app_name)).setMessage(R.string.do_you_want_delete).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i2) {
                if (new File(((ItemPhoto) ActivityShowAlbum.this.arrPhoto.get(i)).getLink()).delete()) {
                    ActivityShowAlbum.this.arrPhoto.remove(i);
                    ActivityShowAlbum.this.photoShowPager.notifyDataSetChanged();
                    ActivityShowAlbum.this.adapterPreViewAlbum.notifyDataSetChanged();
                    Toast.makeText(ActivityShowAlbum.this, "Emoji removed", Toast.LENGTH_SHORT).show();
                    if (ActivityShowAlbum.this.arrPhoto.size() == 0) {
                        ActivityShowAlbum.this.finish();
                    }
                }
                if ( interstitialAd.isReady() )
                {
                    interstitialAd.showAd();
                }
                dialogInterface.dismiss();
            }
        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i2) {
                dialogInterface.dismiss();
            }
        }).create().show();
        if ( interstitialAd.isReady() )
        {
            interstitialAd.showAd();
        }
    }

    void sharePhoto(int i) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("image/*");
        intent.putExtra("android.intent.extra.SUBJECT", "Emoji maker for phone");
        intent.putExtra("android.intent.extra.TEXT", "My emoji created");
        Uri uriForFile = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", new File(this.arrPhoto.get(i).getLink()));
        intent.putExtra("android.intent.extra.STREAM", uriForFile);
        startActivity(Intent.createChooser(intent, "Share this photo"));
        intent.putExtra("android.intent.extra.STREAM", uriForFile);
        startActivity(Intent.createChooser(intent, "Share"));
    }
    void createInterstitialAd()
    {
        interstitialAd = new MaxInterstitialAd( "6d34ab0494a6345e", this );
        interstitialAd.setListener( this );

        // Load the first ad
        interstitialAd.loadAd();
    }


    @Override
    public void onAdLoaded(MaxAd ad) {
        retryAttempt = 0;

    }

    @Override
    public void onAdDisplayed(MaxAd ad) {

    }

    @Override
    public void onAdHidden(MaxAd ad) {
        interstitialAd.loadAd();

    }

    @Override
    public void onAdClicked(MaxAd ad) {

    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {

        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis( (long) Math.pow( 2, Math.min( 6, retryAttempt ) ) );

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                interstitialAd.loadAd();
            }
        }, delayMillis );
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        interstitialAd.loadAd();

    }

    @Override
    public void onAdExpanded(MaxAd ad) {

    }

    @Override
    public void onAdCollapsed(MaxAd ad) {

    }
}
