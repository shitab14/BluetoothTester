package com.mr_mir.bluetoothtester

import android.bluetooth.BluetoothAdapter

/**
 * Created by Shitab Mir on 01,February,2021
 */
class MyBluetoothManager {
    companion object{
        val BTAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    }
}