package com.athenacoree.music

import kotlinx.serialization.Serializable

internal data class CatalogSummary(
    val artists: Int,
    val albums: Int,
    val tracks: Int,
    val videos: Int
)

@Serializable
private data class CatalogId(
    val id: String
)

/**
 * Read-only first connection check. The catalog tables are publicly readable
 * through RLS; user libraries and playlists remain private/authenticated.
 */
internal object MusicCatalogRepository {
    suspend fun loadSummary(): CatalogSummary {
        return CatalogSummary(
            artists = countRows("artists"),
            albums = countRows("albums"),
            tracks = countRows("tracks"),
            videos = countRows("videos")
        )
    }

    private suspend fun countRows(table: String): Int {
        return MusicSupabase.client
            .from(table)
            .select()
            .decodeList<CatalogId>()
            .size
    }
}
