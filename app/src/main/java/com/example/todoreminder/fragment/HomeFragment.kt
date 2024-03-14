package com.example.todoreminder.fragment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.health.connect.datatypes.units.Length
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoreminder.R
import com.example.todoreminder.databinding.FragmentHomeBinding
import com.example.todoreminder.utils.ToDoAdapter
import com.example.todoreminder.utils.model.ToDoData
import com.example.todoreminder.utils.model.User
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment(), todopopupFragment.DialogNextBtnClickListener,
    ToDoAdapter.ToDoAdapterClicksInterface {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: FragmentHomeBinding
    private var popUpFragment: todopopupFragment? = null
    private lateinit var adapter: ToDoAdapter
    private lateinit var user: User
    private lateinit var mList: MutableList<ToDoData>

    //private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getDataFromFirebase()
        registerEvents()
        // createNotificationChannel()

    }

//    private fun createNotificationChannel() {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//
//            val name: CharSequence = "TODOREMINDERCHANNEL"
//            val description = "Channel for Alarm Manager"
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val channel = NotificationChannel("TO DO REMINDER", name, importance)
//            channel.description = description
//            val notificationManager = getSystemService(
//                NotificationManager::class.java
//            )
//
//            notificationManager.createNotificationChannel(channel)
//        }
//    }

    private fun registerEvents() {
        binding.addtask.setOnClickListener {
            if (popUpFragment != null)
                childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
            popUpFragment = todopopupFragment()
            popUpFragment!!.setListener(this)
            popUpFragment!!.show(
                childFragmentManager,
                todopopupFragment.TAG

            )

        }
        binding.logout.setOnClickListener {
            auth.signOut()
            Toast.makeText(context, "logout Successfully", Toast.LENGTH_SHORT).show()
            navController.navigate(R.id.action_homeFragment_to_signinFragment)
            getuserdata()
            //shimmerFrameLayout=binding.shimmer
            //shimmerFrameLayout.startShimmer()
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference
            .child("Tasks").child(auth.currentUser?.uid.toString())
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.layoutManager = LinearLayoutManager(context)
        mList = mutableListOf()
        adapter = ToDoAdapter(mList)
        adapter.setListener(this)
        binding.recyclerview.adapter = adapter
        //if (auth.currentUser?.uid.toString().isNotEmpty())

    }

    private fun getuserdata() {
        databaseRef = FirebaseDatabase.getInstance().getReference("Users")
        databaseRef.child(auth.currentUser?.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)!!
                    binding.usernname.setText(user.Name)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun getDataFromFirebase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for (taskSnapshot in snapshot.children) {
                    val todoTask = taskSnapshot.key?.let {
                        ToDoData(it, taskSnapshot.value.toString())
                    }
                    if (todoTask != null) {
                        //shimmerFrameLayout.stopShimmer()
                        //shimmerFrameLayout.visibility=View.GONE
                        //binding.recyclerview.visibility=View.VISIBLE
                        mList.add(todoTask)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT)
            }
        })
    }

    override fun onSaveTask(todo: String, todoEt: TextInputEditText) {
        databaseRef.push().setValue(todo).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Task Saved succesfully", Toast.LENGTH_SHORT)
                todoEt.text = null
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT)
            }
        }
        popUpFragment!!.dismiss()
    }

    override fun onUpdateTask(toDoData: ToDoData, todoEt: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[toDoData.taskId] = toDoData.task
        databaseRef.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Updated Sucessfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT)
            }
            todoEt.text = null
            popUpFragment!!.dismiss()
        }
    }

    override fun onDeleteTaskBtnClicked(toDoData: ToDoData) {
        databaseRef.child(toDoData.taskId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Delete Sucessfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditTaskBtnClicked(toDoData: ToDoData) {
        if (popUpFragment != null)
            childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
        popUpFragment = todopopupFragment.newInstance(toDoData.taskId, toDoData.task)
        popUpFragment!!.setListener(this)
        popUpFragment!!.show(childFragmentManager, todopopupFragment.TAG)
    }


//    override fun onDeleteTaskBtnClicked(toDoData: ToDoData, position: Int) {
//        databaseRef.child(toDoData.taskId).removeValue().addOnCompleteListener {
//            if (it.isSuccessful) {
//                Toast.makeText(context, "Delete Sucessfully", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    override fun onEditTaskBtnClicked(toDoData: ToDoData, position: Int) {
//        if (popUpFragment != null)
//            childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
//        popUpFragment = todopopupFragment.newInstance(toDoData.taskId, toDoData.task)
//        popUpFragment!!.setListener(this)
//        popUpFragment!!.show(childFragmentManager, todopopupFragment.TAG)
//    }
}