package com.example.implementingserversidekotlindevelopment.presentation

import arrow.core.handleError
import com.example.implementingserversidekotlindevelopment.openapi.generated.controller.ArticlesApi
import com.example.implementingserversidekotlindevelopment.openapi.generated.model.*
import com.example.implementingserversidekotlindevelopment.usecase.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleController(
    val showArticleUseCase: ShowArticleUseCase,
    val createArticleUseCase: CreateArticleUseCase,
    val feedArticleUseCase: FeedArticleUseCase,
    val updateArticleUseCase: UpdateArticleUseCase,
    val deleteArticleUseCase: DeleteArticleUseCase
) : ArticlesApi {
    override fun getArticle(slug: String): ResponseEntity<SingleArticleResponse> {
        val createdArticle = showArticleUseCase.execute(slug).fold(
            { throw ShowArticleUseCaseErrorException(it) },
            { it }
        )

        return ResponseEntity(
            SingleArticleResponse(
                Article(
                    slug = createdArticle.slug.value,
                    title = createdArticle.title.value,
                    description = createdArticle.description.value,
                    body = createdArticle.body.value
                )
            ),
            HttpStatus.OK
        )
    }

    override fun createArticle(newArticleRequest: NewArticleRequest): ResponseEntity<SingleArticleResponse> {
        val createdArticle = createArticleUseCase.execute(
            title = newArticleRequest.article.title,
            description = newArticleRequest.article.description,
            body = newArticleRequest.article.body
        ).fold({ throw CreateArticleUseCaseErrorException(it) }, { it })

        return ResponseEntity(
            SingleArticleResponse(
                article = Article(
                    slug = createdArticle.slug.value,
                    title = createdArticle.title.value,
                    description = createdArticle.description.value,
                    body = createdArticle.body.value
                )
            ),
            HttpStatus.CREATED
        )
    }

    override fun getArticles(): ResponseEntity<MultipleArticleResponse> {
        val articles = feedArticleUseCase.execute()

        return ResponseEntity(
            MultipleArticleResponse(
                articles = articles.createdArticles.map {
                    Article(
                        slug = it.slug.value,
                        title = it.title.value,
                        description = it.description.value,
                        body = it.body.value
                    )
                },
                articleCount = articles.count
            ),
            HttpStatus.OK
        )
    }

    override fun updateArticle(
        slug: String,
        updateArticleRequest: UpdateArticleRequest
    ): ResponseEntity<SingleArticleResponse> {
        val updatedArticle = updateArticleUseCase.execute(
            slug = slug,
            title = updateArticleRequest.article.title,
            description = updateArticleRequest.article.description,
            body = updateArticleRequest.article.body
        ).fold(
            { throw UpdateArticleUseCaseErrorException(it) },
            { it }
        )

        return ResponseEntity(
            SingleArticleResponse(
                Article(
                    slug = updatedArticle.slug.value,
                    title = updatedArticle.title.value,
                    description = updatedArticle.description.value,
                    body = updatedArticle.body.value
                )
            ),
            HttpStatus.OK
        )
    }

    override fun deleteArticle(slug: String): ResponseEntity<Unit> {
        deleteArticleUseCase.execute(slug = slug).handleError {
            throw DeleteArticleUseCaseErrorException(it)
        }

        return ResponseEntity(
            Unit,
            HttpStatus.OK
        )
    }

    data class ShowArticleUseCaseErrorException(val error: ShowArticleUseCase.Error) : Exception()
    data class CreateArticleUseCaseErrorException(val error: CreateArticleUseCase.Error) : Exception()
    data class UpdateArticleUseCaseErrorException(val error: UpdateArticleUseCase.Error) : Exception()
    data class DeleteArticleUseCaseErrorException(val error: DeleteArticleUseCase.Error) : Exception()

    @ExceptionHandler(value = [ShowArticleUseCaseErrorException::class])
    fun onShowArticleUseCaseErrorException(e: ShowArticleUseCaseErrorException): ResponseEntity<GenericErrorModel> =
        when (val error = e.error) {
            is ShowArticleUseCase.Error.NotFoundArticleBySlug -> ResponseEntity<GenericErrorModel>(
                GenericErrorModel(
                    errors = GenericErrorModelErrors(
                        body = listOf("${error.slug.value}に該当する記事は見つかりませんでした。")
                    )
                ),
                HttpStatus.NOT_FOUND
            )

            is ShowArticleUseCase.Error.ValidationErrors -> ResponseEntity<GenericErrorModel>(
                GenericErrorModel(
                    errors = GenericErrorModelErrors(
                        body = error.errors.map { it.message }
                    )
                ),
                HttpStatus.BAD_REQUEST
            )
        }

    @ExceptionHandler(value = [CreateArticleUseCaseErrorException::class])
    fun onCreateArticleUseCaseErrorException(e: CreateArticleUseCaseErrorException): ResponseEntity<GenericErrorModel> =
        when (val error = e.error) {
            is CreateArticleUseCase.Error.InvalidArticle -> {
                ResponseEntity(
                    GenericErrorModel(
                        errors = GenericErrorModelErrors(
                            body = error.errors.map { it.message }
                        )
                    ),
                    HttpStatus.BAD_REQUEST
                )
            }
        }

    @ExceptionHandler(value = [UpdateArticleUseCaseErrorException::class])
    fun onUpdateArticleUseCaseErrorException(e: UpdateArticleUseCaseErrorException): ResponseEntity<GenericErrorModel> {
        when (val error = e.error) {
            is UpdateArticleUseCase.Error.ValidationErrors -> {
                return ResponseEntity(
                    GenericErrorModel(
                        errors = GenericErrorModelErrors(
                            body = error.errors.map { it.message }
                        )
                    ),
                    HttpStatus.BAD_REQUEST
                )
            }

            is UpdateArticleUseCase.Error.InvalidArticleErrors -> {
                return ResponseEntity(
                    GenericErrorModel(
                        errors = GenericErrorModelErrors(
                            body = error.errors.map { it.message }
                        )
                    ),
                    HttpStatus.BAD_REQUEST
                )
            }

            is UpdateArticleUseCase.Error.NotFoundArticleBySlug -> {
                return ResponseEntity(
                    GenericErrorModel(
                        errors = GenericErrorModelErrors(
                            body = listOf("${error.slug.value}に該当する記事は見つかりませんでした。")
                        )
                    ),
                    HttpStatus.NOT_FOUND
                )
            }
        }
    }

    @ExceptionHandler(value = [DeleteArticleUseCaseErrorException::class])
    fun onDeleteArticleUseCaseErrorException(e: DeleteArticleUseCaseErrorException): ResponseEntity<GenericErrorModel> {
        when (val error = e.error) {
            is DeleteArticleUseCase.Error.ValidationErrors -> {
                return ResponseEntity(
                    GenericErrorModel(
                        errors = GenericErrorModelErrors(
                            body = error.errors.map { it.message }
                        )
                    ),
                    HttpStatus.BAD_REQUEST
                )
            }

            is DeleteArticleUseCase.Error.NotFound -> {
                return ResponseEntity(
                    GenericErrorModel(
                        errors = GenericErrorModelErrors(
                            body = listOf("${error.slug.value}に該当する記事は見つかりませんでした。")
                        )
                    ),
                    HttpStatus.NOT_FOUND
                )
            }
        }
    }
}