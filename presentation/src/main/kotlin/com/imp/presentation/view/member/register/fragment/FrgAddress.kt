package com.imp.presentation.view.member.register.fragment

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.imp.domain.model.AddressModel
import com.imp.presentation.R
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.databinding.FrgRegisterAddressBinding
import com.imp.presentation.view.adapter.AddressAdapter
import com.imp.presentation.view.member.register.activity.ActRegister
import com.imp.presentation.viewmodel.MemberViewModel
import com.imp.presentation.widget.extension.focusAndShowKeyboard
import com.imp.presentation.widget.extension.hideKeyboard
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions


/**
 * Register - Profile Fragment
 */
class FrgAddress: BaseFragment<FrgRegisterAddressBinding>() {

    companion object {

        fun newInstance(): FrgAddress {
            return FrgAddress().apply {
                arguments = Bundle().apply {}
            }
        }
    }

    /** Member ViewModel */
    private val viewModel: MemberViewModel by activityViewModels()

    /** Chat List Adapter */
    private lateinit var addressAdapter: AddressAdapter

    /** 지도 관련 변수 */
    private var kakaoMap: KakaoMap? = null
    private var init = true

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FrgRegisterAddressBinding.inflate(inflater, container, false)

    override fun initData() {

    }

    override fun initView() {

        initObserver()
        initDisplay()
        initRecyclerView()
        initMapView()
        initEditText()
        setOnClickListener()
    }

    /**
     * Initialize Observer
     */
    private fun initObserver() {

        /** Address Validation */
        viewModel.addressData.observe(viewLifecycleOwner) { result ->

            addressAdapter.updateList(result.documents)
        }

        /** Error Callback */
        viewModel.errorCallback.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { error ->

                context?.let { Toast.makeText(it, error.message.toString(), Toast.LENGTH_SHORT).show() }
            }
        }
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            // Address
            incAddress.tvTitle.visibility = View.GONE

            // search
            tvSearch.text = getString(R.string.register_text_21)
        }
    }

    /**
     * Initialize RecyclerView
     */
    private fun initRecyclerView() {

        with(mBinding) {

            rvList.apply {

                visibility = View.VISIBLE

                itemAnimator = null
                addressAdapter = AddressAdapter(requireContext(), ArrayList())
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = addressAdapter
                addressAdapter.apply {

                    selectItem = object : AddressAdapter.SelectItem {

                        override fun selectItem(position: Int, dao: AddressModel.Place) {

                            if (position < list.size) {

                                incAddress.etInput.setText(dao.address_name)

                                visibility = View.GONE

                                kakaoMap?.moveCamera(
                                    CameraUpdateFactory.newCenterPosition(LatLng.from(dao.y.toDouble(), dao.x.toDouble()), 17),
                                    CameraAnimation.from(500)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Initialize Map View
     */
    private fun initMapView() {

        with(mBinding) {

            mapView.start(object : MapLifeCycleCallback() {
                override fun onMapDestroy() {}
                override fun onMapError(p0: Exception?) {}
            }, object : KakaoMapReadyCallback() {
                override fun onMapReady(map: KakaoMap) {

                    kakaoMap = map

                    map.setOnCameraMoveEndListener { kakaoMap, cameraPosition, gestureType ->

                        if (!init) addPoint(kakaoMap, cameraPosition.position)
                        else init = false
                    }
                }
            })
        }
    }

    /**
     * Set OnClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

            // 검색
            tvSearch.setOnClickListener {

                hideKeyboard(requireContext(), incAddress.etInput)

                // 검색 화면 설정
                rvList.visibility = View.VISIBLE

                // search api 호출
                viewModel.searchAddress(incAddress.etInput.text.toString())
            }
        }
    }

    /**
     * Initialize EditText
     */
    private fun initEditText() {

        with(mBinding) {

            // address 입력
            incAddress.etInput.apply {

                isSingleLine = true
                hint = getString(R.string.register_text_20)
                inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { rvList.visibility = View.VISIBLE }
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                        // 검색 화면 설정
                        rvList.visibility = View.VISIBLE
                    }
                })
            }

            // 화면 진입 시 키보드 올리기
            context?.let { incAddress.etInput.focusAndShowKeyboard(it) }
        }
    }

    /**
     * Add Center Label
     *
     * @param map
     * @param point
     */
    private fun addPoint(map: KakaoMap, point: LatLng) {

        map.labelManager?.layer?.let { layer ->

            layer.removeAll()
            layer.addLabel(LabelOptions.from("centerLabel", point).setStyles(R.drawable.icon_map_marker))

            // check address validate
            checkAddressValidate(point)
        }
    }

    /**
     * Check Address Validate
     *
     * @param point
     */
    private fun checkAddressValidate(point: LatLng) {

        viewModel.registerData.address = arrayListOf(point.latitude, point.longitude)
        context?.let { if (it is ActRegister) it.controlButtonEnabled(true) }
    }
}