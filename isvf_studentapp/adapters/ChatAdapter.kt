package es.igs.android.adapters

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import es.efb.isvf_studentapp.R
import es.efb.isvf_studentapp.utils.PREFERENCES_FILENAME
import es.igs.android.classes.ChatMessage

class ChatAdapter(
    private val messages: List<ChatMessage>,
    private val mContext: Context,
    private val currentUserUID: String
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.rv_chat_item, parent, false)
        return ChatViewHolder(view, mContext, currentUserUID)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = messages[position]
        holder.bindItem(item)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class ChatViewHolder(
        view: View,
        private val mContext: Context,
        private val currentUserUID: String
    ) : RecyclerView.ViewHolder(view) {

        private val tvUsername: TextView = view.findViewById(R.id.tvChatUser)
        private val tvMessage: TextView = view.findViewById(R.id.tvChatMessage)
        private val cvMessage: CardView = view.findViewById(R.id.cvChatMessage)
        private val tvUID: TextView = view.findViewById(R.id.tvChatUserId)

        fun bindItem(item: ChatMessage) {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            val sharedPreferences = mContext.getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE)
            val storedColor = sharedPreferences.getString(
                es.efb.isvf_studentapp.utils.PREFERENCES_CHAT_COLOR,
                null
            )

            var cvColor = mContext.getColor(R.color.green)
            storedColor?.let {
                try {
                    val parsedColor = Color.parseColor(it)
                    cvColor = parsedColor
                } catch (e: IllegalArgumentException) {
                    cvColor = mContext.getColor(R.color.green)
                }
            }

            if (item.userUID != currentUserUID) {
                params.apply {
                    weight = 1.0f
                    gravity = Gravity.RIGHT
                }
                cvColor = mContext.getColor(R.color.blue)
            } else {
                params.apply {
                    weight = 1.0f
                    gravity = Gravity.LEFT
                }
            }

            tvUsername.layoutParams = params
            tvUID.layoutParams = params
            cvMessage.layoutParams = params
            cvMessage.setCardBackgroundColor(cvColor)

            tvUsername.text = item.username
            tvMessage.text = item.message
            tvUID.text = item.userUID
        }
    }
}
