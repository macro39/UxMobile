package sk.brecka.uxmobile;

import android.graphics.Bitmap;

import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;
import org.jcodec.scale.BitmapUtil;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.Transform;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * This class is part of JCodec ( www.jcodec.org ) This software is distributed
 * under FreeBSD License
 *
 * @author The JCodec project
 */
public class MySequenceEncoder {
    private SeekableByteChannel mByteChannel;
    private Picture mToEncode;
    private Transform mTransform;
    private H264Encoder mH264Encoder;
    private ArrayList<ByteBuffer> mSpsList;
    private ArrayList<ByteBuffer> mPpsList;
    private FramesMP4MuxerTrack mOutTrack;
    private ByteBuffer mByteBuffer;
    private int mFrameNumber;
    private MP4Muxer mMuxer;

    private int mFramerate;

    public MySequenceEncoder(File out, int framerate) throws IOException {
        mFramerate = framerate;

        this.mByteChannel = NIOUtils.writableFileChannel(out);

        // Muxer that will store the encoded frames
        mMuxer = new MP4Muxer(mByteChannel, Brand.MP4);

        // Add video track to mMuxer
        mOutTrack = mMuxer.addTrack(TrackType.VIDEO, mFramerate);

        // Allocate a buffer big enough to hold output frames
        mByteBuffer = ByteBuffer.allocate(1920 * 1080 * 6);

        // Create an instance of mH264Encoder
        mH264Encoder = new H264Encoder();

        // Transform to convert between RGB and YUV
        mTransform = ColorUtil.getTransform(ColorSpace.RGB, mH264Encoder.getSupportedColorSpaces()[0]);

        // Encoder extra data ( SPS, PPS ) to be stored in a special place of
        // MP4
        mSpsList = new ArrayList<ByteBuffer>();
        mPpsList = new ArrayList<ByteBuffer>();
    }

    public void encodeImage(Bitmap bi) throws IOException {
        encodeNativeFrame(BitmapUtil.fromBitmap(bi));
    }

    public void encodeNativeFrame(Picture pic) throws IOException {
        if (mToEncode == null) {
            mToEncode = Picture.create(pic.getWidth(), pic.getHeight(), mH264Encoder.getSupportedColorSpaces()[0]);
        }

        // Perform conversion
        mTransform.transform(pic, mToEncode);

        // Encode image into H.264 frame, the result is stored in 'mByteBuffer' buffer
        mByteBuffer.clear();
        ByteBuffer result = mH264Encoder.encodeFrame(mToEncode, mByteBuffer);

        // Based on the frame above form correct MP4 packet
        mSpsList.clear();
        mPpsList.clear();
        H264Utils.wipePS(result, mSpsList, mPpsList);
        H264Utils.encodeMOVPacket(result);

        // Add packet to video track
        mOutTrack.addFrame(new MP4Packet(result, mFrameNumber, mFramerate, mFramerate, mFrameNumber, true, null, mFrameNumber, 0));

        mFrameNumber++;
    }

    public void finish() throws IOException {
        // Push saved SPS/PPS to a special storage in MP4
        mOutTrack.addSampleEntry(H264Utils.createMOVSampleEntry(mSpsList, mPpsList, 4));

        // Write MP4 header and finalize recording
        mMuxer.writeHeader();
        NIOUtils.closeQuietly(mByteChannel);
    }
}
