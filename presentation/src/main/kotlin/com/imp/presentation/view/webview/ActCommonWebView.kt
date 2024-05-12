package com.imp.presentation.view.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.JavascriptInterface
import android.webkit.JsResult
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.imp.presentation.R
import com.imp.presentation.base.BaseContractActivity
import com.imp.presentation.databinding.ActCommonWebViewBinding
import com.imp.presentation.view.main.activity.ActVideoChat
import com.imp.presentation.widget.extension.toVisibleOrGone
import dagger.hilt.android.AndroidEntryPoint

/**
 * Common WebView Activity
 */
@AndroidEntryPoint
class ActCommonWebView : BaseContractActivity<ActCommonWebViewBinding>() {

    /** Show Header 여부 */
    private var showHeader: Boolean = false

    /** Html 여부 */
    private var isHtml: Boolean = false

    /** Header Title (showHeader 값이 true일 경우) */
    private var headerTitle: String = ""

    /** WebView Url */
    private var webViewUrl: String = ""

    /** Chat 여부 */
    private var isChat: Boolean = false

    override fun getViewBinding() = ActCommonWebViewBinding.inflate(layoutInflater)

    override fun initData() {

        showHeader = intent.getBooleanExtra("header", false)
        isHtml = intent.getBooleanExtra("html", false)
        headerTitle = intent.getStringExtra("header_title") ?: ""
        webViewUrl = intent.getStringExtra("url") ?: ""
        isChat = intent.getBooleanExtra("chat", false)
    }

    override fun initView() {

        initDisplay()
        setOnClickListener()
    }

    /**
     * Initialize Display
     */
    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun initDisplay() {

        with(mBinding) {

            incHeader.apply {

                ctHeader.visibility = showHeader.toVisibleOrGone()
                ivVideoChat.visibility = isChat.toVisibleOrGone()
                tvTitle.text = headerTitle
            }

            webView.apply {

                isHorizontalScrollBarEnabled = false
                isVerticalScrollBarEnabled = false
                settings.javaScriptEnabled = true
                settings.userAgentString = StringBuffer(settings.userAgentString).append(";android").toString()

                webViewClient = WebViewClient()
                webChromeClient = object : WebChromeClient() {

                    override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                        return true
                    }

                    override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                        return true
                    }

                    override fun onPermissionRequest(request: PermissionRequest?) {
                        super.onPermissionRequest(request)
                    }

                    override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
                        return true
                    }
                }

                // Javascript Interface
                this.addJavascriptInterface(AndroidBridgeVoid { type, json -> }, getString(R.string.app_name))

                if (isHtml) {
                    loadData(webViewUrl, "text/html; charset=utf-8", "UTF-8")
                } else {
                    loadUrl(webViewUrl)
                }
            }
        }
    }

    /**
     * Set OnClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

            // 뒤로 가기
            incHeader.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

            // 화상 채팅
            incHeader.ivVideoChat.setOnClickListener { moveToVideoChat() }
        }
    }

    /**
     * Move to Video Chat
     */
    private fun moveToVideoChat() {

        Intent(this@ActCommonWebView, ActVideoChat::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("title", headerTitle)
            startActivity(this)
            finish()
        }
    }

    /**
     * Interface
     */
    private class AndroidBridgeVoid(var function: (type: String, json: String) -> Unit) {

        // 완료
        @JavascriptInterface
        fun sample() {}
    }
}