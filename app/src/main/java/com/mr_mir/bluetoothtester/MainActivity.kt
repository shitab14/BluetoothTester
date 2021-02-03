package com.mr_mir.bluetoothtester

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), PermissionListener {
    private var REQUEST_BLUETOOTH = 1
    private var PERMISSIONS_REQUEST_LOCATION = 100
    private var permanentlyDenied = false

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter() // get bluetooth default adapter

    private var bondedDevicesList: MutableList<BluetoothDevice> = mutableListOf()
    private var bondedDevicesAdapter: BluetoothListItemAdapter? = null
    private var bondedDevicesLinearLayoutManager: LinearLayoutManager? = null

    private var availableDevicesList: MutableList<BluetoothDevice> = mutableListOf()
    private var availableDevicesAdapter: BluetoothListItemAdapter? = null
    private var availableDevicesLinearLayoutManager: LinearLayoutManager? = null

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                Log.e("BLUETOOTH LOG", "Bluetooth device found\n")
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

                if (device != null) {
                    availableDevicesList.add(device)
                }
                availableDevicesAdapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

        askForPermission()
        getThisDeviceData()
        setAvailableRecycleView()
        getBluetoothDevices()
    }

    private fun getThisDeviceData() {
        tvThisDevice.text = "Hardware Address: ${bluetoothAdapter?.address.toString()}\nDevice Name: ${bluetoothAdapter?.name.toString()}"
    }

    private fun setAvailableRecycleView() {
        availableDevicesLinearLayoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL,
            false
        )
        rvAvailableDevices.layoutManager = availableDevicesLinearLayoutManager
        availableDevicesAdapter = BluetoothListItemAdapter(applicationContext, availableDevicesList)
        rvAvailableDevices.adapter = availableDevicesAdapter

    }

    private fun initView() {
        tvLoading.visibility = VISIBLE
        llMainView.visibility = GONE
    }
    private fun mainView() {
        tvLoading.visibility = GONE
        llMainView.visibility = VISIBLE
    }

    private fun getBluetoothDevices() {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)

        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled) {
                val enableBT = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBT, REQUEST_BLUETOOTH)
            } else {
                // Bonded Device
                getPairedDevices()

                registerReceiver(broadcastReceiver, filter)
                bluetoothAdapter.startDiscovery()


            }
        } else {
            // Bluetooth not found
            AlertDialog.Builder(this)
                .setTitle("Not compatible")
                .setMessage("Your phone does not support Bluetooth")
                .setPositiveButton("Exit",
                    DialogInterface.OnClickListener { dialog, which -> System.exit(0) })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
    }

    private fun getPairedDevices() {
        bondedDevicesLinearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvBondedDevices.layoutManager = bondedDevicesLinearLayoutManager
        if (bluetoothAdapter!= null && !bluetoothAdapter.bondedDevices.isNullOrEmpty()) {
            bondedDevicesList = bluetoothAdapter.bondedDevices.toMutableList()
            bondedDevicesAdapter = BluetoothListItemAdapter(this, bondedDevicesList)
            rvBondedDevices.adapter = bondedDevicesAdapter
            mainView()
        }
    }

    //Permissions
    private fun askForPermission() {
        val permissionCollection = android.Manifest.permission.BLUETOOTH//, android.Manifest.permission.BLUETOOTH_ADMIN
        Dexter.withContext(this)
            .withPermission(permissionCollection)
            .withListener(this)
            .check()
    }

    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

        getThisDeviceData()
        setAvailableRecycleView()
        getBluetoothDevices()

    }

    @SuppressLint("SetTextI18n")
    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
        if(p0?.isPermanentlyDenied != null && p0.isPermanentlyDenied) {
            Toast.makeText(this, "Please add Permissions From your Application Settings", Toast.LENGTH_LONG).show()
            permanentlyDenied = true
        }
    }

    override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {
        p1?.continuePermissionRequest()

    }
}