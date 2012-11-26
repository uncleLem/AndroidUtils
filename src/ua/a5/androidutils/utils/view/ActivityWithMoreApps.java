package ua.a5.androidutils.utils.view;

import android.app.Activity;
import android.os.Bundle;
import com.chartboost.sdk.ChartBoost;
import com.chartboost.sdk.ChartBoostDelegate;

/**
 * In order to add promotion to app you have to:
 * 1) add chartboost.jar as library
 * 2) register your app at http://chartboost.com/app/edit
 * 3) in "More Apps Page" section enter your existing and already registered apps names
 * which you want to represent in your More Apps Page
 * 4) if necessary, add created app to others app's "More Apps Page" sections
 * 5) call setupChartBoost(String, String) in onCreate() method of your activity
 * 6) call showMoreApps() to show More Apps screen
 *
 * @author Sergey Khokhlov
 */
public class ActivityWithMoreApps extends Activity {
    private ChartBoost _cb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _cb = ChartBoost.getSharedChartBoost(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        _cb = ChartBoost.getSharedChartBoost(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void setupChartBoost(String appId, String appSignature, ChartBoostDelegate customDelegate) {
        _cb.setAppId(appId);
        _cb.setAppSignature(appSignature);
        _cb.setDelegate(customDelegate);
        _cb.install();
    }

    public void setupChartBoost(String appId, String appSignature) {
        _cb.setAppId(appId);
        _cb.setAppSignature(appSignature);
        _cb.install();
    }

    public void showMoreApps() {
        _cb.showMoreApps();
    }
}
