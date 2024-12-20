package com.example.escapefromtarkovassistant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class QuestItemsFragment : Fragment() {

    private lateinit var dbHelper: DBHelper
    private lateinit var adapter: QuestItemsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quest_items, container, false)
        dbHelper = DBHelper(requireContext(), null)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewQuestItems)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val questItems = dbHelper.getQuestItems().toMutableList() // Преобразуем в изменяемый список
        adapter = QuestItemsAdapter(questItems, onQuantityChanged = { itemId, newQuantity ->
            dbHelper.updateQuantity(itemId, newQuantity) // Обновляем данные в базе
        })
        recyclerView.adapter = adapter

        return view
    }
}
