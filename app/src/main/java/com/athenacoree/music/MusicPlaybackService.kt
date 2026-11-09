package com.athenacoree.music

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture

class MusicPlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this).build().apply { setHandleAudioBecomingNoisy(true) }
        mediaSession = MediaSession.Builder(this, player).build()
    }
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession
    override fun onDestroy() {
        mediaSession?.let { it.player.release(); it.release() }
        mediaSession = null
        super.onDestroy()
    }
}

internal object MusicPlaybackController {
    @Volatile private var controller: MediaController? = null
    @Volatile private var pending: ListenableFuture<MediaController>? = null
    private fun getController(context: Context, action: (MediaController) -> Unit) {
        controller?.let { action(it); return }
        synchronized(this) {
            controller?.let { action(it); return }
            val app = context.applicationContext
            val token = SessionToken(app, ComponentName(app, MusicPlaybackService::class.java))
            val future = pending ?: MediaController.Builder(app, token).buildAsync().also { pending = it }
            future.addListener({
                runCatching { future.get() }.onSuccess { connected ->
                    synchronized(this) { controller = connected; pending = null }
                    action(connected)
                }
            }, app.mainExecutor)
        }
    }
    fun play(context: Context, uri: Uri, title: String) = playQueue(context, listOf(uri to title))
    fun playQueue(context: Context, tracks: List<Pair<Uri, String>>, startIndex: Int = 0) {
        if (tracks.isEmpty()) return
        getController(context) { player ->
            val items = tracks.map { (uri, title) ->
                MediaItem.Builder().setUri(uri).setMediaMetadata(MediaMetadata.Builder().setTitle(title).build()).build()
            }
            player.setMediaItems(items, startIndex.coerceIn(0, items.lastIndex), 0L)
            player.prepare()
            player.play()
        }
    }
    fun toggle(context: Context) = getController(context) { if (it.isPlaying) it.pause() else it.play() }
    fun next(context: Context) = getController(context) { it.seekToNextMediaItem() }
    fun previous(context: Context) = getController(context) { it.seekToPreviousMediaItem() }
}