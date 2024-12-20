package com.example.escapefromtarkovassistant

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class QuestMapFragment : Fragment(R.layout.fragment_questmap) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var questAdapter: QuestAdapter
    private val dbHelper: DBHelper by lazy { DBHelper(requireContext(), null) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.quest_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        val allQuests = dbHelper.getQuests()
        Log.d("QuestMapFragment", "Loaded ${allQuests.size} quests from DB.")

        questAdapter = QuestAdapter(
            questList = allQuests.toMutableList(),
            onQuestComplete = { quest ->
                // Обновляем квест в базе данных
                dbHelper.updateQuestCompletionStatus(quest.id, quest.isCompleted)
            }
        )
        recyclerView.adapter = questAdapter

        // Фильтрация по торговцу (спиннер)
        val spinner = view.findViewById<Spinner>(R.id.trader_spinner)
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.traders,
            R.layout.spinner_dropdown_item
        )
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedTrader = parent?.getItemAtPosition(position).toString().trim()
                val filteredQuests = if (selectedTrader == "Все") {
                    allQuests
                } else {
                    allQuests.filter { it.trader == selectedTrader }
                }
                questAdapter.updateList(filteredQuests)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}
