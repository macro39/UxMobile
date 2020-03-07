package sk.uxtweak.uxmobile

import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.*
import org.junit.Assert.assertTrue
import org.junit.Test

class AtFixedRateTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testFixedRate() = runBlocking<Unit> {
        var lastTime = SystemClock.elapsedRealtime() - WAIT_TIME
        var times = REPEATS
        atFixedRate(Dispatchers.Main, WAIT_TIME) {
            val diff = SystemClock.elapsedRealtime() - lastTime
            Log.d("AtFixedRateTest", "$diff vs $WAIT_TIME")
            assertTrue("$diff < $WAIT_TIME", diff >= WAIT_TIME - 2)
            lastTime = SystemClock.elapsedRealtime()
            if (--times <= 0) {
                cancel()
            }
            delay(WAIT_TIME / 2)
        }
    }

    companion object {
        private const val FRAME_RATE = 45
        private const val WAIT_TIME = 1000L / FRAME_RATE
        private const val REPEATS = 150
    }
}
