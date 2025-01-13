package es.efb.isvf_studentapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import es.efb.isvf_studentapp.databinding.FragmentChatBinding
import es.efb.isvf_studentapp.managers.FirestoreManager
import es.efb.isvf_studentapp.utils.PREFERENCES_FILENAME
import es.efb.isvf_studentapp.utils.PREFERENCES_USERNAME_KEY
import es.efb.isvf_studentapp.utils.PREFERENCES_USER_UID
import es.igs.android.adapters.ChatAdapter
import es.igs.android.classes.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val messages = mutableListOf<ChatMessage>()
    private lateinit var mAdapter: ChatAdapter

    private val firestoreManager: FirestoreManager by lazy { FirestoreManager() }

    private lateinit var username: String
    private lateinit var userUID: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        binding.btnSendMessage.setOnClickListener {
            val message = binding.etMessage.text.toString()
            if (message.isNotBlank()) {
                sendMessage(message)
            } else {
                Toast.makeText(requireContext(), "You must type a message", Toast.LENGTH_SHORT).show()
            }
        }

        loadUsernameAndUID()
        setUpRecycler()

        return binding.root
    }

    private fun loadUsernameAndUID() {
        val prefs = requireContext().getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE)
        username = prefs.getString(PREFERENCES_USERNAME_KEY, "").toString()

        //l odel auid
        userUID = prefs.getString(PREFERENCES_USER_UID, "").toString()
    }

    private fun sendMessage(message: String) {

        val newMessage = ChatMessage(username, message, userUID)

        lifecycleScope.launch(Dispatchers.IO) {
            val result = firestoreManager.addMessage(newMessage)
            if (result) {
                withContext(Dispatchers.Main) {
                    binding.etMessage.setText("")
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error adding the message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setUpRecycler() {

        binding.rvChat.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        mAdapter = ChatAdapter(messages, requireContext(), userUID)
        binding.rvChat.adapter = mAdapter

        getMessages()
    }

    private fun getMessages() {
        lifecycleScope.launch(Dispatchers.IO) {
            firestoreManager.getMessagesFlow()
                .collect { newMessages ->
                    messages.clear()
                    messages.addAll(newMessages)
                    withContext(Dispatchers.Main) {
                        mAdapter.notifyDataSetChanged()
                    }
                }
        }
    }
}
