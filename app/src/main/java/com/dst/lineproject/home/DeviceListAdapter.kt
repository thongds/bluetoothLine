package com.dst.lineproject.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dst.lineproject.databinding.DeviceItemBinding

class DeviceListAdapter : ListAdapter<DeviceModel,DeviceViewHolder>(DeviceDiff()) {
    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
            holder.binding(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        return DeviceViewHolder.from(parent)
    }
}
class DeviceViewHolder(val binding : DeviceItemBinding) : RecyclerView.ViewHolder(binding.root){
    companion object{
        fun from(parent: ViewGroup) : DeviceViewHolder{
            val view = DeviceItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return DeviceViewHolder(view)
        }
    }
    fun binding(device : DeviceModel){
        binding.deviceData = device
    }
}
class DeviceDiff : DiffUtil.ItemCallback<DeviceModel>(){
    override fun areContentsTheSame(oldItem: DeviceModel, newItem: DeviceModel): Boolean {
        return  oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: DeviceModel, newItem: DeviceModel): Boolean {
        return  oldItem.macAddress == newItem.macAddress
    }
}
data class DeviceModel(val name : String?, val macAddress : String?)