package com.example.implementingserversidekotlindevelopment.domain

import arrow.core.Validated
import arrow.core.invalid
import arrow.core.invalidNel
import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import net.jqwik.api.ArbitrarySupplier
import net.jqwik.api.ForAll
import net.jqwik.api.From
import net.jqwik.api.Property
import net.jqwik.api.constraints.NotBlank
import net.jqwik.api.constraints.StringLength
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TitleTest {
    class New {
        @Property
        fun `正常系-長さが有効な場合、検証済の Title が戻り値`(
            @ForAll @From(supplier = TitleValidRange::class) validString: String
        ) {
            val actual = Title.new(validString)
            val expected = Title.newWithoutValidation(validString)
            when(actual){
                is Validated.Invalid -> assert(false){"原因: ${actual.value}"}
                is Validated.Valid -> assertThat(actual.value.value).isEqualTo(expected.value)
            }
        }

        @Property
        fun `異常系-長さが長すぎる場合、バリデーションエラーが戻り値`(
            @ForAll @NotBlank @StringLength(min = 33) tooLongString: String
        ){
            val maximumLength = 32
            val actual = Title.new(tooLongString)

            val expected = Title.CreationError.TooLong(maximumLength).invalidNel()
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Test
    fun `異常系-null の場合、バリデーションエラーが戻り値`() {
        val actual = Title.new(null)
        val expected = Title.CreationError.Required.invalidNel()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `異常系-空白の場合、バリデーションエラーが戻り値`() {
        val actual = Title.new(" ")
        val expected = Title.CreationError.Required.invalidNel()
        assertThat(actual).isEqualTo(expected)
    }

    class TitleValidRange: ArbitrarySupplier<String>{
        override fun get(): Arbitrary<String> =
            Arbitraries.strings()
                .ofMinLength(1)
                .ofMaxLength(32)
                .filter{it.isNotBlank()}
    }
}