package com.example.implementingserversidekotlindevelopment.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.stream.Stream

class CreatedArticleTest {
    data class TestCase(
        val title: String,
        val createdArticle: CreatedArticle,
        val otherCreatedArticle: CreatedArticle,
        val expected: Boolean
    )

    @TestFactory
    fun articleEqualTest(): Stream<DynamicNode>{
        return Stream.of(
            TestCase(
                "slugが一致する場合、他のプロパティが異なっていてもtrueを返すこと",
                CreatedArticle.newWithoutValidation(
                    slug = Slug.newWithoutValidation("dummy-slug"),
                    title = Title.newWithoutValidation("dummy-title"),
                    description = Description.newWithoutValidation("dummy-description"),
                    body = Body.newWithoutValidation("dummy-body")
                ),
                CreatedArticle.newWithoutValidation(
                    slug = Slug.newWithoutValidation("dummy-slug"),
                    title = Title.newWithoutValidation("dummy-other-title"),
                    description = Description.newWithoutValidation("dummy-other-description"),
                    body = Body.newWithoutValidation("dummy-other-body")
                ),
                true
            ),
            TestCase(
                "slugが一致しない場合、falseを返すこと",
                CreatedArticle.newWithoutValidation(
                    slug = Slug.newWithoutValidation("dummy-slug"),
                    title = Title.newWithoutValidation("dummy-title"),
                    description = Description.newWithoutValidation("dummy-description"),
                    body = Body.newWithoutValidation("dummy-body")
                ),
                CreatedArticle.newWithoutValidation(
                    slug = Slug.newWithoutValidation("dummy-other-slug"),
                    title = Title.newWithoutValidation("dummy-other-title"),
                    description = Description.newWithoutValidation("dummy-other-description"),
                    body = Body.newWithoutValidation("dummy-other-body")
                ),
                false
            )
        ).map { testCase ->
            dynamicTest(testCase.title){
                assertThat(testCase.createdArticle == testCase.otherCreatedArticle).isEqualTo(testCase.expected)
            }
        }
    }
}