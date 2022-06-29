package com.example.aoa.services

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.aoa.helpers.Global

class AlwaysOnTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTile(Global.currentAlwaysOnState(this))
    }

    override fun onClick() {
        Global.changeAlwaysOnState(this)
    }

    private fun updateTile(isActive: Boolean) {
        val tile = qsTile
        val newState: Int = if (isActive) {
            Tile.STATE_ACTIVE
        } else {
            Tile.STATE_INACTIVE
        }
        tile.state = newState
        tile.updateTile()
    }
}