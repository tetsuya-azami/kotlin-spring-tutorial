package com.example.implementingserversidekotlindevelopment.domain

import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.validNel
import com.example.implementingserversidekotlindevelopment.common.ValidationError

sealed interface Description {
    val value: String

    private data class ValidatedDescription(override val value: String): Description
    private data class DescriptionWithoutValidation(override val value: String): Description

    companion object{
        private const val maximum: Int = 64

        fun new(description: String?):ValidatedNel<CreationError, Description>{
            if (description == null){
                return CreationError.Required.invalidNel()
            }
            if (maximum < description.length){
                return CreationError.TooLong(maximum).invalidNel()
            }
            return ValidatedDescription(description).validNel()
        }

        fun newWithoutValidation(description: String):Description = DescriptionWithoutValidation(description)
    }

    sealed interface CreationError: ValidationError{
        object Required: CreationError{
            override val message: String
                get() = "descriptionを入力してください。"
        }
        data class TooLong(val maximum: Int): CreationError{
            override val message: String
                get() = "descriptionは${maximum}文字以下にしてください。"
        }
    }
}