package com.muhammetgumus.photosharing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.muhammetgumus.photosharing.databinding.FragmentFeedBinding

class FeedFragment : Fragment() , PopupMenu.OnMenuItemClickListener {

    private lateinit var popup: PopupMenu
    private var _binding: FragmentFeedBinding? = null
    // This property is only valid between onCreateView and  // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestoredb : FirebaseFirestore




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = Firebase.auth
        firestoredb = Firebase.firestore
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener { FloatingButtonClicked(it) }
         firestoreverial()
    }


    private fun firestoreverial() {

     firestoredb.collection("Posts").addSnapshotListener { value, error ->
          if (error!=null) {
              Toast.makeText(requireContext(),error.localizedMessage,Toast.LENGTH_LONG) .show()
               }

         else {
                if (value!=null && !value.isEmpty){

             val documents = value.documents

                for (document in documents) {
                   val comment = document.get("comment") as String //casting
                   println(comment)

                }
                }

         }

     }

    }


    fun FloatingButtonClicked(view: View) {

        popup = PopupMenu(requireContext(), binding.floatingActionButton)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.my_pop_menu, popup.menu)
        popup.setOnMenuItemClickListener(this)
        popup.show()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




    override fun onMenuItemClick(item: MenuItem?): Boolean {


        return  when (item?.itemId) {


            R.id.Upload_item -> {
                val action = FeedFragmentDirections.actionFeedFragmentToLoadingFragment()
                Navigation.findNavController(requireView()).navigate(action)
                true
            }



            R.id.Exit_item -> {
                //Çıkış yapma işlemi
                auth.signOut()
                val action = FeedFragmentDirections.actionFeedFragmentToUserFragment()
                Navigation.findNavController(requireView()).navigate(action)
                true
            }


            else -> false
        }
      }




}