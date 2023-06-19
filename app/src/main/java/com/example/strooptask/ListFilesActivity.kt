package com.example.strooptask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.example.strooptask.databinding.ActivityListFilesBinding
import java.io.File

class ListFilesActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityListFilesBinding
    private lateinit var trialData: TrialData
    private var lists = getList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_files)
        trialData = intent.getSerializableExtra("trialData") as TrialData
        val listview = binding.listView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lists)
        listview.adapter = adapter
        binding.backButton.setOnClickListener(this)
        binding.listViewTitle.text = if (trialData.translation) "File List" else "檔案列表"
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.backButton -> {
                this.finish()
            }
        }
    }

    private fun getList(): List<String>{
        var res = arrayListOf<String>()
        var path = "${Environment.getExternalStorageDirectory()}/strooptask"
        File(path).walkBottomUp().forEach {
            if (it.toString() != path) {
                var file: String = it.toString().substring(path.toString().length+1)
                res.add(file)
            }
        }
        return res
    }
}