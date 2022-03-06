package com.example.mreader

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.util.SparseArray
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer

class MainActivity : AppCompatActivity() {

    private lateinit var textrecog : TextRecognizer
    private lateinit var  tts : TextToSpeech
    lateinit var finalphoto : Bitmap
    var stringresult : String = ""
    val camera_request_code = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CAMERA), camera_request_code)
        } else {
            Toast.makeText(this@MainActivity, "Permission already granted", Toast.LENGTH_SHORT).show()
        }

        tts = TextToSpeech(this,TextToSpeech.OnInitListener {

        })

        capture()

        val btn : Button = findViewById(R.id.button)
        btn.setOnClickListener {
            capture()
        }
    }

    fun capture(){
        val intnt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intnt.resolveActivity(packageManager) != null){
            startActivityForResult(intnt,1)
        }else{
            Toast.makeText(this,"There is no support",Toast.LENGTH_SHORT).show()
        }
        stringresult = ""
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1 && resultCode== RESULT_OK && data!=null){
            val bundle : Bundle? = data.extras
            finalphoto = bundle?.get("data") as Bitmap
            textrecognizer()
        }
    }

    private fun textrecognizer(){
        textrecog = TextRecognizer.Builder(applicationContext).build()
        val frame : Frame = Frame.Builder().setBitmap(finalphoto).build()
        val txtblock : SparseArray<TextBlock> = textrecog.detect(frame)
            for (i in 0..txtblock.size()-1) {
                val txtblk: TextBlock = txtblock.get((txtblock.keyAt(i)))
                stringresult += txtblk.value + " "
            }

        val t : TextView = findViewById(R.id.textView)
        t.setText(stringresult)
        tts.speak(stringresult,TextToSpeech.QUEUE_FLUSH,null,null)
    }
}