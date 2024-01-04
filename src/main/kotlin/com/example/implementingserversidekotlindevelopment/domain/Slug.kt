package com.example.implementingserversidekotlindevelopment.domain

import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.validNel
import com.example.implementingserversidekotlindevelopment.common.ValidationError
import java.util.*

sealed interface Slug {
    val value: String

    private data class ValidatedSlug(override val value: String): Slug
    private data class SlugWithoutValidation(override val value: String): Slug

    companion object{
        private const val format = "^[a-z0-9]{32}$"

        fun newWithoutValidation(slug: String): Slug{
            return SlugWithoutValidation(slug)
        }

        fun new(slug: String?): ValidatedNel<CreationError, Slug>{
            if (slug == null){
                return CreationError.Required.invalidNel()
            }
            if (!slug.matches(Regex(format))){
                return CreationError.InValidFormat.invalidNel()
            }
            return ValidatedSlug(slug).validNel()
        }

        fun new(): Slug{
            return ValidatedSlug(UUID.randomUUID().toString().split("-").joinToString(""))
        }
    }

    sealed interface CreationError: ValidationError{
        object Required: CreationError{
            override val message: String
                get() = "slugは必須です。"

        }
        object InValidFormat: CreationError{
            override val message: String
                get() = "slugは32文字の英小文字数字です。"
        }
    }
}