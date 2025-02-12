package ru.musindev.graduate_work.data.local.dao

import androidx.room.*
import ru.musindev.graduate_work.data.local.request.SearchRequest

@Dao
interface SearchRequestDao {

    // Вставляем новый запрос
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: SearchRequest)

    // Получаем последние 3 запроса
    @Query("SELECT * FROM search_requests ORDER BY id DESC LIMIT 3")
    suspend fun getLastThreeRequests(): List<SearchRequest>

    // Удаляем все записи, кроме последних 3
    @Query("DELETE FROM search_requests WHERE id NOT IN (SELECT id FROM search_requests ORDER BY id DESC LIMIT 3)")
    suspend fun deleteOldRequests()
}
