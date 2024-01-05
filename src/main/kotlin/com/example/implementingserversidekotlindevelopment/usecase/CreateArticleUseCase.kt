package com.example.implementingserversidekotlindevelopment.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.example.implementingserversidekotlindevelopment.common.ValidationError
import com.example.implementingserversidekotlindevelopment.domain.ArticleRepository
import com.example.implementingserversidekotlindevelopment.domain.CreatedArticle
import oracle.net.ns.Message
import org.springframework.stereotype.Service

interface CreateArticleUseCase {
    fun execute(title: String?, description: String?, body: String?):Either<Error, CreatedArticle> = TODO()

    sealed interface Error{
        data class InvalidArticle(val errors: List<ValidationError>): Error
    }
}

@Service
class CreateArticleUseCaseImpl(val articleRepository: ArticleRepository): CreateArticleUseCase{
    override fun execute(
        title: String?,
        description: String?,
        body: String?
    ): Either<CreateArticleUseCase.Error, CreatedArticle> {
        val postedArticle = CreatedArticle.new(title = title, description = description, body = body).fold(
            { return CreateArticleUseCase.Error.InvalidArticle(it).left() }, { it }
        )

        val createdArticle = articleRepository.create(createdArticle = postedArticle).fold(
            { throw UnsupportedOperationException("現在この分岐に入ることはない") }, { it }
        )

        return createdArticle.right()
    }
}