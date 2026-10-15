package com.khodehydro.faimess

import android.content.Context

/**
 * Minimal application-context holder so the [PlaybackViewModel] can create a
 * [MediaPlayer] without passing a Context through every constructor.
 * Populated once in [MainActivity.onCreate].
 */
object FaimessAppContextHolder {
    lateinit var appContext: Context
}