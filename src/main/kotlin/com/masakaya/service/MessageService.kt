package com.masakaya.service

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.*

/**
 * メッセージサービス
 * 国際化対応のメッセージ取得サービス
 */
@Service
class MessageService(private val messageSource: MessageSource) {
    
    /**
     * 現在のロケールでメッセージを取得
     */
    fun getMessage(code: String, vararg args: Any?): String {
        return getMessage(code, LocaleContextHolder.getLocale(), *args)
    }
    
    /**
     * 指定ロケールでメッセージを取得
     */
    fun getMessage(code: String, locale: Locale, vararg args: Any?): String {
        return try {
            messageSource.getMessage(code, args, locale)
        } catch (e: Exception) {
            // メッセージが見つからない場合はコードをそのまま返す
            code
        }
    }
    
    /**
     * デフォルトメッセージ付きでメッセージを取得
     */
    fun getMessageOrDefault(code: String, defaultMessage: String, vararg args: Any?): String {
        return try {
            messageSource.getMessage(code, args, LocaleContextHolder.getLocale())
        } catch (e: Exception) {
            defaultMessage
        }
    }
    
    // === バリデーションメッセージ ===
    
    fun getValidationMessage(code: String, vararg args: Any?): String {
        return getMessage("validation.$code", *args)
    }
    
    fun getCompanyValidationMessage(code: String, vararg args: Any?): String {
        return getMessage("validation.company.$code", *args)
    }
    
    // === ビジネスメッセージ ===
    
    fun getBusinessMessage(code: String, vararg args: Any?): String {
        return getMessage("business.$code", *args)
    }
    
    fun getCompanyBusinessMessage(code: String, vararg args: Any?): String {
        return getMessage("business.company.$code", *args)
    }
    
    // === エラーメッセージ ===
    
    fun getErrorMessage(code: String, vararg args: Any?): String {
        return getMessage("error.$code", *args)
    }
    
    fun getSystemErrorMessage(code: String, vararg args: Any?): String {
        return getMessage("error.system.$code", *args)
    }
    
    fun getBusinessErrorMessage(code: String, vararg args: Any?): String {
        return getMessage("error.business.$code", *args)
    }
    
    // === 現在のロケール情報 ===
    
    /**
     * 現在のロケールを取得
     */
    fun getCurrentLocale(): Locale {
        return LocaleContextHolder.getLocale()
    }
    
    /**
     * 現在の言語コードを取得
     */
    fun getCurrentLanguage(): String {
        return getCurrentLocale().language
    }
    
    /**
     * 日本語かどうかを判定
     */
    fun isJapanese(): Boolean {
        return getCurrentLanguage() == "ja"
    }
    
    /**
     * 英語かどうかを判定
     */
    fun isEnglish(): Boolean {
        return getCurrentLanguage() == "en"
    }
}