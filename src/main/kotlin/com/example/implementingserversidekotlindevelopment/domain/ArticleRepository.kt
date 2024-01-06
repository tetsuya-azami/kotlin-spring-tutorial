package com.example.implementingserversidekotlindevelopment.domain

import arrow.core.Either

interface ArticleRepository {
    fun findBySlug(slug: Slug): Either<FindBySlugError, CreatedArticle> = throw NotImplementedError()

    fun create(createdArticle: CreatedArticle): Either<CreateArticleError, CreatedArticle> = throw NotImplementedError()

    fun all(): List<CreatedArticle> = throw NotImplementedError()

    fun update(
        slug: Slug,
        updatableCreatedArticle: UpdatableCreatedArticle
    ): Either<UpdateError, CreatedArticle> = throw NotImplementedError()

    fun delete(
        slug: Slug
    ): Either<DeleteError, Unit>

    sealed interface FindBySlugError {
        data class NotFound(val slug: Slug) : FindBySlugError
    }

    sealed interface CreateArticleError
    sealed interface UpdateError {
        data class NotFound(val slug: Slug) : UpdateError
    }

    sealed interface DeleteError {
        data class NotFound(val slug: Slug) : DeleteError
    }
}