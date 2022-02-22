package com.tanav.eztoll

import android.app.Activity
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tanav.eztoll.Interfaces.ImageListener
import com.tanav.eztoll.Interfaces.ImageUploadListener
import com.vansuita.pickimage.dialog.PickImageDialog
import java.util.*

class Util
{

    companion object
    {

        fun readImageFromGallery(activity: Activity?, imageListener: ImageListener)
        {
            PickImageDialog.build { r ->
                if (r.error == null) {
                    imageListener.onImageLoaded(false, r.uri, r.bitmap)
                } else {
                    imageListener.onImageLoaded(true, null, null)
                }
            }.show(activity as FragmentActivity?)
        }


        fun uploadImage(uri: Uri?, imageUploadListener: ImageUploadListener)
        {
            if (uri == null) {
                imageUploadListener.onUpload(false, "", "")
                return
            }
            val storageRef: StorageReference = FirebaseStorage.getInstance().getReference()
            val ImagePath=
                String.format("images/%s.jpg", Calendar.getInstance().timeInMillis.toString())
            val imageRef: StorageReference =storageRef.child(ImagePath)

            imageRef.putFile(uri).addOnCompleteListener(OnCompleteListener {
                if (it.isSuccessful)
                {
                    imageRef.downloadUrl
                        .addOnCompleteListener(OnCompleteListener<Uri>
                        { task ->
                            if (task.isSuccessful)
                            {
                                imageUploadListener.onUpload(false,
                                    ImagePath,
                                    task.result.toString()
                                )
                            } else {
                                imageUploadListener.onUpload(true, task.exception!!.message, "")
                            }
                        })
                } else {
                    imageUploadListener.onUpload(true, it.exception!!.message, "")
                }

            })

        }
    }

}