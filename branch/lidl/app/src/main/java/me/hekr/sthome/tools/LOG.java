package me.hekr.sthome.tools;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import io.fabric.sdk.android.Fabric;
import me.hekr.sthome.BuildConfig;

/**
 * Created by ryanhsueh on 2018/11/9
 */
public class LOG {
    private static final String TAG = LOG.class.getSimpleName();

    public static final String KEY_ENDPOINT = "end_point";
    public static final String KEY_CURRENT_CONTROLLER_ID = "current_controller_id";
    public static final String KEY_CURRENT_OBJECT_ID = "current_object_id";

    public static final boolean DEBUG = BuildConfig.DEBUG;

    private static boolean forceToLogcat = false;

    private static WeakReference<Context> contextRef;

    public static void E(String TAG, String info) {
        if (isLoggingToLogcatEnabled()) {
            if (isFabricInitialized()) {
                Crashlytics.log(Log.ERROR, TAG, info);
                Crashlytics.logException(createThrowable(info));
            } else {
                Log.e(TAG, info);
            }
        }
        else {
            if (isFabricInitialized()) {
                Crashlytics.log(TAG + "\tE\t" + info);
                Crashlytics.logException(createThrowable(info));
            }
        }
    }

    public static void E(String TAG, String info, Throwable throwable) {
        if (isLoggingToLogcatEnabled()) {
            if (isFabricInitialized()) {
                Crashlytics.log(TAG + "\tE\t" + info);
                Log.e(TAG, info, throwable);
                Crashlytics.logException(throwable);
            } else {
                Log.e(TAG, info, throwable);
            }
        }
        else {
            if (isFabricInitialized()) {
                Crashlytics.log(TAG + "\tE\t" + info);
                Crashlytics.logException(throwable);
            }
        }
    }

    public static void I(String TAG, String info) {
        if (isLoggingToLogcatEnabled()) {
            if (isFabricInitialized()) {
                Crashlytics.log(Log.INFO, TAG, info);
            }
            else {
                Log.i(TAG, info);
            }
        }
        else {
            if (isFabricInitialized()) {
                Crashlytics.log(TAG + "\tI\t" + info);
            }
        }
    }

    public static void D(String TAG, String info) {
        if (isLoggingToLogcatEnabled()) {
            if (isFabricInitialized()) {
                Crashlytics.log(Log.DEBUG, TAG, info);
            }
            else {
                Log.d(TAG, info);
            }
        }
        else {
            if (isFabricInitialized()) {
                Crashlytics.log(TAG + "\tD\t" + info);
            }
        }
    }

    public static void V(String TAG, String info) {
        if (isLoggingToLogcatEnabled()) {
            if (isFabricInitialized()) {
                Crashlytics.log(Log.VERBOSE, TAG, info);
            }
            else {
                Log.v(TAG, info);
            }
        }
        else {
            if (isFabricInitialized()) {
                Crashlytics.log(TAG + "\tV\t" + info);
            }
        }
    }

    public static void V(String TAG, Object msg1, Object msg2) {
        if(isLoggingToLogcatEnabled()) {
            if (isFabricInitialized()) {
                Crashlytics.log(Log.VERBOSE, TAG, "" + msg1 + msg2);
            }
            else {
                Log.v(TAG, "" + msg1 + msg2);
            }
        }
        else {
            if (isFabricInitialized()) {
                Crashlytics.log(TAG + "\tV\t" + msg1 + msg2);
            }
        }
    }

    public static void V(String TAG, Object msg1, Object msg2, Object msg3) {
        if(isLoggingToLogcatEnabled()) {
            if (isFabricInitialized()) {
                Crashlytics.log(Log.VERBOSE, TAG, "" + msg1 + msg2 + msg3);
            }
            else {
                Log.v(TAG, "" + msg1 + msg2 + msg3);
            }
        }
        else {
            if (isFabricInitialized()) {
                Crashlytics.log(TAG + "\tV\t" + msg1 + msg2 + msg3);
            }
        }
    }

    public static void V(String TAG, Object msg1, Object msg2, Object msg3, Object msg4) {
        if(isLoggingToLogcatEnabled()) {
            if (isFabricInitialized()) {
                Crashlytics.log(Log.VERBOSE, TAG, "" + msg1 + msg2 + msg3 + msg4);
            }
            else {
                Log.v(TAG, "" + msg1 + msg2 + msg3 + msg4);
            }
        }
        else {
            if (isFabricInitialized()) {
                Crashlytics.log(TAG + "\tV\t" + msg1 + msg2 + msg3 + msg4);
            }
        }
    }

    public static void V(String TAG, Object msg1, Object msg2, Object msg3, Object msg4, Object msg5) {
        if(isLoggingToLogcatEnabled()) {
            if (isFabricInitialized()) {
                Crashlytics.log(Log.VERBOSE, TAG, "" + msg1 + msg2 + msg3 + msg4 + msg5);
            }
            else {
                Log.v(TAG, "" + msg1 + msg2 + msg3 + msg4 + msg5);
            }
        }
        else {
            if (isFabricInitialized()) {
                Crashlytics.log(TAG + "\tV\t" + msg1 + msg2 + msg3 + msg4 + msg5);
            }
        }
    }

    public static void V(String TAG, Object... messages) {
        if (messages.length == 0) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (Object message : messages) {
            builder.append(message);
        }

        if(isLoggingToLogcatEnabled()) {
            if (isFabricInitialized()) {
                Crashlytics.log(Log.VERBOSE, TAG, builder.toString());
            }
            else {
                Log.v(TAG, builder.toString());
            }
        }
        else {
            if (isFabricInitialized()) {
                Crashlytics.log(TAG + "\tV\t" + builder.toString());
            }
        }
    }

    public static void W(String TAG, String info) {
        if(isLoggingToLogcatEnabled()) {
            if (isFabricInitialized()) {
                Crashlytics.log(Log.WARN, TAG, info);
            } else {
                Log.w(TAG, info);
            }
        }
        else {
            if (isFabricInitialized()) {
                Crashlytics.log(TAG + "\tW\t" + info);
            }
        }
    }

    public static void W(String TAG, String info, Throwable throwable) {
        if(isLoggingToLogcatEnabled()) {
            if (isFabricInitialized()) {
                Crashlytics.log(TAG + "\tW\t" + info);
                Log.w(TAG, info, throwable);
                Crashlytics.logException(throwable);
            } else {
                Log.w(TAG, info, throwable);
            }
        }
        else {
            if (isFabricInitialized()) {
                Crashlytics.log(TAG + "\tW\t" + info);
                Crashlytics.logException(throwable);
            }
        }
    }

    public static boolean isForcingLogcatEnabled() {
        return forceToLogcat;
    }

    public static void setForcingLogcatEnabled(boolean isEnable) {
        forceToLogcat = isEnable;
    }

//    public static boolean initializeFabric(Context context) {
//        if (context == null) {
//            LOG.E(TAG, "initializeFabric() - context is null");
//            return false;
//        }
//
//        contextRef = new WeakReference<>(context);
//
//        // Initialize Fabric
//        Fabric.with(context, new Crashlytics(), new CrashlyticsNdk());
//
//        // Get identity
//        String identity = SwSetting.getUUID(context);
//
//        // Set identity to Crashlytics
//        Crashlytics.setUserIdentifier(identity);
//
//        LOG.W(TAG, "initializeFabric() - Initialization SUCCESS. User identity = " + identity);
//
//        return true;
//    }

    private static boolean isFabricInitialized() {
        return Fabric.isInitialized();
    }

//    private static boolean resetFabric() {
//        if (!Fabric.isInitialized()) {
//            Log.w(TAG, "resetFabric() - Fabric is not initialized yet, skip the process");
//            return false;
//        }
//
//        Field fieldSingleton = null;
//        try {
//            fieldSingleton = Fabric.class.getDeclaredField("singleton");
//        }
//        catch (NoSuchFieldException noSuchFieldException) {
//            Log.e(TAG, "resetFabric() - Get field failed", noSuchFieldException);
//        }
//
//        if (fieldSingleton == null) {
//            return false;
//        }
//
//        fieldSingleton.setAccessible(true);
//
//        try {
//            fieldSingleton.set(null, null);
//        }
//        catch (IllegalAccessException illegalAccessException) {
//            Log.e(TAG, "resetFabric() - Set field failed", illegalAccessException);
//        }
//
//        Log.w(TAG, "resetFabric() - Reset process " + (Fabric.isInitialized() ? "FAILED" : "SUCCEED"));
//
//        return !Fabric.isInitialized();
//    }
//
//    private static boolean uploadLogsToFabric() {
//        Context context = contextRef != null ? contextRef.get() : null;
//
//        if (context == null) {
//            LOG.E(TAG, "uploadLogsToFabric() - context is null, skip the process");
//            return false;
//        }
//
//        return resetFabric() && initializeFabric(context);
//    }

    private static Throwable createThrowable(String name) {
        final int traceShift = 3;

        Throwable throwable = new Throwable(name);

        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        StackTraceElement[] newStackTraceElements = new StackTraceElement[stackTraceElements.length - traceShift];

        // Copy stack trace elements
        System.arraycopy(stackTraceElements, traceShift, newStackTraceElements, 0, newStackTraceElements.length);

        // Set new stack trace
        throwable.setStackTrace(newStackTraceElements);

        return throwable;
    }

    private static boolean isLoggingToLogcatEnabled() {
        return DEBUG || forceToLogcat;
    }
}
