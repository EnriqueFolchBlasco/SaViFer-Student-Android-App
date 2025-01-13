package es.efb.android.adapters

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import es.efb.isvf_studentapp.R
import es.efb.isvf_studentapp.retrofit.Post

class PostAdapter(
    private val posts: MutableList<Post>,
    private val context: Context,
    private val mListener: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = posts[position]
        holder.bindItem(item, context)
        holder.itemView.setOnClickListener { mListener(item) }
    }

    override fun getItemCount(): Int = posts.size

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val postName: TextView = view.findViewById(R.id.tvPostName)
        private val postDescription: TextView = view.findViewById(R.id.tvPostDesc)
        private val postImage: ImageView = view.findViewById(R.id.ivPostImage)

        fun bindItem(post: Post, context: Context) {
            postName.text = post.name

            val htmlDescription = "<p>${post.description}</p>"
            postDescription.text = Html.fromHtml(htmlDescription, Html.FROM_HTML_MODE_COMPACT)

            Picasso.get()
                .load(post.photoUrl)
                .placeholder(R.mipmap.ic_placeholder_foreground)
                .error(R.mipmap.ic_placeholder_foreground)
                .fit()
                .centerCrop()
                .into(postImage)
        }
    }
}