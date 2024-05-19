package com.imp.data.db.entity

import com.imp.domain.model.RecommendModel

class RecommendData {

    companion object {

        /**
         * Get Recommend Data
         *
         * @param score
         * @return ArrayList<RecommendModel>
         */
        fun getRecommendData(score: Int): ArrayList<RecommendModel> {

            val result: ArrayList<RecommendModel> = ArrayList()

            when {

                else -> {

                    result.add(RecommendModel(
                        id = "0",
                        title = "바베큐 파티는 못 참지",
                        description = "도심 속에서 새로운 사람들을 만나고, 맛있는 바베큐 파티를 즐겨보세요!\n꿀꿀한 기분도 금방 날아갈 거예요!",
                        imageUrl = "https://previews.123rf.com/images/alexraths/alexraths1505/alexraths150500126/40695099-바베큐의-석탄을-통해-야채-모듬-맛있는-구운-고기.jpg",
                        url = "https://m.place.naver.com/place/1809648327/home?entry=pll"
                    ))
                    result.add(RecommendModel(
                        id = "1",
                        title = "고양이가 세상을 구한다 (근엄)",
                        description = "집 근처에 고양이 카페가 있어요!\n귀여운 고양이 친구들과 힐링하는 시간은 어떠신가요?",
                        imageUrl = "https://image.fmkorea.com/files/attach/new/20200630/3842645/7175392/2968293320/238495ea6358d3451339225a324ae75b.jpg",
                        url = "https://m.place.naver.com/place/1452681316/home?entry=pll"
                    ))
                    result.add(RecommendModel(
                        id = "2",
                        title = "삶이 힘들게 느껴질 때",
                        description = "쳇바퀴처럼 반복되는 삶에 지쳐 여유를 잃고 방황하는 당신에게,\n이 책이 위로가 되어줄 거예요.",
                        imageUrl = "https://blog.kakaocdn.net/dn/SsjoA/btqxonv8DQ9/Ot75bypD8NzrYKWMGH1je0/img.jpg",
                        url = "https://brunch.co.kr/@dailynews/1197"
                    ))
                    result.add(RecommendModel(
                        id = "3",
                        title = "아 몰랑 집이 체고!",
                        description = "잡생각은 그만! 오싹오싹한 추리 예능 보면서 머리도 환기 시켜주자구요. 😎",
                        imageUrl = "https://image.tving.com/ntgs/contents/CTC/caip/CAIP0400/ko/20240405/0945/P001754831.jpg/dims/resize/1280",
                        url = "https://www.tving.com/contents/P001754831"
                    ))
                    result.add(RecommendModel(
                        id = "4",
                        title = "날씨도 좋은데 산책 ㄱ?",
                        description = "오늘은 집에만 있었어요! 근처 공원에서 잠깐 산책하는 건 어때요?\n날씨가 정말 좋아요!",
                        imageUrl = "https://i.namu.wiki/i/tQCCtBkamst95Zc0NFm7ceRPz8W3rCbRDpQbQKty0ai0_oyJTo4kwIVfMYDK3feslrrgyLChDlLMMPpYAHs2XQ.webp",
                        url = "https://m.place.naver.com/place/13491889/home?entry=pll"
                    ))
                }
            }

            return result
        }
    }
}