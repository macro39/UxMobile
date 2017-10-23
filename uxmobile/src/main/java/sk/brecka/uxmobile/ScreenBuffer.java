package sk.brecka.uxmobile;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;

/**
 * Created by matej on 23.10.2017.
 */

public class ScreenBuffer {
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Matrix mRenderingMatrix;
    private boolean mIsScaleSet;

    public ScreenBuffer(int width, int height) {
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mRenderingMatrix = new Matrix();
        mIsScaleSet = false;
    }

    public synchronized void drawToBuffer(final View view) {
        if (view == null) {
            return;
        }

        if (!mIsScaleSet) {
            final float scaleX = mBitmap.getWidth() / (float) view.getWidth();
            final float scaleY = mBitmap.getHeight() / (float) view.getHeight();

            mRenderingMatrix.setScale(scaleX, scaleY);
            mCanvas.setMatrix(mRenderingMatrix);

            mIsScaleSet = true;
        }

        view.draw(mCanvas);
    }

    public synchronized Bitmap getBitmap() {
        return mBitmap;
    }
}
