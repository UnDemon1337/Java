package com.example.escapefromtarkovassistant
import ActiveCraftItem
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class CraftingTimerFragment : Fragment(R.layout.fragment_crafting_timer) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CraftingAdapter
    private lateinit var timersRecyclerView: RecyclerView
    private lateinit var activeTimersAdapter: ActiveTimersAdapter

    private val activeTimers = mutableListOf<ActiveCraftItem>() // Список активных таймеров
    private val craftItems = mutableListOf<CraftableItem>() // Список предметов крафта

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка RecyclerView для предметов крафта
        recyclerView = view.findViewById(R.id.itemsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val dbHelper = DBHelper(requireContext(), null)
        val items = dbHelper.getCraftingItems()

        // Заполняем список предметов
        craftItems.addAll(items)

        // Передаем callback для обработки клика по предмету и удалению
        adapter = CraftingAdapter(craftItems, { selectedItem ->
            addCraftingItemToTimers(selectedItem)
        }, { position ->
            removeItemFromCraftList(position)
        })
        recyclerView.adapter = adapter

        // Настройка RecyclerView для активных таймеров
        timersRecyclerView = view.findViewById(R.id.timersRecyclerView)
        timersRecyclerView.layoutManager = LinearLayoutManager(context)

        activeTimersAdapter = ActiveTimersAdapter(activeTimers)
        timersRecyclerView.adapter = activeTimersAdapter
    }

    private fun addCraftingItemToTimers(selectedItem: CraftableItem) {
        // Преобразуем время в миллисекунды для таймера
        val timeString = selectedItem.craftingTime

        // Добавляем "00:" если в формате только минуты и секунды
        val formattedTime = if (timeString.count { it == ':' } == 1) {
            "00:$timeString" // добавляем "00:" перед временем
        } else {
            timeString
        }

        // Разбираем строку времени в формате HH:mm:ss
        val timeParts = formattedTime.split(":").map { it.toIntOrNull() }

        // Проверим, что массив содержит правильное количество элементов и все они валидны
        if (timeParts.size == 3 && timeParts.all { it != null }) {
            val timeInMillis = (timeParts[0]!! * 3600 + timeParts[1]!! * 60 + timeParts[2]!!) * 1000L

            // Создаем новый элемент таймера
            val activeItem = ActiveCraftItem(
                id = 0,
                name = selectedItem.name,
                icon = selectedItem.iconResId,
                craftingTime = selectedItem.craftingTime,
                remainingTime = timeInMillis
            )

            // Добавляем в список активных таймеров
            activeTimers.add(activeItem)

            // Обновляем адаптер
            activeTimersAdapter.notifyItemInserted(activeTimers.size - 1)

            // Создаем отдельный таймер для каждого элемента
            startItemTimer(activeItem)
        } else {
            Log.e("CraftingTimerFragment", "Ошибка в формате времени: ${selectedItem.craftingTime}")
        }
    }
    private fun removeItemFromCraftList(position: Int) {
        // Удаляем элемент из списка
        craftItems.removeAt(position)

        // Уведомляем адаптер об изменениях
        adapter.notifyItemRemoved(position)
    }

    private fun startItemTimer(item: ActiveCraftItem) {
        val countDownTimer = object : CountDownTimer(item.remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                item.remainingTime = millisUntilFinished
                activeTimersAdapter.notifyDataSetChanged() // Обновляем адаптер
            }

            override fun onFinish() {
                item.remainingTime = 0
                activeTimersAdapter.notifyDataSetChanged() // Обновляем адаптер
                // Уведомляем о завершении таймера
                sendTimerFinishedNotification(requireContext(), item.name)

                // Удаляем таймер из списка активных
                activeTimers.remove(item)
                activeTimersAdapter.notifyDataSetChanged() // Обновляем адаптер после удаления
            }
        }
        countDownTimer.start()
    }

    // Метод для отправки уведомлений о завершении таймера
    private fun sendTimerFinishedNotification(context: Context, timerName: String) {
        val channelId = "timer_channel"
        val channelName = "Timer Notifications"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Для Android 8.0 и выше необходимо создать NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Channel for timer notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Создание уведомления
        val notification: Notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Иконка уведомления
            .setContentTitle("Таймер завершён!")
            .setContentText("Таймер для крафта \"$timerName\" завершён.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Уведомление исчезает после клика
            .build()

        // Отправка уведомления с уникальным ID
        val notificationId = System.currentTimeMillis().toInt() // Уникальный ID
        notificationManager.notify(notificationId, notification)
    }
}

