package com.imp.presentation.widget.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.regex.Pattern

/**
 * Validate Util
 */
class ValidateUtil {

    companion object {

        /**
         * 이름 유효성 체크 (한글, 영어 2~10)
         */
        fun checkName(name: String?): Boolean {

            if (name.isNullOrEmpty()) return false

            val pattern = Pattern.compile("^[a-zA-Z가-힣0-9]{2,50}$")
            return pattern.matcher(name).matches()
        }

        /**
         * 이메일 유효성 체크
         */
        fun checkEmail(email: String?): Boolean {

            if (email.isNullOrEmpty()) return false

            val pattern = Pattern.compile("[\\w\\~\\-\\.]+@[\\w\\~\\-]+(\\.[\\w\\~\\-]+)+")
            return pattern.matcher(email).matches()
        }

        /**
         * ID 유효성 체크 (영문 숫자 2~20)
         */
        fun checkId(id: String?): Boolean {
            if (id.isNullOrEmpty()) return false

            val pattern = Pattern.compile("^[a-zA-Z0-9_\\-.]{2,20}$")
            return pattern.matcher(id).matches()
        }

        /**
         * 비밀번호 유효성 체크 (숫자, 영문 포함 8~20)
         */
        fun checkPassword(value: String?): Boolean {

            if (value.isNullOrEmpty()) return false

            val pattern = Pattern.compile("(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#\$%^&\\*+=.~,?,]).{8,20}")
            return pattern.matcher(value).matches()
        }

        /**
         * 휴대폰 유효성 체크
         */
        fun checkPhone(phone: String?): Boolean{

            if (phone.isNullOrEmpty()) return false

            val pattern = Pattern.compile("^\\s*(010|011|012|013|014|015|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$")
            return pattern.matcher(phone).matches()
        }

        /**
         * 생년월일 유효성 체크
         */
        fun checkBirth(birth: String?): Boolean {

            if (birth.isNullOrEmpty()) return false

            val year = Calendar.getInstance().get(Calendar.YEAR).toString()
            val pattern = Pattern.compile("^(19[0-9][0-9]|20[0-${year[2]}][0-${year[3]}])(0[0-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])\$")

            if (pattern.matcher(birth).matches()) {
                return checkDate(birth)
            }

            return false
        }

        /**
         * 현재 날짜보다 이전인지 확인
         */
        private fun checkDate(birth: String): Boolean {

            return try {

                val format = SimpleDateFormat("yyyyMMdd")

                format.isLenient = false
                format.parse(birth)

                format.parse(birth).time <= format.parse(format.format(Calendar.getInstance().time)).time

            } catch (e: ParseException) {

                false
            }
        }
    }
}