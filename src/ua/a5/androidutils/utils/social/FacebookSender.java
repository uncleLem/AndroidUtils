package ua.a5.androidutils.utils.social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;

/**
 * Call configure method to (re)set Facebook constants and then you can use share method for posting.
 * IMPORTANT: you have to call onActivityResultCallback method in onActivityResult of your activity
 * @author Sergey Khokhlov
 */
public class FacebookSender {
    private static Facebook facebook;
    private static long timeToExpire;

    private static boolean configured = false;

    public static void configure(String appId, long msToExpire) {
        facebook = new Facebook(appId);
        timeToExpire = msToExpire;
    }

    public static void share(Activity activity, String message, Bitmap bitmap, SenderCallback callback) throws Exception {

        if (!configured) {
            throw new Exception("You have to call configure(consumerKey, consumerSecret, callbackUrl) first!");
        }

        callback.onSendingStarted();

        /*
        * Get existing access_token if any
        */
        String access_token = activity.getPreferences(Context.MODE_PRIVATE).getString("access_token", null);
        long expires = activity.getPreferences(Context.MODE_PRIVATE).getLong("access_expires", new Date().getTime() + timeToExpire);
        if (access_token != null) {
            facebook.setAccessToken(access_token);
        }
        //if (expires != 0) {
        facebook.setAccessExpires(expires);
        //}

        showFBLoginDialogIfNeededAndDoRequest(activity, message, bitmap, callback);
    }

    // FACEBOOK MANAGEMENT

    /**
     * You have to call this method in onActivityResult() of your activity
     */
    public static void onActivityResultCallback(int requestCode, int resultCode, Intent data) {
        facebook.authorizeCallback(requestCode, resultCode, data);
    }

    private static void showFBLoginDialogIfNeededAndDoRequest(final Activity activity, final String message, final Bitmap bitmap, final SenderCallback callback) {
        /*
        * Only call authorize if the access_token has expired.
        */
        if (!facebook.isSessionValid()) {

            facebook.authorize(activity, new String[]{"email", "publish_stream"}, new Facebook.DialogListener() {

                @Override
                public void onComplete(Bundle values) {
                    SharedPreferences.Editor editor = activity.getPreferences(Context.MODE_PRIVATE).edit();
                    editor.putString("access_token", facebook.getAccessToken());
                    editor.putLong("access_expires", facebook.getAccessExpires());
                    editor.commit();
                    postMessage(message, bitmap, callback);
                }

                @Override
                public void onFacebookError(FacebookError error) {
                    Log.e("FB_FACEBOOK_ERROR", error.getMessage());
                    callback.onSendingFailed();
                    callback.onSendingFinished();
                }

                @Override
                public void onError(DialogError e) {
                    Log.e("FB_ERROR", e.getMessage());
                    callback.onSendingFailed();
                    callback.onSendingFinished();
                }

                @Override
                public void onCancel() {
                    Log.d("FB_CANCEL", "onCancel()");
                    callback.onSendingFailed();
                    callback.onSendingFinished();
                }

            });
        } else {
            postMessage(message, bitmap, callback);
        }
    }

    private static void postMessage(String message, Bitmap bitmap, final SenderCallback callback) {
        final Bundle parameters = new Bundle();

        if (bitmap != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 10, bos);
            byte[] byteArray = bos.toByteArray();
            parameters.putByteArray("picture", byteArray);
        }

        parameters.putString("message", message);

        AsyncFacebookRunner asyncFacebookRunner = new AsyncFacebookRunner(facebook);
        asyncFacebookRunner.request("me/photos", parameters, "POST", new AsyncFacebookRunner.RequestListener() {
            @Override
            public void onMalformedURLException(MalformedURLException e, Object state) {
                Log.v("FB_MALFORMED_URL", "malformed");
                callback.onSendingFailed();
                callback.onSendingFinished();
            }

            @Override
            public void onIOException(IOException e, Object state) {
                Log.e("FB_IO_EXCEPTION", e.getMessage());
                callback.onSendingFailed();
                callback.onSendingFinished();
            }

            @Override
            public void onFileNotFoundException(FileNotFoundException e, Object state) {
                Log.e("FB_FILE_NOT_FOUND", e.getMessage());
                callback.onSendingFailed();
                callback.onSendingFinished();
            }

            @Override
            public void onFacebookError(FacebookError e, Object state) {
                Log.e("FB_FACEBOOK_ERROR", e.getMessage());
                callback.onSendingFailed();
                callback.onSendingFinished();
            }

            @Override
            public void onComplete(String response, Object state) {
                Log.v("FB_COMPLETE", response);
                callback.onSendingSuccess();
                callback.onSendingFinished();
            }
        }, null);
    }
}
