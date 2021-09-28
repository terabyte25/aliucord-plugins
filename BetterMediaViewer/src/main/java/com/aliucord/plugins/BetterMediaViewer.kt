package com.aliucord.plugins

import android.content.Context
import com.aliucord.Logger
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.plugins.bettermediaviewer.Patches
import com.aliucord.plugins.bettermediaviewer.PluginSettings

@AliucordPlugin
class BetterMediaViewer : Plugin() {
    init {
        settingsTab = SettingsTab(PluginSettings::class.java).withArgs(settings)
    }

    companion object {
        val logger = Logger("BetterMediaViewer")
    }

    override fun start(context: Context) {
        with(Patches(patcher), {
            patchMenu()
            patchControls()

            if (settings.getBool("immersiveModeState", false)) patchImmersiveMode()
            if (settings.getBool("hideBackButton", false)) patchBackButton()
            if (settings.getBool("bottomToolbar", false)) patchToolbar()
            if (settings.getBool("removeZoomLimit", true)) patchZoomLimit()
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}