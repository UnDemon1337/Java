package com.example.escapefromtarkovassistant

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView


class QuestAdapter(
    private var questList: MutableList<Quest>, // Список квестов
    private val onQuestComplete: (Quest) -> Unit // Функция обратного вызова
) : RecyclerView.Adapter<QuestAdapter.QuestViewHolder>() {

    class QuestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val level: TextView = itemView.findViewById(R.id.quest_level)
        val title: TextView = itemView.findViewById(R.id.quest_title)
        val shortDescription: TextView = itemView.findViewById(R.id.quest_short_description)
        val completeButton: Button = itemView.findViewById(R.id.quest_complete_button)
        val detailsLink: TextView = itemView.findViewById(R.id.quest_details_link)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.quest_item, parent, false)
        return QuestViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestViewHolder, position: Int) {
        val quest = questList[position]

        holder.level.text = "Уровень: ${quest.levelRequired}"
        holder.title.text = quest.name
        holder.shortDescription.text = quest.shortDescription

        // Установим начальный текст и цвет кнопки
        if (quest.isCompleted) {
            holder.title.setTextColor(Color.RED) // Выполнено
            holder.completeButton.text = "Не выполнено"
        } else {
            holder.title.setTextColor(Color.GREEN) // Не выполнено
            holder.completeButton.text = "Выполнено"
        }

        // Обработчик для кнопки "Выполнено"
        holder.completeButton.setOnClickListener {
            // Меняем состояние квеста
            quest.isCompleted = !quest.isCompleted

            // Обновляем UI
            if (quest.isCompleted) {
                holder.title.setTextColor(Color.RED)
                holder.completeButton.text = "Не выполнено"
            } else {
                holder.title.setTextColor(Color.GREEN)
                holder.completeButton.text = "Выполнено"
            }

            // Вызываем обратный вызов для обновления состояния в базе данных
            onQuestComplete(quest)

            // Обновляем только этот элемент в списке
            notifyItemChanged(position)
        }

        holder.detailsLink.setOnClickListener {
            val questDetailsDialog = QuestDetailsDialogFragment.newInstance(quest)
            questDetailsDialog.show((holder.itemView.context as AppCompatActivity).supportFragmentManager, "QuestDetailsDialog")
        }

    }

    override fun getItemCount(): Int = questList.size

    fun updateList(newList: List<Quest>) {
        questList.clear()
        questList.addAll(newList) // Обновляем список
        notifyDataSetChanged()
    }
}
