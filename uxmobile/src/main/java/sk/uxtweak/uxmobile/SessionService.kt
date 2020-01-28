package sk.uxtweak.uxmobile

interface SessionService {
    suspend fun generateSessionId(): String
}
