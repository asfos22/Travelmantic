/**
 * Author: Asante Foster
 * Email: asantefoster22@gmail.com
 * Phone:+2332459644406
 */

package com.andela.travelmantic;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    /**
     * 1000 sec delays
     **/
    private final int SPLASH_FOR_1000_SEC = 1000;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.splash_activity);

        /**  handler close SplashActivity after 1000 seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /** Creation of main activity intent . */
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, SPLASH_FOR_1000_SEC);
    }
}
