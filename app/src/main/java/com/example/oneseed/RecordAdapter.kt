package com.example.oneseed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.oneseed.databinding.RecordsItemBinding

class RecordAdapter:RecyclerView.Adapter<RecordAdapter.RecordHolder>() {

    var recordsList = ArrayList<Record>()
    class RecordHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val binding = RecordsItemBinding.bind(itemView)
        fun bind(record: Record) = with(binding){
            recordStatusImg.setImageResource(record.imageId)
            recordName.text = record.name
            yieldInRecord.text = record.yield
            recordDateTime.text = record.dateTime
            recordAdress.text = record.adress
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.records_item, parent, false)
        return RecordHolder(view)
    }

    override fun onBindViewHolder(holder: RecordHolder, position: Int) {
        holder.bind(recordsList[position])
    }

    override fun getItemCount(): Int {
        return recordsList.size
    }

    fun addRecord(record: Record){
        recordsList.add(record)
        notifyDataSetChanged()
    }
}