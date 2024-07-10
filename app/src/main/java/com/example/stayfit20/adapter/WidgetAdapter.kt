package com.example.stayfit20.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stayfit20.R
import com.example.stayfit20.model.WidgetItem

class WidgetAdapter(private val widgetList: List<WidgetItem>) :
    RecyclerView.Adapter<WidgetAdapter.WidgetViewHolder>() {

    class WidgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.TaskWidgetView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dash_widget, parent, false)
        return WidgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
        val widgetItem = widgetList[position]
        holder.titleTextView.text = widgetItem.title
        holder.titleTextView.setBackgroundColor(widgetItem.color)
    }

    override fun getItemCount(): Int {
        return widgetList.size
    }
}
