package io.github.unclelem.androidutils.utils.social;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.neovisionaries.android.twitter.TwitterOAuthView;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;

/**
 * Call configure method to (re)set Twitter constants and then you can use sendTweet method for tweeting
 *
 * @author Sergey Khokhlov
 */
public class TwitterSender {

    private static AccessToken accessToken;
    private static TwitterOAuthView twitterOAuthView;
    private static AlertDialog twitterDialog;

    private static String CONSUMER_KEY = "your_consumer_key";
    private static String CONSUMER_SECRET = "your_consumer_secret";
    private static String CALLBACK_URL = "http://any.url";
    private static final boolean DUMMY_CALLBACK_URL = false;

    private static boolean configured = false;


    public static void configure(String consumerKey, String consumerSecret, String callbackUrl) {
        CONSUMER_KEY = consumerKey;
        CONSUMER_SECRET = consumerSecret;
        CALLBACK_URL = callbackUrl;
        configured = true;
    }

    public static void sendTweet(Context context, String message, File file,
                                 SenderCallback callback) throws Exception {

        if (!configured) {
            throw new Exception("You have to call configure(consumerKey, consumerSecret, callbackUrl) first!");
        }

        if (accessToken == null) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            twitterOAuthView = new TwitterOAuthView(context);

            LinearLayout mContent = new LinearLayout(context);
            mContent.setOrientation(LinearLayout.VERTICAL);
            mContent.addView(twitterOAuthView);
            EditText crutch = new EditText(context);
            crutch.setVisibility(View.GONE);
            mContent.addView(crutch);

            alertBuilder.setView(mContent);
            twitterDialog = alertBuilder.create();
            twitterDialog.show();
            twitterOAuthView.start(CONSUMER_KEY, CONSUMER_SECRET, CALLBACK_URL, DUMMY_CALLBACK_URL,
                    new Listener(message, file, callback));
        } else {
            postTweet(message, file, callback);
        }
    }

    private static class Listener implements TwitterOAuthView.Listener {
        private String message;
        private File file;
        private SenderCallback callback;

        public Listener(String message, File file, SenderCallback callback) {
            this.message = message;
            this.file = file;
            this.callback = callback;
        }

        @Override
        public void onSuccess(TwitterOAuthView view, AccessToken accessToken) {
            TwitterSender.accessToken = accessToken;
            twitterDialog.hide();
            TwitterSender.accessToken = accessToken;
            postTweet(message, file, callback);
        }

        @Override
        public void onFailure(TwitterOAuthView view, TwitterOAuthView.Result result) {
            callback.onSendingFailed();
            twitterDialog.hide();
        }
    }

    private static class PostTweet extends AsyncTask<Void, Void, Void> {
        private File file;
        private AccessToken accessToken;
        private String message;
        private SenderCallback callback;

        public PostTweet(String message, File file,
                         SenderCallback callback,
                         AccessToken accessToken) {
            this.message = message;
            this.file = file;
            this.callback = callback;
            this.accessToken = accessToken;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            callback.onSendingStarted();

            ConfigurationBuilder config = new ConfigurationBuilder();
            config.setOAuthConsumerKey(CONSUMER_KEY);
            config.setOAuthConsumerSecret(CONSUMER_SECRET);
            Twitter twitter = new TwitterFactory(config.build()).getInstance(this.accessToken);
            StatusUpdate status = new StatusUpdate(message);
            if (file != null) {
                status.setMedia(file);
            }
            try {
                twitter.updateStatus(status);
                callback.onSendingSuccess();
            } catch (TwitterException e) {
                e.printStackTrace();
                callback.onSendingFailed();
            } finally {
                callback.onSendingFinished();
            }
            return null;
        }
    }

    private static void postTweet(String message, File file, SenderCallback callback) {
        new PostTweet(message, file, callback, accessToken).execute();
    }
}
