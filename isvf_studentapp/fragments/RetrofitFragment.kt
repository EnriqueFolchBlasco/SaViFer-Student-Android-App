package es.efb.isvf_studentapp.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import es.efb.isvf_studentapp.R
import es.efb.isvf_studentapp.retrofit.Post
import es.efb.android.adapters.PostAdapter
import es.efb.isvf_studentapp.databinding.FragmentRetrofitBinding
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.retrofit.RetrofitInstance
import com.squareup.picasso.Picasso
import es.efb.isvf_studentapp.managers.FirestoreManager
import es.efb.isvf_studentapp.retrofit.ApiService
import es.efb.isvf_studentapp.retrofit.RetrofitObject
import es.efb.isvf_studentapp.utils.IS_ADMINISTRADOR
import es.efb.isvf_studentapp.utils.IS_PROFESSOR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RetrofitFragment : Fragment() {

    private var _binding: FragmentRetrofitBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAdapter: PostAdapter
    private lateinit var listaPosts: MutableList<Post>
    private var listener: OnFragmentInteractionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw ClassCastException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentRetrofitBinding.inflate(inflater, container, false)

        if (IS_PROFESSOR) {
            //sajsahinnjsfdajfbsad ili eae per al boto que apareix desapareiox
            binding.fabAddPost.visibility = View.VISIBLE
            binding.fabAddPost.isEnabled = true
        } else if (IS_ADMINISTRADOR) {
            //sajsahinnjsfdajfbsad ili eae per al boto que apareix desapareiox
            binding.fabAddPost.visibility = View.VISIBLE
            binding.fabAddPost.isEnabled = true
        } else {
            binding.fabAddPost.visibility = View.GONE
            binding.fabAddPost.isEnabled = false
        }

        binding.fabAddPost.setOnClickListener {
            if (IS_PROFESSOR){
                listener?.openAddPostFragment()
            } else if (IS_ADMINISTRADOR){
                addProfessorDialogo()
            }

        }

        listaPosts = mutableListOf()
        mAdapter = PostAdapter(listaPosts, requireContext()) { post ->
            mostrarDialogoPost(post)
        }
        obtenerClima()
        setUpRecycler()

        return binding.root
    }

    private fun addProfessorDialogo() {
        val dialogo = Dialog(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialogo_add_prof, null)

        val emailEditText: EditText = view.findViewById(R.id.editTextProfessorEmail)
        val addButton: Button = view.findViewById(R.id.add_button)
        val removeButton: Button = view.findViewById(R.id.remove_button)
        val closeButton: Button = view.findViewById(R.id.botoClose)

        closeButton.setOnClickListener {
            dialogo.dismiss()
        }

        removeButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isNotEmpty()) {
                lifecycleScope.launch {
                    val success = FirestoreManager().removeProfessor(email)
                    if (success) {
                        Toast.makeText(requireContext(), "Professor removed successfully.", Toast.LENGTH_SHORT).show()
                        dialogo.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Failed to remove professor. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please enter an email.", Toast.LENGTH_SHORT).show()
            }
        }

        addButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isNotEmpty()) {
                lifecycleScope.launch {
                    val success = FirestoreManager().addProfessor(email)
                    if (success) {
                        Toast.makeText(requireContext(), "Professor added successfully.", Toast.LENGTH_SHORT).show()
                        dialogo.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Failed to add professor. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please enter an email.", Toast.LENGTH_SHORT).show()
            }
        }

        dialogo.setContentView(view)
        dialogo.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialogo.show()
    }

    interface OnFragmentInteractionListener {
        fun openAddPostFragment()
    }

    private fun setUpRecycler() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val call = RetrofitObject.getInstance()
                    .create(ApiService::class.java)
                    .getPosts()

                if (call.isSuccessful) {
                    call.body()?.let { posts ->
                        withContext(Dispatchers.Main) {
                            if (isAdded && _binding != null) {
                                listaPosts.clear()
                                listaPosts.addAll(posts)
                                mAdapter.notifyDataSetChanged()

                                binding.recyclerFriends.layoutManager = GridLayoutManager(requireContext(), 2)
                                binding.recyclerFriends.adapter = mAdapter
                            }
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        if (isAdded && _binding != null) {
                            Toast.makeText(requireContext(), getString(R.string.error_posts), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (isAdded && _binding != null) {
                        Toast.makeText(requireContext(), getString(R.string.error_network) + " Api esta off", Toast.LENGTH_LONG).show()
                        println(e.message.toString())

                        val predefinedPosts = listOf(
                            Post(id = 7, name = "Nueva asignatura de Tecnología y Robótica en 4º de ESO", description = "<p>La dirección del instituto ha anunciado la incorporación de una nueva asignatura de <b>Tecnología y Robótica</b> para los alumnos de 4º de ESO. Esta asignatura ofrecerá a los estudiantes una formación práctica en el uso de tecnologías avanzadas, como impresoras 3D y programación de robots. La idea es fomentar el interés por la ciencia y la tecnología en los jóvenes. Para más información, consulta <a href='https://example.com'>aquí</a>.</p>", photoUrl = "https://images.griddo.universitatcarlemany.com/como-la-tecnologia-impulsa-el-desarrollo-y-viceversa"),
                            Post(id = 8, name = "Visita al Museo de Ciencias para los alumnos de 1º de Bachillerato", description = "<p>Los estudiantes de 1º de Bachillerato han realizado una salida cultural al <i>Museo de Ciencias</i> de la ciudad. Durante la visita, pudieron ver exposiciones interactivas sobre física, biología y tecnología. Los alumnos quedaron fascinados con la <b>exposición sobre el espacio</b>, que incluye una réplica de un <a href='https://example.com/museo'>satélite</a> en escala real.</p>", photoUrl = "https://www.barcelo.com/guia-turismo/wp-content/uploads/2024/09/ok-museo-de-las-ciencias-valencia.jpg"),
                            Post(id = 9, name = "Curso de preparación para la EBAU en el instituto", description = "<p>El instituto ofrecerá un curso de <b>preparación intensiva para la EBAU</b> dirigido a los estudiantes de 2º de Bachillerato. Este curso incluirá clases prácticas, simulacros de examen y asesoramiento sobre técnicas de estudio. Se recomienda a los estudiantes interesados en mejorar su rendimiento que se inscriban a través del portal web del instituto.</p>", photoUrl = "https://fotografias.lasexta.com/clipping/cmsimages01/2022/05/06/F5147E33-27A2-40F9-AAF2-DA23C993E17E/que-ebau_98.jpg?crop=1462,823,x0,y10&width=1900&height=1069&optimize=low&format=webply"),
                            Post(id = 10, name = "Certificación en inglés B2 para estudiantes de 3º de ESO", description = "<p>Los alumnos de 3º de ESO tendrán la oportunidad de obtener la certificación oficial de inglés de nivel <b>B2</b> tras completar un curso intensivo que incluirá tanto clases teóricas como actividades prácticas. Los estudiantes interesados deberán inscribirse antes del 15 de marzo.</p>", photoUrl = "https://www.etsist.upm.es/uploaded/606/ingles-b2-1.jpg"),
                            Post(id = 11, name = "Conferencia sobre Inteligencia Artificial en el instituto", description = "<p>Este mes, el instituto organiza una conferencia sobre <b>Inteligencia Artificial</b> y su impacto en la educación. El evento contará con la participación de expertos en el área, quienes hablarán sobre los avances en la automatización y cómo la IA puede revolucionar el aprendizaje. Los estudiantes podrán asistir en el aula magna. <a href='https://example.com/conferencia'>Regístrate aquí</a>.</p>", photoUrl = "https://estaticos-cdn.prensaiberica.es/clip/11ab085f-868d-4fb6-9c7b-5eb4600ec4af_16-9-aspect-ratio_default_0.jpg"),
                            Post(id = 12, name = "Exposición de arte de los alumnos de Bachillerato", description = "<p>Los alumnos de <b>Bachillerato de Artes</b> organizarán una exposición en el instituto donde mostrarán sus obras más representativas. La exposición incluirá pinturas, esculturas y fotografías que reflejan la creatividad y el esfuerzo de los estudiantes. La inauguración será el próximo viernes a las 18:00 horas en el pasillo principal.</p>", photoUrl = "https://www.granadilladeabona.org/wp-content/uploads/2024/06/Foto-Exposicion-IES-Magallanes-1-des.jpg"),
                            Post(id = 13, name = "Visita a la Universidad para los estudiantes de 2º de Bachillerato", description = "<p>Los estudiantes de 2º de Bachillerato tienen la oportunidad de asistir a una <b>visita guiada a la universidad</b> para conocer los distintos grados que ofrece y las instalaciones del campus. Esta visita es una excelente oportunidad para aquellos interesados en continuar sus estudios en educación superior. <a href='https://example.com/universidad'>Más detalles aquí</a>.</p>", photoUrl = "https://iesaricel.org/wp-content/uploads/2015/02/IMG_9110.jpg"),
                            Post(id = 14, name = "Curso de Fotografía para los estudiantes de Ciclos Formativos", description = "<p>Los estudiantes de <b>Ciclos Formativos</b> tendrán la oportunidad de participar en un curso de fotografía profesional. Durante el curso, los estudiantes aprenderán desde el uso básico de la cámara hasta técnicas avanzadas de edición. El curso se llevará a cabo en el aula de informática, con materiales proporcionados por el instituto.</p>", photoUrl = "https://xabec.es/wp-content/uploads/2023/09/WhatsApp-Image-2023-09-11-at-10.17.47-1.jpeg"),
                            Post(id = 15, name = "¡Día de Ciencia en el Instituto!", description = "<p><b>Este viernes celebramos el Día de la Ciencia en nuestro instituto, un evento lleno de actividades educativas y demostraciones científicas.</b></p><p>Los alumnos de 4º de ESO han preparado experimentos fascinantes en el laboratorio y presentaciones sobre los avances científicos más impresionantes. Este evento es una oportunidad para fomentar el interés de los jóvenes por la ciencia y la investigación.</p><p><img src='https://www.educaciontrespuntocero.com/wp-content/uploads/2019/10/3314.jpg' alt='Día de Ciencia' style='max-width: 100%; height: auto; border-radius: 12px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);' /></p><p><i>Además, tendremos una charla especial sobre las últimas innovaciones tecnológicas y su impacto en la educación, presentada por un reconocido experto en el área.</i></p><p><b>¡No te lo pierdas!</b> Ven a disfrutar de un día lleno de aprendizaje y diversión.</p>", photoUrl = "https://www.noticiasneo.com/sites/default/files/2018-04/Ciencia.jpg"),
                            Post(id = 19, name = "Novedades del IES", description = "Un centro d\ne educación secundaria que se caracteriza por su compromiso con la formación integral de los estudiantes. Este año hemos implementado diversas nuevas iniciativas para enriquecer la experiencia educativa.\n<h3>Actividades Extraescolares</h3> Este año, los estudiantes tendrán la oportunidad de participar en diversas actividades que complementan su aprendizaje: <ul> <li><strong>Talleres de robótica</strong>: Desarrollo de proyectos tecnológicos para fomentar la creatividad.</li> <li><strong>Programas de intercambio</strong>: Colaboración con colegios de Francia y Alemania.</li> <li><strong>Actividades deportivas</strong>: Fútbol, baloncesto, y deportes acuáticos.</li> <li><strong>Clases de arte</strong>: Fomento de la creatividad a través de la pintura, escultura y fotografía.</li> </ul> <h3>Innovación Educativa</h3> Este año, hemos introducido un nuevo sistema de **aprendizaje digital**: <p>Los estudiantes podrán acceder a una plataforma en línea donde encontrarán materiales educativos, tareas y recursos adicionales para complementar sus estudios. Además, este sistema permite una comunicación directa con los profesores y compañeros de clase.</p> <h3>Calendario Escolar</h3> Aquí te dejamos el calendario con las fechas más importantes para este año escolar: <ul> <li><strong>Inicio de clases:</strong> 1 de septiembre de 2025</li> <li><strong>Vacaciones de Navidad:</strong> 20 de diciembre de 2025 - 7 de enero de 2026</li> <li><strong>Exámenes finales:</strong> 15-20 de junio de 2026</li> </ul> <h3>¡Te esperamos!</h3> <p>El IES San José sigue siendo un lugar donde el aprendizaje se combina con el desarrollo de habilidades sociales, culturales y deportivas. ¡Nos alegra tenerte con nosotros en este nuevo curso!</p>", photoUrl = "https://ies-fernandozobel.centros.castillalamancha.es/sites/ies-fernandozobel.centros.castillalamancha.es/files/imagenes/IESZOBEL-01.JPG")
                        )



                        listaPosts.addAll(predefinedPosts)
                        mAdapter.notifyDataSetChanged()

                        binding.recyclerFriends.layoutManager = GridLayoutManager(requireContext(), 2)
                        binding.recyclerFriends.adapter = mAdapter
                    }
                }
            }
        }
    }



    private fun mostrarDialogoPost(post: Post) {
        val dialogo = Dialog(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialogo_post, null)

        val tituloTextView: TextView = view.findViewById(R.id.tvPostTitle)
        val descripcionTextView: TextView = view.findViewById(R.id.tvPostDescription)
        val botonCerrar: Button = view.findViewById(R.id.btnClose)

        tituloTextView.text = post.name

        val htmlDescription = """<p>${post.description}</p>"""

        val formattedDescription = Html.fromHtml(htmlDescription, Html.FROM_HTML_MODE_COMPACT)
        descripcionTextView.text = formattedDescription

        val params = descripcionTextView.layoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        descripcionTextView.layoutParams = params

        botonCerrar.setOnClickListener {
            dialogo.dismiss()
        }

        dialogo.setContentView(view)

        dialogo.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialogo.show()
    }

    private fun obtenerClima() {
        val latitud = 39.1827
        val longitud = -0.3831
        val claveApi = ""
        val unidades = "metric"
        val idioma = "es"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.getInstance()
                    .create(ApiService::class.java)
                    .obtenerClimaActual(latitud, longitud, claveApi, unidades, idioma)

                if (response.isSuccessful) {
                    val respuestaClima = response.body()

                    respuestaClima?.let {
                        val temperatura = it.principal.temperatura
                        val temperaturaSensible = it.principal.temperaturaSensible
                        val descripcion = it.clima[0].descripcion
                        val iconoClima = it.clima[0].icono

                        //withContext(Dispatchers.Main) {
                        //    Toast.makeText(requireContext(), iconoClima, Toast.LENGTH_SHORT).show()
                        //}

                        val precipitacion1h = it.lluvia?.precipitacion1h ?: 0.0
                        val precipitacion3h = it.lluvia?.precipitacion3h ?: 0.0

                        withContext(Dispatchers.Main) {
                            if (isAdded && binding != null) {
                                binding?.textViewTemperatura?.text = "$temperatura°C"
                                binding?.textViewTemperaturaSensible?.text = "$temperaturaSensible°C"
                                binding?.textViewDescripcion?.text = descripcion.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase() else it.toString()
                                }
                                binding?.textViewPrecipitacion1h?.text = "$precipitacion1h mm"

                                val iconResourceId = resources.getIdentifier("icon_$iconoClima", "drawable", requireContext().packageName)
                                binding?.climaImatge?.setImageResource(iconResourceId)

                                if (iconResourceId == 0) {
                                    binding?.climaImatge?.setImageResource(R.mipmap.ic_error_foreground)
                                }
                            }
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), getString(R.string.error_Clima), Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), getString(R.string.error_network) + "${e.message}", Toast.LENGTH_LONG).show()
                    //println("Error de red: ${e.message}")

                    //Si no te internet n/a pa tots
                    binding?.let {
                        it.textViewTemperatura.text = "N/A"
                        it.textViewTemperaturaSensible.text = "N/A"
                        it.textViewDescripcion.text = "N/A"
                        it.textViewPrecipitacion1h.text = "N/A"
                        it.climaImatge.setImageResource(R.mipmap.ic_error_foreground)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
