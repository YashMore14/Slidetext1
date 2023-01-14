package com.example.slidetext

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MainAdapter(private val mList: List<ItemsViewModel>) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    class ViewHolder (ItemView : View) : RecyclerView.ViewHolder(ItemView){

        val imageView:ImageView=itemView.findViewById(R.id.image)
        val textView :TextView = itemView.findViewById(R.id.item_text)
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        holder.imageView.setImageResource(ItemsViewModel.image)
        holder.textView.text = ItemsViewModel.text
    }

    override fun getItemCount(): Int {
    return mList.size
    }

}