package com.example.strooptask

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.strooptask.databinding.TrialScreenBinding
import java.lang.System.currentTimeMillis
import kotlin.random.Random

class TrialActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: TrialScreenBinding
    private lateinit var trialData: TrialData
    private lateinit var wordView: TextView
    private lateinit var colorButtons: List<Button>
    private lateinit var backgroundBlock: RelativeLayout

    private var roundStartTime: Long = 0
    private var elapsedTime: Long = 0
    private val totalHandler = Handler()
    private lateinit var totalRunnable: Runnable
    private var totalRoundTimes: MutableList<Long> = arrayListOf()

    private var limitHandler = Handler()
    private var limitRunnable: Runnable? = null

    private var totalTrailNum: Int = 0
    private var choices: List<String> = emptyList()
    private var currentTrialNum: Int = 0
    private lateinit var timerHandler: Handler
    private var currentRealColor = "" //此回合的顏色名稱
    private var currentFakeColor = "" //此回合的顏色名稱
    private var colorChineseNames = mutableListOf<String>("紅", "橘", "黃", "綠", "藍", "紫", "粉")
    private var colorEnglishNames = mutableListOf<String>("Red", "Orange", "Yellow", "Green", "Blue", "Purple", "Pink")
    private var colorHexNames =  mutableListOf<Long>(0xFFFF0000, 0xFFFF8C00, 0xFFFFFF00, 0xFF008000, 0xFF0000FF, 0xff77428D, 0xFFF596AA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.trial_screen)
        trialData = intent.getSerializableExtra("trialData") as TrialData


        totalTrailNum = trialData.fixedTrialNum
        wordView = binding.wordView

        colorButtons = listOf(binding.button1, binding.button2, binding.button3, binding.button4)
        backgroundBlock = binding.backgroundBlock
        binding.totalTrailNum.text = if (trialData.fixedFactor == "固定題數"){
            if (trialData.translation) "Total $totalTrailNum items" else "共 $totalTrailNum 題"
        } else {
            ""
        }

        if (trialData.isBackgroundShowed == "不顯示") {
            binding.wordView.textSize = 96F
            backgroundBlock
        }

        for (button in colorButtons) {
            button.setOnClickListener(this)
        }
        binding.backButton.setOnClickListener {
            this.finish()
        }


        totalRunnable = Runnable {
            endTrial()
        }
        if (trialData.fixedFactor == "固定時間"){
            totalHandler.postDelayed(totalRunnable!!, trialData.fixedAnswerTime.toLong() * 1000)
        }

        startNewRound()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 移除 handler 中的 runnable，避免 memory leak
        timerHandler.removeCallbacks(timerRunnable)
        totalHandler.removeCallbacks(totalRunnable)
        limitRunnable?.let { limitHandler.removeCallbacks(it) }
    }


    private val timerRunnable = object : Runnable{
        override fun run(){
            elapsedTime = currentTimeMillis() - roundStartTime
            timerHandler.postDelayed(this, 0)
        }
    }

    private fun colorNameToInt(name: String) : Int{
        return colorHexNames[languageFilter().indexOf(name)].toInt()
    }


    override fun onClick(p0: View?) {

        if (p0 is Button) {

            currentTrialNum ++
            timerHandler.removeCallbacks(timerRunnable)
            if (trialData.limitTime != 0) {
                limitHandler.removeCallbacks(limitRunnable!!)
            }
            totalRoundTimes.add(elapsedTime)
            // 判斷是否回答正確
            var answerColor = choices[colorButtons.indexOf(p0)]
            when(trialData.mode){
                "中性" -> {
                    correctOrWrong(answerColor, currentRealColor)
                }
                "一致" -> {
                    correctOrWrong(answerColor, currentRealColor)
                }
                "不一致" -> {
                    if (trialData.trialType == "顏色"){
                        correctOrWrong(answerColor, currentFakeColor)
                    }else{
                        correctOrWrong(answerColor, currentRealColor)
                    }

                }
            }




            startNewRound()

        }
    }

    private fun languageFilter(): MutableList<String>{
        return if (trialData.language == "中文"){
            colorChineseNames
        }else{
            colorEnglishNames
        }
    }

    private fun correctOrWrong(input:String, correct:String) {
        if (input == correct){
            Log.i("Trial $currentTrialNum", "答對!!!!!")
            trialData.correctTrialNum ++
        }else{
            Log.i("Trial $currentTrialNum", "答錯:(")
            trialData.wrongTrialNum ++
        }
    }

    private fun endTrial(){
        timerHandler.removeCallbacks(timerRunnable)
        Log.i("所有反應時間", totalRoundTimes.toString())

        if (trialData.fixedFactor == "固定時間"){
            trialData.fixedTrialNum = currentTrialNum
            trialData.fixedAnswerTime *= 1000
        }else{
            trialData.fixedAnswerTime = (if (totalRoundTimes.isNotEmpty()) totalRoundTimes.sum().toInt() else 0)
        }
        trialData.avgReactionTime = (if (totalRoundTimes.isNotEmpty()) totalRoundTimes.average() else 0.0)
        val intent = Intent(this@TrialActivity, TrialResultActivity::class.java)
        intent.putExtra("trialData", trialData)
        startActivity(intent)
        finish()
    }

    private fun startNewRound() {
        binding.currentTrialNum.text = if (trialData.translation){
            "Completed $currentTrialNum items"
        } else {
            "已完成 $currentTrialNum 題"
        }

        if (currentTrialNum == totalTrailNum && trialData.fixedFactor == "固定題數"){
            endTrial()
            return
        }


        // 挑選 currentTargetColor
        var newRealColor = ""
        while (newRealColor == currentRealColor || newRealColor == "") {
            newRealColor = languageFilter()[Random.nextInt(languageFilter().size)]
        }
        currentRealColor = newRealColor


        // 挑選 colorChoices : 4個按鈕的顏色選項
        var colorChoices = mutableListOf(currentRealColor)
        for (c in languageFilter().toMutableList().shuffled()) {
            if (c == currentRealColor){
                continue
            }
            colorChoices.add(c)
            if (colorChoices.size > 3){
                break
            }
        }

        // 挑選 currentFakeColor
        var newFakeColor = ""
        while (newFakeColor == currentRealColor || newFakeColor == "" || newFakeColor == currentFakeColor) {
            newFakeColor = colorChoices[Random.nextInt(colorChoices.size)]
        }
        currentFakeColor = newFakeColor

        // 設定 Buttons 顏色
        choices = colorChoices.shuffled()
        for (i in colorButtons.indices) {
            var t = colorNameToInt(choices[i])
            colorButtons[i].setBackgroundColor(t)
        }

        // 設定 TextView & Background 顏色
        wordView.text = currentRealColor
        when(trialData.mode){
            "中性" -> {
                if (trialData.trialType == "顏色"){
                    wordView.setTextColor(colorNameToInt(currentRealColor))
                    binding.tempBackground.setBackgroundColor(colorNameToInt(currentRealColor))
                }else {
                    wordView.setTextColor(0xFF000000.toInt())
                    backgroundBlock.setBackgroundColor(0xFFFFFFFF.toInt())
                }

            }
            "一致" -> {
                wordView.setTextColor(colorNameToInt(currentRealColor))
                if(trialData.isBackgroundShowed == "顯示") {
                    backgroundBlock.setBackgroundColor(colorNameToInt(currentRealColor))
                }else{
                    backgroundBlock.setBackgroundColor(0xFFFFFFFF.toInt())
                }
            }
            "不一致" -> {
                wordView.setTextColor(colorNameToInt(currentFakeColor))
                if(trialData.isBackgroundShowed == "顯示") {
                    backgroundBlock.setBackgroundColor(colorNameToInt(currentFakeColor))
                }else{
                    backgroundBlock.setBackgroundColor(0xFFFFFFFF.toInt())
                }
            }
        }



        roundStartTime = currentTimeMillis()
        timerHandler = Handler()
        timerHandler.postDelayed(timerRunnable, 0)

        if (trialData.limitTime != 0) {


            limitRunnable = Runnable {

                currentTrialNum++
                totalRoundTimes.add((trialData.limitTime * 1000).toLong())
                Log.i("Trial $currentTrialNum", "超時:(((")
                trialData.wrongTrialNum++
                trialData.overtimeTrialNum++
                limitHandler.removeCallbacks(limitRunnable!!)
                limitHandler = Handler()
                limitRunnable = null
                startNewRound()


            }
            limitHandler.postDelayed(limitRunnable!!, (trialData.limitTime * 1000).toLong())
        }
    }
}