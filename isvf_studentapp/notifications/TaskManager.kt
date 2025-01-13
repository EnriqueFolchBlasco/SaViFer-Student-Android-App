package es.efb.isvf_studentapp.notifications

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.efb.isvf_studentapp.classes.Task

class TaskManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("TASK_PREFS", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveTask(task: Task) {
        val tasks = getAllTasks().toMutableList()
        tasks.add(task)
        saveTasks(tasks)
    }

    fun getAllTasks(): List<Task> {
        val tasksJson = prefs.getString("TASK_LIST", null) ?: return emptyList()
        val type = object : TypeToken<List<Task>>() {}.type
        return gson.fromJson(tasksJson, type)
    }

    fun deleteTask(task: Task) {
        val tasks = getAllTasks().toMutableList()
        tasks.removeIf { it.id == task.id }
        saveTasks(tasks)
    }

    private fun saveTasks(tasks: List<Task>) {
        val tasksJson = gson.toJson(tasks)
        prefs.edit().putString("TASK_LIST", tasksJson).apply()
    }
}
