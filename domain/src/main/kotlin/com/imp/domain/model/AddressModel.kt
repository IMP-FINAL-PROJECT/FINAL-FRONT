package com.imp.domain.model

/**
 * Address Model
 */
data class AddressModel (

    var meta: Meta? = null,
    var documents: ArrayList<Place>

) {

    data class Meta(

        var total_count: Int,               // 검색어에 검색된 문서 수
        var pageable_count: Int,            // total_count 중 노출 가능 문서 수 (최대: 45)
        var is_end: Boolean,                // 현재 페이지가 마지막 페이지인지 여부, 값이 false면 다음 요청 시 page 값을 증가시켜 다음 페이지 요청 가능
    )

    data class Place(

        var address_name: String,            // 전체 지번 주소 또는 전체 도로명 주소, 입력에 따라 결정됨
        var address_type: String,            // address_name의 값의 타입(Type) -> (REGION(지명), ROAD(도로명), REGION_ADDR(지번 주소), ROAD_ADDR(도로명 주소))
        var x: String,                       // X 좌표값, 경/위도인 경우 경도(longitude)
        var y: String,                       // Y 좌표값, 경/위도인 경우 위도(latitude)
    )
}