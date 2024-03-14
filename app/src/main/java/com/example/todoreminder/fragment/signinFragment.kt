package com.example.todoreminder.fragment

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.todoreminder.R
import com.example.todoreminder.databinding.FragmentSigninBinding
import com.google.firebase.auth.FirebaseAuth


class signinFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: FragmentSigninBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSigninBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(view)
        registerEvent()
        emailFocusListener(view)
        passwordFocusListener(view)
        //alert(view)
        //binding.btn.setOnClickListener { submitForm(view) }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()

    }

    private fun submitForm(view: View) {
        binding.signinemailcontainer.helperText = validEmail()
        binding.signinpassworcontainer.helperText = validPassword()

        val validEmail = binding.signinemailcontainer.helperText == null
        val validPassword = binding.signinpassworcontainer.helperText == null

        if (validEmail || validPassword)
            resetForm()

    }

    private fun resetForm() {
        var message = ""
        if (binding.signinemailcontainer.helperText != null)
            Toast.makeText(context, "Enter a mail id", Toast.LENGTH_SHORT).show()
        //message="Enter a mail id"
        if (binding.signinpassworcontainer.helperText != null)
            Toast.makeText(context, "Enter a password", Toast.LENGTH_SHORT).show()
//            message="Enter a password"
//            AlertDialog.Builder(this)
//            .setTitle("Missing credentials")
//            .setMessage(message)
//            .setPositiveButton("Okkay") { _, _ ->
//                //do nothing
//            }
    }

    private fun emailFocusListener(view: View) {
        binding.sigininemail.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.signinemailcontainer.helperText = validEmail()
            }
        }
    }

    private fun validEmail(): String? {
        val emailText = binding.sigininemail.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return "Invalid Email Address"
        }
        return null
    }

    private fun passwordFocusListener(view: View) {
        binding.sigininpass.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.signinpassworcontainer.helperText = validPassword()
            }
        }
    }

    private fun validPassword(): String? {
        val passwordText = binding.sigininpass.text.toString()
        if (passwordText.length < 8) {
            return "Minimum 8 Character Password"
        }
        if (!passwordText.matches(".*[A-Z].*".toRegex())) {
            return "Must Contain 1 Upper-case Character"
        }
        if (!passwordText.matches(".*[a-z].*".toRegex())) {
            return "Must Contain 1 Lower-case Character"
        }
        if (!passwordText.matches(".*[@#\$%^&+=].*".toRegex())) {
            return "Must Contain 1 Special Character (@#\$%^&+=)"
        }
        return null
    }


    private fun registerEvent() {
        binding.btn.setOnClickListener {
            if (binding.sigininemail.text.toString().length == 0 && binding.sigininpass.text.toString().length == 0) {
                Toast.makeText(context, "Enter a mail id and password", Toast.LENGTH_SHORT).show()
            } else if (binding.sigininpass.text.toString().length == 0) {
                Toast.makeText(context, "Enter a password", Toast.LENGTH_SHORT).show()
            } else if (binding.sigininemail.text.toString().length == 0) {
                Toast.makeText(context, "Enter a mail id", Toast.LENGTH_SHORT).show()
            }
            val email = binding.sigininemail.text.toString().trim()
            val pass = binding.sigininpass.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                binding.progressBar.visibility = View.VISIBLE
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Login Successfully", Toast.LENGTH_SHORT).show()
                        navController.navigate(R.id.action_signinFragment_to_homeFragment)
                    } else {
                        Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
        binding.textView.setOnClickListener {
            navController.navigate(R.id.action_signinFragment_to_signupFragment)
        }
    }
}
