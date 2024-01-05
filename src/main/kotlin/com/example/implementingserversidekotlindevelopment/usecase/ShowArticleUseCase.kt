package com.example.implementingserversidekotlindevelopment.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.example.implementingserversidekotlindevelopment.common.ValidationError
import com.example.implementingserversidekotlindevelopment.domain.ArticleRepository
import com.example.implementingserversidekotlindevelopment.domain.CreatedArticle
import com.example.implementingserversidekotlindevelopment.domain.Slug
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.stereotype.Service

interface ShowArticleUseCase {
    fun execute(slug: String?):Either<Error, CreatedArticle> = throw NotImplementedError()

    sealed interface Error{
        data class ValidationErrors(val errors: List<ValidationError>): Error
        data class NotFoundArticleBySlug(val slug: Slug): Error
    }
}

@Service
class ShowArticleUseCaseImpl(val articleRepository: ArticleRepository): ShowArticleUseCase {
    override fun execute(slug: String?): Either<ShowArticleUseCase.Error, CreatedArticle> {
        val validatedSlug = Slug.new(slug).fold(
            { return ShowArticleUseCase.Error.ValidationErrors(it.all).left() },
            { it }
        )

        val createdArticle = articleRepository.findBySlug(validatedSlug).fold(
            {
            return when(it){
                is ArticleRepository.FindBySlugError.NotFound -> ShowArticleUseCase.Error.NotFoundArticleBySlug(validatedSlug).left()
            }
            },
            { it }
        )

        return createdArticle.right()
    }
}