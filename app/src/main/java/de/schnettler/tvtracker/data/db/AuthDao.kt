package de.schnettler.tvtracker.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import de.schnettler.tvtracker.data.models.AuthTokenEntity

@Dao
interface AuthDao {
    /*
     * Auth
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthToken(authTokenDB: AuthTokenEntity)

    @Query("SELECT * FROM table_auth WHERE tokenName = :type")
    fun getAuthToken(type: String): LiveData<AuthTokenEntity?>
}