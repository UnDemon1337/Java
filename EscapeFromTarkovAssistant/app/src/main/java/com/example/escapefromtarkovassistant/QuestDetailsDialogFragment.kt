package com.example.escapefromtarkovassistant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class QuestDetailsDialogFragment : DialogFragment(R.layout.fragment_quest_details_dialog) {

    private lateinit var quest: Quest

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Получаем данные о квесте, переданные через аргументы
        quest = arguments?.getParcelable("quest")!!

        return inflater.inflate(R.layout.fragment_quest_details_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val questName: TextView = view.findViewById(R.id.quest_name)
        val questDescription: TextView = view.findViewById(R.id.quest_description)
        val questImage: ImageView = view.findViewById(R.id.quest_image)
        val closeButton: Button = view.findViewById(R.id.close_button)

        questName.text = quest.name
        questDescription.text = quest.detailedDescription
        questImage.setImageResource(quest.imageResource)

        closeButton.setOnClickListener {
            dismiss() // Закрываем диалог при нажатии на кнопку "Закрыть"
        }
    }

    companion object {
        fun newInstance(quest: Quest): QuestDetailsDialogFragment {
            val fragment = QuestDetailsDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable("quest", quest)
            fragment.arguments = bundle
            return fragment
        }
    }
}
