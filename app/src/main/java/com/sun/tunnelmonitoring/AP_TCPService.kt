package com.sun.tunnelmonitoring

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.threshold.logger.PrettyLogger
import com.threshold.logger.debug
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.ServerSocket

private const val ACTION_TCP_ORD = "com.sun.tunnelmonitoring.action.TCP.ORD"
private const val ACTION_TCP_FILE = "com.sun.tunnelmonitoring.action.TCP.FILE"
private const val ACTION_UDP_ORD = "com.sun.tunnelmonitoring.action.UDP.ORD"
private const val ACTION_UDP_FILE = "com.sun.tunnelmonitoring.action.UDP.FILE"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class AP_TCPService : IntentService("AP_TCPService"), PrettyLogger {
    var serverSocket: ServerSocket? = null
    private var rec_text: String? = null
    private var localBroadcastManager: LocalBroadcastManager? = null

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_TCP_ORD -> {
                Log.d("AP_TCPService", ACTION_TCP_ORD)
                handleActionOrd()
            }
            ACTION_TCP_FILE -> {
                debug { ACTION_TCP_FILE }
                handleActionFile()
            }
            ACTION_UDP_ORD -> {

            }
            ACTION_UDP_FILE -> {

            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionOrd() {
        serverSocket = ServerSocket()
        serverSocket!!.reuseAddress = true
        serverSocket!!.bind(InetSocketAddress(8888))
        //waiting clients connect
        Log.d("AP_TCPService", "监听客服端")
        var client = serverSocket!!.accept()
        var inputStream = client.getInputStream()
        var buf = BufferedReader(InputStreamReader(inputStream))
        rec_text = buf.readText()
        debug { rec_text }
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        var intent = Intent("com.sun.tunnelmonitoring.LOCAL_BROADCAST")
        intent.putExtra("rec_text", rec_text)
        localBroadcastManager!!.sendBroadcast(intent)

        startActionOrd(this)
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionFile() {
        TODO("Handle action Baz")
    }

    companion object {
        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionOrd(context: Context) {
            Log.d("startActionOrd", "打开服务")
            val intent = Intent(context, AP_TCPService::class.java)
            intent.action = ACTION_TCP_ORD
            context.startService(intent)
        }

        /**
         * Starts this service to perform action Baz with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionFile(context: Context) {
            val intent = Intent(context, AP_TCPService::class.java)
            context.startService(intent)
        }
    }
}
