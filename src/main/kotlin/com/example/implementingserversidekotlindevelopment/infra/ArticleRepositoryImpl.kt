package com.example.implementingserversidekotlindevelopment.infra

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.example.implementingserversidekotlindevelopment.domain.*
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class ArticleRepositoryImpl(val namedParameterJdbcTemplate: NamedParameterJdbcTemplate):ArticleRepository{

    override fun findBySlug(slug: Slug): Either<ArticleRepository.FindBySlugError, CreatedArticle> {
        val sql = """
            SELECT
                articles.slug
                , articles.title
                , articles.body
                , articles.description
            FROM
                articles
            WHERE
                slug = :slug
        """.trimIndent()
        val articleMapList =
            namedParameterJdbcTemplate.queryForList(sql, MapSqlParameterSource().addValue("slug", slug.value))

        if (articleMapList.isEmpty()){
            return ArticleRepository.FindBySlugError.NotFound(slug = slug).left()
        }

        val articleMap = articleMapList.first()

        return CreatedArticle.newWithoutValidation(
            slug = Slug.newWithoutValidation(articleMap["slug"].toString()),
            title = Title.newWithoutValidation(articleMap["title"].toString()),
            description = Description.newWithoutValidation(articleMap["description"].toString()),
            body = Body.newWithoutValidation(articleMap["body"].toString())
        ).right()
    }

    override fun create(createdArticle: CreatedArticle): Either<ArticleRepository.CreateArticleError, CreatedArticle> {
        val sql = """
            INSERT INTO articles (
                    slug
                    , title
                    , body
                    , description
                )
            VALUES (
                :slug
                , :title
                , :body
                , :description
            )
        """.trimIndent()
        namedParameterJdbcTemplate.update(
            sql,
            MapSqlParameterSource()
                .addValue("slug", createdArticle.slug.value)
                .addValue("title", createdArticle.title.value)
                .addValue("body", createdArticle.body.value)
                .addValue("description", createdArticle.description.value)
        )

        return createdArticle.right()
    }
}