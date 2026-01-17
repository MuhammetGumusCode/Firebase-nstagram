package com.muhammetgumus.photosharing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.muhammetgumus.photosharing.databinding.FragmentUserBinding

class UserFragment : Fragment() {
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = Firebase.auth
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.SignUpbutton.setOnClickListener { ClickSignUp(it) }
        binding.SignInbutton.setOnClickListener { ClickSignIn(it) }


        val currentUser = auth.currentUser
        if (currentUser != null ){

            val action = UserFragmentDirections.actionUserFragmentToFeedFragment()
            Navigation.findNavController(view).navigate(action)
        }


    }


    fun ClickSignUp(view: View) {


        val email = binding.editTextEmailAddress1.text.toString()
        val password = binding.editTextNumberPassword.text.toString()


        if (email.isNotBlank() && password.isNotBlank()) {
           auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
               if (task.isSuccessful) {

                   val action = UserFragmentDirections.actionUserFragmentToFeedFragment()
                   Navigation.findNavController(view).navigate(action)


               }

           }.addOnFailureListener { exception ->
               Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_LONG).show()
               println(exception.localizedMessage)
           }

       } else {
           Toast.makeText(requireContext(),"Enter email and password",Toast.LENGTH_LONG).show()
       }



    }


    fun ClickSignIn(view: View) {


      val email = binding.editTextEmailAddress1.text.toString()
        val password = binding.editTextNumberPassword.text.toString()


        if (email.isNotBlank() && password.isNotBlank()) {

        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {

            val action = UserFragmentDirections.actionUserFragmentToFeedFragment()
            Navigation.findNavController(view).navigate(action)

        } .addOnFailureListener { exception ->
            Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
        }


        }

        else {
            Toast.makeText(requireContext(),"Enter email and password",Toast.LENGTH_LONG).show()
        }
 }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}