package com.imp.presentation.view.main.activity

import android.net.Uri
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.gorisse.thomas.sceneform.scene.await
import com.imp.presentation.R
import com.imp.presentation.base.BaseContractActivity
import com.imp.presentation.databinding.ActVideoChatBinding
import com.imp.presentation.widget.extension.toVisibleOrGone
import com.imp.presentation.widget.utils.MethodStorageUtil
import com.imp.presentation.widget.utils.PermissionUtil
import dagger.hilt.android.AndroidEntryPoint


/**
 * Chat - Video Chatting Activity
 */
@AndroidEntryPoint
class ActVideoChat : BaseContractActivity<ActVideoChatBinding>() {

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
        if (!PermissionUtil.checkPermissionCamera(this)) {
            PermissionUtil.requestPermissionCamera(this, permissionActivityResultLauncher, permissionDeniedActivityResultLauncher, true, permissionDeniedPopup)
            return
        }
    }

    /** AR 관련 변수 */
    private lateinit var arFragment: ArFragment
    private var model: Renderable? = null
    private var modelView: ViewRenderable? = null
    private var node: Node? = null

    /** Title */
    private var title: String? = null

    override fun getViewBinding() = ActVideoChatBinding.inflate(layoutInflater)

    override fun initData() {

        fullScreen = true

        title = intent?.getStringExtra("title")

//        selectionPermission()
    }

    override fun initView() {

        initDisplay()
        initSceneForm()
        setOnClickListener()
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            // title
            tvTitle.text = title

            // loading lottie
            lottieLoading.visibility = View.GONE

            // header top margin
            val layoutParams = ctHeader.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topMargin = MethodStorageUtil.getStatusBarHeight(this@ActVideoChat)
            ctHeader.layoutParams = layoutParams
        }
    }

    /**
     * Set onClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

            // 뒤로 가기
            ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
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

        loadingLottieControl(true)

        if (model == null) {
            return
        }

        arFragment.apply {

            if (node != null) {
                arSceneView.scene.removeChild(node)
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

            loadingLottieControl(false)
        }
    }

    /**
     * loading lottie control
     *
     * @param start true -> start / false -> stop
     */
    private fun loadingLottieControl(start: Boolean) {

        with(mBinding.lottieLoading) {

            visibility = start.toVisibleOrGone()
            if (start) { playAnimation() } else { pauseAnimation() }
        }
    }
}