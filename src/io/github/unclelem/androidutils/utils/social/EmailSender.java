package io.github.unclelem.androidutils.utils.social;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * @author Sergey Khokhlov
 */
public class EmailSender {
    public static void send(Context context, String recipient, String subject, String message, File fileToSend) {
        send(context, new String[]{recipient}, subject, message, fileToSend);
    }

    public static void send(Context context, String[] recipients, String subject, String message, File fileToSend) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        if (fileToSend != null) {
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + fileToSend.getAbsolutePath()));
        }
        context.startActivity(Intent.createChooser(emailIntent, "e-mail"));
    }
}
