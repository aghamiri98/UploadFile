package com.aghamiri.uploadfile

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var realPath: String?=""
    private val PERMISSION_REQUEST_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn.setOnClickListener {
            if (!checkPermission()) {
                requestPermission()
            } else {

                chooseImage()


            }

        }


        btnUpload.setOnClickListener {
         val repo =AppRepository(this@MainActivity)
           repo.uploadToServer(realPath)


        }


    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@MainActivity)
            .setMessage(message)
            .setPositiveButton("agree", okListener)
            .setNegativeButton("cancel", null)
            .create()
            .show()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty()) {
                val writeExternalStorageAccepted =
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (writeExternalStorageAccepted) {
                    Snackbar.make(
                        window.decorView.rootView,
                        "Allow Access external Storage",
                        Snackbar.LENGTH_LONG
                    ).show()
                    chooseImage()
                } else {
                    Snackbar.make(
                        window.decorView.rootView,
                        "Deny Access external Storage",
                        Snackbar.LENGTH_LONG
                    ).show()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                            showMessageOKCancel("you should access external storage",
                                DialogInterface.OnClickListener { dialog, which ->
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(
                                            arrayOf(WRITE_EXTERNAL_STORAGE),
                                            PERMISSION_REQUEST_CODE
                                        )
                                    }
                                })
                            return
                        }
                    }

                }
            }
        }
    }


    private fun chooseImage() {
        val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val chosenImageUri: Uri? = data!!.data
             realPath = ImageFilePath.getPath(this@MainActivity, data.data)
            // AndroidUtilities.log("imagePath: " + realPath)
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                    this@MainActivity.contentResolver,
                    chosenImageUri
                )
                imageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }


    }


}