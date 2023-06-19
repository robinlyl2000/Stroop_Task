package com.example.strooptask

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.strooptask.databinding.TrialStartScreenBinding

class TrialStartActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: TrialStartScreenBinding
    private lateinit var trialData: TrialData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.trial_start_screen)
        trialData = intent.getSerializableExtra("trialData") as TrialData



        binding.backButton.setOnClickListener(this)
        binding.startButton.setOnClickListener(this)

        setupInfo()
    }

    @SuppressLint("SetTextI18n")
    private fun setupInfo() {
        binding.idTitle.text = if (trialData.translation) "ID : " else "測驗編號 : "
        binding.id.text = trialData.id

        binding.modeTitle.text = if (trialData.translation) "Task Mode : " else "測驗模式 : "
        binding.mode.text = trialData.getTrialModeName()

        binding.trialTypeTitle.text = if (trialData.translation) "Trial Type : " else "題目類型 : "
        binding.trialType.text = trialData.getTrialTypeString()

        binding.isBackgroundShowedTitle.text = if (trialData.translation) "Background : " else "背景 : "
        binding.isBackgroundShowed.text = trialData.getTrialIsBackgroundShowed()

        binding.fixedFactorTitle.text = if (trialData.translation) "Fixed : " else "固定基準 : "
        binding.fixedFactor.text = trialData.getTrialFixedFactor()

        binding.fixedLengthTitle.text = if (trialData.translation) "Units : " else "固定基準長度 : "
        binding.fixedLength.text = trialData.getTrialFixedLength()

        binding.languageTitle.text = if (trialData.translation) "Language : " else "題目語言 : "
        binding.language.text = trialData.getTrialLanguage()

        binding.limitTimeTitle.text = if (trialData.translation) "Time limit : " else "答題限時 : "
        binding.limitTime.text = "${trialData.limitTime}" + if (trialData.translation) " sec" else " 秒"

        binding.isSavedTitle.text = if (trialData.translation) "Is saved : " else "是否要儲存 : "
        binding.isSaved.text = if (trialData.isSaved) {
            if (trialData.translation) "true" else "是"
        } else {
            if (trialData.translation) "false" else "否"
        }

        binding.startButton.text = if (trialData.translation) "Start" else "開始"
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.backButton -> {
                this.finish()
            }
            R.id.startButton ->{
                val intent = Intent(this, TrialActivity::class.java)
                intent.putExtra("trialData", trialData)
                startActivity(intent)
                this.finish()
            }
        }
    }
}