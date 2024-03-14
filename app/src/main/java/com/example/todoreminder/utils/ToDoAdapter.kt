package com.example.todoreminder.utils


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoreminder.databinding.EachTodoItemBinding
import com.example.todoreminder.utils.model.ToDoData
import android.util.Log
import com.facebook.shimmer.ShimmerFrameLayout

class ToDoAdapter(private val list: MutableList<ToDoData>) :
    RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>() {
    //private  val TAG = "TaskAdapter"
    private var listener: ToDoAdapterClicksInterface? = null

    //private var isShimmer=true
    fun setListener(listener: ToDoAdapterClicksInterface) {
        this.listener = listener
    }

    class ToDoViewHolder(val binding: EachTodoItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        /* if(isShimmer){
             val shimmerView=LayoutInflater.from(parent.context)
                 .inflate(R.layout.shimmer_layout,parent,false) as ShimmerFrameLayout
             return ShimmerViewHolder(shimmerView)
         }
         else{
             val binding=EachTodoItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
             return ToDoViewHolder(binding)
         }*/
        val binding =
            EachTodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToDoViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.todoTask.text = this.task
                //Log.d(TAG, "onBindViewHolder: "+this)
                binding.deleteTask.setOnClickListener {
                    listener?.onDeleteTaskBtnClicked(this)
                }
                binding.editTask.setOnClickListener {
                    listener?.onEditTaskBtnClicked(this)
                }
                /*binding.deleteTask.setOnClickListener {
                    listener?.onDeleteTaskBtnClicked(this)
                }*/
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    //  interface ToDoAdapterClicksInterface {
//        fun onDeleteTaskBtnClicked(toDoData: ToDoData , position: Int)
//        fun onEditTaskBtnClicked(toDoData: ToDoData , position: Int)
//    }
    interface ToDoAdapterClicksInterface {
        fun onDeleteTaskBtnClicked(toDoData: ToDoData)
        fun onEditTaskBtnClicked(toDoData: ToDoData)
    }
}