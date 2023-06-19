package com.example.strooptask

import java.io.Serializable

class TrialData(
    var id: String = "",
    var mode: String = "中性",
    var trialType: String = "顏色",
    var isBackgroundShowed: String = "顯示",
    var fixedFactor: String = "固定題數",
    var fixedAnswerTime: Int = 30,
    var fixedTrialNum: Int = 30,
    var language: String = "中文",
    var limitTime: Int = 0,
    var isSaved: Boolean = false,

    var correctTrialNum: Int = 0,
    var wrongTrialNum: Int = 0,
    var overtimeTrialNum: Int = 0,
    var avgReactionTime: Double = 0.0,

    var translation: Boolean = false
)  : Serializable {

    fun getTrialModeName() : String{
        return if (translation){
            when(mode) {
                "一致" -> "Congruent"
                "不一致" -> "Incongruent"
                else -> "Neutral" // 中性
            }
        } else {
            mode
        }
    }

    fun getTrialTypeString(): String{
        return if (translation){
            when(trialType) {
                "字義" -> "Semantics"
                else -> "Color" // 顏色
            }
        }else{
            trialType
        }
    }

    fun getTrialIsBackgroundShowed() : String{
        return if (translation){
            when(isBackgroundShowed) {
                "不顯示" -> "Off"
                else -> "On" // 顯示
            }
        } else {
            isBackgroundShowed
        }
    }

    fun getTrialFixedFactor():  String{
        return if (translation){
            when(fixedFactor) {
                "固定題數" -> "Number"
                else -> "Time" // 固定時間
            }
        } else {
            fixedFactor
        }
    }

    fun getTrialFixedLength(): String{
        return if (fixedFactor == "固定時間"){
            if (translation){
                "$fixedAnswerTime secs"
            } else {
                "$fixedAnswerTime 秒"
            }
        } else {
            if (translation){
                "$fixedTrialNum items"
            } else {
                "$fixedTrialNum 題"
            }
        }
    }

    fun getTrialLanguage(): String{
        return if (translation){
            when(language) {
                "英文" -> "English"
                else -> "Chinese" // 中文
            }
        } else {
            language
        }
    }
}

