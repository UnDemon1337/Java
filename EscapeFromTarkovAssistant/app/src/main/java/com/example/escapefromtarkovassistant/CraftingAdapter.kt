package com.example.escapefromtarkovassistant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CraftingAdapter(
    private val items: MutableList<CraftableItem>, // Используем MutableList для возможности изменения списка
    private val onItemClick: (CraftableItem) -> Unit, // Callback для кликов по элементам
    private val onDeleteClick: (Int) -> Unit // Callback для клика по кнопке удаления
) : RecyclerView.Adapter<CraftingAdapter.CraftingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CraftingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.craftable_item, parent, false)
        return CraftingViewHolder(view)
    }

    override fun onBindViewHolder(holder: CraftingViewHolder, position: Int) {
        val item = items[position]
        holder.itemIcon.setImageResource(item.iconResId)
        holder.itemName.text = item.name
        holder.craftingTime.text = item.craftingTime

        // Обработка клика по элементу
        holder.itemView.setOnClickListener {
            onItemClick(item) // Вызов callback при клике
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class CraftingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemIcon: ImageView = itemView.findViewById(R.id.timer_icon)
        val itemName: TextView = itemView.findViewById(R.id.timer_name)
        val craftingTime: TextView = itemView.findViewById(R.id.timer_time_left)
    }
}
