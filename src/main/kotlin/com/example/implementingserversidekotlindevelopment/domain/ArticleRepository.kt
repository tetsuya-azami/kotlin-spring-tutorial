package com.example.implementingserversidekotlindevelopment.domain

import arrow.core.Either

interface ArticleRepository {
    fun findBySlug(slug: Slug): Either<FindBySlugError, CreatedArticle> = throw NotImplementedError()

    fun create(createdArticle: CreatedArticle): Either<CreateArticleError, CreatedArticle> = throw NotImplementedError()

    sealed interface FindBySlugError{
        data class NotFound(val slug: Slug): FindBySlugError
    }

    sealed interface CreateArticleError
}