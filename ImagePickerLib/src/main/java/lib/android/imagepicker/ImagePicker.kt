package lib.android.imagepicker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import lib.android.imagepicker.utils.compressImageFile
import java.io.File

interface ImagePickerContract {
    var imagePickerHelper: ImagePickerHelper
    fun takePhotoFromCamera()
    fun takePhotoFromGallery()
    fun setImageSelectedListener(listener: ImagePicker.OnImageSelectedListener)
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
}

class ImagePicker(private val context: AppCompatActivity, private val applicationId: String) :
    ImagePickerContract {

    private var queryImageUrl: String = ""
    private var imgPath: String = ""
    private var imageUri: Uri? = null
    private val permissions = arrayOf(Manifest.permission.CAMERA)
    private val requestCameraPermissionCode = 1000
    var cameraPermissionErrorString: String = "Permissions not grated"

    override var imagePickerHelper: ImagePickerHelper = ImagePickerHelper(context)
    private lateinit var onImageSelectedListener: OnImageSelectedListener
    private var cameraOrGalleryActivityLauncher: ActivityResultLauncher<Intent>

    init {
        cameraOrGalleryActivityLauncher =
            context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    handleImageRequest(it.data)
                }
            }
    }

    override fun takePhotoFromCamera() {
        if (imagePickerHelper.isPermissionsAllowed(
                permissions,
                true,
                requestCameraPermissionCode
            )
        ) {
            captureImageFromCamera()
        }
    }

    private fun captureImageFromCamera() {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri())
        cameraOrGalleryActivityLauncher.launch(takePhotoIntent)
    }

    private fun setImageUri(): Uri {
        val folder = File("${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)}")
        folder.mkdirs()

        val file = File(folder, "image_tmp.jpg")
        if (file.exists())
            file.delete()
        file.createNewFile()
        imageUri = FileProvider.getUriForFile(
            context,
            applicationId + context.getString(R.string.file_provider_name),
            file
        )
        imgPath = file.absolutePath
        return imageUri!!
    }

    override fun takePhotoFromGallery() {
        val takePhotoFromGalleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        cameraOrGalleryActivityLauncher.launch(takePhotoFromGalleryIntent)
    }

    override fun setImageSelectedListener(listener: OnImageSelectedListener) {
        this.onImageSelectedListener = listener
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            requestCameraPermissionCode -> {
                if (imagePickerHelper.isAllPermissionsGranted(grantResults)) {
                    takePhotoFromCamera()
                } else {
                    Toast.makeText(
                        context,
                        cameraPermissionErrorString,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun handleImageRequest(data: Intent?) {
        val exceptionHandler = CoroutineExceptionHandler { _, t ->
            t.printStackTrace()
            if (::onImageSelectedListener.isInitialized){
                onImageSelectedListener.onImageSelectFailure()
            }
            Toast.makeText(
                context,
                t.localizedMessage ?: context.getString(R.string.some_err),
                Toast.LENGTH_SHORT
            ).show()
        }

        GlobalScope.launch(Dispatchers.Main + exceptionHandler) {
            if (data?.data != null) {     //Photo from gallery
                imageUri = data.data
                queryImageUrl = imageUri?.path!!
                queryImageUrl = context.compressImageFile(queryImageUrl, shouldOverride = false, imageUri!!)
            } else {
                queryImageUrl = imgPath
                context.compressImageFile(queryImageUrl, uri = imageUri!!)
            }
            imageUri = Uri.fromFile(File(queryImageUrl))

            if (queryImageUrl.isNotEmpty()) {
                if (::onImageSelectedListener.isInitialized){
                    onImageSelectedListener.onImageSelectSuccess(queryImageUrl)
                }
            } else {
                if (::onImageSelectedListener.isInitialized){
                    onImageSelectedListener.onImageSelectFailure()
                }
            }
        }
    }

    interface OnImageSelectedListener {
        fun onImageSelectSuccess(imagePath: String)
        fun onImageSelectFailure()
    }
}
