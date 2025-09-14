package io.restia.kotlinphoenix.client

import platform.Foundation.NSData
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURLSession
import platform.Foundation.NSURLSessionConfiguration
import platform.Foundation.NSURLSessionWebSocketCloseCode
import platform.Foundation.NSURLSessionWebSocketDelegateProtocol
import platform.Foundation.NSURLSessionWebSocketMessage
import platform.Foundation.NSURLSessionWebSocketTask
import platform.darwin.NSObject

public actual class WebSocketTransport actual constructor(
    public actual val url: URL,
    public actual val decode: DecodeClosure,
) : WebSocketTransportCommon() {
    private var connection: NSURLSessionWebSocketTask? = null

    actual override fun connect(): SocketFlow {
        readyState = Transport.ReadyState.CONNECTING

        val client =
            NSURLSession.sessionWithConfiguration(
                configuration = NSURLSessionConfiguration.defaultSessionConfiguration(),
                delegate =
                    object : NSObject(), NSURLSessionWebSocketDelegateProtocol {
                        override fun URLSession(
                            session: NSURLSession,
                            webSocketTask: NSURLSessionWebSocketTask,
                            didOpenWithProtocol: String?,
                        ) {
                            readyState = Transport.ReadyState.OPEN
                            sharedFlow.tryEmit(SocketEvent.OpenEvent)
                        }

                        override fun URLSession(
                            session: NSURLSession,
                            webSocketTask: NSURLSessionWebSocketTask,
                            didCloseWithCode: NSURLSessionWebSocketCloseCode,
                            reason: NSData?,
                        ) {
                            println("ON CLOSED: ${didCloseWithCode.toInt()} :: $reason")
                            readyState = Transport.ReadyState.CLOSED
                            sharedFlow.tryEmit(SocketEvent.CloseEvent(didCloseWithCode.toInt()))
                        }
                    },
                delegateQueue = NSOperationQueue.currentQueue(),
            )
        connection = url.URL?.let { client.webSocketTaskWithURL(it) }

        listenMessages()

        connection?.resume()

        return sharedFlow
    }

    actual override fun disconnect(
        code: Int,
        reason: String?,
    ) {
        connection?.cancelWithCloseCode(code.toLong(), null)
        connection = null
    }

    actual override fun send(data: String) {
        val message = NSURLSessionWebSocketMessage(data)
        connection?.sendMessage(message) { error ->
            error?.let {
                println("SEND ERROR : $data ::: $it")
            }
        }
    }

    private fun listenMessages() {
        connection?.receiveMessageWithCompletionHandler { message, nsError ->
            when {
                nsError != null -> {
                    readyState = Transport.ReadyState.CLOSED
                    sharedFlow.tryEmit(
                        SocketEvent.FailureEvent(
                            Throwable(nsError.description),
                            null,
                        ),
                    )
                    sharedFlow.tryEmit(SocketEvent.CloseEvent(1001)) // WS_CLOSE_ABNORMAL
                    return@receiveMessageWithCompletionHandler
                }

                message != null -> {
                    message.string?.let { sharedFlow.tryEmit(SocketEvent.MessageEvent(decode(it))) }
                    listenMessages()
                }

                else -> {
                    listenMessages()
                }
            }
        }
    }
}
