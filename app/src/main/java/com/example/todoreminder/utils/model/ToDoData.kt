package com.example.todoreminder.utils.model

data class ToDoData(val taskId: String, var task: String)
data class User(val Email: String? = null, val Name: String? = null, val Pass: String? = null)


data class NotificationItem(val id: Int, val title: String, val content: String)