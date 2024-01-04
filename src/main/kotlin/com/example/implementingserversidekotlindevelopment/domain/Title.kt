package com.example.implementingserversidekotlindevelopment.domain

import arrow.core.*
import com.example.implementingserversidekotlindevelopment.common.ValidationError

sealed interface Title {
    val value: String

    private data class ValidatedTitle(override val value: String): Title

    private data class TitleWithoutValidation(override val value: String) :Title

    companion object{
     private const val maximumLength = 32

        /**
         * DBにデータを入れた当時とはバリデーションが変更されている可能性があるため、DBに入っているデータをモデル化する際にはバリデーションなしでのstatic factory methodを使用する。
         */
        fun newWithoutValidation(title: String): Title = TitleWithoutValidation(title)

        fun new(title: String?): ValidatedNel<CreationError, Title>{
            if (title.isNullOrBlank()){
                return CreationError.Required.invalidNel()
            }

            if (title.length > maximumLength){
                return CreationError.TooLong(maximumLength).invalidNel()
            }
            return ValidatedTitle(title).validNel()
        }
    }

    sealed interface CreationError : ValidationError{
        object Required: CreationError{
            override val message: String
                get() = "titleは必須です。"
        }

        data class TooLong(val maximum: Int): CreationError{
            override val message: String
                get() = "titleは${maximum}文字以下にしてください。"
        }
    }
}