package com.example.taskwhatsappstatussaver

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.GridLayoutManager
import com.example.taskwhatsappstatussaver.databinding.ActivityWhatsappMainBinding
import java.io.File
import java.io.FileOutputStream

class WhatsappMainActivity : AppCompatActivity(), StatusAdapter.ItemClickListener {

    private lateinit var binding: ActivityWhatsappMainBinding
    private val uriList = ArrayList<String>()
    private val REQUEST_CODE_PERMISSIONS = 101
    private var uri: String? = null
    private var isLightMode = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityWhatsappMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences: SharedPreferences = getSharedPreferences("app", MODE_PRIVATE)
        val uri: String? = preferences.getString("uri", "no")

        if (checkAndRequestPermissions()) {
            setupRecyclerView()
        }

        if (!uri.equals("no", ignoreCase = true)) {
            Log.d("MainActivity", "URI is not 'no': $uri")
            runRecyclerViewCode(uri!!, uriList)
        } else {
            Log.d("MainActivity", "URI is 'no': $uri")
            gettingAllData()
        }


        binding.RecyclerView.layoutManager = GridLayoutManager(this, 2)  // Initialize and set the click listener for the adapter
        val adapter = StatusAdapter(uriList, this)
        adapter.setItemClickListener(this)                                  // Set the itemClickListener here
        binding.RecyclerView.adapter = adapter


        binding.brightness.setOnClickListener {   // Click to toggle brightness mode
            if (isLightMode) {
                // Light mode to Dark mode
                binding.mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_color1))
                val darkImageResource = R.drawable.baseline_brightness_4_24 // Replace with your dark mode image resource
                binding.brightness.setImageResource(darkImageResource)
            } else {
                // Dark mode to Light mode
                binding.mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                val lightImageResource = R.drawable.baseline_brightness_high_24 // Replace with your light mode image resource
                binding.brightness.setImageResource(lightImageResource)
            }

            // Toggle the mode
            isLightMode = !isLightMode
        }

    }

    private fun checkAndRequestPermissions(): Boolean {          // Function to check and request necessary permissions
        val readPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val writePermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val permissions = mutableListOf<String>()
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                REQUEST_CODE_PERMISSIONS
            )
            return false
        }
        return true
    }

    private fun setupRecyclerView() {    // Function to set up the RecyclerView

        val preferences: SharedPreferences = getSharedPreferences("app", MODE_PRIVATE)
        val uri: String? = preferences.getString("uri", "no")

        if (!uri.equals("no", ignoreCase = true)) {
            runRecyclerViewCode(uri!!, uriList)
        } else {
            gettingAllData()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                setupRecyclerView()  // All permissions granted, set up the RecyclerView
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                finish()              // Permission denied, show a message to the user
            }
        }
    }

    override fun onDownloadClick(uri: String) {     // Function to handle download click
        val contentResolver = this.contentResolver
        val inputStream = contentResolver.openInputStream(Uri.parse(uri))
        val fileName: String

        val currentTimeMillis = System.currentTimeMillis()

        if (uri.endsWith(".mp4")) {
            fileName = "video_$currentTimeMillis.mp4"
        } else {
            fileName = "image_$currentTimeMillis.jpg"
        }

        val outputStream = FileOutputStream(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName))

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)      // Notify the system that the file is available for download
        mediaScanIntent.data = Uri.fromFile(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName))
        this.sendBroadcast(mediaScanIntent)

        if (uri.endsWith(".mp4")) {
            Toast.makeText(this, "Video saved in Gallery", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Image saved in Gallery", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onItemClick(uri: String) {     // Function to handle item click
        val previewIntent = Intent(this, PreviewActivity::class.java)
        previewIntent.putExtra("URI", uri)
        startActivity(previewIntent)
    }

    private fun gettingAllData() {    //// Function to handle result of directory selection
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, 1234)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1234 && resultCode == RESULT_OK) {
            val treeUri: Uri = data?.data!!

            contentResolver.takePersistableUriPermission(
                treeUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )

            val preferences: SharedPreferences = getSharedPreferences("app", MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putString("uri", treeUri.toString())
            editor.apply()

            val fileDoc: DocumentFile? = DocumentFile.fromTreeUri(this, treeUri)

            if (fileDoc != null) {
                for (file in fileDoc.listFiles()) {
                    Log.d("Shahzaib", "File Name - " + file.name)
                    Log.d("Shahzaib", "File URI - " + file.uri)
                }
            }
            runRecyclerViewCode(treeUri.toString(), uriList)
        }
    }

    private fun runRecyclerViewCode(uri: String, uriList: ArrayList<String>) {
        val fileDoc: DocumentFile? = DocumentFile.fromTreeUri(this, Uri.parse(uri))
        // Function to populate RecyclerView with URIs from selected directory
        if (fileDoc != null) {
            for (file in fileDoc.listFiles()) {
                this.uriList.add(file.uri.toString())
            }

            binding.RecyclerView.layoutManager = GridLayoutManager(this, 2)
            val adapter = StatusAdapter(this.uriList, this)
            binding.RecyclerView.adapter = adapter
        }
    }
}
