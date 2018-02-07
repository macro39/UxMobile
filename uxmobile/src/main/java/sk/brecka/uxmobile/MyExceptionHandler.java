package sk.brecka.uxmobile;

import android.util.Log;

/**
 * Created by matej on 20.1.2018.
 */

public class MyExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler mExceptionHandler;

    public MyExceptionHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
        mExceptionHandler = exceptionHandler;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // TODO: nejaky log
        Log.e("UxMobile", "uncaughtException: " + e);
        e.printStackTrace();

        UxMobile.getSession().addExceptionEvent(e);

        Log.d("UxMobile", "uncaughtException: attempting to upload");
        UxMobile.getSession().uploadRecordings();

        // TODO: pozor: moze byt volane multithreadovo

        mExceptionHandler.uncaughtException(t, e);
    }

    public static void register() {
        Thread.UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();

        if (!(currentHandler instanceof MyExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(currentHandler));
        }
    }
}
