package com.imp.presentation.widget.extension
import android.graphics.Rect
import android.view.View
import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import java.util.Locale
import kotlin.math.abs

/**
 * ViewPager ITem Decoration
 *
 * @param horizontalMarginInDp
 */
class ViewPagerItemDecoration(private val horizontalMarginInDp: Float) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        outRect.right = horizontalMarginInDp.toInt()
        outRect.left = horizontalMarginInDp.toInt()
    }
}

/**
 * Set Preview both side
 *
 * @param nextItemVisibleSize
 * @param currentItemHorizontalMargin
 * @param size
 */
fun ViewPager2.setPreviewBothSide(nextItemVisibleSize: Float, currentItemHorizontalMargin: Float, size: Int) {

    val isRtl = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL

    this.offscreenPageLimit = size
    this.clipChildren = false
    this.clipToPadding = false
    this.isUserInputEnabled = true

    this.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

    val pageTranslationX = nextItemVisibleSize + currentItemHorizontalMargin
    val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->

        page.apply {

            translationX = if (isRtl) pageTranslationX * position else -pageTranslationX * position
            this.alpha = abs(alpha)
        }
    }

    // 초기화
    this.setPageTransformer(null)
    while (itemDecorationCount > 0) removeItemDecorationAt(0)

    this.setPageTransformer(pageTransformer)

    val itemDecoration = ViewPagerItemDecoration(currentItemHorizontalMargin)
    this.addItemDecoration(itemDecoration)
}