package ua.a5.androidutils.utils.social;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * @author Sergey Khokhlov
 */
public class EmailSender {
    public static void send(Context context, String subject, String message, File fileToSend) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, message);
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + fileToSend.getAbsolutePath()));
            context.startActivity(Intent.createChooser(emailIntent, "e-mail"));
    }
}
