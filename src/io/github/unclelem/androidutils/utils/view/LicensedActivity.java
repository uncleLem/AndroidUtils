package io.github.unclelem.androidutils.utils.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.Policy;
import ua.a5.androidutils.R;
import io.github.unclelem.androidutils.utils.licensing.SimplePolicy;

/**
 * 1) Add INTERNET and CHECK_LICENSE permissions to your AndroidManifest.xml
 * 2) Call setBase64PublicKey(String) in onCreate() method to set public key and implement methods for UI interaction
 * 3) Change salt in SimplePolicy class
 * See values/string.xml for messages, used in this class.
 * @author Sergey Khokhlov
 */
public abstract class LicensedActivity extends Activity {
    private static String BASE64_PUBLIC_KEY;

    private LicenseCheckerCallback mLicenseCheckerCallback;
    private LicenseChecker mChecker;

    protected Handler mHandler = new Handler();

    protected abstract void updateUIonCheckStarted(String result);

    protected abstract void updateUIonAllow(String msg);

    protected abstract void updateUIonDeny(String msg, boolean b);

    protected abstract void updateUIonError(String msg);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }

    protected void setBase64PublicKey(String key) {
        BASE64_PUBLIC_KEY = key;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Library calls this when it's done.
        mLicenseCheckerCallback = new MyLicenseCheckerCallback();
        // Construct the LicenseChecker with a policy.
        mChecker = new LicenseChecker(
                this, new SimplePolicy(this),
                BASE64_PUBLIC_KEY);
        doCheck();
    }

    private void doCheck() {
        setProgressBarIndeterminateVisibility(true);
        updateUIonCheckStarted(getString(R.string.checking_license));
        mChecker.checkAccess(mLicenseCheckerCallback);
    }

    protected Dialog onCreateDialog(int id) {
        final boolean bRetry = id == 1;
        return new AlertDialog.Builder(this)
                .setTitle(R.string.unlicensed_dialog_title)
                .setCancelable(false)
                .setMessage(bRetry ? R.string.unlicensed_dialog_retry_body : R.string.unlicensed_dialog_body)
                .setPositiveButton(bRetry ? R.string.retry_button : R.string.buy_button, new DialogInterface.OnClickListener() {
                    boolean mRetry = bRetry;

                    public void onClick(DialogInterface dialog, int which) {
                        if (mRetry) {
                            doCheck();
                        } else {
                            Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                    "http://market.android.com/details?id=" + getPackageName()));
                            startActivity(marketIntent);
                        }
                    }
                })
                .setNegativeButton(R.string.quit_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create();
    }

    private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
        public void allow(int policyReason) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            // Should allow user access.
            updateUIonAllow(getString(R.string.allow));
        }

        public void dontAllow(int policyReason) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }

            // Should not allow access. In most cases, the app should assume
            // the user has access unless it encounters this. If it does,
            // the app should inform the user of their unlicensed ways
            // and then either shut down the app or limit the user to a
            // restricted set of features.
            // In this example, we show a dialog that takes the user to Market.
            // If the reason for the lack of license is that the service is
            // unavailable or there is another problem, we display a
            // retry button on the dialog and a different message.
            updateUIonDeny(getString(R.string.dont_allow), policyReason == Policy.RETRY);
        }

        public void applicationError(int errorCode) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            // This is a polite way of saying the developer made a mistake
            // while setting up or calling the license checker library.
            // Please examine the error code and fix the error.
            String result;
            switch (errorCode) {
                case LicenseCheckerCallback.ERROR_INVALID_PACKAGE_NAME:
                    result = getString(R.string.error_invalid_package_name);
                    break;
                case LicenseCheckerCallback.ERROR_NON_MATCHING_UID:
                    result = getString(R.string.error_non_matching_uid);
                    break;
                case LicenseCheckerCallback.ERROR_NOT_MARKET_MANAGED:
                    result = getString(R.string.error_not_market_managed);
                    break;
                case LicenseCheckerCallback.ERROR_CHECK_IN_PROGRESS:
                    result = getString(R.string.error_check_in_progress);
                    break;
                case LicenseCheckerCallback.ERROR_INVALID_PUBLIC_KEY:
                    result = getString(R.string.error_invalid_public_key);
                    break;
                case LicenseCheckerCallback.ERROR_MISSING_PERMISSION:
                    result = getString(R.string.error_missing_permissions);
                    break;
                default:
                    result = String.format(getString(R.string.application_error), errorCode);
            }
            updateUIonError(result);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChecker.onDestroy();
    }
}
