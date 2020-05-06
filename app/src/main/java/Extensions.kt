import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.os.CountDownTimer
import android.view.View

fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
fun Float.dpToPx(): Float = (this * Resources.getSystem().displayMetrics.density)
fun String.asColor(): Int = Color.parseColor(this)
fun View.setVisible(visible: Boolean) = when {
    visible -> {
        this.visibility = View.VISIBLE
    }
    else -> {
        this.visibility = View.GONE
    }
}
fun Activity.delay(ms: Long, block: () -> Unit){
    object : CountDownTimer(ms, ms) {
        override fun onFinish() {
            block.invoke()
        }

        override fun onTick(millisUntilFinished: Long) {}
    }.start()
}
