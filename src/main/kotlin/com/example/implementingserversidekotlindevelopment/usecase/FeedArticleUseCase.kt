package com.example.implementingserversidekotlindevelopment.usecase

import com.example.implementingserversidekotlindevelopment.domain.ArticleRepository
import com.example.implementingserversidekotlindevelopment.domain.CreatedArticle
import org.springframework.stereotype.Service

interface FeedArticleUseCase {
    data class CreatedArticles(
        val createdArticles: List<CreatedArticle>,
        val count: Int
    )

    fun execute(): CreatedArticles = throw NotImplementedError()
}

@Service
class FeedArticleUseCaseImpl(val articleRepository: ArticleRepository) : FeedArticleUseCase {
    override fun execute(): FeedArticleUseCase.CreatedArticles {
        val selectedArticles = articleRepository.all()
        return FeedArticleUseCase.CreatedArticles(
            createdArticles = selectedArticles,
            count = selectedArticles.size
        )
    }
}
