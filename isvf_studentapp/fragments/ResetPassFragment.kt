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
import es.efb.isvf_studentapp.databinding.FragmentResetPassBinding
import es.efb.isvf_studentapp.managers.AuthManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResetPassFragment : Fragment(R.layout.fragment_reset_pass) {

    interface ResetPassFragmentListener {
        fun onPasswordReset(email: String)
    }

    private var listener: ResetPassFragmentListener? = null
    private var _binding: FragmentResetPassBinding? = null
    private val binding get() = _binding!!
    private val authManager : AuthManager by lazy { AuthManager() }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ResetPassFragmentListener) {
            listener = context
        } else {
            throw ClassCastException("$context must implement ResetPassFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnResetPassword.setOnClickListener {
            val email = binding.editTextEmail.text.toString()

            if (!email.isNullOrBlank()){
                lifecycleScope.launch ( Dispatchers.IO ){

                    val userPassResetted = authManager.resetPassword(requireContext(),email)

                    withContext(Dispatchers.Main){
                        if (userPassResetted == true){
                            Toast.makeText(requireContext(), "$email" + getString(R.string.recovery_sent_text), Toast.LENGTH_SHORT).show()
                            listener?.onPasswordReset(email)
                            requireActivity().supportFragmentManager.popBackStack()
                        }else{
                            Toast.makeText(requireContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                        }
                        //"$email has been sent the reset password email."
                    }

                }
            }

            //listener?.onPasswordReset(email)
            //requireActivity().supportFragmentManager.popBackStack()
        }

        binding.tvBackToLogin.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResetPassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
