package sk.brecka.uxmobile;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;

/**
 * Created by matej on 23.10.2017.
 */

public class ScreenBuffer {
    private static final float ROTATION_ORIENTATION = -90.0f;

    private final int mWidth;
    private final int mHeight;
    private final boolean mIsLandscape;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Matrix mRenderingMatrix;

    private boolean mIsInitialized;
    private boolean mIsEmpty;

    public ScreenBuffer(int width, int height) {
        mWidth = width;
        mHeight = height;

        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mRenderingMatrix = new Matrix();

        mIsLandscape = isLandscape(mWidth, mHeight);
        mIsEmpty = true;
        mIsInitialized = false;
    }

    public synchronized void drawToBuffer(final View view) {
        if (view == null) {
            return;
        }

        final int viewWidth = view.getWidth();
        final int viewHeight = view.getHeight();

        if (!mIsInitialized) {
            final float scaleX = mWidth / (float) viewWidth;
            final float scaleY = mHeight / (float) viewHeight;

            mRenderingMatrix.setScale(scaleX, scaleY);

            mCanvas.setMatrix(mRenderingMatrix);

            mIsInitialized = true;
        }

        // TODO: mozno otacat inym smerom podla mIsLandscape
        if (mIsLandscape != isLandscape(viewWidth, viewHeight)) {
            // orientacia bufferu a obrazu sa nezhoduje, rotuj obraz
            mCanvas.save();

            mCanvas.translate(0, viewWidth);
            mCanvas.rotate(ROTATION_ORIENTATION);
            view.draw(mCanvas);

            mCanvas.restore();
        } else {
            // orientacia bufferu a obrazu sa zhodnuje
            view.draw(mCanvas);
        }

        mIsEmpty = false;
    }

    public synchronized Bitmap getBitmap() {
        return mBitmap;
    }

    public synchronized boolean isEmpty() {
        return mIsEmpty;
    }

    private boolean isLandscape(int width, int height) {
        return width > height;
    }
}
