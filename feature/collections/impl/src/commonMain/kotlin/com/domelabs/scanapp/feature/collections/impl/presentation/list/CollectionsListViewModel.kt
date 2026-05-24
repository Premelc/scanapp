package com.domelabs.scanapp.feature.collections.impl.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domelabs.scanapp.core.navigation.NavRoute
import com.domelabs.scanapp.core.navigation.NavigationDispatcher
import com.domelabs.scanapp.feature.collections.impl.domain.model.CollectionSummary
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.ObserveCollectionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class CollectionsListViewModel(
    observeCollectionsUseCase: ObserveCollectionsUseCase,
) : ViewModel() {

    private val showCreateSheet = MutableStateFlow(false)

    val viewState: StateFlow<CollectionsListViewState> = combine(
        observeCollectionsUseCase().map { it.toRows() },
        showCreateSheet,
    ) { rows, showSheet ->
        CollectionsListViewState(
            rows = rows,
            showCreateSheet = showSheet,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000L),
        CollectionsListViewState(),
    )

    fun onInteraction(interaction: CollectionsListInteraction) {
        when (interaction) {
            CollectionsListInteraction.Back -> viewModelScope.launch {
                NavigationDispatcher.back()
            }

            is CollectionsListInteraction.OpenCollection -> viewModelScope.launch {
                NavigationDispatcher.navigate(NavRoute.CollectionDetails(id = interaction.id))
            }

            CollectionsListInteraction.OpenCreateSheet -> {
                showCreateSheet.value = true
            }

            CollectionsListInteraction.DismissCreateSheet -> {
                showCreateSheet.value = false
            }
        }
    }

    private fun List<CollectionSummary>.toRows(): List<CollectionRowUi> {
        val now = Clock.System.now().toEpochMilliseconds()
        val unspecified = firstOrNull { it.collection.isSystemManaged }
        val rest = filter { !it.collection.isSystemManaged }
            .sortedBy { it.collection.name.lowercase() }
        val ordered = if (unspecified != null) listOf(unspecified) + rest else rest
        return ordered.map { it.toRowUi(now) }
    }

    private fun CollectionSummary.toRowUi(nowEpochMillis: Long): CollectionRowUi = CollectionRowUi(
        id = collection.id,
        name = collection.name,
        colorHex = collection.colorHex,
        itemCount = itemCount,
        itemCountLabel = itemCountLabel(itemCount),
        lastUpdatedLabel = lastUpdatedLabel(
            lastItemTimestampEpochMillis = lastItemTimestampEpochMillis,
            collectionUpdatedAt = collection.updatedAtEpochMillis,
            nowEpochMillis = nowEpochMillis,
        ),
        isSystemManaged = collection.isSystemManaged,
    )

    private fun itemCountLabel(count: Int): String = when {
        count == 0 -> "Empty"
        count == 1 -> "1 item"
        count > 99 -> "99+ items"
        else -> "$count items"
    }

    private fun lastUpdatedLabel(
        lastItemTimestampEpochMillis: Long?,
        collectionUpdatedAt: Long,
        nowEpochMillis: Long,
    ): String {
        val ts = lastItemTimestampEpochMillis ?: collectionUpdatedAt
        val delta = max(nowEpochMillis - ts, 0L)
        val seconds = delta / 1_000L
        val relative = when {
            seconds < 5 -> "just now"
            seconds < 60 -> "${seconds}s ago"
            seconds < 3_600 -> "${seconds / 60}m ago"
            seconds < 86_400 -> "${seconds / 3_600}h ago"
            else -> "${seconds / 86_400}d ago"
        }
        return if (lastItemTimestampEpochMillis == null) "Created $relative" else "Updated $relative"
    }
}
