package com.dst.lineproject.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dst.lineproject.BluetoothService
import com.dst.lineproject.R
import kotlinx.android.synthetic.main.home_fragment.*

const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 33
class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var bluetoothService : BluetoothService
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }
    //TODO : Broadcast listen BT change
    private val receiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context, intent: Intent) {
            when(intent.action){
                BluetoothDevice.ACTION_FOUND ->{
                    val device : BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        val deviceModel = DeviceModel(it.name,it.address)
                        if(::viewModel.isInitialized){
                            viewModel.pushDevice(deviceModel)
                        }
                        val deviceName = it.name
                        val macAddress = it.address
                        Toast.makeText(context,"device $deviceName address $macAddress",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        requireActivity().registerReceiver(receiver,filter)
        bluetoothService = BluetoothService()
        scanBtn.setOnClickListener {
            if(permissionApproved()){
                val result = bluetoothService.bluetoothAdapter!!.startDiscovery()
                Log.e("xxx","start discovery $result")
            }else{
                requestPermissions()
            }
        }
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val adapter = DeviceListAdapter()
        ryc.adapter = adapter
        viewModel.deviceModel.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it?.toMutableList())
        })
        if(bluetoothService.isEnable() == false){
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent,1)
        }
        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        startActivity(discoverableIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){

        }
        if(resultCode == Activity.RESULT_CANCELED){

        }
    }
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE -> when {
                grantResults.isEmpty() ->{
                    //user interaction was interrupted

                }
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    // Permission was accepted
                    val result = bluetoothService.bluetoothAdapter!!.startDiscovery()
                    Log.e("xxx","start discovery $result")
                }
                else -> {
                    // Permission denied
                }
            }
        }
    }
    private fun permissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
    private fun requestPermissions() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        )
    }
    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(receiver)
    }

}