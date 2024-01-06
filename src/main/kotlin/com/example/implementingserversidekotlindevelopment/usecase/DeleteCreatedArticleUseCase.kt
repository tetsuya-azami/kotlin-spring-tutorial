package com.example.implementingserversidekotlindevelopment.usecase

import arrow.core.Either
import arrow.core.getOrHandle
import arrow.core.left
import arrow.core.right
import com.example.implementingserversidekotlindevelopment.common.ValidationError
import com.example.implementingserversidekotlindevelopment.domain.ArticleRepository
import com.example.implementingserversidekotlindevelopment.domain.Slug
import org.springframework.stereotype.Service

interface DeleteArticleUseCase {

    fun execute(slug: String?): Either<Error, Unit> = throw NotImplementedError()

    sealed interface Error {
        data class ValidationErrors(val errors: List<ValidationError>) : Error
        data class NotFound(val slug: Slug) : Error
    }
}

@Service
class DeleteArticleUseCaseImpl(val articleRepository: ArticleRepository) : DeleteArticleUseCase {
    override fun execute(slug: String?): Either<DeleteArticleUseCase.Error, Unit> {
        val validatedSlug = Slug.new(slug = slug).fold(
            { return DeleteArticleUseCase.Error.ValidationErrors(it.all).left() },
            { it }
        )

        articleRepository.delete(slug = validatedSlug).getOrHandle {
            return when (it) {
                is ArticleRepository.DeleteError.NotFound ->
                    DeleteArticleUseCase.Error.NotFound(slug = validatedSlug).left()
            }
        }

        return Unit.right()
    }
}