package sk.uxtweak.uxmobile.recorder.screen

import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaFormat
import android.os.Build
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class VideoFormat(
    val width: Int,
    val height: Int,
    val frameRate: Int = DEFAULT_FRAME_RATE,
    val bitRate: Int = DEFAULT_BIT_RATE,
    iFrameInterval: Int = DEFAULT_I_FRAME_INTERVAL
) {
    val frameTime = 1000L / frameRate

    private val mediaFormat = MediaFormat.createVideoFormat(VIDEO_FORMAT, width, height)

    init {
        mediaFormat.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, iFrameInterval)

        GlobalScope.launch {
            printFormats()
        }
    }

    fun printFormats() {
        Log.d(VIDEO_TAG, "-------------------- Codec infos start --------------------")
        Log.d(VIDEO_TAG, "API level ${Build.VERSION.SDK_INT}")
        val codecList = MediaCodecList(MediaCodecList.ALL_CODECS)
        Log.d(VIDEO_TAG, "Encoders: ${codecList.codecInfos.count { it.isEncoder }}")
        codecList.codecInfos.forEach {
            if (it.isEncoder) {
                Log.d(VIDEO_TAG, "----------")
                printCodecInfo(it)
            }
        }
        Log.d(VIDEO_TAG, "-------------------- Codec infos end --------------------")
    }

    private fun printCodecInfo(codecInfo: MediaCodecInfo) {
        Log.d(VIDEO_TAG, "Name: ${codecInfo.name}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d(VIDEO_TAG, "Canonical name: ${codecInfo.canonicalName} (is alias: ${codecInfo.isAlias})")
            Log.d(VIDEO_TAG, "Hardware accelerated: ${codecInfo.isHardwareAccelerated}, Is software only: ${codecInfo.isSoftwareOnly}")
            Log.d(VIDEO_TAG, "Provided by ${if (codecInfo.isVendor) "device manufacturer" else "Android platform"}")
        }
        Log.d(VIDEO_TAG, "Supported types: ${codecInfo.supportedTypes.joinToString()}")
        codecInfo.supportedTypes.forEach { type ->
            Log.d(VIDEO_TAG, "---- $type ----")
            val capabilities = codecInfo.getCapabilitiesForType(type)
            printVideoCapabilities(capabilities.videoCapabilities)
            printEncoderCapabilities(capabilities.encoderCapabilities)
            Log.d(VIDEO_TAG, "Color formats: ${capabilities.colorFormats.joinToString()}")
            Log.d(VIDEO_TAG, "Default format [${capabilities.defaultFormat}]")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Log.d(VIDEO_TAG, "Keys: ${capabilities.defaultFormat.keys.joinToString()}")
                Log.d(VIDEO_TAG, "Features: ${capabilities.defaultFormat.features.joinToString()}")
            }
            Log.d(VIDEO_TAG, "Profile levels: ${capabilities.profileLevels.joinToString { "${it.profile}: ${it.level}" }}")
            Log.d(VIDEO_TAG, "Is media format supported: ${capabilities.isFormatSupported(mediaFormat)}")
            printFeature(capabilities, MediaCodecInfo.CodecCapabilities.FEATURE_AdaptivePlayback)
            printFeature(capabilities, MediaCodecInfo.CodecCapabilities.FEATURE_SecurePlayback)
            printFeature(capabilities, MediaCodecInfo.CodecCapabilities.FEATURE_TunneledPlayback)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                printFeature(capabilities, MediaCodecInfo.CodecCapabilities.FEATURE_IntraRefresh)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                printFeature(capabilities, MediaCodecInfo.CodecCapabilities.FEATURE_PartialFrame)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                printFeature(capabilities, MediaCodecInfo.CodecCapabilities.FEATURE_DynamicTimestamp)
                printFeature(capabilities, MediaCodecInfo.CodecCapabilities.FEATURE_FrameParsing)
                printFeature(capabilities, MediaCodecInfo.CodecCapabilities.FEATURE_MultipleFrames)
            }
        }
    }

    private fun printVideoCapabilities(capabilities: MediaCodecInfo.VideoCapabilities?) {
        if (capabilities == null) {
            Log.d(VIDEO_TAG, "No video capabilities!")
            return;
        }
        Log.d(VIDEO_TAG, "-- Video capabilities --")
        Log.d(VIDEO_TAG, "Supported widths: ${capabilities.supportedWidths}")
        Log.d(VIDEO_TAG, "Supported heights: ${capabilities.supportedHeights}")
        Log.d(VIDEO_TAG, "Height alignment: ${capabilities.heightAlignment}")
        Log.d(VIDEO_TAG, "Width alignment: ${capabilities.widthAlignment}")
        Log.d(VIDEO_TAG, "Supported frame rates: ${capabilities.supportedFrameRates}")
        Log.d(VIDEO_TAG, "Bitrate: ${capabilities.bitrateRange}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d(VIDEO_TAG, "Supported performance points: ${capabilities.supportedPerformancePoints?.joinToString()}")
        }
        Log.d(VIDEO_TAG, "Is ${width}x$height supported: ${capabilities.isSizeSupported(width, height)}")
        Log.d(VIDEO_TAG, "Is ${width}x$height and $frameRate fps supported: ${capabilities.areSizeAndRateSupported(width, height, frameRate.toDouble())}")
        try {
            Log.d(VIDEO_TAG, "Supported frame rates for ${width}x$height: ${capabilities.getSupportedFrameRatesFor(width, height)}")
        } catch (exception: IllegalArgumentException) {
            Log.d(VIDEO_TAG, "No supported frame rates for ${width}x$height")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Log.d(VIDEO_TAG, "Achievable frame rates for ${width}x$height: ${capabilities.getAchievableFrameRatesFor(width, height)}")
            } catch (exception: IllegalArgumentException) {
                Log.d(VIDEO_TAG, "No achievable frame rates for ${width}x$height")
            }
        }
        try {
            Log.d(VIDEO_TAG, "Supported heights for width $width: ${capabilities.getSupportedHeightsFor(width)}")
        } catch (exception: IllegalArgumentException) {
            Log.d(VIDEO_TAG, "Unsupported width $width")
        }
        try {
            Log.d(VIDEO_TAG, "Supported widths for height $height: ${capabilities.getSupportedWidthsFor(height)}")
        } catch (exception: IllegalArgumentException) {
            Log.d(VIDEO_TAG, "Unsupported height $height")
        }
    }

    private fun printEncoderCapabilities(capabilities: MediaCodecInfo.EncoderCapabilities?) {
        if (capabilities == null) {
            Log.d(VIDEO_TAG, "No encoder capabilities!")
            return;
        }
        Log.d(VIDEO_TAG, "-- Encoder capabilities --")
        Log.d(VIDEO_TAG, "Is bitrate supported: ${capabilities.isBitrateModeSupported(bitRate)}")
        Log.d(VIDEO_TAG, "Complexity range: ${capabilities.complexityRange}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Log.d(VIDEO_TAG, "Quality range: ${capabilities.qualityRange}")
        }
    }

    private fun printFeature(capabilities: MediaCodecInfo.CodecCapabilities, feature: String) {
        Log.d(VIDEO_TAG, "$feature (supported: ${capabilities.isFeatureSupported(feature)}, required: ${capabilities.isFeatureRequired(feature)})")
    }


    operator fun invoke(): MediaFormat = mediaFormat

    companion object {
        private const val VIDEO_TAG = "CodecInfo"
        const val VIDEO_FORMAT = "video/avc"
        private const val DEFAULT_FRAME_RATE = 25
        private const val DEFAULT_BIT_RATE = 500_000
        private const val DEFAULT_I_FRAME_INTERVAL = 10
    }
}
