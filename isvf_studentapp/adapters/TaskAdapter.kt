package es.efb.isvf_studentapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.efb.isvf_studentapp.R
import es.efb.isvf_studentapp.classes.Task

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val context: Context,
    private val mListener: ((Task) -> Unit)? = null
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = tasks[position]
        holder.bindItem(item, context)
        holder.itemView.findViewById<View>(R.id.btnDelete).setOnClickListener {
            mListener?.invoke(item)
        }
    }

    override fun getItemCount(): Int = tasks.size

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTitulo: TextView = view.findViewById(R.id.tvTitulo)
        private val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        private val tvFechaHora: TextView = view.findViewById(R.id.tvFechaHora)

        fun bindItem(task: Task, context: Context) {
            tvTitulo.text = task.titulo
            tvDescripcion.text = task.descripcion
            tvFechaHora.text = "${task.fecha} - ${task.hora}"
        }
    }
}