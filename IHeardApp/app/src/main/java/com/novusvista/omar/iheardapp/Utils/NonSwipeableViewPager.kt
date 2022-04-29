package com.novusvista.omar.iheardapp.Utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager


class NonSwipeableViewPager: ViewPager {
    constructor(context: Context):super(context)
    {
        setMyScroller()
    }
    constructor(context: Context,attribruteSet:AttributeSet):super(context,attribruteSet)
    {
        setMyScroller()
    }


    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }


    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    private fun setMyScroller() {


        try {
            val viewPagaer = ViewPager::class.java
            val scroller=viewPagaer.getDeclaredField("mScroller")
            scroller.isAccessible=true
            scroller.set(this,MyScroller(context))


        }catch  (e:Exception)
        {
            e.printStackTrace()
        }
    }
}

class MyScroller(context: Context): Scroller(context,DecelerateInterpolator()) {
    //ctrl+0
    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
        super.startScroll(startX, startY, dx, dy,400)
    }


}
