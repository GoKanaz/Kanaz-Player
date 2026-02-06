package com.gokanaz.kanazplayer.core.common.local

object Config {
    const val NOTIFICATION_CHANNEL_ID = "kanaz_music_channel"
    const val NOTIFICATION_ID = 1
    const val MEDIA_ROOT_ID = "kanaz_player_root"
    const val MEDIA_SESSION_TAG = "KanazMusicService"
    
    const val QUERY_ORDER_BY = "${android.provider.MediaStore.Audio.Media.DATE_ADDED} DESC"
    const val QUERY_SELECTION = "${android.provider.MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${android.provider.MediaStore.Audio.Media.DURATION} >= 60000"
    
    const val SEEK_INCREMENT_MS = 10000L
    const val PLAYBACK_SPEED_MIN = 0.5f
    const val PLAYBACK_SPEED_MAX = 2.0f
    const val PLAYBACK_SPEED_DEFAULT = 1.0f
    
    const val MAX_RECENT_SONGS = 50
    const val MAX_QUEUE_SIZE = 500
    const val CACHE_SIZE_BYTES = 100 * 1024 * 1024L
}