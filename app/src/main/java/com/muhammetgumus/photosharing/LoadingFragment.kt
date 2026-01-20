package com.muhammetgumus.photosharing

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage

import com.muhammetgumus.photosharing.databinding.FragmentLoadingBinding // ÖNEMLİ: Doğru binding sınıfını import edin.
import java.util.UUID

class LoadingFragment : Fragment() {


    private var _binding: FragmentLoadingBinding? = null

    private val binding get() = _binding!!


    // İzin ve Galeri işlemlerini modern şekilde yönetmek için launcher'lar
    private lateinit var activityResultLauncher: ActivityResultLauncher<String>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    var secilenGorsel: Uri? = null
    var secilenBitmap: Bitmap? = null


  private lateinit var auth: FirebaseAuth
  private lateinit var storage: FirebaseStorage
  private lateinit var firestoredb : FirebaseFirestore






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        storage = Firebase.storage
        firestoredb = Firebase.firestore

        // Launcher'ları fragment oluşturulurken kaydediyoruz.
        registerLaunchers()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoadingBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.Uploadbutton.setOnClickListener { ClickUpload(it) }
        binding.imageView.setOnClickListener { ClickImage(it) }

    }


    fun ClickUpload(view: View) {

        val uuid = UUID.randomUUID()
        val gorselIsmi = "${uuid}.png"



        val referance = storage.reference
        val gorselReferance = referance.child("images").child(gorselIsmi)

         if (secilenGorsel!=null) {
             gorselReferance.putFile(secilenGorsel!!).addOnSuccessListener { taskSnapshot ->

              //url yi al
             gorselReferance.downloadUrl.addOnSuccessListener { uri ->
                 val downloadUrl = uri.toString()
                // veri tabanına yazmamız lazım

               val postmap = hashMapOf<String, Any>()
                 postmap.put("DowloandUrı", downloadUrl)
                 postmap.put("userEmail",auth.currentUser!!.email.toString())
                 postmap.put("comment",binding.CommentText.text.toString())
                 postmap.put("date", Timestamp.now())

                 firestoredb.collection("Posts").add(postmap).addOnSuccessListener {

                     // veri database yüklenmiş oluyor
                     val action = LoadingFragmentDirections.actionLoadingFragmentToFeedFragment()
                     Navigation.findNavController(view).navigate(action)

                 }.addOnFailureListener { exception ->
                     Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_LONG).show()
                 }


             }


             }
                 .addOnFailureListener { exception ->
                     Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
                 }
         }

    }


    fun ClickImage(view: View) {

        // İzinleri Android sürümüne göre belirle
        // Android 13 (API 33) ve sonrası için -> READ_MEDIA_IMAGES
        // Android 13 öncesi için -> READ_EXTERNAL_STORAGE
        val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {

            // 1. Durum: İzin zaten verilmiş mi?
            ContextCompat.checkSelfPermission(requireContext(), permissionToRequest) == PackageManager.PERMISSION_GRANTED -> {
                // İzin varsa, direkt galeriyi açan launcher'ı başlat.
                activityResultLauncher.launch("image/*")
            }

            // 2. Durum: Kullanıcıya iznin mantığını açıklamamız gerekiyor mu?
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permissionToRequest) -> {
                // Bu, kullanıcı izni daha önce reddettiğinde ama "bir daha sorma" demediğinde çalışır.
                Snackbar.make(view, "Galeriye erişim için izin gerekiyor.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("İzin Ver") {
                        // İzin isteme dialoğunu göster.
                        permissionLauncher.launch(permissionToRequest)
                    }.show()
            }

            // 3. Durum: İzin ilk defa isteniyor VEYA kullanıcı "bir daha sorma" demiş.
            else -> {
                // Doğrudan izin isteme dialoğunu göster.
                permissionLauncher.launch(permissionToRequest)
            }
        }
    }

    // İzin isteme ve galeriye gitme işlemlerinin sonucunu yakalamak için launcher'ları hazırlayan fonksiyon
    private fun registerLaunchers() {
        // Galeriden görsel seçme işleminin sonucunu yakalar (GetContent kullanarak)
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                secilenGorsel = it
                try {
                    if (Build.VERSION.SDK_INT >= 28) {
                        val source = ImageDecoder.createSource(requireActivity().contentResolver, it)
                        secilenBitmap = ImageDecoder.decodeBitmap(source)
                        binding.imageView.setImageBitmap(secilenBitmap)
                    } else {
                        secilenBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
                        binding.imageView.setImageBitmap(secilenBitmap)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // İzin isteme işleminin sonucunu yakalar
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Kullanıcı izni verdi, şimdi galeriyi aç.
                activityResultLauncher.launch("image/*")
            } else {
                // Kullanıcı izni reddetti.
                Snackbar.make(requireView(), "İzin reddedildi.", Snackbar.LENGTH_SHORT).show()
                // Not: Eğer kullanıcı "bir daha sorma" dediyse, gorselSec fonksiyonu onu doğrudan
                // permissionLauncher'a gönderecek ve burası çalışacaktır.
                // İsterseniz burada ayarlara yönlendiren bir Snackbar gösterebilirsiniz.
            }
        }
        
    }



    // onDestroyView() metodunu kullanmak daha güvenlidir.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
