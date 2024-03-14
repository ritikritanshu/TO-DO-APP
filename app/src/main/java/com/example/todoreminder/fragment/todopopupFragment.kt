package com.example.todoreminder.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.todoreminder.databinding.FragmentTodopopupBinding
import com.example.todoreminder.utils.model.ToDoData
import com.google.android.material.textfield.TextInputEditText

class todopopupFragment : DialogFragment() {

    private lateinit var binding: FragmentTodopopupBinding
    private lateinit var listener: DialogNextBtnClickListener
    private var toDoData: ToDoData? = null

    fun setListener(listener: HomeFragment) {
        this.listener = listener
    }

    companion object {
        const val TAG = "todopopupFragment"

        @JvmStatic
        fun newInstance(taskId: String, task: String) = todopopupFragment().apply {
            arguments = Bundle().apply {
                putString("taskId", taskId)
                putString("task", task)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTodopopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            toDoData =
                ToDoData(
                    arguments?.getString("taskId").toString(),
                    arguments?.getString("task").toString()
                )
            binding.todoEt.setText(toDoData?.task)
        }
        registerEvent()
    }

    private fun registerEvent() {
        binding.todoadd.setOnClickListener {
            val todotask = binding.todoEt.text.toString()
            if (todotask.isNotEmpty()) {
                if (toDoData == null) {
                    listener.onSaveTask(todotask, binding.todoEt)
                } else {
                    toDoData?.task = todotask
                    listener.onUpdateTask(toDoData!!, binding.todoEt)
                }
                Toast.makeText(context, "Task added succesfully", Toast.LENGTH_SHORT).show()
//                listener.onSaveTask(todotask, binding.todoEt)
            } else {
                Toast.makeText(context, "Please add some task", Toast.LENGTH_SHORT).show()
            }
        }
        binding.todoClose.setOnClickListener {
            dismiss()
        }
    }

    interface DialogNextBtnClickListener {
        fun onSaveTask(todo: String, todoEt: TextInputEditText)
        fun onUpdateTask(toDoData: ToDoData, todoEt: TextInputEditText)
    }
}