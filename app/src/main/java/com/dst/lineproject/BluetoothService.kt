package com.dst.lineproject

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.*

val MY_UUID_INSECURE : UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66")
const val NAME_INSECURE = "BluetoothChatInsecure"
class BluetoothService {
    var bluetoothAdapter: BluetoothAdapter? = null
    init {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    }
    fun start(){
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        }
    }
    fun isEnable() : Boolean? {
        return bluetoothAdapter?.enable()
    }
    // device run as Client , try to connect other devices

    private inner class ConnectThread(device: BluetoothDevice) : Thread(){

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(MY_UUID_INSECURE)
        }

        override fun run() {
            bluetoothAdapter?.cancelDiscovery()
            mmSocket?.use {socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()
                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                // manageMyConnectedSocket(socket)
            }
        }
        fun cancel(){
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e("xxx", "Could not close the client socket", e)
            }
        }
    }

    // device run as Server
    private inner class AcceptThread : Thread() {
        private val mmServerSocket : BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE){
            bluetoothAdapter?.listenUsingRfcommWithServiceRecord(NAME_INSECURE,
                MY_UUID_INSECURE)
        }

        override fun run() {
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e("xxx", "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
                    //manageMyConnectedSocket(it)
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }
        }
        // Closes the connect socket and causes the thread to finish.
        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e("xxx", "Could not close the connect socket", e)
            }
        }
    }
}