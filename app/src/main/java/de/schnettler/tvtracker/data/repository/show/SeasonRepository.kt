package de.schnettler.tvtracker.data.repository.show

import androidx.lifecycle.Transformations
import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.db.ShowDao
import de.schnettler.tvtracker.data.mapping.ListMapper
import de.schnettler.tvtracker.data.mapping.ListMapperWithId
import de.schnettler.tvtracker.data.mapping.SeasonSummaryMapper
import de.schnettler.tvtracker.data.mapping.SeasonWithEpisodeMapper
import timber.log.Timber

class SeasonRepository (private val remoteService: ShowDataSourceRemote, private val localDao: ShowDao) {

}