package com.mr_mir.bluetoothtester

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Shitab Mir on 01,February,2021
 */
class BluetoothListItemAdapter(val context: Context, val pairedDevices: MutableList<BluetoothDevice>): RecyclerView.Adapter<BluetoothListItemAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDeviceName = view.findViewById<TextView>(R.id.tvDeviceName)
        val tvDeviceInfo1 = itemView.findViewById<TextView>(R.id.tvDeviceInfo1)
        val tvDeviceInfo2 = itemView.findViewById<TextView>(R.id.tvDeviceInfo2)
        val tvDeviceInfo3 = itemView.findViewById<TextView>(R.id.tvDeviceInfo3)
        val ivStatusImage = itemView.findViewById<ImageView>(R.id.ivStatusImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvDeviceName.text = pairedDevices.elementAt(position).name?.toString()
        holder.tvDeviceInfo1.text = pairedDevices.elementAt(position).address?.toString()
        holder.tvDeviceInfo2.text = pairedDevices.elementAt(position).uuids?.toString()
        holder.tvDeviceInfo3.text = pairedDevices.elementAt(position).bluetoothClass?.deviceClass?.toString()

        when (pairedDevices.elementAt(position).bondState) {
            BluetoothDevice.BOND_BONDED -> {
                holder.ivStatusImage.setBackgroundResource(R.drawable.ic_baseline_bluetooth_connected_24)
            }
            BluetoothDevice.BOND_BONDING -> {
                holder.ivStatusImage.setBackgroundResource(R.drawable.ic_baseline_bluetooth_searching_24)
            }
            BluetoothDevice.BOND_NONE -> {
                holder.ivStatusImage.setBackgroundResource(R.drawable.ic_baseline_bluetooth_24)
            }
            else -> {
                holder.ivStatusImage.setBackgroundResource(R.drawable.ic_baseline_bluetooth_24)
            }
        }


    }

    override fun getItemCount(): Int {
        return pairedDevices.size
    }

}