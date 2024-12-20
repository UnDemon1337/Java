package com.example.escapefromtarkovassistant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuestItemsAdapter(
    private var items: MutableList<QuestItem>, // Изменяемый список
    private val onQuantityChanged: (Int, Int) -> Unit
) : RecyclerView.Adapter<QuestItemsAdapter.QuestItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_quest_item, parent, false)
        return QuestItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, position)
    }

    override fun getItemCount() = items.size

    fun updateItem(position: Int, newQuantity: Int) {
        items[position].quantity = newQuantity
        notifyItemChanged(position) // Обновляем только изменённую карточку
    }

    inner class QuestItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.itemName)
        private val iconImageView: ImageView = itemView.findViewById(R.id.itemIcon)
        private val quantityTextView: TextView = itemView.findViewById(R.id.itemQuantity)
        private val plusButton: Button = itemView.findViewById(R.id.buttonPlus)
        private val minusButton: Button = itemView.findViewById(R.id.buttonMinus)

        fun bind(item: QuestItem, position: Int) {
            nameTextView.text = item.name
            iconImageView.setImageResource(item.icon)
            quantityTextView.text = item.quantity.toString()

            plusButton.setOnClickListener {
                val newQuantity = item.quantity + 1
                quantityTextView.text = newQuantity.toString()
                onQuantityChanged(item.id, newQuantity)
                updateItem(position, newQuantity) // Синхронизация UI
            }

            minusButton.setOnClickListener {
                val newQuantity = maxOf(0, item.quantity - 1) // Не меньше 0
                quantityTextView.text = newQuantity.toString()
                onQuantityChanged(item.id, newQuantity)
                updateItem(position, newQuantity) // Синхронизация UI
            }
        }
    }
}
