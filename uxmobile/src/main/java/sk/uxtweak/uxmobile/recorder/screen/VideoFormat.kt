package sk.uxtweak.uxmobile.recorder.screen

import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaFormat
import android.os.Build
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import sk.uxtweak.uxmobile.BuildConfig

class VideoFormat(
    width: Int,
    height: Int,
    val profile: Profile
) {
    data class Profile(val format: String, val frameRate: Int, val bitRate: Int, val iFrameInterval: Int) {
        companion object {
            const val VIDEO_AVC = MediaFormat.MIMETYPE_VIDEO_AVC
            const val VIDEO_HEVC = MediaFormat.MIMETYPE_VIDEO_HEVC
            const val VIDEO_VP8 = MediaFormat.MIMETYPE_VIDEO_VP8
            const val VIDEO_VP9 = MediaFormat.MIMETYPE_VIDEO_VP9

            const val DEFAULT_I_FRAME_INTERVAL = 10
            const val LOW_FRAME_RATE = 15
            const val NORMAL_FRAME_RATE = 30
            const val HIGH_FRAME_RATE = 60

            val FULL_HD_HFR = Profile(VIDEO_AVC, HIGH_FRAME_RATE, 10_000_000, DEFAULT_I_FRAME_INTERVAL)
            val HD_HFR = Profile(VIDEO_AVC, HIGH_FRAME_RATE, 4_000_000, DEFAULT_I_FRAME_INTERVAL)
            val SD_HIGH_HFR = Profile(VIDEO_AVC, HIGH_FRAME_RATE, 2_000_000, DEFAULT_I_FRAME_INTERVAL)
            val SD_LOW_HFR = Profile(VIDEO_AVC, HIGH_FRAME_RATE, 384_000, DEFAULT_I_FRAME_INTERVAL)

            val FULL_HD = Profile(VIDEO_AVC, NORMAL_FRAME_RATE, 10_000_000, DEFAULT_I_FRAME_INTERVAL)
            val HD = Profile(VIDEO_AVC, NORMAL_FRAME_RATE, 4_000_000, DEFAULT_I_FRAME_INTERVAL)
            val SD_HIGH = Profile(VIDEO_AVC, 20, 2_000_000, DEFAULT_I_FRAME_INTERVAL)
            val SD_LOW = Profile(VIDEO_AVC, NORMAL_FRAME_RATE, 384_000, DEFAULT_I_FRAME_INTERVAL)

            val FULL_HD_LFR = Profile(VIDEO_AVC, LOW_FRAME_RATE, 10_000_000, DEFAULT_I_FRAME_INTERVAL)
            val HD_LFR = Profile(VIDEO_AVC, LOW_FRAME_RATE, 4_000_000, DEFAULT_I_FRAME_INTERVAL)
            val SD_HIGH_LFR = Profile(VIDEO_AVC, LOW_FRAME_RATE, 2_000_000, DEFAULT_I_FRAME_INTERVAL)
            val SD_LOW_LFR = Profile(VIDEO_AVC, LOW_FRAME_RATE, 384_000, DEFAULT_I_FRAME_INTERVAL)
        }
    }

    val width = if (width % 2 == 1) width + 1 else width
    val height = if (height % 2 == 1) height + 1 else height
    val frameTime = 1000L / profile.frameRate

    private val mediaFormat = MediaFormat.createVideoFormat(profile.format, this.width, this.height)

    init {
        mediaFormat.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, profile.bitRate)
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, profile.frameRate)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, profile.iFrameInterval)

        @Suppress("ConstantConditionIf")
        if (BuildConfig.TEST_MODE) {
            GlobalScope.launch {
                printFormats()
            }
        }
    }

    fun printFormats() {
        Log.d(VIDEO_TAG, "-------------------- Codec infos start --------------------")
        Log.d(VIDEO_TAG, "API level ${Build.VERSION.SDK_INT}")
        val codecList = MediaCodecList(MediaCodecList.ALL_CODECS)
        Log.d(VIDEO_TAG, "Encoders: ${codecList.codecInfos.count { it.isEncoder }}")
        codecList.codecInfos.forEach {
            if (it.isEncoder && !it.supportedTypes.any { it.startsWith("audio") }) {
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
        val w = height
        val h = width
        Log.d(VIDEO_TAG, "Is ${w}x$h supported: ${capabilities.isSizeSupported(w, h)}")
        Log.d(VIDEO_TAG, "Is ${w}x$h and ${profile.frameRate} fps supported: ${capabilities.areSizeAndRateSupported(w, h, profile.frameRate.toDouble())}")
        try {
            Log.d(VIDEO_TAG, "Supported frame rates for ${w}x$h: ${capabilities.getSupportedFrameRatesFor(w, h)}")
        } catch (exception: IllegalArgumentException) {
            Log.d(VIDEO_TAG, "No supported frame rates for ${w}x$h")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Log.d(VIDEO_TAG, "Achievable frame rates for ${w}x$h: ${capabilities.getAchievableFrameRatesFor(w, h)}")
            } catch (exception: IllegalArgumentException) {
                Log.d(VIDEO_TAG, "No achievable frame rates for ${w}x$h")
            }
        }
        try {
            Log.d(VIDEO_TAG, "Supported heights for width $w: ${capabilities.getSupportedHeightsFor(w)}")
        } catch (exception: IllegalArgumentException) {
            Log.d(VIDEO_TAG, "Unsupported width $w")
        }
        try {
            Log.d(VIDEO_TAG, "Supported widths for height $h: ${capabilities.getSupportedWidthsFor(h)}")
        } catch (exception: IllegalArgumentException) {
            Log.d(VIDEO_TAG, "Unsupported height $h")
        }
    }

    private fun printEncoderCapabilities(capabilities: MediaCodecInfo.EncoderCapabilities?) {
        if (capabilities == null) {
            Log.d(VIDEO_TAG, "No encoder capabilities!")
            return;
        }
        Log.d(VIDEO_TAG, "-- Encoder capabilities --")
        Log.d(VIDEO_TAG, "Bitrate modes: ${getBitrateModes(capabilities)}")
        Log.d(VIDEO_TAG, "Complexity range: ${capabilities.complexityRange}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Log.d(VIDEO_TAG, "Quality range: ${capabilities.qualityRange}")
        }
    }

    private fun printFeature(capabilities: MediaCodecInfo.CodecCapabilities, feature: String) {
        Log.d(VIDEO_TAG, "$feature (supported: ${capabilities.isFeatureSupported(feature)}, required: ${capabilities.isFeatureRequired(feature)})")
    }

    private fun getBitrateModes(capabilities: MediaCodecInfo.EncoderCapabilities): String {
        val supportedBitrateModes = mutableListOf<String>()
        val bitrateModes = mapOf(
            MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR to "CBR",
            MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ to "CQ",
            MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR to "VBR"
        )
        bitrateModes.forEach {
            if (capabilities.isBitrateModeSupported(it.key)) {
                supportedBitrateModes += it.value
            }
        }
        return supportedBitrateModes.joinToString()
    }

    operator fun invoke(): MediaFormat = mediaFormat

    companion object {
        private const val VIDEO_TAG = "CodecInfo"
    }
}
