package com.example.taskwhatsappstatussaver

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.taskwhatsappstatussaver.databinding.ViewBinding


class StatusAdapter(private val uriStringList: ArrayList<String>, private val activity: Activity) :
    RecyclerView.Adapter<StatusAdapter.ViewHolder>() {

    interface ItemClickListener {
        fun onDownloadClick(uri: String)
        fun onItemClick(uri: String)
    }

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(listener: ItemClickListener) {
        this.itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        /*val view = LayoutInflater.from(parent.context).inflate(R.layout.view, parent, false)
        return ViewHolder(view)*/
        var bindingView = ViewBinding.inflate(LayoutInflater.from(activity),parent,false)
        return ViewHolder(bindingView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uri = Uri.parse(uriStringList[position])
        if (uriStringList[position].endsWith(".mp4")) {
            // It's a video, set video thumbnail or placeholder image
            // For example, you can set a placeholder image for videos
            holder.playIcon.visibility = View.VISIBLE
            Glide.with(activity)
                .load(uriStringList[position]) // Placeholder image resource ID for videos
                .into(holder.imageView)
        } else {
            // It's an image, load it using Glide
            holder.playIcon.visibility = View.GONE
            Glide.with(activity)
                .load(uri)
                .into(holder.imageView)
        }

        holder.downloadButton.setOnClickListener {
            itemClickListener.onDownloadClick(uriStringList[position])
        }

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(uriStringList[position])
        }
    }

    override fun getItemCount(): Int {
        return uriStringList.size
    }

    class ViewHolder(var binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

        val imageView: ImageView = binding.imageView2
        val downloadButton: AppCompatButton = binding.downloadButton
        val playIcon: ImageView = binding.playIcon
    }
}
