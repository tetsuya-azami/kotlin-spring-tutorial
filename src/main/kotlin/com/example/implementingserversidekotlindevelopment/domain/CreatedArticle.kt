package com.example.implementingserversidekotlindevelopment.domain

import arrow.core.ValidatedNel
import arrow.core.zip
import com.example.implementingserversidekotlindevelopment.common.ValidationError

class CreatedArticle private constructor(
    val slug: Slug,
    val title:Title,
    val description: Description,
    val body: Body
) {
    companion object{
        // RequestからのEntity生成は基本型で行う。
        fun new(
            title: String?,
            description: String?,
            body: String?
        ): ValidatedNel<ValidationError, CreatedArticle>{
            return Title.new(title).zip(
                Description.new(description), Body.new(body)
            ){ a,b,c -> CreatedArticle(Slug.new(), a,b,c)}
        }

        // DBからのEntity生成は値オブジェクトを使用して行う。
        fun newWithoutValidation(
            slug: Slug,
            title: Title,
            description: Description,
            body: Body
        ): CreatedArticle{
            return CreatedArticle(
                slug,
                title,
                description,
                body
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is CreatedArticle){
            return false
        }
        return this.slug == other.slug
    }

    override fun hashCode(): Int {
        return this.slug.hashCode() * 31
    }
}