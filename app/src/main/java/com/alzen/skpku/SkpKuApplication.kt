package com.alzen.skpku

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main Application class for the SkpKu app.
 * Annotated with [HiltAndroidApp] to trigger Hilt's code generation.
 */
@HiltAndroidApp
class SkpKuApplication : Application()
