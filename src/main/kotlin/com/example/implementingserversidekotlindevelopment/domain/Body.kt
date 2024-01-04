package com.example.implementingserversidekotlindevelopment.domain

import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.valid
import arrow.core.validNel
import com.example.implementingserversidekotlindevelopment.common.ValidationError

sealed interface Body{
    val value: String
    private data class ValidatedBody(override val value: String): Body
    private data class BodyWithoutValidation(override val value: String): Body

    companion object {
        private const val maximum: Int = 32
        fun new(body: String?): ValidatedNel<CreationError, Body> {
            if (body == null) {
                return CreationError.Required.invalidNel()
            }
            if (maximum < body.length) {
                return CreationError.TooLong(maximum).invalidNel()
            }
            return ValidatedBody(body).validNel()
        }

        fun newWithoutValidation(body: String): Body{
            return BodyWithoutValidation(body)
        }
    }

    sealed interface CreationError: ValidationError{
        object Required: CreationError{
            override val message: String
                get() = "本文は必須です。"
        }

        data class TooLong(val maximum: Int): CreationError{
            override val message: String
                get() = "bodyは${maximum}文字以下にしてください。"
        }
    }
}