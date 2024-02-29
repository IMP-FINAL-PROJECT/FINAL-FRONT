package com.imp.presentation.widget.extension

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import java.util.Locale
import kotlin.math.abs

/**
 * ViewPager Item Decoration
 */
class ViewPagerItemDecoration(context: Context, @DimenRes horizontalMarginInDp: Int) : RecyclerView.ItemDecoration() {

    private var horizontalMarginInPx: Int = context.resources.getDimension(horizontalMarginInDp).toInt()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        outRect.right = horizontalMarginInPx
        outRect.left = horizontalMarginInPx
    }
}

fun ViewPager2.setPreviewBothSide(
    @DimenRes nextItemVisibleSize: Int,
    @DimenRes currentItemHorizontalMargin: Int,
    size: Int,
    showScaleAnim: Boolean = false
) {

    val isRtl = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL

    this.offscreenPageLimit = size
    this.clipChildren = false
    this.clipToPadding = false
    this.isUserInputEnabled = true

    // ViewPager2 스크롤 색상 없애기
    this.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

    val nextItemVisiblePx = resources.getDimension(nextItemVisibleSize)
    val currentItemHorizontalMarginPx = resources.getDimension(currentItemHorizontalMargin)
    val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx

    val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->

        var scaleFactor = 1f.coerceAtLeast(1 - abs(position))
        var alpha = if (position == 0f) 1f else 1f

        // scale animation 추가
        if (showScaleAnim) {

            scaleFactor = 0.9f.coerceAtLeast(1 - abs(position))
            alpha = if (position < 0) (1 - 0.8f) * position + 1 else (0.8f - 1) * position + 1
        }

        page.apply {

            translationX = if (isRtl) pageTranslationX * position else -pageTranslationX * position
            scaleX = scaleFactor
            scaleY = scaleFactor
            this.alpha = abs(alpha)
        }
    }

    // 기존의 PageTransformer 초기화
    this.setPageTransformer(null)

    // 기존의 아이템 데코레이션 초기화
    while (itemDecorationCount > 0) {
        removeItemDecorationAt(0)
    }

    this.setPageTransformer(pageTransformer)

    val itemDecoration = ViewPagerItemDecoration(context, currentItemHorizontalMargin)
    this.addItemDecoration(itemDecoration)
}