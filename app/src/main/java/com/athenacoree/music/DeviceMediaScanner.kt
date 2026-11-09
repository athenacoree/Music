package com.athenacoree.music
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
internal data class ScanBatchResult(val imported: Int, val nextOffset: Int, val finished: Boolean)
internal class DeviceMediaScanner(private val context: Context, private val repository: MusicLibraryRepository) {
    suspend fun scanBatch(offset: Int, batchSize: Int = 5): ScanBatchResult = withContext(Dispatchers.IO) {
        val collection = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.MediaColumns.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM, MediaStore.MediaColumns.DURATION)
        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?, ?)"
        val args = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO.toString(), MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())
        val sort = "${MediaStore.MediaColumns.DATE_ADDED} DESC LIMIT $batchSize OFFSET $offset"
        val found = mutableListOf<LocalTrackEntity>()
        context.contentResolver.query(collection, projection, selection, args, sort)?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val typeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
            val titleCol = cursor.getColumnIndex(MediaStore.MediaColumns.TITLE)
            val artistCol = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val albumCol = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val durationCol = cursor.getColumnIndex(MediaStore.MediaColumns.DURATION)
            while (cursor.moveToNext()) {
                val uri = ContentUris.withAppendedId(collection, cursor.getLong(idCol))
                val video = cursor.getInt(typeCol) == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                val title = if (titleCol >= 0) cursor.getString(titleCol).orEmpty() else ""
                val artist = if (artistCol >= 0) cursor.getString(artistCol).orEmpty() else ""
                val album = if (albumCol >= 0) cursor.getString(albumCol).orEmpty() else ""
                val duration = if (durationCol >= 0 && !cursor.isNull(durationCol)) cursor.getLong(durationCol) else null
                found += LocalTrackEntity(uri.toString(), title.ifBlank { "Archivo multimedia" },
                    artist.takeUnless { it == "<unknown>" }.orEmpty(), album, mediaUri = uri.toString(),
                    source = if (video) "device-video" else "device-audio", durationMs = duration)
            }
        }
        repository.addTracks(found)
        ScanBatchResult(found.size, offset + found.size, found.size < batchSize)
    }
}