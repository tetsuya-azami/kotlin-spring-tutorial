package com.example.implementingserversidekotlindevelopment.domain

import arrow.core.ValidatedNel
import arrow.core.zip
import com.example.implementingserversidekotlindevelopment.common.ValidationError

interface UpdatableCreatedArticle {
    val title: Title
    val description: Description
    val body: Body

    private data class ValidatedUpdatableCreatedArticle(
        override val title: Title,
        override val description: Description,
        override val body: Body
    ) : UpdatableCreatedArticle

    companion object {
        fun new(
            title: String?,
            description: String?,
            body: String?,
        ): ValidatedNel<ValidationError, UpdatableCreatedArticle> {
            return Title.new(title).zip(Description.new(description), Body.new(body)) { e1, e2, e3 ->
                ValidatedUpdatableCreatedArticle(e1, e2, e3)
            }
        }
    }
}