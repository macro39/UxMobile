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
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by matej on 22.10.2017.
 */

public class NativeEncoder {


    int screenWidth;
    int screenHeight;
    Rect rect;
    MediaCodec mediaCodec;
    MediaFormat mediaFormat;
    Surface surface;
    ByteBuffer[] outputBuffers;
    MediaCodec.BufferInfo bufferInfo;

    MediaMuxer mediaMuxer;

    private static final String MIME_TYPE = "video/avc";
    private static final int FRAME_RATE = 1;               // 1fps
    private static final int IFRAME_INTERVAL = 10;          // 10 seconds between I-frames


    private boolean isInitialized = false;
    private boolean muxerStarted;

    String path;
    int trackIndex = -1;

    public NativeEncoder(String path) {
        this.path = path;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void init() {
        try {
            screenWidth = 384;
            screenHeight = 240;

            int bitRate = 64000;


            mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE, screenWidth, screenHeight);
            // Set some properties.  Failing to specify some of these can cause the MediaCodec
            // configure() call to throw an unhelpful exception.
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);

            mediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
            mediaCodec.reset();
            mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            surface = mediaCodec.createInputSurface();
            mediaCodec.start();
            outputBuffers = mediaCodec.getOutputBuffers();

            rect = new Rect(0, 0, screenWidth, screenHeight);
            bufferInfo = new MediaCodec.BufferInfo();

            mux();

            trackIndex = -1;
            muxerStarted = false;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void recordFrame(Bitmap bitmap) {

        try {
            drainEncoder(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Canvas canvas = null;
        if (surface.isValid()) {
            try {
                canvas = surface.lockCanvas(rect);
//                canvas.drawColor(-16777216);
                canvas.drawBitmap(bitmap, 0, 0, null);
            } finally {
                surface.unlockCanvasAndPost(canvas);
            }
        } else {
            System.out.println("surface not valid");
        }
    }

    /**
     * Extracts all pending data from the encoder.
     * <p>
     * If endOfStream is not set, this returns when there is no more data to drain.  If it
     * is set, we send EOS to the encoder, and then iterate until we see EOS on the output.
     * Calling this with endOfStream set should be done once, right before stopping the muxer.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void drainEncoder(boolean endOfStream) throws IOException {
        final int TIMEOUT_USEC = 10000;

        if (endOfStream) {
            mediaCodec.signalEndOfInputStream();
        }

        ByteBuffer[] encoderOutputBuffers = mediaCodec.getOutputBuffers();
        while (true) {
            int encoderStatus = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                if (!endOfStream) {
                    break;      // out of while
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                encoderOutputBuffers = mediaCodec.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                if (muxerStarted) {
                    throw new RuntimeException("format changed twice");
                }
                MediaFormat newFormat = mediaCodec.getOutputFormat();

                // now that we have the Magic Goodies, start the muxer
                trackIndex = mediaMuxer.addTrack(newFormat);
                mediaMuxer.start();
                muxerStarted = true;
            } else if (encoderStatus < 0) {
//                Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
//                        encoderStatus);
                // let's ignore it
            } else {
                ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus +
                            " was null");
                }

                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // The codec config data was pulled out and fed to the muxer when we got
                    // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                    bufferInfo.size = 0;
                }

                if (bufferInfo.size != 0) {
//                    if (!mMuxerStarted) {
//                        throw new RuntimeException("muxer hasn't started");
//                    }

                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    encodedData.position(bufferInfo.offset);
                    encodedData.limit(bufferInfo.offset + bufferInfo.size);

                    mediaMuxer.writeSampleData(trackIndex, encodedData, bufferInfo);
                }

                mediaCodec.releaseOutputBuffer(encoderStatus, false);

                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (!endOfStream) {
                        System.out.println("reached end of stream unexpectedly");
                    }
                    break;      // out of while
                }
            }
        }
    }

    private static long computePresentationTimeNsec(int frameIndex) {
        final long ONE_BILLION = 1000000000;
        return frameIndex * ONE_BILLION / FRAME_RATE;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void mux() {
        // TODO: memory leak

        try {
            System.out.println("Muxing to " + path);
            mediaMuxer = new MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
//            trackIndex = mediaMuxer.addTrack(mediaCodec.getOutputFormat());
//            mediaMuxer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void stop() {

        System.out.println("Stopping!");

        try {
            drainEncoder(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mediaCodec != null) {
//            mediaCodec.signalEndOfInputStream();
//            mediaCodec.reset();
            mediaCodec.stop();
            mediaCodec.release();
        }

        if (mediaMuxer != null) {
            mediaMuxer.stop();
            mediaMuxer.release();
        }

        if (surface != null) {
            surface.release();
        }

        mediaCodec = null;
        mediaMuxer = null;
        mediaFormat = null;
        surface = null;
    }

}
