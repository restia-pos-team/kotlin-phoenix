@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.restia.kotlinphoenix.client

import kotlinx.coroutines.CoroutineScope
import java.net.URL

public actual typealias URL = URL

public actual class Socket actual constructor(
    url: String,
    paramsClosure: PayloadClosure,
    vsn: String,
    encode: EncodeClosure,
    decode: DecodeClosure,
    scope: CoroutineScope,
) : SocketCommon(paramsClosure, vsn, encode, decode, scope) {
    actual override var endpoint: String
    actual override var endpointUrl: URL

    actual override val protocol: String
        get() =
            when (endpointUrl.protocol) {
                "https" -> "wss"
                "http" -> "ws"
                else -> endpointUrl.protocol
            }

    public actual constructor(
        url: String,
        params: Payload?,
        vsn: String,
        encode: EncodeClosure,
        decode: DecodeClosure,
        scope: CoroutineScope,
    ) : this(url, { params }, vsn, encode, decode, scope)

    init {
        var mutableUrl = url

        // Ensure that the URL ends with "/websocket"
        if (!mutableUrl.contains("/websocket")) {
            // Do not duplicate '/' in path
            if (mutableUrl.last() != '/') {
                mutableUrl += "/"
            }

            // append "websocket" to the path
            mutableUrl += "websocket"
        }

        // Store the endpoint before changing the protocol
        endpoint = mutableUrl

        // Store the URL that will be used to establish a connection. Could potentially be
        // different at the time connect() is called based on a changing params closure.
        endpointUrl = DefaultsPlatform.buildEndpointUrl(endpoint, paramsClosure, vsn)
    }
}
