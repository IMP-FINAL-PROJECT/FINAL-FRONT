package com.imp.presentation.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.imp.presentation.databinding.ItemRecommendListBinding
import com.imp.presentation.widget.utils.MethodStorageUtil

/**
 * Home - Recommend List Adapter
 */
class RecommendListAdapter(var context: Context?, val list: ArrayList<String>) : RecyclerView.Adapter<RecommendListAdapter.CustomViewHolder>() {

    interface SelectItem { fun selectItem(position: Int, url: String) }
    var selectItem: SelectItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

        val binding = ItemRecommendListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        if (position < list.size) {
            holder.bindView(context, position, list[position], selectItem, list.size)
        }
    }

    class CustomViewHolder(private val binding: ItemRecommendListBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindView(context: Context?, position: Int, dao: String, selectItem: SelectItem?, size: Int) {

            context?.let { ctx ->

                with(binding) {

                    var url = "https://m.naver.com"

                    when(position) {

                        0 -> {

                            tvTitle.text = "바베큐 파티는 못 참지"
                            tvDescription.text = "도심 속에서 새로운 사람들을 만나고, 맛있는 바베큐 파티를 즐겨보세요!\n꿀꿀한 기분도 금방 날아갈 거예요!"
                            MethodStorageUtil.setImageUrl(ivMain, "https://lh3.googleusercontent.com/proxy/4WBHxE9d2UcYjg3z2W2hgcoITERLxDcZtX12e1yCQdnRqsH_thfpEZuEUBM2oqTzb-oyhYgKTRVXtqYECzcYmjoOce8f7P-aCbum8Drf3Q1aJnGnilI")
                            url = "https://m.place.naver.com/place/1809648327/home?entry=pll"
                        }

                        1 -> {

                            tvTitle.text = "고양이가 세상을 구한다 (근엄)"
                            tvDescription.text = "집 근처에 고양이 카페가 있어요!\n귀여운 고양이 친구들과 힐링하는 시간은 어떠신가요?"
                            MethodStorageUtil.setImageUrl(ivMain, "https://image.fmkorea.com/files/attach/new/20200630/3842645/7175392/2968293320/238495ea6358d3451339225a324ae75b.jpg")
                            url = "https://m.place.naver.com/place/1452681316/home?entry=pll"
                        }

                        2 -> {

                            tvTitle.text = "삶이 힘들게 느껴질 때"
                            tvDescription.text = "쳇바퀴처럼 반복되는 삶에 지쳐 여유를 잃고 방황하는 당신에게,\n이 책이 위로가 되어줄 거예요."
                            MethodStorageUtil.setImageUrl(ivMain, "https://blog.kakaocdn.net/dn/SsjoA/btqxonv8DQ9/Ot75bypD8NzrYKWMGH1je0/img.jpg")
                            url = "https://brunch.co.kr/@dailynews/1197"
                        }

                        3 -> {

                            tvTitle.text = "아 몰랑 집이 체고!"
                            tvDescription.text = "잡생각은 그만! 오싹오싹한 추리 예능 보면서 머리도 환기 시켜주자구요. 😎"
                            MethodStorageUtil.setImageUrl(ivMain, "https://image.tving.com/ntgs/contents/CTC/caip/CAIP0400/ko/20240405/0945/P001754831.jpg/dims/resize/1280")
                            url = "https://www.tving.com/contents/P001754831"
                        }

                        4 -> {

                            tvTitle.text = "날씨도 좋은데 산책 ㄱ?"
                            tvDescription.text = "오늘은 집에만 있었어요! 근처 공원에서 잠깐 산책하는 건 어때요?\n날씨가 정말 좋아요!"
                            MethodStorageUtil.setImageUrl(ivMain, "https://i.namu.wiki/i/tQCCtBkamst95Zc0NFm7ceRPz8W3rCbRDpQbQKty0ai0_oyJTo4kwIVfMYDK3feslrrgyLChDlLMMPpYAHs2XQ.webp")
                            url = "https://m.place.naver.com/place/13491889/home?entry=pll"
                        }
                    }

                    // Item 클릭
                    ctItem.tag = position
                    ctItem.setOnClickListener {

                        val pos = it.tag.toString().toInt()
                        selectItem?.selectItem(pos, url)
                    }
                }
            }
        }
    }
}