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
import com.example.todoreminder.databinding.FragmentSignupBinding
import com.example.todoreminder.utils.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class signupFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: FragmentSignupBinding
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(view)
        registerEvent()
        emailFocusListener(view)
        passwordFocusListener(view)
        verifypasswordFocusListener(view)
    }

    private fun emailFocusListener(view: View) {
        binding.signupemail.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.signupemailcotainer.helperText = validEmail()
            }
        }
    }

    private fun validEmail(): String? {
        val emailText = binding.signupemail.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return "Invalid Email Address"
        }
        return null
    }

    private fun passwordFocusListener(view: View) {
        binding.signuppass.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.signuppasswordcontainer.helperText = validPassword()
            }
        }
    }

    private fun validPassword(): String? {
        val passwordText = binding.signuppass.text.toString()
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

    private fun verifypasswordFocusListener(view: View) {
        binding.signuppass.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.signupverifypasswordcontainer.helperText = verifyvalidPassword()
            }
        }
    }

    private fun verifyvalidPassword(): String? {
        val passwordText = binding.signuppass.text.toString()
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

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
//        databaseRef=FirebaseDatabase.getInstance().//getReference("Users")
//        reference.child("Users").child(auth.currentUser?.uid.toString())
//        //.child(auth.currentUser?.uid?:"")
    }

    private fun registerEvent() {
        binding.btn.setOnClickListener {
            if (binding.signupemail.text.toString().length == 0 && binding.signuppass.text.toString().length == 0 && binding.signupname.text.toString().length == 0) {
                Toast.makeText(context, "Enter a mail id and password and name", Toast.LENGTH_SHORT)
                    .show()
            } else if (binding.signupemail.text.toString().length == 0 && binding.signuppass.text.toString().length == 0) {
                Toast.makeText(context, "Enter a mail id and password", Toast.LENGTH_SHORT).show()
            } else if (binding.signupname.text.toString().length == 0 && binding.signuppass.text.toString().length == 0) {
                Toast.makeText(context, "Enter a password and name", Toast.LENGTH_SHORT).show()
            } else if (binding.signuppass.text.toString().length == 0) {
                Toast.makeText(context, "Enter a password", Toast.LENGTH_SHORT).show()
            } else if (binding.signupemail.text.toString().length == 0) {
                Toast.makeText(context, "Enter a mail id", Toast.LENGTH_SHORT).show()
            } else if (binding.signupname.text.toString().length == 0) {
                Toast.makeText(context, "Enter name", Toast.LENGTH_SHORT).show()
            }
            val Email = binding.signupemail.text.toString().trim()
            val Pass = binding.signuppass.text.toString().trim()
            val Vpass = binding.signupvpass.text.toString().trim()
            val Name = binding.signupname.text.toString().trim()
            val user = User(Email, Name, Pass)

            if (Email.isNotEmpty() && Pass.isNotEmpty() && Vpass.isNotEmpty()) {
                if (Pass == Vpass) {
                    binding.progressBar2.visibility = View.VISIBLE
                    auth.createUserWithEmailAndPassword(Email, Pass).addOnCompleteListener(
                        OnCompleteListener {
                            if (it.isSuccessful) {
                                databaseRef = FirebaseDatabase.getInstance().//getReference("Users")
                                reference.child("Users").child(auth.currentUser?.uid.toString())
                                databaseRef.push().setValue(user).addOnCompleteListener {
                                    Toast.makeText(
                                        context,
                                        "Register Successfully",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    navController.navigate(R.id.action_signupFragment_to_homeFragment)
                                }
                            } else {
                                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                            binding.progressBar2.visibility = View.GONE
                        })
                } else {
                    Toast.makeText(context, "Password doesn't match", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.log.setOnClickListener {
            navController.navigate((R.id.action_signupFragment_to_signinFragment))
        }
    }

}