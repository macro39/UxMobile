package sk.uxtweak.uxmobile.persister

import android.media.MediaFormat
import sk.uxtweak.uxmobile.recorder.screen.EncodedFrame

sealed class MuxerCommand {
    class MuxFrame(val frame: EncodedFrame) : MuxerCommand()
    class ChangeOutputFormat(val format: MediaFormat) : MuxerCommand()
    object StopMuxer : MuxerCommand()
}
