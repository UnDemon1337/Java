package com.example.escapefromtarkovassistant

import ActiveCraftItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.TimeUnit

class ActiveTimersAdapter(private val items: MutableList<ActiveCraftItem>) : RecyclerView.Adapter<ActiveTimersAdapter.ActiveTimerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveTimerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.active_timer_item, parent, false)
        return ActiveTimerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActiveTimerViewHolder, position: Int) {
        val item = items[position]
        holder.timerIcon.setImageResource(item.icon)
        holder.timerName.text = item.name

        // Получаем время в формате HH:MM:SS
        holder.timerTimeLeft.text = item.getTimeFormatted()

    }

    override fun getItemCount(): Int {
        return items.size
    }

    // Метод для обновления всех таймеров
    fun updateTimers() {
        notifyDataSetChanged()
    }

    inner class ActiveTimerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timerIcon: ImageView = itemView.findViewById(R.id.timer_icon)
        val timerName: TextView = itemView.findViewById(R.id.timer_name)
        val timerTimeLeft: TextView = itemView.findViewById(R.id.timer_time_left)
    }
}
