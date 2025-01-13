package es.efb.isvf_studentapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import es.efb.isvf_studentapp.R
import es.efb.isvf_studentapp.databinding.FragmentRegisterBinding
import es.efb.isvf_studentapp.managers.AuthManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment(R.layout.fragment_register) {

    interface RegisterFragmentListener {
        fun onRegisterCompleted()
    }

    private var listener: RegisterFragmentListener? = null
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val authManager : AuthManager by lazy { AuthManager() }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is RegisterFragmentListener) {
            listener = context
        } else {
            throw ClassCastException("$context must implement RegisterFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvAlreadyHaveAccount.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnRegister.setOnClickListener {

            val email = binding.editTextEmail.text.toString()
            //TODO Save user in credentials or whateeeever
            val pass = binding.editTextPassword.text.toString()
            val pass2 = binding.editTextConfirmPassword.text.toString()

            if (pass.equals(pass2)){

                if (!email.isNullOrBlank() && !pass.isNullOrBlank() && !pass2.isNullOrBlank()){
                    lifecycleScope.launch ( Dispatchers.IO ){

                        val userRegistered = authManager.register(requireContext(),email,pass)

                        withContext(Dispatchers.Main){
                            if (userRegistered != null){
                                Toast.makeText(requireContext(),userRegistered.email, Toast.LENGTH_SHORT).show()

                                listener?.onRegisterCompleted()
                                requireActivity().supportFragmentManager.popBackStack()

                            }else{

                                Toast.makeText(requireContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                            }

                        }

                    }
                }
            } else {
                Toast.makeText(requireContext(),getString(R.string.password_not_match), Toast.LENGTH_SHORT).show()
            }



            //listener?.onRegisterCompleted()
            //requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
