package es.efb.isvf_studentapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import es.efb.isvf_studentapp.R
import es.efb.isvf_studentapp.retrofit.ApiService
import es.efb.isvf_studentapp.retrofit.RetrofitObject
import es.efb.isvf_studentapp.retrofit.Post
import es.efb.isvf_studentapp.databinding.FragmentAddPostBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddPostFragment : Fragment() {

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?

    ): View? {

        _binding = FragmentAddPostBinding.inflate(inflater, container, false)
        apiService = RetrofitObject.getInstance().create(ApiService::class.java)

        binding.btnAddPost.setOnClickListener {
            val title = binding.etPostTitle.text.toString()
            val description = binding.etPostDescription.text.toString()
            val url = binding.etPostImatgeURL.text.toString()

            if (title.isNotEmpty() && description.isNotEmpty() && url.isNotEmpty()) {
                addPost(title, description, url)
            }
        }

        return binding.root
    }

    private fun addPost(name: String, description: String, url: String) {
        lifecycleScope.launch {
            try {
                val newPost = Post(
                    id = 0,
                    name = name,
                    description = description,
                    photoUrl = url
                )

                val response = apiService.createPost(newPost)

                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), getString(R.string.Uploaded), Toast.LENGTH_SHORT).show()
                        requireActivity().supportFragmentManager.popBackStack()

                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), getString(R.string.Failed) + "${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), getString(R.string.Error) + "${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
