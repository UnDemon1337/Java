package com.example.escapefromtarkovassistant

import android.os.Parcel
import android.os.Parcelable

data class Quest(
    val id: Int, // Уникальный идентификатор
    val name: String, // Название квеста
    val trader: String, // Торговец, который дает квест
    val levelRequired: Int, // Уровень, на котором становится доступным
    val shortDescription: String, // Краткое описание выполнения
    val detailedDescription: String, // Подробное описание выполнения
    val imageResource: Int, // Ресурс изображения квеста
    var isCompleted: Boolean = false // Флаг выполнения квеста
) : Parcelable {

    // Метод, который описывает содержимое объекта для сериализации
    override fun describeContents(): Int {
        return 0 // Обычно возвращаем 0, если нет специфических флагов
    }

    // Метод для записи данных объекта в Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(trader)
        parcel.writeInt(levelRequired)
        parcel.writeString(shortDescription)
        parcel.writeString(detailedDescription)
        parcel.writeInt(imageResource)
        parcel.writeByte(if (isCompleted) 1 else 0) // Преобразуем Boolean в 1 или 0
    }

    // Создание объекта из Parcel
    companion object CREATOR : Parcelable.Creator<Quest> {
        override fun createFromParcel(parcel: Parcel): Quest {
            return Quest(
                id = parcel.readInt(),
                name = parcel.readString() ?: "",
                trader = parcel.readString() ?: "",
                levelRequired = parcel.readInt(),
                shortDescription = parcel.readString() ?: "",
                detailedDescription = parcel.readString() ?: "",
                imageResource = parcel.readInt(),
                isCompleted = parcel.readByte() != 0.toByte() // Преобразуем 1 или 0 обратно в Boolean
            )
        }

        override fun newArray(size: Int): Array<Quest?> {
            return arrayOfNulls(size)
        }
    }
}
