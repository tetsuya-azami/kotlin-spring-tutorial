package com.example.implementingserversidekotlindevelopment.presentation

import com.example.implementingserversidekotlindevelopment.openapi.generated.controller.ArticlesApi
import com.example.implementingserversidekotlindevelopment.openapi.generated.model.Article
import com.example.implementingserversidekotlindevelopment.openapi.generated.model.GenericErrorModel
import com.example.implementingserversidekotlindevelopment.openapi.generated.model.GenericErrorModelErrors
import com.example.implementingserversidekotlindevelopment.openapi.generated.model.SingleArticleResponse
import com.example.implementingserversidekotlindevelopment.usecase.ShowArticleUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleController(val showArticleUseCase: ShowArticleUseCase): ArticlesApi{
    override fun getArticle(slug: String): ResponseEntity<SingleArticleResponse> {
        val createdArticle = showArticleUseCase.execute(slug).fold(
            { throw ShowArticleUseCaseErrorException(it) },
            { it }
        )

        return ResponseEntity(
            SingleArticleResponse(
                Article(
                  slug =  createdArticle.slug.value,
                    title = createdArticle.title.value,
                    description =  createdArticle.description.value,
                    body =  createdArticle.body.value
                )
            ),
            HttpStatus.OK
        )
    }

    data class ShowArticleUseCaseErrorException(val error: ShowArticleUseCase.Error): Exception()

    @ExceptionHandler(value = [ShowArticleUseCaseErrorException::class])
    fun onShowArticleUseCaseErrorException(e: ShowArticleUseCaseErrorException): ResponseEntity<GenericErrorModel> =
        when(val error = e.error){
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
                HttpStatus.FORBIDDEN
            )
        }
}