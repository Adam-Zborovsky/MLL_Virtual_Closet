package com.example.myapplication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat


class MainActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val switch = findViewById<SwitchCompat>(R.id.switchOnOff)
        val containerRL = findViewById<RelativeLayout>(R.id.idRLContainer)
        val tvSwitchShahar = findViewById<TextView>(R.id.tvSwitchYes)
        val tvSwitchAdam = findViewById<TextView>(R.id.tvSwitchNo)
        containerRL.background = ResourcesCompat.getDrawable(resources, R.drawable.background_adam, null)

        switch.setOnClickListener {
            if (switch.isChecked) {
                containerRL.background = ResourcesCompat.getDrawable(resources, R.drawable.background_shahar, null)
                tvSwitchShahar.setTextColor(ContextCompat.getColor(this,R.color.white))
                tvSwitchAdam.setTextColor(ContextCompat.getColor(this,R.color.blue_color))
            }
            else {
                containerRL.background = ResourcesCompat.getDrawable(resources, R.drawable.background_adam, null)
                tvSwitchShahar.setTextColor(ContextCompat.getColor(this,R.color.blue_color))
                tvSwitchAdam.setTextColor(ContextCompat.getColor(this,R.color.white))
            }
        }
    }
}