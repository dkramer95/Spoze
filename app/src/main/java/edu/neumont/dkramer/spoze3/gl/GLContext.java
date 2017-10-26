package edu.neumont.dkramer.spoze3.gl;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.Display;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo;
import edu.neumont.dkramer.spoze3.gl.deviceinfo.GLRotationVectorInfo;
import edu.neumont.dkramer.spoze3.gl.deviceinfo.GLTouchInfo;

/**
 * Adapter class for regular Context class that provides additional OpenGL methods
 * Created by dkramer on 10/20/17.
 */

public final class GLContext extends Context {
    private Map<GLDeviceInfo.Type, GLDeviceInfo> mDeviceInfo;
    private GLActivity mActivity;
    private GLView mGLView;




    public GLContext(GLActivity activity) {
        mActivity = activity;
        mDeviceInfo = new HashMap<>();
    }

    public GLView getGLView() {
        return mGLView;
    }

    public void setGLView(GLView glView) {
        mGLView = glView;
    }

    public void onStart() {
        for (GLDeviceInfo i : mDeviceInfo.values()) {
            i.start();
        }
    }

    public void onStop() {
        for (GLDeviceInfo i : mDeviceInfo.values()) {
            i.stop();
        }
    }

    public void enableDeviceInfo(GLDeviceInfo.Type infoType) {
        GLDeviceInfo info = null;

        switch (infoType) {
            case TOUCH_INPUT:
                info = new GLTouchInfo(this);
                break;
            case ROTATION_VECTOR:
                info = new GLRotationVectorInfo(this);
                break;
            case ACCELEROMETER:
                info = new GLAccelerometerInfo(this);
                break;
            default:
                throw new UnsupportedOperationException("Specified type hasn't been implemented yet!");
        }
        if (mDeviceInfo.containsKey(infoType)) {
            throw new IllegalArgumentException("Specified GLDeviceInfo already exists!");
        }
        mDeviceInfo.put(infoType, info);
    }





    /* Method wrappers, that just use our GLActivity, but are needed in order to extend GLContext */

    @Override
    public AssetManager getAssets() {
        return mActivity.getAssets();
    }

    @Override
    public Resources getResources() {
        return mActivity.getResources();
    }

    @Override
    public PackageManager getPackageManager() {
        return mActivity.getPackageManager();
    }

    @Override
    public ContentResolver getContentResolver() {
        return mActivity.getContentResolver();
    }

    @Override
    public Looper getMainLooper() {
        return mActivity.getMainLooper();
    }

    @Override
    public Context getApplicationContext() {
        return mActivity.getApplicationContext();
    }

    @Override
    public void setTheme(int i) {
        mActivity.setTheme(i);
    }

    @Override
    public Resources.Theme getTheme() {
        return mActivity.getTheme();
    }

    @Override
    public ClassLoader getClassLoader() {
        return mActivity.getClassLoader();
    }

    @Override
    public String getPackageName() {
        return mActivity.getPackageName();
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return mActivity.getApplicationInfo();
    }

    @Override
    public String getPackageResourcePath() {
        return mActivity.getPackageResourcePath();
    }

    @Override
    public String getPackageCodePath() {
        return mActivity.getPackageCodePath();
    }

    @Override
    public SharedPreferences getSharedPreferences(String s, int i) {
        return mActivity.getSharedPreferences(s, i);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean moveSharedPreferencesFrom(Context context, String s) {
        return mActivity.moveSharedPreferencesFrom(context, s);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean deleteSharedPreferences(String s) {
        return mActivity.deleteSharedPreferences(s);
    }

    @Override
    public FileInputStream openFileInput(String s) throws FileNotFoundException {
        return mActivity.openFileInput(s);
    }

    @Override
    public FileOutputStream openFileOutput(String s, int i) throws FileNotFoundException {
        return mActivity.openFileOutput(s, i);
    }

    @Override
    public boolean deleteFile(String s) {
        return mActivity.deleteFile(s);
    }

    @Override
    public File getFileStreamPath(String s) {
        return mActivity.getFileStreamPath(s);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public File getDataDir() {
        return mActivity.getDataDir();
    }

    @Override
    public File getFilesDir() {
        return mActivity.getFilesDir();
    }

    @Override
    public File getNoBackupFilesDir() {
        return mActivity.getNoBackupFilesDir();
    }

    @Nullable
    @Override
    public File getExternalFilesDir(@Nullable String s) {
        return mActivity.getExternalFilesDir(s);
    }

    @Override
    public File[] getExternalFilesDirs(String s) {
        return mActivity.getExternalFilesDirs(s);
    }

    @Override
    public File getObbDir() {
        return mActivity.getObbDir();
    }

    @Override
    public File[] getObbDirs() {
        return mActivity.getObbDirs();
    }

    @Override
    public File getCacheDir() {
        return mActivity.getCacheDir();
    }

    @Override
    public File getCodeCacheDir() {
        return mActivity.getCodeCacheDir();
    }

    @Nullable
    @Override
    public File getExternalCacheDir() {
        return mActivity.getExternalCacheDir();
    }

    @Override
    public File[] getExternalCacheDirs() {
        return mActivity.getExternalCacheDirs();
    }

    @Override
    public File[] getExternalMediaDirs() {
        return mActivity.getExternalMediaDirs();
    }

    @Override
    public String[] fileList() {
        return mActivity.fileList();
    }

    @Override
    public File getDir(String s, int i) {
        return mActivity.getDir(s, i);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String s, int i, SQLiteDatabase.CursorFactory cursorFactory) {
        return mActivity.openOrCreateDatabase(s, i, cursorFactory);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String s, int i, SQLiteDatabase.CursorFactory cursorFactory, @Nullable DatabaseErrorHandler databaseErrorHandler) {
        return mActivity.openOrCreateDatabase(s, i, cursorFactory, databaseErrorHandler);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean moveDatabaseFrom(Context context, String s) {
        return mActivity.moveDatabaseFrom(context, s);
    }

    @Override
    public boolean deleteDatabase(String s) {
        return mActivity.deleteDatabase(s);
    }

    @Override
    public File getDatabasePath(String s) {
        return mActivity.getDatabasePath(s);
    }

    @Override
    public String[] databaseList() {
        return mActivity.databaseList();
    }

    @Override
    public Drawable getWallpaper() {
        return mActivity.getWallpaper();
    }

    @Override
    public Drawable peekWallpaper() {
        return mActivity.peekWallpaper();
    }

    @Override
    public int getWallpaperDesiredMinimumWidth() {
        return mActivity.getWallpaperDesiredMinimumWidth();
    }

    @Override
    public int getWallpaperDesiredMinimumHeight() {
        return mActivity.getWallpaperDesiredMinimumHeight();
    }

    @Override
    public void setWallpaper(Bitmap bitmap) throws IOException {
        mActivity.setWallpaper(bitmap);
    }

    @Override
    public void setWallpaper(InputStream inputStream) throws IOException {
        mActivity.setWallpaper(inputStream);
    }

    @Override
    public void clearWallpaper() throws IOException {
        mActivity.clearWallpaper();
    }

    @Override
    public void startActivity(Intent intent) {
        mActivity.startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle bundle) {
        mActivity.startActivity(intent, bundle);
    }

    @Override
    public void startActivities(Intent[] intents) {
        mActivity.startActivities(intents);
    }

    @Override
    public void startActivities(Intent[] intents, Bundle bundle) {
        mActivity.startActivities(intents, bundle);
    }

    @Override
    public void startIntentSender(IntentSender intentSender, @Nullable Intent intent, int i, int i1, int i2) throws IntentSender.SendIntentException {
        mActivity.startIntentSender(intentSender, intent, i, i1, i2);
    }

    @Override
    public void startIntentSender(IntentSender intentSender, @Nullable Intent intent, int i, int i1, int i2, @Nullable Bundle bundle) throws IntentSender.SendIntentException {
        mActivity.startIntentSender(intentSender, intent, i, i1, i2, bundle);
    }

    @Override
    public void sendBroadcast(Intent intent) {
        mActivity.sendBroadcast(intent);
    }

    @Override
    public void sendBroadcast(Intent intent, @Nullable String s) {
        mActivity.sendBroadcast(intent, s);
    }

    @Override
    public void sendOrderedBroadcast(Intent intent, @Nullable String s) {
        mActivity.sendOrderedBroadcast(intent, s);
    }

    @Override
    public void sendOrderedBroadcast(@NonNull Intent intent, @Nullable String s, @Nullable BroadcastReceiver broadcastReceiver, @Nullable Handler handler, int i, @Nullable String s1, @Nullable Bundle bundle) {
        mActivity.sendOrderedBroadcast(intent, s, broadcastReceiver, handler, i, s1, bundle);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle userHandle) {
        mActivity.sendBroadcastAsUser(intent, userHandle);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle userHandle, @Nullable String s) {
        mActivity.sendBroadcastAsUser(intent, userHandle, s);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle userHandle, @Nullable String s, BroadcastReceiver broadcastReceiver, @Nullable Handler handler, int i, @Nullable String s1, @Nullable Bundle bundle) {
        mActivity.sendOrderedBroadcastAsUser(intent, userHandle, s, broadcastReceiver, handler, i, s1, bundle);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void sendStickyBroadcast(Intent intent) {
        mActivity.sendStickyBroadcast(intent);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver broadcastReceiver, @Nullable Handler handler, int i, @Nullable String s, @Nullable Bundle bundle) {
        mActivity.sendStickyOrderedBroadcast(intent, broadcastReceiver, handler, i, s, bundle);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void removeStickyBroadcast(Intent intent) {
        mActivity.removeStickyBroadcast(intent);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void sendStickyBroadcastAsUser(Intent intent, UserHandle userHandle) {
        mActivity.sendStickyBroadcastAsUser(intent, userHandle);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle userHandle, BroadcastReceiver broadcastReceiver, @Nullable Handler handler, int i, @Nullable String s, @Nullable Bundle bundle) {
        mActivity.sendStickyOrderedBroadcastAsUser(intent, userHandle, broadcastReceiver, handler, i, s, bundle);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void removeStickyBroadcastAsUser(Intent intent, UserHandle userHandle) {
        mActivity.removeStickyBroadcastAsUser(intent, userHandle);
    }

    @Nullable
    @Override
    public Intent registerReceiver(@Nullable BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        return mActivity.registerReceiver(broadcastReceiver, intentFilter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public Intent registerReceiver(@Nullable BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, int i) {
        return mActivity.registerReceiver(broadcastReceiver, intentFilter, i);
    }

    @Nullable
    @Override
    public Intent registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, @Nullable String s, @Nullable Handler handler) {
        return mActivity.registerReceiver(broadcastReceiver, intentFilter, s, handler);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public Intent registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, @Nullable String s, @Nullable Handler handler, int i) {
        return mActivity.registerReceiver(broadcastReceiver, intentFilter, s, handler, i);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver broadcastReceiver) {
        mActivity.unregisterReceiver(broadcastReceiver);
    }

    @Nullable
    @Override
    public ComponentName startService(Intent intent) {
        return mActivity.startService(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public ComponentName startForegroundService(Intent intent) {
        return mActivity.startForegroundService(intent);
    }

    @Override
    public boolean stopService(Intent intent) {
        return mActivity.stopService(intent);
    }

    @Override
    public boolean bindService(Intent intent, @NonNull ServiceConnection serviceConnection, int i) {
        return mActivity.bindService(intent, serviceConnection, i);
    }

    @Override
    public void unbindService(@NonNull ServiceConnection serviceConnection) {
        mActivity.unbindService(serviceConnection);
    }

    @Override
    public boolean startInstrumentation(@NonNull ComponentName componentName, @Nullable String s, @Nullable Bundle bundle) {
        return mActivity.startInstrumentation(componentName, s, bundle);
    }

    @Nullable
    @Override
    public Object getSystemService(@NonNull String s) {
        return mActivity.getSystemService(s);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public String getSystemServiceName(@NonNull Class<?> aClass) {
        return mActivity.getSystemServiceName(aClass);
    }

    @Override
    public int checkPermission(@NonNull String s, int i, int i1) {
        return mActivity.checkPermission(s, i, i1);
    }

    @Override
    public int checkCallingPermission(@NonNull String s) {
        return mActivity.checkCallingPermission(s);
    }

    @Override
    public int checkCallingOrSelfPermission(@NonNull String s) {
        return mActivity.checkCallingOrSelfPermission(s);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int checkSelfPermission(@NonNull String s) {
        return mActivity.checkSelfPermission(s);
    }

    @Override
    public void enforcePermission(@NonNull String s, int i, int i1, @Nullable String s1) {
        mActivity.enforcePermission(s, i, i1, s1);
    }

    @Override
    public void enforceCallingPermission(@NonNull String s, @Nullable String s1) {
        mActivity.enforceCallingPermission(s, s1);
    }

    @Override
    public void enforceCallingOrSelfPermission(@NonNull String s, @Nullable String s1) {
        mActivity.enforceCallingOrSelfPermission(s, s1);
    }

    @Override
    public void grantUriPermission(String s, Uri uri, int i) {
        mActivity.grantUriPermission(s, uri, i);
    }

    @Override
    public void revokeUriPermission(Uri uri, int i) {
        mActivity.revokeUriPermission(uri, i);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void revokeUriPermission(String s, Uri uri, int i) {
        mActivity.revokeUriPermission(s, uri, i);
    }

    @Override
    public int checkUriPermission(Uri uri, int i, int i1, int i2) {
        return mActivity.checkUriPermission(uri, i, i1, i2);
    }

    @Override
    public int checkCallingUriPermission(Uri uri, int i) {
        return mActivity.checkCallingUriPermission(uri, i);
    }

    @Override
    public int checkCallingOrSelfUriPermission(Uri uri, int i) {
        return mActivity.checkCallingOrSelfUriPermission(uri, i);
    }

    @Override
    public int checkUriPermission(@Nullable Uri uri, @Nullable String s, @Nullable String s1, int i, int i1, int i2) {
        return mActivity.checkUriPermission(uri, s, s1, i, i1, i2);
    }

    @Override
    public void enforceUriPermission(Uri uri, int i, int i1, int i2, String s) {
        mActivity.enforceUriPermission(uri, i, i1, i2, s);
    }

    @Override
    public void enforceCallingUriPermission(Uri uri, int i, String s) {
        mActivity.enforceCallingUriPermission(uri, i, s);
    }

    @Override
    public void enforceCallingOrSelfUriPermission(Uri uri, int i, String s) {
        mActivity.enforceCallingOrSelfUriPermission(uri, i, s);
    }

    @Override
    public void enforceUriPermission(@Nullable Uri uri, @Nullable String s, @Nullable String s1, int i, int i1, int i2, @Nullable String s2) {
        mActivity.enforceUriPermission(uri, s, s1, i, i1, i2, s2);
    }

    @Override
    public Context createPackageContext(String s, int i) throws PackageManager.NameNotFoundException {
        return mActivity.createPackageContext(s, i);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Context createContextForSplit(String s) throws PackageManager.NameNotFoundException {
        return mActivity.createContextForSplit(s);
    }

    @Override
    public Context createConfigurationContext(@NonNull Configuration configuration) {
        return mActivity.createConfigurationContext(configuration);
    }

    @Override
    public Context createDisplayContext(@NonNull Display display) {
        return mActivity.createDisplayContext(display);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Context createDeviceProtectedStorageContext() {
        return mActivity.createDeviceProtectedStorageContext();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean isDeviceProtectedStorage() {
        return mActivity.isDeviceProtectedStorage();
    }
}
