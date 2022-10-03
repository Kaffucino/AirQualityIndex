package com.marina.pokusajstoti.ui.control

import CustomAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBindings
import com.google.android.material.dialog.MaterialDialogs
import com.marina.pokusajstoti.R
import com.marina.pokusajstoti.databinding.FragmentControlBinding
import com.marina.pokusajstoti.util.ItemsViewModel
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList


class ControlFragment : Fragment() {

    private var _binding: FragmentControlBinding? = null

    private var bluetooth_adapter: BluetoothAdapter? = null
    private lateinit var bluetooth_socket : BluetoothSocket
    private var outputStream : OutputStream? = null
    var btConnected : Boolean = false;

    private var myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val REQUEST_ENABLE_BLUETOOTH = 1
    private val DISCOVERY_REQUEST =2

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var  recylcler_view:RecyclerView

    private var  paired_devices_data:ArrayList<ItemsViewModel> = ArrayList<ItemsViewModel>()
    private  var adapter :CustomAdapter = CustomAdapter(paired_devices_data, { item : ItemsViewModel -> onListItemClick(item)})
    private  var paired_devices : ArrayList<BluetoothDevice> = ArrayList<BluetoothDevice>()
    private lateinit var myContext : Context
    private var receiver_registered : Boolean = false

    @SuppressLint("MissingPermission")
    private fun onListItemClick(item: ItemsViewModel) {
        if(btConnected) {
            Toast.makeText(myContext, "This device is already connected.", Toast.LENGTH_SHORT).show()
            return
        }
        var myDevice  = paired_devices.find { device -> device.address.equals(item.MAC)}
        bluetooth_socket = myDevice!!.createRfcommSocketToServiceRecord(myUUID)
        if(bluetooth_socket  != null && myDevice.name.equals("HC-05")) {
            bluetooth_socket.connect()
            if (bluetooth_socket != null && bluetooth_socket.isConnected) {
                btConnected = true
                Toast.makeText(myContext, "Successfully connected to the filter!", Toast.LENGTH_SHORT).show()
                outputStream = bluetooth_socket.outputStream

                binding.ConnectedLayout.RadioGroupOptions.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener{
                        group,checkedId ->
                    var rbStop : Int = R.id.optionStop
                    var rbSlow : Int = R.id.optionSlow
                    var rbNormal : Int = R.id.optionNormal
                    var rbFast : Int = R.id.optionFast

                    when(checkedId){
                        rbStop->{ outputStream!!.write(0) ; outputStream!!.write(0)  }
                        rbSlow->{outputStream!!.write(1); outputStream!!.write(1) }
                        rbNormal->{outputStream!!.write(2); outputStream!!.write(2)}
                        rbFast->{outputStream!!.write(3); outputStream!!.write(3) }
                    }

                })

                binding.btNotConnected.visibility=View.INVISIBLE
                binding.btConnected.visibility=View.VISIBLE

            }
        } else {
            Toast.makeText(myContext, "This device does not support the filter!", Toast.LENGTH_SHORT).show()
        }
    }

     inner  class MyBroadcastReceiver : BroadcastReceiver(){

        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            val action: String? = intent?.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                    if(device?.name !=  null && !deviceInList(device.name, device.address)) {
                        val deviceName = device!!.name
                        val deviceHardwareAddress = device!!.address// MAC address

                        paired_devices_data.add(
                            ItemsViewModel(
                                R.drawable.devices_foreground,
                                deviceName,
                                deviceHardwareAddress
                            )
                        )
                        paired_devices.add(device)
                        adapter.notifyDataSetChanged()
                    }
                }
            }        }

         @SuppressLint("MissingPermission")
         private fun deviceInList(name: String?, address: String?): Boolean {
            return paired_devices_data.contains(ItemsViewModel(R.drawable.devices_foreground, name, address))
         }

     }
    private var receiver : BroadcastReceiver = MyBroadcastReceiver()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext=context
    }


    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(ControlViewModel::class.java)

        _binding = FragmentControlBinding.inflate(inflater, container, false)
        val root: View = binding.root
        bluetooth_adapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetooth_adapter == null) {
            Toast.makeText(activity, "Device does not support bluetooth!", Toast.LENGTH_SHORT).show()
        }

        if (bluetooth_adapter?.isEnabled == false) {
            Log.i("ENABLE BT", "Bt is not enabled")
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            Log.e("ENABLE BT", "Enable request sent")
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        if(btConnected == false) {
            binding.btNotConnected.visibility = View.VISIBLE
            binding.btConnected.visibility = View.INVISIBLE
        }
        else {
            binding.btNotConnected.visibility = View.INVISIBLE
            binding.btConnected.visibility = View.VISIBLE
        }
        recylcler_view=binding.recyclerView
        binding.buttonFindDevice.setOnClickListener { findDevices() }
        recylcler_view.layoutManager=LinearLayoutManager(activity)
        recylcler_view.adapter=adapter
        binding.ConnectedLayout.buttonDisconnect.setOnClickListener { disconnect() }
        binding.ConnectedLayout.buttonChangeMode.setOnClickListener { change_fan_mode() }
        binding.ConnectedLayout.RadioGroupOptions.visibility=View.INVISIBLE
        binding.ConnectedLayout.textViewChooseSpeed.visibility=View.INVISIBLE


        return root
    }



    private fun change_fan_mode() {

        if(binding.ConnectedLayout.textViewCurrentMode.text.equals("Current mode: Auto")){
            binding.ConnectedLayout.textViewCurrentMode.text="Current mode: Manual"
            binding.ConnectedLayout.textViewChooseSpeed.visibility=View.VISIBLE
            binding.ConnectedLayout.RadioGroupOptions.visibility=View.VISIBLE

        }else{
            binding.ConnectedLayout.textViewCurrentMode.text="Current mode: Auto"
            binding.ConnectedLayout.RadioGroupOptions.visibility=View.INVISIBLE
            binding.ConnectedLayout.textViewChooseSpeed.visibility=View.INVISIBLE

        }
        outputStream!!.write(4);
        outputStream!!.write(4);
        outputStream!!.write(4);
        outputStream!!.write(4);
        outputStream!!.write(4);
        outputStream!!.write(4);


        outputStream!!.write(0);
        outputStream!!.write(0);
        outputStream!!.write(0);
        outputStream!!.write(0);
        outputStream!!.write(0);
        outputStream!!.write(0);

    }



    private fun disconnect() {

        outputStream!!.close()
        bluetooth_socket.close()

        binding.btConnected.visibility=View.INVISIBLE
        binding.btNotConnected.visibility=View.VISIBLE
        btConnected=false

    //    zakomentarisala sam ovo i disconnect je proradio
    //    myContext.unregisterReceiver(receiver)
        receiver_registered=false

    }


    @SuppressLint("MissingPermission")
    fun findDevices(){
        val discoverFilter = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        startActivityForResult(discoverFilter, DISCOVERY_REQUEST)

    }

    @SuppressLint("MissingPermission")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("BLUETOOTH", "Entered onActivity")
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (bluetooth_adapter!!.isEnabled) {
                    Toast.makeText(activity, "BlueTooth has been enabled!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(activity, "BlueTooth has been disabled!", Toast.LENGTH_SHORT)
                        .show()
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(
                    activity,
                    "BlueTooth enabeling has been canceled!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else if (requestCode == DISCOVERY_REQUEST) {
            Toast.makeText(myContext, "Discovery in progress", Toast.LENGTH_SHORT).show()
            discoverDevices()
        }

    }

    @SuppressLint("MissingPermission")
    private fun discoverDevices() {
        if(bluetooth_adapter?.startDiscovery() == true) {
            Toast.makeText(myContext, "Scanning...", Toast.LENGTH_SHORT).show()
            myContext.registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
           // receiver_registered=true
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}