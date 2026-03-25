package dev.dair.simswitcher

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.widget.Toast

object SimHelper {

    /**
     * アクティブなSIMカードの一覧を返す。
     * READ_PHONE_STATE 権限がない場合は空リスト。
     */
    fun getSimList(context: Context): List<SubscriptionInfo> {
        return try {
            val sm = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE)
                    as SubscriptionManager
            sm.activeSubscriptionInfoList ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * デフォルトの通話用SIMのサブスクリプションIDを返す。
     */
    fun getDefaultVoiceSubId(context: Context): Int {
        return try {
            SubscriptionManager.getDefaultVoiceSubscriptionId()
        } catch (e: Exception) {
            SubscriptionManager.INVALID_SUBSCRIPTION_ID
        }
    }

    /**
     * デフォルトのデータ用SIMのサブスクリプションIDを返す。
     */
    fun getDefaultDataSubId(context: Context): Int {
        return try {
            SubscriptionManager.getDefaultDataSubscriptionId()
        } catch (e: Exception) {
            SubscriptionManager.INVALID_SUBSCRIPTION_ID
        }
    }

    /**
     * デフォルトのSMS用SIMのサブスクリプションIDを返す。
     */
    fun getDefaultSmsSubId(context: Context): Int {
        return try {
            SubscriptionManager.getDefaultSmsSubscriptionId()
        } catch (e: Exception) {
            SubscriptionManager.INVALID_SUBSCRIPTION_ID
        }
    }

    // ── SIM設定を開くメソッド群 ────────────────────────────────

    /** 個別SIMの詳細設定画面を開く */
    fun openSimDetail(context: Context, subscriptionId: Int) {
        tryStartActivity(context, buildList {
            add(Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS).apply {
                putExtra("android.provider.extra.SUB_ID", subscriptionId)
            })
            add(Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS))
        })
    }

    /** SIM設定画面を開く (SIM一覧・プライマリSIM設定がある画面) */
    fun openSimSettings(context: Context) {
        tryStartActivity(context, listOf(
            Intent("android.settings.MANAGE_ALL_SIM_PROFILES_SETTINGS"),
            Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS),
            Intent(Settings.ACTION_WIRELESS_SETTINGS)
        ))
    }

    /** ネットワーク設定画面を開く (WIRELESS_SETTINGS) */
    fun openNetworkSettings(context: Context) {
        tryStartActivity(context, listOf(
            Intent(Settings.ACTION_WIRELESS_SETTINGS)
        ))
    }

    /** インテントリストを順に試し、最初に成功したものでアクティビティを起動する */
    private fun tryStartActivity(context: Context, intents: List<Intent>) {
        for (intent in intents) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(intent)
                return
            } catch (_: Exception) {
                // 次のインテントを試す
            }
        }
        Toast.makeText(context, "設定画面を開けませんでした", Toast.LENGTH_SHORT).show()
    }
}
