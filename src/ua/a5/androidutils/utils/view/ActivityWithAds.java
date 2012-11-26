package ua.a5.androidutils.utils.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import ua.a5.androidutils.R;

/**
 * Add INTERNET permissions to your AndroidManifest.xml and add this code in <application> scope:
 *     <activity android:name="com.google.ads.AdActivity"
 *         android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|uiMode|screenSize|smallestScreenSize"
 *     />
 * in /res/layout/ac_parent_layout_with_ads.xml set ads:adUnitId to publisherID of your Ads provider
 * Also you may want to use another Layout or change Ad position (by default, it's LinearLayout and Ad is in bottom of screen)
 * @author Sergey Khokhlov
 */
public class ActivityWithAds extends Activity {
    private AdView adView;
    private FrameLayout pageContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.ac_parent_layout_with_ads);

        this.pageContent = (FrameLayout) findViewById(R.id.page_content);

        // admob
        adView = (AdView) this.findViewById(R.id.adView);
        final AdRequest adRequest = new AdRequest();
        adView.setAdListener(new AdListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                Log.d("##", "onReceiveAd");
            }

            @Override
            public void onFailedToReceiveAd(Ad ad, AdRequest.ErrorCode errorCode) {
                Log.d("##", "onFailedToReceiveAd");
            }

            @Override
            public void onPresentScreen(Ad ad) {
                Log.d("##", "onPresentScreen");
            }

            @Override
            public void onDismissScreen(Ad ad) {
                Log.d("##", "onDismissScreen");
            }

            @Override
            public void onLeaveApplication(Ad ad) {
                Log.d("##", "onLeaveApplication");
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adView.loadAd(adRequest);
                adView.invalidate();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adView != null) {
            adView.destroy();
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        View toAdd = getLayoutInflater().inflate(layoutResID, null);
        pageContent.removeAllViews();
        pageContent.addView(toAdd);
    }
}
