package com.masakaya.service.repository

import com.masakaya.jooq.generated.tables.pojos.Companies
import java.time.LocalDate

/**
 * 企業リポジトリインターフェース
 * Infrastructure Layer のインターフェース定義
 */
interface CompanyRepository {
    
    /**
     * 全ての企業を取得
     */
    fun findAll(): List<Companies>
    
    /**
     * IDで企業を取得
     */
    fun findById(id: Long): Companies?
    
    /**
     * 企業コードで企業を取得
     */
    fun findByCode(code: String): Companies?
    
    /**
     * 削除されていない企業のみを取得
     */
    fun findActiveCompanies(): List<Companies>
    
    /**
     * 企業を作成
     */
    fun create(
        name: String,
        code: String,
        industry: String? = null,
        website: String? = null,
        phone: String? = null,
        address: String? = null,
        establishedDate: LocalDate? = null,
        employeeCount: Int? = null
    ): Companies
    
    /**
     * 企業を更新
     */
    fun update(company: Companies): Companies
    
    /**
     * 企業を物理削除
     */
    fun delete(id: Long): Boolean
    
    /**
     * 企業を論理削除（deleted_atを設定）
     */
    fun softDelete(id: Long): Boolean
    
    /**
     * 論理削除された企業を復活
     */
    fun restore(id: Long): Boolean
}