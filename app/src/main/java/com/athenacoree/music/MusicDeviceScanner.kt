package com.athenacoree.music

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal data class DeviceScanResult(val imported: Int, val hasMore: Boolean, val scanned: Int)

internal object MusicDeviceScanner {
    private const val BATCH_SIZE = 5
    private const val PREFS = "music_device_scan"
    private const val OFFSET = "audio_offset"

    suspend fun scanNextAudioBatch(context: Context, repository: MusicLibraryRepository): DeviceScanResult =
        withContext(Dispatchers.IO) {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val offset = prefs.getInt(OFFSET, 0)
            val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION
            )
            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
            val sort = "${MediaStore.Audio.Media.DATE_ADDED} DESC LIMIT $BATCH_SIZE OFFSET $offset"
            val found = mutableListOf<LocalTrackEntity>()
            context.contentResolver.query(collection, projection, selection, null, sort)?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                while (cursor.moveToNext()) {
                    val mediaId = cursor.getLong(idCol)
                    val uri = ContentUris.withAppendedId(collection, mediaId)
                    found += LocalTrackEntity(
                        id = uri.toString(),
                        title = cursor.getString(titleCol) ?: "Canción sin título",
                        artistName = cursor.getString(artistCol)?.takeUnless { it == "<unknown>" } ?: "",
                        albumTitle = cursor.getString(albumCol)?.takeUnless { it == "<unknown>" } ?: "",
                        mediaUri = uri.toString(),
                        source = "device",
                        durationMs = cursor.getLong(durationCol)
                    )
                }
            }
            if (found.isNotEmpty()) repository.addTracks(found)
            prefs.edit().putInt(OFFSET, offset + found.size).apply()
            DeviceScanResult(found.size, found.size == BATCH_SIZE, offset + found.size)
        }

    fun resetAudioScan(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().remove(OFFSET).apply()
    }
}
