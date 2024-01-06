package com.example.implementingserversidekotlindevelopment.usecase

import arrow.core.Either
import arrow.core.getOrHandle
import arrow.core.left
import arrow.core.right
import com.example.implementingserversidekotlindevelopment.common.ValidationError
import com.example.implementingserversidekotlindevelopment.domain.ArticleRepository
import com.example.implementingserversidekotlindevelopment.domain.CreatedArticle
import com.example.implementingserversidekotlindevelopment.domain.Slug
import com.example.implementingserversidekotlindevelopment.domain.UpdatableCreatedArticle
import org.springframework.stereotype.Service

interface UpdateArticleUseCase {

    fun execute(slug: String?, title: String?, description: String?, body: String?): Either<Error, CreatedArticle> =
        throw NotImplementedError()

    sealed interface Error {
        data class ValidationErrors(val errors: List<ValidationError>) : Error
        data class InvalidArticleErrors(val errors: List<ValidationError>) : Error
        data class NotFoundArticleBySlug(val slug: Slug) : Error
    }
}

@Service
class UpdateArticleUseCaseImpl(val articleRepository: ArticleRepository) : UpdateArticleUseCase {
    override fun execute(
        slug: String?,
        title: String?,
        description: String?,
        body: String?
    ): Either<UpdateArticleUseCase.Error, CreatedArticle> {
        val validatedSlug = Slug.new(slug).fold(
            { return UpdateArticleUseCase.Error.ValidationErrors(it).left() },
            { it }
        )

        val unsavedCreatedArticle = UpdatableCreatedArticle.new(title, description, body).fold(
            { return UpdateArticleUseCase.Error.InvalidArticleErrors(it).left() },
            { it }
        )

        val updatedArticle =
            articleRepository.update(slug = validatedSlug, updatableCreatedArticle = unsavedCreatedArticle)
                .getOrHandle {
                    when (it) {
                        is ArticleRepository.UpdateError.NotFound -> {
                            return UpdateArticleUseCase.Error.NotFoundArticleBySlug(validatedSlug).left()
                        }
                    }
                }

        return updatedArticle.right()
    }
}
