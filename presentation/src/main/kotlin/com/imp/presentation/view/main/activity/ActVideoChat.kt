package com.imp.presentation.view.main.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.gorisse.thomas.sceneform.scene.await
import com.imp.presentation.R
import com.imp.presentation.base.BaseContractActivity
import com.imp.presentation.databinding.ActVideoChatBinding
import com.imp.presentation.viewmodel.ChatViewModel
import com.imp.presentation.widget.extension.toGoneOrVisible
import com.imp.presentation.widget.extension.toVisibleOrGone
import com.imp.presentation.widget.utils.MethodStorageUtil
import com.imp.presentation.widget.utils.PermissionUtil
import com.imp.presentation.widget.utils.PreferencesUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


/**
 * Chat - Video Chatting Activity
 */
@AndroidEntryPoint
class ActVideoChat : BaseContractActivity<ActVideoChatBinding>() {

    companion object {

        private const val TTS_ID = "VIDEO_TTS_ID"
    }

    /** 권한 요청 */
    private val permissionActivityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { selectionPermission() }

    /** 권한 거부 시 런처 */
    private val permissionDeniedActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { selectionPermission() }

    /**
     * Show Permission Denied Popup
     */
    private val permissionDeniedPopup: (() -> Unit) -> Unit = { deniedCallback ->

        showCommonPopup(
            titleText = getString(R.string.popup_text_1),
            leftText = getString(R.string.cancel),
            leftCallback = { onBackPressedDispatcher.onBackPressed() },
            rightText = getString(R.string.permission_text_11),
            rightCallback = { deniedCallback.invoke() },
            cancelable = false
        )
    }

    private fun selectionPermission() {

        // 카메라 권한 요청
//        if (!PermissionUtil.checkPermissionCamera(this)) {
//            PermissionUtil.requestPermissionCamera(this, permissionActivityResultLauncher, permissionDeniedActivityResultLauncher, true, permissionDeniedPopup)
//            return
//        }

        // 오디오 권한 요청
        if (!PermissionUtil.checkPermissionAudio(this)) {
            PermissionUtil.requestPermissionAudio(this, permissionActivityResultLauncher, permissionDeniedActivityResultLauncher, true, permissionDeniedPopup)
            return
        }

        // recognizer speech 초기화
        initRecognizeSpeech()

        // text to speech 초기화
        initTextToSpeech()
    }

    /** Chat ViewModel */
    private val viewModel: ChatViewModel by viewModels()

    /** AR 관련 변수 */
    private var arFragment: ArFragment? = null
    private var model: Renderable? = null
    private var modelView: ViewRenderable? = null
    private var node: Node? = null

    /** Chatting 정보 관련 변수 */
    private var title: String = ""
    private var number: String = ""

    /** STT Animator */
    private var sttShowAnimator: AnimatorSet? = null
    private var sttEndAnimator: AnimatorSet? = null

    /** TTS 관련 변수 */
    private var textToSpeech: TextToSpeech? = null
    private var voiceList: ArrayList<Voice> = ArrayList()

    /** STT 관련 변수 */
    private var recognizerIntent: Intent? = null
    private var speechRecognizer: SpeechRecognizer? = null

    /** Recognizer Listener */
    private val recognizerListener = object : RecognitionListener {

        override fun onReadyForSpeech(p0: Bundle?) {
            // 음성 인식 준비 완료
            controlSpeechLottie(true)
            controlSttView(false)
        }

        override fun onBeginningOfSpeech() {
            // 음성 인식 시작
        }

        override fun onRmsChanged(p0: Float) {
            // 입력 받은 음성 크기
        }

        override fun onBufferReceived(p0: ByteArray?) {
            // 인식된 단어 buffer
        }

        override fun onEndOfSpeech() {
            // 음성 인식 종료
        }

        override fun onError(p0: Int) {
            // 음성 인식 오류
            Toast.makeText(this@ActVideoChat, getString(R.string.popup_text_6), Toast.LENGTH_SHORT).show()
            controlSpeechLottie(false)
            controlSttView(false)
        }

        override fun onResults(p0: Bundle?) {

            var result = ""
            val matches = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: return
            for (i in matches.indices) result = matches[i]

            mBinding.tvStt.text = "$result?"

            controlSpeechLottie(false)
            controlLoadingLottie(true)
            controlSttView(true)
            sendChat(result)
        }

        override fun onPartialResults(p0: Bundle?) {
            // 부분 인식 결과
        }

        override fun onEvent(p0: Int, p1: Bundle?) {
            // 음성 인식 완료 이벤트
        }
    }

    override fun getViewBinding() = ActVideoChatBinding.inflate(layoutInflater)

    override fun initData() {

        fullScreen = true

        title = intent?.getStringExtra("title") ?: ""
        number = intent?.getStringExtra("number") ?: ""

        selectionPermission()
    }

    override fun initView() {

        initObserver()
        initDisplay()
        initSceneForm()
        setOnClickListener()
    }

    override fun onDestroy() {

        sttShowAnimator?.cancel()
        sttShowAnimator = null

        sttEndAnimator?.cancel()
        sttEndAnimator = null

        speechRecognizer?.destroy()
        speechRecognizer = null
        recognizerIntent = null

        arFragment?.destroy()
        arFragment = null

        model = null
        modelView = null
        node = null

        stopTextToSpeech()
        super.onDestroy()
    }

    /**
     * Initialize Recognize Speech
     */
    private fun initRecognizeSpeech() {

        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        }
    }

    /**
     * Initialize Text to Speech
     */
    private fun initTextToSpeech() {

        textToSpeech = TextToSpeech(this@ActVideoChat) { status ->

            if (status != TextToSpeech.ERROR) {

                textToSpeech?.let { tts ->

                    tts.setLanguage(Locale.KOREAN)

                    // ko-KR-SMTf00 (일반 여성 아나운서 느낌), ko-KR-SMTl08 (소심한 여성)
                    // ko-KR-SMTl01 (일반 여성), ko-KR-SMTl04 (일반 여성 전자녀 느낌), ko-KR-SMTl05 (일반 여성 좀더 높나?..)
                    // ko-KR-default (일반 남성 아나운서 느낌), ko-KR-SMTg01 (일반 남성), ko-KR-SMTm01 (일반 남성 전자녀 남자버전?)
                    val voices = tts.voices.filter { it.name.contains("ko-KR-SMTg01", ignoreCase = true) || it.name.contains("ko-KR-SMTl01", ignoreCase = true) }
                    voices.firstOrNull()?.let { tts.setVoice(it) }

                    voiceList.addAll(voices)
                }
            }
        }
    }

    /**
     * Stop Text to Speech
     */
    private fun stopTextToSpeech() {

        if(textToSpeech != null){

            textToSpeech?.stop()
            textToSpeech?.shutdown()
            textToSpeech = null
        }
    }

    /**
     * Initialize Observer
     */
    private fun initObserver() {

        /** Chat List */
        viewModel.chatResponse.observe(this) { chat ->

            controlLoadingLottie(false)
            textToSpeech?.speak(chat.response, TextToSpeech.QUEUE_FLUSH, null, TTS_ID)
        }

        /** Error Callback */
        viewModel.errorCallback.observe(this) { event ->
            event.getContentIfNotHandled()?.let { error ->

                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            // title
            tvTitle.text = title

            // description
            tvDescription.text = getString(R.string.chat_text_2)

            // loading lottie
            lottieLoading.visibility = View.GONE

            // header top margin
            val layoutParams = ctHeader.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topMargin = MethodStorageUtil.getStatusBarHeight(this@ActVideoChat)
            ctHeader.layoutParams = layoutParams

            // stt 숨김 처리
            controlSttView(false)

            // speech 버튼 숨김 처리
            ctSpeech.visibility = View.GONE

            // voice 버튼 숨김 처리
            cvVoice.visibility = View.GONE
        }
    }

    /**
     * Set onClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

            // 뒤로 가기
            ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

            // 눌러서 말하기
            ctSpeech.setOnClickListener {

                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@ActVideoChat)
                speechRecognizer?.setRecognitionListener(recognizerListener)
                speechRecognizer?.startListening(recognizerIntent)
            }

            // 남성 목소리 선택
            tvMale.setOnClickListener {

                textToSpeech?.let { tts ->

                    val voice = voiceList.filter { it.name.contains("ko-KR-SMTg01", ignoreCase = true) }
                    voice.firstOrNull()?.let { tts.setVoice(it) }
                }

                // 버튼 선택 초기화
                controlVoiceButton(true)
            }

            // 여성 목소리 선택
            tvFemale.setOnClickListener {

                textToSpeech?.let { tts ->

                    val voice = voiceList.filter { it.name.contains("ko-KR-SMTl01", ignoreCase = true) }
                    voice.firstOrNull()?.let { tts.setVoice(it) }
                }

                // 버튼 선택 초기화
                controlVoiceButton(false)
            }
        }
    }

    /**
     * Initialize SceneForm
     */
    private fun initSceneForm() {

        arFragment = (supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment).apply {
            setOnSessionConfigurationListener { session, config ->
                // Modify the AR session configuration here
            }
            setOnViewCreatedListener { arSceneView ->
                arSceneView.setFrameRateFactor(SceneView.FrameRate.FULL)
            }
            setOnTapArPlaneListener(::setOnTapPlane)
        }

        lifecycleScope.launchWhenCreated {
            loadModel()
        }
    }

    /**
     * Load Model
     */
    private suspend fun loadModel() {

        model = ModelRenderable.builder()
            .setSource(this@ActVideoChat, Uri.parse("models/model.glb"))
            .setIsFilamentGltf(true)
            .await()
    }

    /**
     * Set OnTapPlane
     */
    private fun setOnTapPlane(hitResult: HitResult, plane: Plane, motionEvent: MotionEvent) {

        controlLoadingLottie(true)

        if (model == null) return

        arFragment?.apply {

            if (node != null) {
                arSceneView?.scene?.removeChild(node)
            }

            node = AnchorNode(hitResult.createAnchor()).apply {

                addChild(TransformableNode(transformationSystem).apply {

                    renderable = model
                    scaleController.maxScale = 0.2f
                    scaleController.minScale = 0.1f
                    renderableInstance.animate(true).start()

//                    addChild(Node().apply {
//                        localPosition = Vector3(0.0f, 1f, 0.0f)
//                        localScale = Vector3(0.7f, 0.7f, 0.7f)
//                        renderable = modelView
//                    })
                })
            }
            arSceneView.scene.addChild(node)

            controlLoadingLottie(false)

            // 노출 여부 설정
            mBinding.tvDescription.visibility = View.GONE
            mBinding.ctSpeech.visibility = View.VISIBLE
            mBinding.cvVoice.visibility = View.VISIBLE
        }
    }

    /**
     * Control Loading Lottie
     *
     * @param start true -> start / false -> stop
     */
    private fun controlLoadingLottie(start: Boolean) {

        with(mBinding.lottieLoading) {

            visibility = start.toVisibleOrGone()
            if (start) { playAnimation() } else { pauseAnimation() }
        }
    }

    /**
     * Control Speech Lottie
     *
     * @param start true -> start / false -> stop
     */
    private fun controlSpeechLottie(start: Boolean) {

        with(mBinding) {

            ivMic.visibility = start.toGoneOrVisible()

            lottieSpeech.apply {

                visibility = start.toVisibleOrGone()
                if (start) { playAnimation() } else { pauseAnimation() }
            }
        }
    }

    /**
     * Control Stt View
     *
     * @param show
     */
    private fun controlSttView(show: Boolean) {

        with(mBinding) {

            sttShowAnimator?.cancel()
            sttEndAnimator?.cancel()

            sttEndAnimator = AnimatorSet().apply {
                playTogether(getAlphaAnimator(tvStt, false), getAlphaAnimator(ivSttBackground, false))
                doOnEnd {

                    tvStt.alpha = 0f
                    tvStt.visibility = View.GONE

                    ivSttBackground.alpha = 0f
                    ivSttBackground.visibility = View.GONE
                }
                if (show) startDelay = 3000
                duration = 300
            }

            if (show) {

                sttShowAnimator = AnimatorSet().apply {
                    playTogether(getAlphaAnimator(tvStt, true), getAlphaAnimator(ivSttBackground, true))
                    doOnStart {

                        tvStt.alpha = 0f
                        tvStt.visibility = View.VISIBLE

                        ivSttBackground.alpha = 0f
                        ivSttBackground.visibility = View.VISIBLE
                    }
                    doOnEnd { sttEndAnimator?.start() }
                    duration = 300
                }

                sttShowAnimator?.start()

            } else {

                sttEndAnimator?.start()
            }
        }
    }

    /**
     * Control Voice Button
     *
     * @param isMale
     */
    private fun controlVoiceButton(isMale: Boolean) {

        with(mBinding) {

            tvMale.isSelected = isMale
            tvFemale.isSelected = !isMale

            if (isMale) {

                tvMale.setBackgroundColor(ContextCompat.getColor(this@ActVideoChat, R.color.color_3377ff))
                tvMale.setTextColor(ContextCompat.getColor(this@ActVideoChat, R.color.white))

                tvFemale.setBackgroundColor(ContextCompat.getColor(this@ActVideoChat, R.color.white))
                tvFemale.setTextColor(ContextCompat.getColor(this@ActVideoChat, R.color.black))

            } else {

                tvMale.setBackgroundColor(ContextCompat.getColor(this@ActVideoChat, R.color.white))
                tvMale.setTextColor(ContextCompat.getColor(this@ActVideoChat, R.color.black))

                tvFemale.setBackgroundColor(ContextCompat.getColor(this@ActVideoChat, R.color.color_3377ff))
                tvFemale.setTextColor(ContextCompat.getColor(this@ActVideoChat, R.color.white))
            }
        }
    }

    /**
     * Get Alpha Animator
     *
     * @param view
     * @param show
     * @return ObjectAnimator
     */
    private fun getAlphaAnimator(view: View, show: Boolean): ObjectAnimator {

        val start = if (show) 0f else 1f
        val end = if (show) 1f else 0f

        return ObjectAnimator.ofFloat(view, View.ALPHA, start, end)
    }

    /**
     * Send Chat
     *
     * @param request
     */
    private fun sendChat(request: String) {

        // log data api 호출
        val id = PreferencesUtil.getPreferencesString(this@ActVideoChat, PreferencesUtil.AUTO_LOGIN_ID_KEY)
        viewModel.sendChat(id, number, request)
    }
}