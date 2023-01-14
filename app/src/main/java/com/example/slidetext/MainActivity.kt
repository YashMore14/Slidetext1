package com.example.slidetext

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(),MyButtonOnClickListener {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerViewmain = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerViewmain.layoutManager = LinearLayoutManager(this)

        val data = ArrayList<ItemsViewModel>()

        for (i in 1..10){
            data.add(ItemsViewModel(R.drawable.ic_launcher_foreground,"item  " +i))
        }
        val adapter = MainAdapter(data)
        recyclerViewmain.adapter=adapter

        val swipe =object  : SwipeHelper(this,recyclerViewmain,200)
        {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(MyButton(this@MainActivity,
                "Delete",
                    30,
                    R.drawable.ic_delete_circle,
                    Color.parseColor("#FFBB86FC"),
                object :MyButtonOnClickListener{
                    override fun onClick(pos: Int) {

                        Toast.makeText(this@MainActivity, "Delete $pos", Toast.LENGTH_SHORT).show()
                    }

                }))

                buffer.add(MyButton(this@MainActivity,
                    "Update",
                    30,
                    R.drawable.ic_message_un_read,
                    Color.parseColor("#FF03DAC5"),
                    object :MyButtonOnClickListener{
                        override fun onClick(pos: Int) {

                            Toast.makeText(this@MainActivity, "Update $pos", Toast.LENGTH_SHORT).show()

                        }

                    }))
            }
        }
    }

    override fun onClick(pos: Int) {
        TODO("Not yet implemented")
    }
}