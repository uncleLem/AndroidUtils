package ua.a5.androidutils.utils.view;

import android.app.Activity;
import android.os.Bundle;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ChartboostDelegate;

/**
 * In order to add promotion to app you have to:
 * 1) add chartboost.jar as library
 * 2) register your app at http://chartboost.com/app/edit
 * 3) in "More Apps Page" section enter your existing and already registered apps names
 * which you want to represent in your More Apps Page
 * 4) if necessary, add created app to others app's "More Apps Page" sections
 * 5) change APP_ID, APP_SIGNATURE and (if necessary) CHARTBOOST_DELEGATE
 * 6) call showMoreApps() to show MoreApps screen
 * 7) you may also want to override onBackPressed method (https://help.chartboost.com/documentation/android)
 *
 * @author Sergey Khokhlov
 */
public class ActivityWithMoreApps extends Activity {
    private Chartboost _cb;
    private static String APP_ID = "APP_ID";
    private static String APP_SIGNATURE = "APP_SIGNATURE";
    private static ChartboostDelegate CHARTBOOST_DELEGATE = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _cb = Chartboost.sharedChartboost();
        _cb.onCreate(this, APP_ID, APP_SIGNATURE, CHARTBOOST_DELEGATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        _cb.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        _cb.onStop(this);
    }

    public void showMoreApps() {
        _cb.showMoreApps();
    }
}
