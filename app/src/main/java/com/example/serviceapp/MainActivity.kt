package com.example.serviceapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.serviceapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initClickListeners()
        if (checkPermission()) {
            Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
        ) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Permission required")
            builder.setMessage("This app requires permission to work as expected")
                .setCancelable(false)
                .setPositiveButton(
                    "OK"
                ) { p0, p1 ->
                    requestPermission()
                }
                .setNegativeButton("Cancel") { p0, p1 ->
                    p0.dismiss()
                }

            builder.show()

        } else {
            requestPermission()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initClickListeners() {
        binding.startBtn.setOnClickListener {
            val intent = Intent(this, MyService::class.java)
            intent.action = Actions.START.toString()
            startService(intent)
        }

        binding.stopBtn.setOnClickListener {
            val intent = Intent(this, MyService::class.java)
            intent.action = Actions.STOP.toString()
            startService(intent)
        }
    }

    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            101
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty()) {
            val res1 = grantResults[0]
            if (res1 == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show()
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Permission required")
                builder.setMessage("This app requires permission to work as expected")
                    .setCancelable(false)
                    .setPositiveButton(
                        "OK"
                    ) { p0, p1 ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.setData(uri)
                        startActivity(intent)
                    }
                    .setNegativeButton("Cancel") { p0, p1 ->
                        p0.dismiss()
                    }

                builder.show()
            }
        }
    }
}