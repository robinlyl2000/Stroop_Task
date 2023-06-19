package com.example.strooptask

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.strooptask.databinding.MainMenuBinding


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: MainMenuBinding
    private lateinit var trialData: TrialData
    private lateinit var modeButtons: List<Button>
    private val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
            !Environment.isExternalStorageManager()) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
            startActivityForResult(intent, 2296)
        }



        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_menu)

        modeButtons = listOf(binding.mode1, binding.mode2, binding.mode3)
        for (button in modeButtons) {
            button.setOnClickListener(this)
        }
        binding.settingButton.setOnClickListener(this)
        binding.clearButton.setOnClickListener(this)
        binding.showlistButton.setOnClickListener(this)
        binding.translationButton.setOnClickListener(this)
        trialData = TrialData()
    }


    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.mode1 -> {
                trialData.mode = "中性"
                sendData()
            }
            R.id.mode2 -> {
                trialData.mode = "一致"
                sendData()
            }
            R.id.mode3 -> {
                trialData.mode = "不一致"
                sendData()
            }
            R.id.settingButton -> {
                val intent = Intent(this, SettingActivity::class.java)
                intent.putExtra("trialData", trialData)
                startActivityForResult(intent, REQUEST_CODE)
            }
            R.id.clearButton -> {
                binding.editText.setText("")
            }
            R.id.showlistButton -> {
                val intent = Intent(this, ListFilesActivity::class.java)
                intent.putExtra("trialData", trialData)
                startActivity(intent)
            }
            R.id.translationButton -> {
                trialData.translation = !trialData.translation
                if (trialData.translation){ // English
                    binding.editText.hint = "Enter ID"
                    binding.clearButton.text = "Clear"
                    binding.mode1.text = "Neutral"
                    binding.mode1.textSize = 30f
                    binding.mode2.text = "Congruent"
                    binding.mode2.textSize = 30f
                    binding.mode3.text = "Incongruent"
                    binding.mode3.textSize = 30f
                } else { // Chinese
                    binding.editText.hint = "輸入測驗編號"
                    binding.clearButton.text = "清除"
                    binding.mode1.text = "中性"
                    binding.mode1.textSize = 36f
                    binding.mode2.text = "一致"
                    binding.mode2.textSize = 36f
                    binding.mode3.text = "不一致"
                    binding.mode3.textSize = 36f
                }
            }
        }
    }
    private fun sendData() {
        trialData.id = binding.editText.text.toString()
        trialData.isSaved = (trialData.id != "")
        val intent = Intent(this, TrialStartActivity::class.java)
        intent.putExtra("trialData", trialData)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    trialData = data.getSerializableExtra("trialData") as TrialData
                }
            }else if (requestCode == 2296) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                    Environment.isExternalStorageManager()) {
                }
            }
        }
    }
}