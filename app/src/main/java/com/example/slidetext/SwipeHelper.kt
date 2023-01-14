package com.example.slidetext

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.util.Half.toFloat
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.util.LinkedList


abstract class SwipeHelper (context : Context, private val recyclerView: RecyclerView,internal var buttonWidth : Int,) :
    ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {

    private var buttonList : MutableList<MyButton>?= null
    lateinit var gestureDetector : GestureDetector
    var swipPosition = -1
    var swipThreshold = 0.5f
    val buttonBuffer : MutableMap<Int,MutableList<MyButton>>
    lateinit var removeQueue : LinkedList<Int>

    abstract fun instantiateMyButton(viewHolder: RecyclerView.ViewHolder,
    buffer :MutableList<MyButton>)

    private val gestureListener = object :GestureDetector.SimpleOnGestureListener(){
        override fun onSingleTapUp(e: MotionEvent?): Boolean {

            for (button in buttonList!!)
                if (button.onClick(e!!.x,e!!.y))
                    break
            return true
        }
    }

    private val onTouchListener =View.OnTouchListener{_,motionEvent ->
        if (swipPosition< 0) return@OnTouchListener false
        val point = Point (motionEvent.rawX.toInt(),motionEvent.rawY.toInt())
        val swipeViewHolder = recyclerView.findViewHolderForAdapterPosition(swipPosition)
        val swipedItem = swipeViewHolder!!.itemView
        val rect = Rect()
        swipedItem.getGlobalVisibleRect(rect)

        if (motionEvent.action == MotionEvent.ACTION_DOWN ||
            motionEvent.action == MotionEvent.ACTION_MOVE ||
                motionEvent.action == MotionEvent.ACTION_UP){
            if (rect.top < point.y && rect.bottom > point.y){
                gestureDetector.onTouchEvent(motionEvent)
            }else{
                removeQueue.add(swipPosition)
                swipPosition = -1
                recoverSwipOption()
            }
        }
        false
    }


    @Synchronized
    private fun recoverSwipOption(){
        while (!removeQueue.isEmpty()) {
            val pos = removeQueue.poll()!!.toInt()

            if (pos > -1){
                recyclerView.adapter!!.notifyItemChanged(pos)
            }
        }
    }

    init {
        this.buttonList = ArrayList()
        this.gestureDetector = GestureDetector(context,gestureListener)
        this.recyclerView.setOnTouchListener(onTouchListener)
        this.buttonBuffer = HashMap()
        this.removeQueue = IntLinkList()

        attachedSwipe()
    }

    private fun attachedSwipe() {
        val itemTouchHelper =ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    class IntLinkList : LinkedList<Int>() {
        override fun contains(element: Int): Boolean {
            return false
        }

        override fun lastIndexOf(element: Int): Int {
            return element
        }

        override fun remove(element: Int): Boolean {
            return false
        }

        override fun indexOf(element: Int): Int {
            return element
        }

        override fun add(element: Int): Boolean {
            return if (contains(element))
                false
            else super.add(element)
        }

    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition
        if (swipPosition != pos)
            removeQueue.add(swipPosition)
            swipPosition = pos

        if (buttonBuffer.containsKey(swipPosition))
            buttonList =buttonBuffer[swipPosition]
        else
            buttonList!!.clear()
        buttonBuffer.clear()
       swipThreshold = 0.5f*buttonList!!.size.toFloat()*buttonWidth.toFloat()
        recoverSwipOption()
    }

    override fun getSwipeThreshold(viewHolder: ViewHolder): Float {
        return swipThreshold
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return 0.5f * defaultValue
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return 0.5f * defaultValue
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val pos = viewHolder.adapterPosition
        var transationX = dX
        var itemView = viewHolder.itemView
        if (pos < 0){
            swipPosition = pos
            return
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            if (dX < 0){
                var buffer :MutableList<MyButton> = ArrayList()
                if (!buttonBuffer.containsKey(pos)) {
                    instantiateMyButton(viewHolder, buffer)
                    buttonBuffer[pos] = buffer
                }
                else{
                    buffer = buttonBuffer[pos]!!
                }
                transationX = dX * buffer.size.toFloat() * buttonWidth.toFloat() / itemView.width
                drawButton(c,itemView,buffer,pos,transationX)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, transationX, dY, actionState, isCurrentlyActive)

    }

    private fun drawButton(c: Canvas, itemView: View, buffer: MutableList<MyButton>, pos: Int, transationX: Float) {
        var right=itemView.right.toFloat ()

        val dButtonWidth = -1*transationX/buffer.size

                for (button in buffer){
                    val left = right- dButtonWidth
                    button.onDraw (c,RectF(left,itemView.top.toFloat(),right,itemView.bottom.toFloat()),pos)
                    right=left

                }
      }
}