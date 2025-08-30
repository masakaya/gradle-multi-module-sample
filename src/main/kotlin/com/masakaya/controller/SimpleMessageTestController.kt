package com.masakaya.controller

import com.masakaya.service.MessageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * 簡易メッセージテスト用コントローラー（jOOQ不要）
 * 国際化機能のテスト・デモ用
 */
@RestController
@RequestMapping("/api/simple-messages")
@ConditionalOnProperty(name = ["app.demo-controller.enabled"], havingValue = "true")
class SimpleMessageTestController(private val messageService: MessageService) {
    
    /**
     * 現在のロケール情報を取得
     */
    @GetMapping("/locale")
    fun getCurrentLocale(): Map<String, Any> {
        return mapOf(
            "locale" to messageService.getCurrentLocale().toString(),
            "language" to messageService.getCurrentLanguage(),
            "isJapanese" to messageService.isJapanese(),
            "isEnglish" to messageService.isEnglish()
        )
    }
    
    /**
     * バリデーションメッセージのテスト
     */
    @GetMapping("/validation")
    fun getValidationMessages(@RequestParam(defaultValue = "TEST001") code: String): Map<String, String> {
        return mapOf(
            "company.name.required" to messageService.getCompanyValidationMessage("name.required"),
            "company.code.duplicate" to messageService.getCompanyValidationMessage("code.duplicate", code),
            "company.employee.count.positive" to messageService.getCompanyValidationMessage("employee.count.positive")
        )
    }
    
    /**
     * ビジネスメッセージのテスト
     */
    @GetMapping("/business")
    fun getBusinessMessages(@RequestParam(defaultValue = "テスト企業") companyName: String): Map<String, String> {
        return mapOf(
            "company.created" to messageService.getCompanyBusinessMessage("created", companyName),
            "company.updated" to messageService.getCompanyBusinessMessage("updated", companyName),
            "company.deleted" to messageService.getCompanyBusinessMessage("deleted", companyName)
        )
    }
    
    /**
     * エラーメッセージのテスト
     */
    @GetMapping("/errors")
    fun getErrorMessages(@RequestParam(defaultValue = "123") id: String): Map<String, String> {
        return mapOf(
            "company.not.found" to messageService.getBusinessErrorMessage("company.not.found", id),
            "system.general" to messageService.getSystemErrorMessage("general"),
            "system.database" to messageService.getSystemErrorMessage("database")
        )
    }
    
    /**
     * 指定ロケールでメッセージを取得
     */
    @GetMapping("/test/{messageCode}")
    fun getMessageByLocale(
        @PathVariable messageCode: String,
        @RequestParam(required = false) locale: String?,
        @RequestParam(required = false) args: List<String>?
    ): Map<String, Any> {
        val targetLocale = if (locale != null) Locale.forLanguageTag(locale) else messageService.getCurrentLocale()
        val messageArgs = args?.toTypedArray() ?: emptyArray()
        
        return mapOf(
            "messageCode" to messageCode,
            "locale" to targetLocale.toString(),
            "args" to (args ?: emptyList()),
            "message" to messageService.getMessage(messageCode, targetLocale, *messageArgs)
        )
    }
    
    /**
     * 全言語でのメッセージ比較
     */
    @GetMapping("/compare/{messageCode}")
    fun compareMessages(
        @PathVariable messageCode: String,
        @RequestParam(required = false) args: List<String>?
    ): Map<String, String> {
        val messageArgs = args?.toTypedArray() ?: emptyArray()
        
        return mapOf(
            "japanese" to messageService.getMessage(messageCode, Locale.JAPANESE, *messageArgs),
            "english" to messageService.getMessage(messageCode, Locale.ENGLISH, *messageArgs),
            "korean" to messageService.getMessage(messageCode, Locale("ko"), *messageArgs)
        )
    }
}