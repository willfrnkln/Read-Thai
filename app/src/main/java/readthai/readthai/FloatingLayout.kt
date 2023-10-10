package readthai.readthai

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.LinearLayout

class FloatingLayout: LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var listener: ((activate: Boolean) -> Unit)? = null
    fun setListener(listener: (activate: Boolean) -> Unit) {
        this.listener = listener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_OUTSIDE) {
            listener?.invoke(false)
        }
        else{
            listener?.invoke(true)
        }
        return super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean  {
        if (event.action == MotionEvent.ACTION_OUTSIDE) {
            listener?.invoke(false)
        }
        else{
            listener?.invoke(true)
        }
        return super.onInterceptTouchEvent(event)
    }
}