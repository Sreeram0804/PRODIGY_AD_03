package com.example.stopwatch

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.stopwatch.databinding.ActivityMainBinding
import java.lang.String
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private var milli:Int=0
    private var sec:Int=0
    private var running:Boolean=false
    private var lapcount:Int=0
    private var lsec=0
    private var lmilli=0
    private val handler= Handler(Looper.getMainLooper())
    private val runnable=object:Runnable{
        override fun run() {
            milli++
            sec=milli/100
            val hours= sec / 3600
            val minutes = (sec % 3600) / 60
            val seconds = sec % 60
            val milliseconds=milli%100
            val time = String.format(Locale.getDefault(),"%02d:%02d:%02d.%02d", hours, minutes, seconds,milliseconds)
            binding.timer.text=time
            handler.postDelayed( this,10)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.start.setOnClickListener {
            startTimer()
        }
        binding.stop.setOnClickListener {
            stopTimer()
        }
        binding.resetTimer.setOnClickListener {
            reset()
        }
        binding.lap.setOnClickListener {
            lapContent()
        }
        binding.copy.setOnClickListener{
            var text=String.format("Time:%s",binding.timer.text)
            val l=String.format(Locale.getDefault(),"%s",binding.laptext.text)
            text=text+"\n"+String.format(Locale.getDefault(),"%s",binding.header.text)+"\n"+l
            val clipboard=getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip= ClipData.newPlainText("copy",text)
            clipboard.setPrimaryClip(clip)
        }
    }
    private fun startTimer(){
        if(!running){
            handler.postDelayed(runnable,10)
            running=true

            binding.start.isVisible=false
            binding.stop.isEnabled=true
            binding.stop.isVisible=true
            binding.lap.isEnabled=true
            binding.lap.isVisible=true
            binding.start.isEnabled=false
            binding.resetTimer.isEnabled=false
            binding.resetTimer.isVisible=false
        }
    }
    private fun stopTimer(){
        if(running){
            handler.removeCallbacks(runnable)
            running=false

            binding.start.text="Resume"
            binding.start.isEnabled=true
            binding.start.isVisible=true
            binding.stop.isEnabled=false
            binding.stop.isVisible=false
            binding.lap.isEnabled=false
            binding.lap.isVisible=false
            binding.resetTimer.isEnabled=true
            binding.resetTimer.isVisible=true
        }
    }
    private fun reset() {
        stopTimer()
        sec = 0
        milli=0
        lapcount=0
        lsec=0
        lmilli=0
        binding.timer.text = "00:00:00.00"

        binding.lap.isVisible = true
        binding.resetTimer.isEnabled = false
        binding.resetTimer.isVisible = false
        binding.start.text = "Start"
        binding.header.isVisible=false
        binding.laptext.text=""
    }
    private fun lapContent(){
        lapcount++
        var hours= sec / 3600
        var minutes = (sec % 3600) / 60
        var seconds = sec % 60
        val milliseconds=milli%100
        var laps=0
        var lapm=0
        var lapmi=0
        val time = String.format(Locale.getDefault(),"%02d:%02d:%02d.%02d", hours, minutes, seconds,milliseconds)
        val laptext=String.format(Locale.getDefault(),"%s",binding.laptext.text)
        var t=""
        if((lmilli%100)>milliseconds) {
            seconds--
            lapmi=100+milliseconds-(lmilli%100)
        }
        else{
            lapmi=milliseconds-(lmilli%100)
        }
        if((lsec%60)>seconds){
            minutes--
            laps=60+seconds-(lsec%60)
        }
        else{
            laps=seconds-(lsec%60)
        }
        if((lsec%3600)/60>minutes){
            hours--
            lapm=60+minutes-(lsec%3600)/60
        }
        else{
            lapm=minutes-(lsec%3600)/60
        }
        val diff=String.format(Locale.getDefault(),"                Lap%-10d   %02d:%02d:%02d.%02d",lapcount,hours-(lsec/3600),lapm,laps,lapmi)
        if(lapcount==1){
            binding.header.isVisible=true
            t=diff+"         "+time
        }
        else{
            t=diff+"         "+time+"\n\n"+laptext
        }
        binding.laptext.text=t
        binding.header.isVisible=true
        lsec=sec
        lmilli=milli
    }
}