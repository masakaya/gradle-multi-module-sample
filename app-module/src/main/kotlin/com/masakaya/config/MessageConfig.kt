package com.masakaya.config

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * 国際化(i18n)設定
 * MessageSource と Locale解決の設定
 */
@Configuration
class MessageConfig : WebMvcConfigurer {

    /**
     * メッセージソースの設定
     * messages_*.properties ファイルからメッセージを読み込み
     */
    @Bean
    fun messageSource(): MessageSource {
        val messageSource = ResourceBundleMessageSource()
        messageSource.setBasenames(
            "messages/validation",      // バリデーションメッセージ
            "messages/business",        // ビジネスメッセージ  
            "messages/error"            // エラーメッセージ
        )
        messageSource.setDefaultEncoding("UTF-8")
        messageSource.setUseCodeAsDefaultMessage(false)  // キーが見つからない場合は例外
        messageSource.setCacheSeconds(0)  // 開発時はキャッシュ無効（本番では有効にする）
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setFallbackToSystemLocale(false)  // システムロケールにフォールバックしない
        return messageSource
    }

    /**
     * ロケール解決の設定
     * Accept-Languageヘッダーからロケールを解決
     */
    @Bean
    fun localeResolver(): LocaleResolver {
        val resolver = AcceptHeaderLocaleResolver()
        resolver.setSupportedLocales(listOf(
            Locale.JAPANESE,
            Locale.ENGLISH,
            Locale("ko")  // 韓国語
        ))
        resolver.setDefaultLocale(Locale.JAPANESE)  // デフォルトは日本語
        return resolver
    }

    /**
     * ロケール変更インターセプター
     * ?lang=en のようなパラメータでロケールを変更可能
     */
    @Bean
    fun localeChangeInterceptor(): LocaleChangeInterceptor {
        val interceptor = LocaleChangeInterceptor()
        interceptor.paramName = "lang"
        return interceptor
    }

    /**
     * ロケール変更インターセプターを登録
     */
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(localeChangeInterceptor())
    }
}