package com.example.escapefromtarkovassistant

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "CraftingDB", factory, 14) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createCraftingItemsTable = """
            CREATE TABLE CraftingItems (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                icon INTEGER NOT NULL,
                crafting_time TEXT NOT NULL
            );
        """.trimIndent()
        db?.execSQL(createCraftingItemsTable)

        // Таблица квестов
        val createQuestsTable = """
            CREATE TABLE Quests (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                trader TEXT NOT NULL,
                level_required INTEGER NOT NULL,
                short_description TEXT NOT NULL,
                detailed_description TEXT NOT NULL,
                image_resource INTEGER NOT NULL,
                is_completed INTEGER DEFAULT 0
            );
        """.trimIndent()
        db?.execSQL(createQuestsTable)

        // Создание таблицы квестовых предметов
        val createQuestItemsTable = """
        CREATE TABLE QuestItems (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            icon INTEGER NOT NULL,
            quantity INTEGER NOT NULL DEFAULT 0
        );
    """.trimIndent()
        db?.execSQL(createQuestItemsTable)

        populateInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS CraftingItems")
        db?.execSQL("DROP TABLE IF EXISTS Quests")
        db?.execSQL("DROP TABLE IF EXISTS QuestItems")
        onCreate(db)
    }

    // Вставка элементов в таблицу CraftingItems
    fun insertCraftingItem(name: String, icon: Int, craftingTime: String) {
        val db = this.writableDatabase
        val query = """
            INSERT INTO CraftingItems (name, icon, crafting_time) 
            VALUES (?, ?, ?)
        """.trimIndent()
        db.execSQL(query, arrayOf(name, icon, craftingTime))
    }

    // Получение всех элементов из CraftingItems
    fun getCraftingItems(): List<CraftableItem> {
        val db = this.readableDatabase
        val query = "SELECT * FROM CraftingItems"
        val cursor = db.rawQuery(query, null)

        val items = mutableListOf<CraftableItem>()
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val icon = cursor.getInt(cursor.getColumnIndexOrThrow("icon"))
                val craftingTime = cursor.getString(cursor.getColumnIndexOrThrow("crafting_time"))
                items.add(CraftableItem(icon, name, craftingTime))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return items
    }

    // Функция для наполнения начальными данными в таблицы
    private fun populateInitialData(db: SQLiteDatabase?) {
        val cursor = db?.query("CraftingItems", null, null, null, null, null, null)
        if (cursor?.count == 0) { // Если данных нет, добавляем начальные
            val initialData = listOf(
                CraftableItem(R.drawable.car_battery_icon, "Аккумулятор", "00:04:00"),
                CraftableItem(R.drawable.can_of_thermite_icon, "Термит", "00:01:30"),
                CraftableItem(R.drawable.gunpowder_eagle_icon, "Оружейный порох", "01:55:15"),
                CraftableItem(R.drawable.microcontrollerboard_icon, "Микроконтроллер", "00:00:05")
            )
            initialData.forEach { item ->
                val query = """
                    INSERT INTO CraftingItems (name, icon, crafting_time) 
                    VALUES (?, ?, ?)
                """.trimIndent()
                db?.execSQL(query, arrayOf(item.name, item.iconResId, item.craftingTime))
            }
        }
        cursor?.close()

        populateQuests(db)

        val questItems = listOf(
            QuestItem(id = 1, name = "Болты", icon = R.drawable.bolts, quantity = 15),
            QuestItem(id = 2, name = "Сигареты", icon = R.drawable.cigarettes, quantity = 1),
            QuestItem(id = 3, name = "Процессорный кулер", icon = R.drawable.cpufan, quantity = 0),
            QuestItem(id = 4, name = "Жесткий диск", icon = R.drawable.hdd, quantity = 5),
            QuestItem(id = 5, name = "Снаряд ОФЗ", icon = R.drawable.ofz_shell, quantity = 3),
            QuestItem(id = 6, name = "Паракорд", icon = R.drawable.paracord, quantity = 2),
            QuestItem(id = 7, name = "Изолента", icon = R.drawable.tape, quantity = 7)
        )
        questItems.forEach { item ->
            val query = """
            INSERT INTO QuestItems (name, icon, quantity)
            VALUES (?, ?, ?)
        """.trimIndent()
            db?.execSQL(query, arrayOf(item.name, item.icon, item.quantity))
        }
    }
    fun getQuestItems(): List<QuestItem> {
        val db = this.readableDatabase
        val query = "SELECT id, name, icon, quantity FROM QuestItems"
        val cursor = db.rawQuery(query, null)

        val items = mutableListOf<QuestItem>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val icon = cursor.getInt(cursor.getColumnIndexOrThrow("icon"))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))
                items.add(QuestItem(id, icon, name, quantity))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return items
    }

    fun updateQuantity(itemId: Int, newQuantity: Int) {
        val db = this.writableDatabase
        val query = "UPDATE QuestItems SET quantity = ? WHERE id = ?"
        db.execSQL(query, arrayOf(newQuantity, itemId))
    }



    // Функция для наполнения таблицы Quests начальными данными
    private fun populateQuests(db: SQLiteDatabase?) {
        val cursor = db?.query("Quests", null, null, null, null, null, null)
        if (cursor?.count == 0) {
            val initialQuests = listOf(
                Quest(
                    id = 0,
                    name = "Посылка из прошлого",
                    trader = "Прапор",
                    isCompleted = false,
                    levelRequired = 10,
                    shortDescription = "Найти папку с информацией 022",
                    detailedDescription = "Найти защищенный кейс в кабинете директора Tarcone на таможенном терминале\n" +
                            "Оставить груз на Заводе во времянке на 2-м этаже около Ворот 3\n" +
                            "Выжить и выйти с локации Завод",
                    imageResource = R.drawable.posylka_iz_proshlogo
                ),
                Quest(
                    id = 1,
                    name = "Нефтянка",
                    trader = "Прапор",
                    isCompleted = false,
                    levelRequired = 15,
                    shortDescription = "Найти и пометить цистерны с топливом",
                    detailedDescription = "Найти и пометить первую прицеп-цистерну Маркером MS2000 на Таможне\n" +
                            "Найти и пометить вторую прицеп-цистерну Маркером MS2000 на Таможне\n" +
                            "Найти и пометить третью прицеп-цистерну Маркером MS2000 на Таможне\n" +
                            "Найти и пометить четвертую прицеп-цистерну Маркером MS2000 на Таможне\n" +
                            "Выжить и выйти с локации",
                    imageResource = R.drawable.netyanka_map
                ),
                Quest(
                    id = 3,
                    name = "Дефицит",
                    trader = "Терапевт",
                    isCompleted = false,
                    levelRequired = 7,
                    shortDescription = "Найти в рейда и передать аптечки",
                    detailedDescription = "Найти в рейде 3 Аптечка Salewa First Aid Kit\n" +
                            "    Передать 3 Аптечка Salewa First Aid Kit",
                    imageResource = R.drawable.salewa
                ),
                Quest(
                    id = 4,
                    name = "Водолей",
                    trader = "Терапевт",
                    isCompleted = false,
                    levelRequired = 16,
                    shortDescription = "Найти спрятанную воду в общежитии на локации таможня",
                    detailedDescription = "Найти спрятанную воду в общежитии\n" +
                            "    Выжить и выйти с локации Таможня",
                    imageResource = R.drawable.aquarius
                ),
                Quest(
                    id = 5,
                    name = "Сигнал",
                    trader = "Механик",
                    isCompleted = false,
                    levelRequired = 20,
                    shortDescription = "Установить подавители сигнала на карте Берег",
                    detailedDescription = "Установить первый Подавитель сигналов в условленном месте на Берегу\n" +
                            "Установить второй Подавитель сигналов в условленном месте на Берегу\n" +
                            "Установить третий Подавитель сигналов в условленном месте на Берегу\n" +
                            "Выжить и выйти с локации",
                    imageResource = R.drawable.signal
                ),
                Quest(
                    id = 6,
                    name = "Оружейник",
                    trader = "Механик",
                    isCompleted = false,
                    levelRequired = 13,
                    shortDescription = "Модифицировать HK 416A5 в соответствии с требуемой спецификацией",
                    detailedDescription = "Нужно собрать HK 416A5 со следующими параметрами:\n" +
                            "\n" +
                            "Прочность не менее 80\n" +
                            "Эргономика от 60\n" +
                            "Суммарная отдача 300 или меньше\n" +
                            "Вес не более 4 кг\n" +
                            "Глушитель SureFire \"SOCOM556-RC2\" 5.56x45\n" +
                            "Тактическая рукоятка Magpul \"RVG\" (FDE)\n" +
                            "Голографический прицел EOTech \"EXPS3\" (Песочный)\n" +
                            "Приклад Magpul \"UBR GEN2\" (FDE)\n" +
                            "Пистолетная рукоятка Magpul \"MOE\" для AR-15 (FDE)\n" +
                            "Тактический блок \"LA-5B/PEQ\"\n" +
                            "Магазин (Принимаются большинство, но не все магазины - цвет FDE не требуется)",
                    imageResource = R.drawable.gunsmith
                ),
                Quest(
                    id = 7,
                    name = "Реагент",
                    trader = "Лыжник",
                    isCompleted = false,
                    levelRequired = 32,
                    shortDescription = "Найти какую-либо информацию о жизни друга Лыжника",
                    detailedDescription = "Найти какую-то информацию о прошлой жизни замначальника\n" +
                            "(опционально) Найти место ночлега бывшего замначальника службы безопасности\n" +
                            "Передать ее Лыжнику\n" +
                            "Найти предметы, которые могут помочь расследованию\n" +
                            "Передать их Лыжнику",
                    imageResource = R.drawable.reagent
                ),
                Quest(
                    id = 8,
                    name = "Кремень",
                    trader = "Лыжник",
                    isCompleted = false,
                    levelRequired = 16,
                    shortDescription = "Повысить навык стрессоустойчивости до 6 уровня",
                    detailedDescription = "Навык стрессоустойчивости персонажа прокачивается при беге, прыжках и лечении ранений",
                    imageResource = R.drawable.flint
                ),
                Quest(
                    id = 9,
                    name = "Путь выживальщика: запасливый",
                    trader = "Егерь",
                    isCompleted = false,
                    levelRequired = 27,
                    shortDescription = "Заложить припасы в бункерах",
                    detailedDescription = "Заложить пакет \"Искра\" в бункер ЗБ-016\n" +
                            "Заложить бутылку воды в бункер ЗБ-016\n" +
                            "Заложить пакет \"Искра\" в бункер ЗБ-014\n" +
                            "Заложить бутылку воды в бункер ЗБ-014",
                    imageResource = R.drawable.bunkers
                ),
                Quest(
                    id = 10,
                    name = "Ностальгия",
                    trader = "Егерь",
                    isCompleted = false,
                    levelRequired = 36,
                    shortDescription = "Найти фотоальбом Егеря",
                    detailedDescription = "Найти номер с видом на залив где отдыхал Егерь\n" +
                            "Найти фотоальбом Егеря\n" +
                            "Вернуть Егерю фотоальбом",
                    imageResource = R.drawable.photoalbum
                )
            )
            initialQuests.forEach { quest ->
                val query = """
                    INSERT INTO Quests (
                        name, trader, level_required, short_description, detailed_description, image_resource, is_completed
                    ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """.trimIndent()
                db?.execSQL(
                    query,
                    arrayOf(
                        quest.name,
                        quest.trader,
                        quest.levelRequired,
                        quest.shortDescription,
                        quest.detailedDescription,
                        quest.imageResource,
                        if (quest.isCompleted) 1 else 0
                    )
                )
            }
        }
        cursor?.close()
    }

    // Получение всех квестов
    fun getQuests(): List<Quest> {
        val db = this.readableDatabase
        val query = "SELECT * FROM Quests"
        val cursor = db.rawQuery(query, null)

        val quests = mutableListOf<Quest>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val trader = cursor.getString(cursor.getColumnIndexOrThrow("trader"))
                val levelRequired = cursor.getInt(cursor.getColumnIndexOrThrow("level_required"))
                val shortDescription = cursor.getString(cursor.getColumnIndexOrThrow("short_description"))
                val detailedDescription = cursor.getString(cursor.getColumnIndexOrThrow("detailed_description"))
                val imageResource = cursor.getInt(cursor.getColumnIndexOrThrow("image_resource"))
                val isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("is_completed")) == 1

                quests.add(
                    Quest(
                        id = id,
                        name = name,
                        trader = trader,
                        levelRequired = levelRequired,
                        shortDescription = shortDescription,
                        detailedDescription = detailedDescription,
                        imageResource = imageResource,
                        isCompleted = isCompleted
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return quests
    }

    // Обновление статуса квеста (завершен/незавершен)
    fun updateQuestCompletionStatus(questId: Int, isCompleted: Boolean) {
        val db = this.writableDatabase
        val query = """
        UPDATE Quests
        SET is_completed = ?
        WHERE id = ?
    """.trimIndent()
        db.execSQL(query, arrayOf(if (isCompleted) 1 else 0, questId))
    }
}
