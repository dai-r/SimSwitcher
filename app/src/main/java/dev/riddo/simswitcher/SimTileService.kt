package dev.riddo.simswitcher

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

/**
 * Quick Settings タイル (通知バー → 下スワイプで表示されるタイル)
 *
 * 追加方法:
 *   通知バーを2回下スワイプ → 鉛筆アイコン(編集) → 「SIM切替」をドラッグ追加
 *
 * タップするとSIM設定画面へ直接ジャンプする。
 * TileService からのアクティビティ起動は startActivityAndCollapse() を使う必要がある。
 */
class SimTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()
        val intent = buildBestIntent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        // API 34+ は PendingIntent 版が必要 (Intent 版は deprecated)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val pending = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            startActivityAndCollapse(pending)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        val simList = SimHelper.getSimList(applicationContext)

        tile.state = Tile.STATE_ACTIVE
        tile.label = getString(R.string.tile_label)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = when (simList.size) {
                0    -> "SIMなし"
                1    -> "SIM×1"
                else -> "SIM×${simList.size}"
            }
        }

        tile.updateTile()
    }

    /** SIM設定を開くのと同じ遷移先 (MANAGE_ALL_SIM_PROFILES_SETTINGS) */
    private fun buildBestIntent(): Intent {
        val candidates = listOf(
            Intent("android.settings.MANAGE_ALL_SIM_PROFILES_SETTINGS"),
            Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS),
            Intent(Settings.ACTION_WIRELESS_SETTINGS)
        )
        for (intent in candidates) {
            val resolves = packageManager.resolveActivity(intent, 0) != null
            if (resolves) return intent
        }
        return candidates.last()
    }
}
