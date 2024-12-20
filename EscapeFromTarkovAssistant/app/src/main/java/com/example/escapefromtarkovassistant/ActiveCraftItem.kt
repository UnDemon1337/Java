import android.util.Log
import java.util.concurrent.TimeUnit

data class ActiveCraftItem(
    val id: Int,
    val name: String,
    val icon: Int,
    val craftingTime: String, // Время будет храниться в формате "часы:минуты:секунды"
    var remainingTime: Long // Храним оставшееся время в миллисекундах
) {
    // Преобразуем строку времени в миллисекунды
// Метод для преобразования времени в миллисекунды
    fun getTimeInMillis(): Long {
        val timeParts = craftingTime.split(":").map { it.toIntOrNull() }

        // Проверим, что строка корректно разбита на части
        if (timeParts.size == 2) {
            // Если это MM:ss, добавим 0 для часов
            return (0 * 3600 + timeParts[0]!! * 60 + timeParts[1]!!) * 1000L
        } else if (timeParts.size == 3) {
            // Если это HH:mm:ss
            return (timeParts[0]!! * 3600 + timeParts[1]!! * 60 + timeParts[2]!!) * 1000L
        } else {
            Log.e("CraftingTimerFragment", "Неверный формат времени: $craftingTime")
            return 0L
        }
    }


    // Преобразует время обратно в строку в формате "часы:минуты:секунды"
    fun getTimeFormatted(): String {
        val hours = TimeUnit.MILLISECONDS.toHours(remainingTime)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTime) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTime) % 60

        // Форматируем время как чч:мм:сс
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }


}