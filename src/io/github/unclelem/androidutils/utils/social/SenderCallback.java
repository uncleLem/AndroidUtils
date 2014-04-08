package io.github.unclelem.androidutils.utils.social;

/**
 * Use this class "as is", if you want to post messages in background
 *
 * Override some of these methods if you want to add interaction with UI.
 * In this case, you have to run this methods in UI thread by using runOnUiThread(Runnable).
 * For example:
 * public void onSendingSuccess() {
 *     runOnUiThread(new Runnable() {
 *         public void run() {
 *             Toast.makeText(this,"Sending finished successfully",30).show();
 *         }
 *     });
 * }
 *
 * @author Sergey Khokhlov
 */
public class SenderCallback {
    /**
     * Sending started. For example, you can show alert with text "Sending, please wait…"
     */
    public void onSendingStarted() {}

    /**
     * Sending finished successfully. For example, you can show toast with text "Sending finished successfully"
     */
    public void onSendingSuccess() {}

    /**
     * Sending failed.  For example, you can show toast with text "Sending failed"
     */
    public void onSendingFailed() {}

    /**
     * Sending finished. For example, you can hide alert, shown in onSendingStarted()
     */
    public void onSendingFinished() {}
}
