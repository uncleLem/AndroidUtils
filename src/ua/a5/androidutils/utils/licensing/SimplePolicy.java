package ua.a5.androidutils.utils.licensing;

import android.content.Context;
import android.provider.Settings;
import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.ServerManagedPolicy;

/**
 * This is policy for LicensedActivity - simplified version of default ServerManagedPolicy.
 * It is strongly recommended to change SALT.
 * @author Sergey Khokhlov
 */
public class SimplePolicy extends ServerManagedPolicy {
    private static final byte[] SALT = "a5.ua.androidutils".getBytes();


    /**
     * @param context The context for the current application
     */
    public SimplePolicy(Context context) {
        super(context, new AESObfuscator(SALT, context.getPackageName(), getDeviceID(context)));
    }

    private static String getDeviceID(Context context) {
        // Try to use more data here. ANDROID_ID is a single point of attack.
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
