package sk.uxtweak.uxmobile.server

interface SessionService {
    suspend fun generateSessionId(): String
}
