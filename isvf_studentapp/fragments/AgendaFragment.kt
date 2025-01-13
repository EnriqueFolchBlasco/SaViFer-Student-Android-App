package es.efb.isvf_studentapp.fragments

import NotificationWorker
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import es.efb.isvf_studentapp.R
import es.efb.isvf_studentapp.databinding.FragmentAgendaBinding
import es.efb.isvf_studentapp.notifications.TaskManager
import es.efb.isvf_studentapp.classes.Task
import es.efb.isvf_studentapp.adapters.TaskAdapter
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit
import androidx.activity.result.contract.ActivityResultContracts
import java.util.Calendar

class AgendaFragment : Fragment() {

    private var _binding: FragmentAgendaBinding? = null
    private val binding get() = _binding!!

    private val tasks = mutableListOf<Task>()
    private lateinit var mAdapter: TaskAdapter

    private val taskManager: TaskManager by lazy { TaskManager(requireContext()) }

    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            Toast.makeText(requireContext(), getString(R.string.Permission_granted), Toast.LENGTH_SHORT).show()
            //Log.d("AgendaFragment", "permiso fet, pot ficar notis")
        } else {
            Toast.makeText(requireContext(), getString(R.string.Permission_denied), Toast.LENGTH_SHORT).show()
            //Log.d("AgendaFragment", "permiso no fet, no pot ficar notis.")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgendaBinding.inflate(inflater, container, false)

        checkNotificationPermission()
        setupRecyclerView()
        setupFloatingActionButton()

        return binding.root
    }

    private fun checkNotificationPermission() {
        val permissionStatus = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.POST_NOTIFICATIONS
        )

        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            permissionRequest.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun setupRecyclerView() {
        binding.rvTasks.layoutManager = LinearLayoutManager(requireContext())
        mAdapter = TaskAdapter(tasks, requireContext()) { task ->
            deleteTask(task)
        }
        binding.rvTasks.adapter = mAdapter

        loadTasks()
    }

    private fun setupFloatingActionButton() {
        binding.fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(
            R.layout.dialog_add_task, null, false
        )

        val inputTitulo = dialogView.findViewById<TextInputEditText>(R.id.etTitulo)
        val inputDescripcion = dialogView.findViewById<TextInputEditText>(R.id.etDescripcion)
        val inputFecha = dialogView.findViewById<TextInputEditText>(R.id.etFecha)
        val inputHora = dialogView.findViewById<TextInputEditText>(R.id.etHora)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.nueva_tarea))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.anadir)) { _, _ ->
                val titulo = inputTitulo.text.toString()
                val descripcion = inputDescripcion.text.toString()
                val fecha = inputFecha.text.toString()
                val hora = inputHora.text.toString()

                if (titulo.isBlank() || fecha.isBlank() || hora.isBlank()) {
                    Toast.makeText(requireContext(), getString(R.string.campos_obligatorios), Toast.LENGTH_SHORT).show()
                } else {
                    addTask(titulo, descripcion, fecha, hora)
                }
            }
            .setNegativeButton(getString(R.string.Cancelar), null)
            .show()
    }


    private fun addTask(titulo: String, descripcion: String, fecha: String, hora: String) {
        val task = Task(titulo, descripcion, fecha, hora)
        tasks.add(task)
        mAdapter.notifyDataSetChanged()

        taskManager.saveTask(task)
        scheduleNotification(task)
    }

    private fun scheduleNotification(task: Task) {
        val permissionStatus = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.POST_NOTIFICATIONS
        )

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            val inputData = workDataOf(
                "taskTitle" to task.titulo,
                "taskDescription" to task.descripcion
            )

            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(calculateDelay(task), TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(requireContext()).enqueue(workRequest)
        } else {
            Toast.makeText(requireContext(), getString(R.string.Permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteTask(task: Task) {
        taskManager.deleteTask(task)
        tasks.remove(task)
        mAdapter.notifyDataSetChanged()

        Toast.makeText(requireContext(), getString(R.string.taskDeleted), Toast.LENGTH_SHORT).show()
    }

    private fun calculateDelay(task: Task): Long {
        try {
            val calendar = Calendar.getInstance().apply {
                val (day, month, year) = task.fecha.split("/").map { it.toIntOrNull() }
                val (hour, minute) = task.hora.split(":").map { it.toIntOrNull() }

                if (day == null || month == null || year == null || hour == null || minute == null) {
                    throw IllegalArgumentException("La data n oes valida")
                }

                //datos pal calendari
                set(year, month - 1, day, hour, minute)
            }

            val currentTime = Calendar.getInstance().timeInMillis
            return calendar.timeInMillis - currentTime

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), getString(R.string.invalid_date), Toast.LENGTH_SHORT).show()
            return 0
        }
    }

    private fun loadTasks() {
        tasks.clear()
        tasks.addAll(taskManager.getAllTasks())
        mAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
