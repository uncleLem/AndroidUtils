package io.github.unclelem.androidutils.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.storage.OnObbStateChangeListener;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * For more info about APK Expansion files see http://developer.android.com/google/play/expansion-files.html
 *
 * @author Sergey Khokhlov
 * 
 */
public class ObbExpansionsManager {
    public static String TAG = "ObbExpansions";

    private String packageName;
    private int packageVersion;

    private String main;
    private File mainFile;
    private String patch;
    private File patchFile;

    private StorageManager sm;
    private ObbListener listener;

    private MountChecker mainChecker = new MountChecker(true);

    private static ObbExpansionsManager instance;

    private ObbExpansionsManager(Context context, final ObbListener listener) {
        Log.d(TAG, "Creating new instance...");
        packageName = context.getPackageName();
        Log.d(TAG, "Package name = " + packageName);

        int versionCode = 1;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        packageVersion = versionCode;
        Log.d(TAG, "Package version = " + packageVersion);
        this.listener = listener;
        sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        patchFile = new File(Environment.getExternalStorageDirectory() + "/Android/obb/" + packageName + "/"
                + "patch." + packageVersion + "." + packageName + ".obb");
        mainFile = new File(Environment.getExternalStorageDirectory() + "/Android/obb/" + packageName + "/"
                + "main." + packageVersion + "." + packageName + ".obb");

        Log.d(TAG, "Check if patch file already mounted: " + sm.isObbMounted(patchFile.getAbsolutePath()));
        if (sm.isObbMounted(patchFile.getAbsolutePath())) {
            Log.d(TAG, "Patch file already mounted.");
            patch = sm.getMountedObbPath(patchFile.getAbsolutePath());
        } else {
            mountPatch();
        }

        Log.d(TAG, "Check if main file already mounted: " + sm.isObbMounted(mainFile.getAbsolutePath()));
        if (sm.isObbMounted(mainFile.getAbsolutePath())) {
            Log.d(TAG, "Main file already mounted.");
            main = sm.getMountedObbPath(mainFile.getAbsolutePath());
            listener.onMountSuccess();
        } else {
            mountMain();
        }

        if (!mainFile.exists() && !patchFile.exists()) {
            Log.d(TAG, "No expansion files found!");
            listener.onFilesNotFound();
        }
    }

    private void mountPatch() {
        if (patchFile.exists()) {
            Log.d(TAG, "Mounting patch file...");
            sm.mountObb(patchFile.getAbsolutePath(), null, patchObbStateChangeListener );
            if (sm.isObbMounted(patchFile.getAbsolutePath())) {
                patch = sm.getMountedObbPath(patchFile.getAbsolutePath());
                listener.onMountSuccess();
            }
        } else {
            Log.d(TAG, "Patch file not found");
        }
    }
    
    
    // Making the listener into an instance variable ensures that the reference is not lost 
    // and zeroed in the storage manager before it can be used
    OnObbStateChangeListener patchObbStateChangeListener = new OnObbStateChangeListener() {
            @Override
            public void onObbStateChange(String path, int state) {
                super.onObbStateChange(path, state);
                if (state == MOUNTED) {
                    Log.d(TAG, "Mounting patch file done.");
                    patch = sm.getMountedObbPath(patchFile.getAbsolutePath());
                } else {
                    Log.d(TAG, "Mounting patch file failed with state = " + state);
                    if ( listener != null )
                    	listener.onObbStateChange(path, state);
                }
            }
    };

    private void mountMain() {
        if (mainFile.exists()) {
            Log.d(TAG, "Mounting main file...");
            Log.d(TAG, "Scheduling mount checker...");
            // I have left the mount checker in place but extended it's initial activation to
            // 8 seconds.  If it is clear that this modification is working the mountchecker 
            // can be removed altogether
            (new Timer()).schedule(mainChecker, 8000);
            sm.mountObb(mainFile.getAbsolutePath(), null, mainObbStateChangeListener );
        } else {
            Log.d(TAG, "Patch file not found");
        }
    }

        
    // Making the listener into an instance variable ensures that the reference is not lost 
    // and zeroed in the storage manager before it can be used..
    OnObbStateChangeListener mainObbStateChangeListener = new OnObbStateChangeListener() {
            @Override
            public void onObbStateChange(String path, int state) {
                super.onObbStateChange(path, state);
                if (state == MOUNTED) {
                    Log.d(TAG, "Mounting main file done.");
                    main = sm.getMountedObbPath(mainFile.getAbsolutePath());
                    if ( listener != null ) {
                    	listener.onMountSuccess();
                    	mainChecker.cancel();
                    }
                } else {
                    Log.d(TAG, "Mounting main file failed with state = " + state);
                    if ( listener != null ) 
                    	listener.onObbStateChange(path, state);
                }
            }
    };

    public static boolean isMainFileExists(Context context) {
        String packageName = context.getPackageName();
        Log.d(TAG, "Package name = " + packageName);
        int packageVersion = getAppVersionCode(context);
        Log.d(TAG, "Package version = " + packageVersion);
        File main = new File(Environment.getExternalStorageDirectory() + "/Android/obb/" + packageName + "/"
                + "main." + packageVersion + "." + packageName + ".obb");
        Log.d(TAG, "Check if main file " + main.getAbsolutePath() + " exists: " + main.exists());
        return main.exists();
    }

    public static boolean isPatchFileExists(Context context) {
        String packageName = context.getPackageName();
        Log.d(TAG, "Package name = " + packageName);
        int packageVersion = getAppVersionCode(context);
        Log.d(TAG, "Package version = " + packageVersion);
        File patch = new File(Environment.getExternalStorageDirectory() + "/Android/obb/" + packageName + "/"
                + "patch." + packageVersion + "." + packageName + ".obb");
        Log.d(TAG, "Check if patch file " + patch.getAbsolutePath() + " exists: " + patch.exists());
        return patch.exists();
    }

    public static int getAppVersionCode(Context context) {
        int versionCode = 1;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public String getMainRoot() {
        return sm.getMountedObbPath(mainFile.getAbsolutePath());
    }

    public String getPatchRoot() {
        return sm.getMountedObbPath(patchFile.getAbsolutePath());
    }

    /**
     * Use this method to create (or reinit) manager
     *
     * @param context
     * @param listener
     * @return
     */
    public static ObbExpansionsManager createNewInstance(Context context, ObbListener listener) {
        instance = new ObbExpansionsManager(context, listener);
        return instance;
    }

    /**
     * Use this method to get existing instance of manager
     *
     * @return instance of manager. If null - call createNewInstance to create new one
     */
    public static ObbExpansionsManager getInstance() {
        return instance;
    }

    /**
     * First, read from patch file. If patch doesn't contains file - search for it in main.
     *
     * @param pathToFile - path to file inside of .obb
     * @return
     */
    public File getFile(String pathToFile) {
        if (!pathToFile.startsWith(File.separator)) {
            pathToFile = File.separator + pathToFile;
        }
        File file = new File(patch + pathToFile);
        if (file.exists()) {
            return file;
        }
        file = new File(main + pathToFile);
        if (file.exists()) {
            return file;
        }
        return null;
    }

    /**
     * Read file directly from main extension file
     *
     * @param pathToFile - path to file inside of .obb
     * @return
     */
    public File getFileFromMain(String pathToFile) {
        if (!pathToFile.startsWith(File.separator)) {
            pathToFile = File.separator + pathToFile;
        }
        File file = new File(main + pathToFile);
        if (file.exists()) {
            return file;
        }
        return null;
    }

    /**
     * Read file directly from patch extension file
     *
     * @param pathToFile - path to file inside of .obb
     * @return
     */
    public File getFileFromPatch(String pathToFile) {
        if (!pathToFile.startsWith(File.separator)) {
            pathToFile = File.separator + pathToFile;
        }
        File file = new File(patch + pathToFile);
        if (file.exists()) {
            return file;
        }
        return null;
    }

    public static abstract class ObbListener extends OnObbStateChangeListener {
        /**
         * if state == 1 (MOUNTED) - mounting obb files finished successfully
         * else - mounting obb files finished with errors.
         */
        @Override
        public void onObbStateChange(String path, int state) {
            super.onObbStateChange(path, state);
        }

        /**
         * Extension files not found - download required
         */
        public abstract void onFilesNotFound();

        /**
         * Mounting obb files finished successfully
         */
        public abstract void onMountSuccess();
    }

    /**
     * Russel's teapot, forgive me this dirty hack!
     * Sometimes obb is mounting without calling OnObbStateChangeListener.onObbStateChange.
     * So that's how I fixed that
     */
    private class MountChecker extends TimerTask {
        private boolean isMainFile;

        public MountChecker(boolean isMainFile) {
            this.isMainFile = isMainFile;
        }

        @Override
        public void run() {
            Log.d(TAG, "MountChecker: Check if " + (isMainFile ? "main" : "patch") + " file mounted without calling callback: " +
                    sm.isObbMounted(mainFile.getAbsolutePath()));
            File file = isMainFile ? mainFile : patchFile;
            if (sm != null && file != null && sm.isObbMounted(file.getAbsolutePath())) {
                if (isMainFile) {
                    main = sm.getMountedObbPath(file.getAbsolutePath());
                    listener.onMountSuccess();
                } else {
                    patch = sm.getMountedObbPath(file.getAbsolutePath());
                }
            } else {
                mainChecker = new MountChecker(isMainFile);
                (new Timer()).schedule(mainChecker, 1000);
            }
        }
    }
}
