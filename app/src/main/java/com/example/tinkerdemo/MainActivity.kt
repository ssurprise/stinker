package com.example.tinkerdemo

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tencent.tinker.lib.tinker.Tinker
import com.tencent.tinker.lib.tinker.TinkerInstaller
import com.tencent.tinker.loader.shareutil.ShareConstants
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var mPatchDir: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btn).setOnClickListener(this)
        findViewById<View>(R.id.showInfo).setOnClickListener {
            showInfo(it.context)
        }
        findViewById<TextView>(R.id.tv_channel).apply {
            text = "当前渠道为：${
                ChannelUtils.getChannel(
                    this.context,
                    "主渠道"
                )
            }"
        }
        findViewById<TextView>(R.id.tv_package_name).apply {
            text = "包名：${AppUtils.getInstance().appPackage}"
        }
        askForRequiredPermissions()

        makeDir()
    }

    /**
     * 动态申请权限
     */
    private fun askForRequiredPermissions() {
        if (Build.VERSION.SDK_INT < 23) {
            return
        }
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                0
            )
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= 16) {
            val res = ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            res == PackageManager.PERMISSION_GRANTED
        } else {
            // When SDK_INT is below 16, READ_EXTERNAL_STORAGE will also be granted if WRITE_EXTERNAL_STORAGE is granted.
            val res = ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            res == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * 创建保存patch包的文件夹
     */
    private fun makeDir() {
        // /sdcard/tpatch
        mPatchDir = Environment.getExternalStorageDirectory().absolutePath
        Log.e(TAG, "path:$mPatchDir")
    }


    override fun onClick(v: View?) {
        loadPatch()
    }

    /**
     * 加载patch包
     */
    private fun loadPatch() {
        val patch: String =
            Environment.getExternalStorageDirectory().absolutePath + "/patch_signed.apk"
        if (Tinker.isTinkerInstalled()) {
            Log.e(TAG, "loadPath:$patch")
            TinkerInstaller.onReceiveUpgradePatch(applicationContext, patch)
        }
    }

    private fun showInfo(context: Context?): Boolean {
        // add more Build Info
        val sb = StringBuilder()
        val tinker = Tinker.with(applicationContext)
        if (tinker.isTinkerLoaded) {
            sb.append(String.format("[patch is loaded] \n"))
            sb.append(
                java.lang.String.format(
                    "[buildConfig TINKER_ID] %s \n",
                    BuildInfo.TINKER_ID
                )
            )
            sb.append(
                java.lang.String.format(
                    "[buildConfig BASE_TINKER_ID] %s \n",
                    "1.0"
                )
            )
            sb.append(
                java.lang.String.format(
                    "[buildConfig MESSSAGE] %s \n",
                    BuildInfo.MESSAGE
                )
            )
            sb.append(
                String.format(
                    "[TINKER_ID] %s \n",
                    tinker.tinkerLoadResultIfPresent.getPackageConfigByName(ShareConstants.TINKER_ID)
                )
            )
            sb.append(
                String.format(
                    "[packageConfig patchMessage] %s \n",
                    tinker.tinkerLoadResultIfPresent.getPackageConfigByName("patchMessage")
                )
            )
            sb.append(String.format("[TINKER_ID Rom Space] %d k \n", tinker.tinkerRomSpace))
        } else {
            sb.append(String.format("[patch is not loaded] \n"))
            sb.append(
                java.lang.String.format(
                    "[buildConfig TINKER_ID] %s \n",
                    BuildInfo.TINKER_ID
                )
            )
            sb.append(
                java.lang.String.format(
                    "[buildConfig BASE_TINKER_ID] %s \n",
                    "1.0"
                )
            )
            sb.append(
                java.lang.String.format(
                    "[buildConfig MESSSAGE] %s \n",
                    BuildInfo.MESSAGE
                )
            )
            sb.append(
                String.format(
                    "[TINKER_ID] %s \n", ShareTinkerInternals.getManifestTinkerID(
                        applicationContext
                    )
                )
            )
        }

        val v = TextView(context)
        v.text = sb
        v.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
        v.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10f)
        v.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        v.setTextColor(-0x1000000)
        v.setTypeface(Typeface.MONOSPACE)
        val padding = 16
        v.setPadding(padding, padding, padding, padding)
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setView(v)
        val alert = builder.create()
        alert.show()
        return true
    }

    companion object {
        const val TAG = "tinker test"
    }

}