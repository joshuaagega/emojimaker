package com.emojimaker.creator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.core.app.ActivityCompat;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.emojimaker.creator.dialog.DialogLoading;
import com.emojimaker.creator.librate.FeedbackDialog;
import com.emojimaker.creator.librate.RatingDialog;
import com.emojimaker.creator.ultis.Constant;
import com.emojimaker.creator.ultis.PermissionManager;
import com.emojimaker.creator.ultis.UltilsMethod;

import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity implements View.OnClickListener, MaxAdListener, MaxAdViewAdListener {
    public static DialogLoading dialogLoading;
    private FeedbackDialog feedbackDialog;
    private LinearLayout layoutContrainButton;

    private RatingDialog mRatingDialog;
    private SharedPreferences sharedPreferences;
    private UltilsMethod ultilsMethod;
    private MaxInterstitialAd interstitialAd;
    private int retryAttempt;

    private MaxAdView adView;
    void createBannerAd()
    {
        adView = new MaxAdView( "35fc3e043ebfaca7", this );
        adView.setListener( this );

        // Stretch to the width of the screen for banners to be fully functional
        int width = ViewGroup.LayoutParams.MATCH_PARENT;

        // Banner height on phones and tablets is 50 and 90, respectively
        int heightPx = getResources().getDimensionPixelSize( R.dimen.banner_height );

        adView.setLayoutParams( new FrameLayout.LayoutParams( width, heightPx ) );

        // Set background or background color for banners to be fully functional
        adView.setBackgroundColor(Color.TRANSPARENT );

        ViewGroup rootView = findViewById( android.R.id.content );
        rootView.addView( adView );

        // Load the ad
        adView.loadAd();
        createBannerAd();
    }

    private void findViews() {
        findViewById(R.id.btnEmojiMacker).setOnClickListener(this);
        findViewById(R.id.btnMyEmoji).setOnClickListener(this);
        findViewById(R.id.imgTextInEmoji).setOnClickListener(this);

        if (!PermissionManager.getIntance().hasReadExternal(MainActivity.this) || !PermissionManager.getIntance().hasWriteExternal(MainActivity.this) || !PermissionManager.getIntance().hasCamera(MainActivity.this)) {
            MainActivity.this.requestPermission();
        }

        this.layoutContrainButton = (LinearLayout) findViewById(R.id.layoutContrainButton);
        this.sharedPreferences = getSharedPreferences(Constant.NAME_SHAREDPREFERENCES, 0);
        this.ultilsMethod = new UltilsMethod(this);
        this.mRatingDialog = new RatingDialog(this);
        this.mRatingDialog.setRatingDialogListener(new RatingDialog.RatingDialogInterFace() {
            @Override
            public void maybe() {
            }

            @Override
            public void onDismiss() {
            }

            @Override
            public void onRatingChanged(float f) {
            }

            @Override
            public void onSubmit(float f, boolean z) {
                if (f <= 3.0f) {
                    MainActivity.this.feedbackDialog.showDialog(false, MainActivity.this);
                    return;
                }
                MainActivity.this.ultilsMethod.rateApp(MainActivity.this);
                SharedPreferences.Editor edit = MainActivity.this.sharedPreferences.edit();
                edit.putBoolean(Constant.RATE_APP, true);
                edit.apply();
            }
        });
        this.feedbackDialog = new FeedbackDialog(this);
        this.feedbackDialog.setFeedbackDialogListener(new FeedbackDialog.FeedbackDialogInterFace() {
            @Override
            public void onDismiss() {
            }

            @Override
            public void onSubmit(String str, boolean z) {
                if (!str.equalsIgnoreCase("")) {
                    MainActivity.this.ultilsMethod.sendFeedback(MainActivity.this, str);
                }
            }
        });
        findViewById(R.id.gifRate).setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_main);
        findViews();
        this.layoutContrainButton.getLayoutParams().height = ((getResources().getDisplayMetrics().widthPixels / 2) * 211) / 316;

        createInterstitialAd();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEmojiMacker:
                dialogLoading = new DialogLoading(this, "Loading...");
                dialogLoading.show();
                startActivity(new Intent(this, ActivityEmojiMaker.class));
                if ( interstitialAd.isReady() )
                {
                    interstitialAd.showAd();
                }
                return;
            case R.id.btnMyEmoji:
                startActivity(new Intent(this, ActivityAlbum.class));
                if ( interstitialAd.isReady() )
                {
                    interstitialAd.showAd();
                }
                return;
            case R.id.gifRate:
                this.mRatingDialog.showDialog(false, this);
                if ( interstitialAd.isReady() )
                {
                    interstitialAd.showAd();
                }
                return;

            case R.id.imgTextInEmoji:
                startActivity(new Intent(this, EmojiShopActivity.class));
                if ( interstitialAd.isReady() )
                {
                    interstitialAd.showAd();
                }
                return;
            default:
                return;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences.Editor edit = this.sharedPreferences.edit();
        edit.putBoolean(Constant.FROM_SMILEYS, false);
        edit.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!PermissionManager.getIntance().hasCamera(this) && ActivityCompat.checkSelfPermission(this, "android.permission.CAMERA") != 0) {
                requestPermissions(new String[]{"android.permission.CAMERA"}, 1);
            }
            if (!PermissionManager.getIntance().hasWriteExternal(this) && ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 2);
            }
            if (!PermissionManager.getIntance().hasReadExternal(this) && ActivityCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != 0) {
                requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 3);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (iArr.length == 1 && iArr[0] != 0) {
            super.onRequestPermissionsResult(i, strArr, iArr);
        }
    }
    void createInterstitialAd()
    {
        interstitialAd = new MaxInterstitialAd( "1207a2f2caaf1728", this );
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
        // Interstitial ad failed to load
        // AppLovin recommends that you retry with exponentially higher delays up to a maximum delay (in this case 64 seconds)

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
