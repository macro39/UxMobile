package sk.uxtweak.uxmobile.net

import com.neovisionaries.ws.client.WebSocketException
import com.neovisionaries.ws.client.WebSocketFrame
import io.github.sac.BasicListener
import io.github.sac.Socket

abstract class BasicListenerAdapter : BasicListener {
    override fun onAuthentication(socket: Socket?, status: Boolean?) {}
    override fun onConnectError(socket: Socket?, exception: WebSocketException?) {}
    override fun onSetAuthToken(token: String?, socket: Socket?) {}
    override fun onConnected(socket: Socket?, headers: MutableMap<String, MutableList<String>>?) {}
    override fun onDisconnected(
        socket: Socket?,
        serverCloseFrame: WebSocketFrame?,
        clientCloseFrame: WebSocketFrame?,
        closedByServer: Boolean
    ) {}
}
