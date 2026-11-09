package com.athenacoree.music

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal data class ScanBatchResult(val imported: Int, val nextOffset: Int, val finished: Boolean)

internal class DeviceMediaScanner(private val context: Context, private val repository: MusicLibraryRepository) {
    suspend fun scanBatch(offset: Int, batchSize: Int = 5): ScanBatchResult = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val found = mutableListOf<LocalTrackEntity>()
        val sources = listOf(
            Triple(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "audio", arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION)),
            Triple(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video", arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.ARTIST, MediaStore.Video.Media.ALBUM, MediaStore.Video.Media.DURATION))
        )
        var remaining = batchSize
        for ((collection, type, projection) in sources) {
            if (remaining <= 0) break
            val sort = "${MediaStore.MediaColumns.DATE_ADDED} DESC LIMIT $remaining OFFSET $offset"
            resolver.query(collection, projection, null, null, sort)?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val titleCol = cursor.getColumnIndex(MediaStore.MediaColumns.TITLE)
                val artistCol = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val albumCol = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
                val durationCol = cursor.getColumnIndex(MediaStore.MediaColumns.DURATION)
                while (cursor.moveToNext() && remaining > 0) {
                    val mediaId = cursor.getLong(idCol)
                    val uri = ContentUris.withAppendedId(collection, mediaId)
                    val title = if (titleCol >= 0) cursor.getString(titleCol).orEmpty() else ""
                    val artist = if (artistCol >= 0) cursor.getString(artistCol).orEmpty() else ""
                    val album = if (albumCol >= 0) cursor.getString(albumCol).orEmpty() else ""
                    val duration = if (durationCol >= 0 && !cursor.isNull(durationCol)) cursor.getLong(durationCol) else null
                    found += LocalTrackEntity(id = uri.toString(), title = title.ifBlank { "Archivo multimedia" }, artistName = artist.takeUnless { it == "<unknown>" }.orEmpty(), albumTitle = album, mediaUri = uri.toString(), source = "device-$type", durationMs = duration)
                    remaining--
                }
            }
        }
        repository.addTracks(found)
        ScanBatchResult(found.size, offset + found.size, found.size < batchSize)
    }
}