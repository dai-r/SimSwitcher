package dev.riddo.simswitcher

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SubscriptionInfo
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import dev.riddo.simswitcher.databinding.ActivityMainBinding
import dev.riddo.simswitcher.databinding.ItemSimCardBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.READ_PHONE_STATE] == true) {
            loadSimInfo()
        } else {
            Toast.makeText(this, "SIM情報の表示にはアクセス許可が必要です", Toast.LENGTH_LONG).show()
            showPermissionDeniedState()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
        checkPermissionAndLoad()
    }

    override fun onResume() {
        super.onResume()
        // 設定画面から戻った際にSIM情報を再読み込み
        if (hasPhoneStatePermission()) {
            loadSimInfo()
        }
    }

    private fun setupButtons() {
        binding.btnOpenSimSettings.setOnClickListener {
            SimHelper.openSimSettings(this)
        }
        binding.btnOpenNetworkSettings.setOnClickListener {
            SimHelper.openNetworkSettings(this)
        }
    }

    private fun checkPermissionAndLoad() {
        if (hasPhoneStatePermission()) {
            loadSimInfo()
        } else {
            permissionLauncher.launch(arrayOf(Manifest.permission.READ_PHONE_STATE))
        }
    }

    private fun hasPhoneStatePermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) ==
                PackageManager.PERMISSION_GRANTED

    private fun loadSimInfo() {
        val simList        = SimHelper.getSimList(this)
        val defaultCallId  = SimHelper.getDefaultVoiceSubId(this)
        val defaultDataId  = SimHelper.getDefaultDataSubId(this)
        val defaultSmsId   = SimHelper.getDefaultSmsSubId(this)

        binding.simCardsContainer.removeAllViews()

        if (simList.isEmpty()) {
            binding.tvNoSim.visibility = View.VISIBLE
            binding.simCardsContainer.visibility = View.GONE
            return
        }

        binding.tvNoSim.visibility = View.GONE
        binding.simCardsContainer.visibility = View.VISIBLE

        simList.forEach { sim ->
            addSimCard(sim, defaultCallId, defaultDataId, defaultSmsId)
        }
    }

    private fun addSimCard(
        sim: SubscriptionInfo,
        defaultCallId: Int,
        defaultDataId: Int,
        defaultSmsId: Int,
    ) {
        val cardBinding = ItemSimCardBinding.inflate(
            LayoutInflater.from(this), binding.simCardsContainer, false
        )

        cardBinding.tvSimName.text    = sim.displayName?.toString() ?: "SIM ${sim.simSlotIndex + 1}"
        cardBinding.tvSimSlot.text    = "スロット ${sim.simSlotIndex + 1}"
        cardBinding.tvSimCarrier.text = sim.carrierName?.toString().takeIf { !it.isNullOrBlank() }
            ?: "キャリア情報なし"

        // デフォルトSIMバッジ
        cardBinding.badgeCall.visibility = if (sim.subscriptionId == defaultCallId) View.VISIBLE else View.GONE
        cardBinding.badgeData.visibility = if (sim.subscriptionId == defaultDataId) View.VISIBLE else View.GONE
        cardBinding.badgeSms.visibility  = if (sim.subscriptionId == defaultSmsId)  View.VISIBLE else View.GONE

        // SIMカラー
        val color = sim.iconTint
        cardBinding.simColorBar.setBackgroundColor(color)

        // SIM変更ボタン → 該当SIMの詳細設定画面
        cardBinding.btnChangeSim.setOnClickListener {
            SimHelper.openSimDetail(this, sim.subscriptionId)
        }

        binding.simCardsContainer.addView(cardBinding.root)
    }

    private fun showPermissionDeniedState() {
        binding.tvNoSim.visibility = View.VISIBLE
        binding.tvNoSim.text = getString(R.string.permission_denied_message)
        binding.simCardsContainer.visibility = View.GONE
    }
}
