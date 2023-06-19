package com.example.strooptask

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.strooptask.databinding.SettingScreenBinding


class SettingActivity : AppCompatActivity(), TextWatcher {

    private lateinit var binding: SettingScreenBinding
    private lateinit var trialData: TrialData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.setting_screen)

        // Get TrialData object from MainActivity
        trialData = intent.getSerializableExtra("trialData") as TrialData


        setupDefaultChoices(trialData)
        setupChoiceListener()

        // Set OnClickListener for button in SettingActivity
        binding.backButton.setOnClickListener {this.finish()}
        binding.cancelButton.text = if (trialData.translation) "Cancel" else "取消"
        binding.cancelButton.setOnClickListener {this.finish()}
        binding.checkButton.text = if (trialData.translation) "Submit" else "確定"
        binding.checkButton.setOnClickListener {
            if (binding.fixedEditText.text.toString() != ""){
                val intent = Intent()
                intent.putExtra("trialData", trialData)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }else{
                Toast.makeText(this, "固定基準長度不得為空!!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupChoiceListener() {
        binding.title.text = if (trialData.translation) "Setting" else "設定"

        // RadioGroup1
        binding.radioGroup1Title.text = if (trialData.translation) "Mode" else "題目類型"
        binding.radioGroup1choice1.text = if (trialData.translation) "Color" else "顏色"
        binding.radioGroup1choice2.text = if (trialData.translation) "Semantics" else "字義"
        binding.radioGroup1choice2.textSize = if (trialData.translation) 16f else 18f
        binding.radioGroup1.setOnCheckedChangeListener { _, i ->
            when(i){
                R.id.radioGroup1choice1 -> trialData.trialType = "顏色"
                else -> trialData.trialType = "字義"
            }
            binding.radioGroup1.check(i)
        }

        // RadioGroup2
        binding.radioGroup2Title.text = if (trialData.translation) "Background" else "背景"
        binding.radioGroup2Title.textSize = if (trialData.translation) 18f else 20f
        binding.radioGroup2choice1.text = if (trialData.translation) "On" else "顯示"
        binding.radioGroup2choice2.text = if (trialData.translation) "Off" else "不顯示"
        binding.radioGroup2.setOnCheckedChangeListener { _, i ->
            when(i){
                R.id.radioGroup2choice1 -> trialData.isBackgroundShowed = "顯示"
                else -> trialData.isBackgroundShowed = "不顯示"
            }
            binding.radioGroup2.check(i)
        }

        // RadioGroup3
        binding.radioGroup3Title.text = if (trialData.translation) "Fixed" else "固定基準"
        binding.radioGroup3choice1.text = if (trialData.translation) "Time" else "固定時間"
        binding.radioGroup3choice2.text = if (trialData.translation) "Number" else "固定題數"
        binding.radioGroup3.setOnCheckedChangeListener { _, i ->
            when(i){
                R.id.radioGroup3choice1 -> {
                    trialData.fixedFactor = "固定時間"
                    binding.unit.text = if (trialData.translation) "sec" else "秒"
                    binding.fixedEditText.setText(trialData.fixedAnswerTime.toString())
                }
                else -> {
                    trialData.fixedFactor = "固定題數"
                    binding.unit.text = if (trialData.translation) "item" else "題"
                    binding.fixedEditText.setText(trialData.fixedTrialNum.toString())
                }
            }
            binding.radioGroup3.check(i)
        }

        // fixedEditText
        binding.fixedEditText.addTextChangedListener(this)
        binding.fixedEditTextTitle.text = if (trialData.translation) "Unit" else "固定基準長度"

        // RadioGroup4
        binding.radioGroup4Title.text = if (trialData.translation) "Language" else "題目語言"
        binding.radioGroup4choice1.text = if (trialData.translation) "Chinese" else "中文"
        binding.radioGroup4choice2.text = if (trialData.translation) "English" else "英文"
        binding.radioGroup4.setOnCheckedChangeListener { _, i ->
            when(i){
                R.id.radioGroup4choice1 -> trialData.language = "中文"
                else -> trialData.language = "英文"
            }
            binding.radioGroup4.check(i)
        }

        // RadioGroup5
        binding.radioGroup5Title.text = if (trialData.translation) "Time limit" else "答題限時"
        binding.radioGroup5choice1.text = if (trialData.translation) "None" else "無"
        binding.radioGroup5choice2.text = if (trialData.translation) "1 sec" else "1秒"
        binding.radioGroup5choice3.text = if (trialData.translation) "2 sec" else "2秒"
        binding.radioGroup5.setOnCheckedChangeListener { _, i ->
            when(i){
                R.id.radioGroup5choice1 -> trialData.limitTime = 0
                R.id.radioGroup5choice2 -> trialData.limitTime = 1
                else -> trialData.limitTime = 2
            }
            binding.radioGroup5.check(i)
        }
    }

    private fun setupDefaultChoices(data: TrialData) {
        binding.radioGroup1.check(if (data.trialType == "顏色") R.id.radioGroup1choice1 else R.id.radioGroup1choice2)

        binding.radioGroup2.check(if (data.isBackgroundShowed == "顯示") R.id.radioGroup2choice1 else R.id.radioGroup2choice2)

        binding.radioGroup3.check(if (data.fixedFactor == "固定題數") R.id.radioGroup3choice2 else R.id.radioGroup3choice1)

        binding.fixedEditText.setText(if (data.fixedFactor == "固定題數") data.fixedTrialNum.toString() else data.fixedAnswerTime.toString())
        binding.unit.text = if (data.fixedFactor == "固定題數") {
            if (data.translation) "item" else "題"
        } else{
            if (data.translation) "sec" else "秒"
        }

        binding.radioGroup4.check(if (data.language == "中文") R.id.radioGroup4choice1 else R.id.radioGroup4choice2)

        binding.radioGroup5.check(
            when(data.limitTime){
                0 -> R.id.radioGroup5choice1
                1 -> R.id.radioGroup5choice2
                else -> R.id.radioGroup5choice3
            }
        )
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(p0: Editable?) {
        var t = binding.fixedEditText.text.toString()
        if (trialData.fixedFactor == "固定時間"){
            trialData.fixedAnswerTime = if (t != "") t.toInt() else 0
        }else{
            trialData.fixedTrialNum = if (t != "") t.toInt() else 0
        }
    }
}