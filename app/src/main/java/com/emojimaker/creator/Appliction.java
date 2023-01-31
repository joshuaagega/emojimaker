package com.emojimaker.creator;

import androidx.multidex.MultiDexApplication;

import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;

public class Appliction extends MultiDexApplication {

    @Override
    public void onCreate() {

        // Make sure to set the mediation provider value to "max" to ensure proper functionality
        AppLovinSdk.getInstance( Appliction.this ).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( Appliction.this, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
            {
                // AppLovin SDK is initialized, start loading ads
            }
        } );
        super.onCreate();


    }
}
