package ru.fitsuli.developerslifeviewer.utils

import kotlinx.serialization.Serializable

@Serializable
data class DLifeData(
    val result: List<Result>,
    val totalCount: Int // 12924
)