package com.example.mywaste

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.barcode.BarcodeDetector
import android.Manifest
import android.annotation.SuppressLint
import android.util.SparseArray
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var detector: BarcodeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED)
        {
            askForCameraPermission()
        }
        else
        {
            setupControls()
        }
    }

    private fun setupControls() {
        detector = BarcodeDetector.Builder(this@MainActivity).build()
        cameraSource = CameraSource.Builder(this@MainActivity, detector).setAutoFocusEnabled(true).build()
        cameraSurfaceView.holder.addCallback(surfaceCallBack)
        detector.setProcessor(processor)
    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(this@MainActivity,arrayOf(Manifest.permission.CAMERA),
        requestCodeCameraPermission)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == requestCodeCameraPermission && grantResults.isNotEmpty())
        {
            setupControls()
        }
        else
        {
            Toast.makeText(applicationContext,"Доступ запрещён", Toast.LENGTH_SHORT).show()
        }
    }

    private val surfaceCallBack = object : SurfaceHolder.Callback {
        @SuppressLint("MissingPermission")
        override fun surfaceCreated(surfaceHolder: SurfaceHolder?) {
            try {
                cameraSource.start(surfaceHolder)
            }
            catch (exception: Exception) {
                Toast.makeText(applicationContext,"Что-то пошло не так", Toast.LENGTH_SHORT).show()
            }
        }

        override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {

        }

        override fun surfaceDestroyed(p0: SurfaceHolder?) {
            cameraSource.stop()
        }
    }

    private val processor = object : Detector.Processor<Barcode> {
        override fun release() {
            TODO("Not yet implemented")
        }

        override fun receiveDetections(detections : Detector.Detections<Barcode>) {
            if (detections != null && detections.detectedItems.isNotEmpty()) {
                val qrCodes: SparseArray<Barcode> = detections.detectedItems
                val code = qrCodes.valueAt(0)
                textScanResult.text = code.displayValue
            }
            else
            {
                textScanResult.text = ""
            }
        }

    }
}

private fun <E> SparseArray<E>.isNotEmpty(): Boolean {
    return this.valueAt(0) != null;
}
