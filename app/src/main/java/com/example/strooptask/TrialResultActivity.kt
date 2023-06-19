package com.example.strooptask

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.strooptask.databinding.TrialResultBinding
import java.io.*
import kotlin.math.roundToInt

class TrialResultActivity : AppCompatActivity() {

    private lateinit var binding: TrialResultBinding
    private lateinit var trialData: TrialData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.trial_result)
        trialData = intent.getSerializableExtra("trialData") as TrialData

        binding.backButton.setOnClickListener{
            this.finish()
        }

        binding.resultTitle.text = if (trialData.translation) "Trial Result : " else "測驗結果 : "

        binding.resultIdTitle.text = if (trialData.translation) "ID : " else "測驗編號 :"
        binding.id.text = trialData.id

        binding.resultModeTitle.text = if (trialData.translation) "Task Mode : " else "測驗模式 : "
        binding.mode.text = trialData.getTrialModeName()

        binding.resultTrialNumTitle.text = if (trialData.translation) "Total Numbers : " else "答題數 :　"
        binding.trialNum.text = buildString {
            append(if (trialData.translation) "${trialData.fixedTrialNum} items" else "${trialData.fixedTrialNum} 題")
            append(
                if (trialData.fixedFactor == "固定題數") {
                    if (trialData.translation) "(Fixed)" else " (此項固定)"
                } else ""
            )
        }

        binding.resultAnswerTimeTitle.text = if (trialData.translation) "Finish Time : " else "完成時間 : "
        binding.answerTime.text = buildString {
            append("%.3f %s".format(trialData.fixedAnswerTime/1000.0, if (trialData.translation) "secs" else " 秒"))
            append(
                if (trialData.fixedFactor == "固定時間") {
                    if (trialData.translation) "(Fixed)" else " (此項固定)"
                } else ""
            )
        }

        binding.resultCorrectTrialNumTitle.text = if (trialData.translation) "Correct Numbers : " else "正確題數 : "
        binding.correctTrialNum.text = "%d %s".format(trialData.correctTrialNum, if (trialData.translation) "items" else " 題")

        binding.resultWrongTrialNumTitle.text = if (trialData.translation) "Wrong Numbers : " else "錯誤題數 : "
        binding.wrongTrialNum.text = "%d %s".format(trialData.wrongTrialNum, if (trialData.translation) "items" else " 題")
        binding.overtimeTrialNum.text = buildString {
            append(
                if (trialData.translation) "\n(" else "( 超時 "
            )
            append("${trialData.overtimeTrialNum}")
            append(
                if (trialData.translation) "items timed out)" else " 題 )"
            )
        }

        binding.resultAvgReactionTimeTitle.text = if (trialData.translation) "Avg Reaction Time" else "平均反應時間 : "
        binding.avgReactionTime.text = "%d %s".format(
            trialData.avgReactionTime.roundToInt(),
            if (trialData.translation) "ms" else "毫秒"
        )

        Log.i("sec", trialData.fixedAnswerTime.toString())
        if (trialData.isSaved){
            savingFile()
        }

    }

    private fun getLimitTime(input:Int): String{
        return when(input){
            0 -> "無"
            1 -> "1秒"
            else -> "2秒"
        }
    }


    private fun savingFile() {
        var path = Environment.getExternalStorageDirectory()
        val file = File("${path}/strooptask/${trialData.id}_${getMode()}.txt")
        val content = """
            Trial ID: ${trialData.id}
            Mode: ${trialData.mode}
            TrialType: ${trialData.trialType}
            isBackgroundShowed: ${trialData.isBackgroundShowed}
            FixedFactor: ${trialData.fixedFactor}
            Language: ${trialData.language}
            LimitedTime: ${getLimitTime(trialData.limitTime)}
            TrialNum: ${trialData.fixedTrialNum}
            AnswerTime: %.3f
            CorrectTrialNum: ${trialData.correctTrialNum}
            WrongTrialNum: ${trialData.wrongTrialNum}
            OvertimeTrialNum: ${trialData.overtimeTrialNum}
            AvgReactionTime: ${trialData.avgReactionTime.roundToInt()}
            
        """.format(trialData.fixedAnswerTime/1000.0).trimIndent()
        var myExternalFile:File = file
        try {
            val fileOutPutStream = FileOutputStream(myExternalFile, true)
            fileOutPutStream.write(content.toByteArray())
            fileOutPutStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }



    }

    private fun getMode(): String {
        return when(trialData.mode){
            "中性" -> "neu"
            "一致" -> "con"
            else -> "incon"
        }
    }




}