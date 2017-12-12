package sk.brecka.uxmobile;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Prerobeny http://bigflake.com/mediacodec/EncodeAndMuxTest.java.txt
 * Created by matej on 22.10.2017.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class NativeEncoder {
    private static final String MIME_TYPE = "video/avc";
    private static final int IFRAME_INTERVAL = 10;
    private static final int UNDEFINED = -1;
    private static final int TIMEOUT_USEC = 10000;

    private MediaFormat mMediaFormat;
    private MediaCodec mEncoder;
    private MediaMuxer mMuxer;
    private Surface mRenderingSurface;
    private MediaCodec.BufferInfo mBufferInfo;
    private Rect mRect;

    private int mTrackIndex;
    private boolean mMuxerStarted;
    private boolean mIsRunning;

    // TODO: kompatibilita so starsimi verziami (<21, resp. <5.0)
    public NativeEncoder(int screenWidth, int screenHeight, int framerate, int bitrate, String filePath) throws IOException {

        // format
        mMediaFormat = MediaFormat.createVideoFormat(MIME_TYPE, screenWidth, screenHeight);
        mMediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
        mMediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, framerate);
        mMediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);

        // codec
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder.reset();
        mEncoder.configure(mMediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        // muxer
        mMuxer = new MediaMuxer(filePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

        // misc
        mRenderingSurface = mEncoder.createInputSurface();
        mRect = new Rect(0, 0, screenWidth, screenHeight);
        mBufferInfo = new MediaCodec.BufferInfo();
        mTrackIndex = UNDEFINED;
        mMuxerStarted = false;

        //
        mIsRunning = true;
        mEncoder.start();
    }

    public void encodeFrame(Bitmap bitmap) throws IOException {
//        drainEncoder(false);

        if (mRenderingSurface == null) {
            return;
        }

        if (mRenderingSurface.isValid()) {
            Canvas canvas = null;
            try {
                canvas = mRenderingSurface.lockCanvas(mRect);

                if (canvas != null) {
                    canvas.drawBitmap(bitmap, 0, 0, null);
                }
            } finally {
                // tu to failne na emulatore
                if (canvas != null) {
                    mRenderingSurface.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    /**
     * Extracts all pending data from the encoder.
     * <p>
     * If endOfStream is not set, this returns when there is no more data to drain.  If it
     * is set, we send EOS to the encoder, and then iterate until we see EOS on the output.
     * Calling this with endOfStream set should be done once, right before stopping the muxer.
     */
    private void drainEncoder(boolean endOfStream) throws IOException {

        if (endOfStream) {
            mEncoder.signalEndOfInputStream();
        }

        encodingCycle:
        while (true) {
            final int encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);

            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet

                if (!endOfStream) {
                    break encodingCycle;
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once

                if (mMuxerStarted) {
                    throw new RuntimeException("Format changed twice");
                }
                MediaFormat newFormat = mEncoder.getOutputFormat();

                // now that we have the Magic Goodies, start the muxer
                mTrackIndex = mMuxer.addTrack(newFormat);
                mMuxer.start();
                mMuxerStarted = true;
            } else if (encoderStatus < 0) {
                // ignored
            } else {
                final ByteBuffer encodedData = mEncoder.getOutputBuffer(encoderStatus);

                if (encodedData == null) {
                    throw new RuntimeException("encodedData " + encoderStatus + " is null");
                }

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // The codec config data was pulled out and fed to the muxer when we got
                    // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                    mBufferInfo.size = 0;
                }

                if (mBufferInfo.size != 0) {
                    if (!mMuxerStarted) {
                        throw new RuntimeException("Muxer not started");
                    }

                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    encodedData.position(mBufferInfo.offset);
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size);

                    mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                }

                mEncoder.releaseOutputBuffer(encoderStatus, false);

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    break encodingCycle;
                }
            }
        }

    }

    public void finish() throws IOException {
        //
        mIsRunning = false;
        drainEncoder(true);

        // stop & cleanup
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }

        if (mMuxer != null) {
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }

        if (mRenderingSurface != null) {
            mRenderingSurface.release();
            mRenderingSurface = null;
        }

        mMediaFormat = null;
    }

    public boolean isRunning() {
        return mIsRunning;
    }
}
