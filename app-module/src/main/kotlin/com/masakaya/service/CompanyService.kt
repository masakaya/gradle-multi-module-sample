package com.masakaya.service

import com.masakaya.service.repository.CompanyRepository
import com.masakaya.jooq.generated.tables.pojos.Companies
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * 企業サービス
 * Service Layer - ビジネスロジックを実装
 */
@Service
@Transactional
class CompanyService(
    private val companyRepository: CompanyRepository,
    private val messageService: MessageService
) {
    
    /**
     * 全ての企業を取得
     */
    @Transactional(readOnly = true)
    fun getAllCompanies(): List<Companies> {
        return companyRepository.findAll()
    }
    
    /**
     * IDで企業を取得
     */
    @Transactional(readOnly = true)
    fun getCompanyById(id: Long): Companies? {
        return companyRepository.findById(id)
    }
    
    /**
     * 企業コードで企業を取得
     */
    @Transactional(readOnly = true)
    fun getCompanyByCode(code: String): Companies? {
        return companyRepository.findByCode(code)
    }
    
    /**
     * 削除されていない企業のみを取得
     */
    @Transactional(readOnly = true)
    fun getActiveCompanies(): List<Companies> {
        return companyRepository.findActiveCompanies()
    }
    
    /**
     * 企業を作成
     * ビジネスルール: 企業コードの重複チェック
     */
    fun createCompany(
        name: String,
        code: String,
        industry: String? = null,
        website: String? = null,
        phone: String? = null,
        address: String? = null,
        establishedDate: LocalDate? = null,
        employeeCount: Int? = null
    ): Companies {
        // ビジネスルール: 企業コードの重複チェック
        val existingCompany = companyRepository.findByCode(code)
        if (existingCompany != null) {
            throw IllegalArgumentException(
                messageService.getCompanyValidationMessage("code.duplicate", code)
            )
        }
        
        // ビジネスルール: 従業員数の妥当性チェック
        if (employeeCount != null && employeeCount < 0) {
            throw IllegalArgumentException(
                messageService.getCompanyValidationMessage("employee.count.positive")
            )
        }
        
        return companyRepository.create(
            name = name,
            code = code,
            industry = industry,
            website = website,
            phone = phone,
            address = address,
            establishedDate = establishedDate,
            employeeCount = employeeCount
        )
    }
    
    /**
     * 企業を更新
     * ビジネスルール: 削除済み企業は更新不可
     */
    fun updateCompany(
        id: Long,
        name: String? = null,
        industry: String? = null,
        website: String? = null,
        phone: String? = null,
        address: String? = null,
        establishedDate: LocalDate? = null,
        employeeCount: Int? = null
    ): Companies {
        val existing = companyRepository.findById(id)
            ?: throw IllegalArgumentException(
                messageService.getBusinessErrorMessage("company.not.found", id)
            )
        
        // ビジネスルール: 削除済み企業は更新不可
        if (existing.deletedAt != null) {
            throw IllegalStateException(
                messageService.getBusinessErrorMessage("company.cannot.update.deleted")
            )
        }
        
        // ビジネスルール: 従業員数の妥当性チェック
        if (employeeCount != null && employeeCount < 0) {
            throw IllegalArgumentException(
                messageService.getCompanyValidationMessage("employee.count.positive")
            )
        }
        
        val updated = Companies(
            id = existing.id,
            name = name ?: existing.name,
            code = existing.code, // コードは変更不可
            industry = industry ?: existing.industry,
            website = website ?: existing.website,
            phone = phone ?: existing.phone,
            address = address ?: existing.address,
            establishedDate = establishedDate ?: existing.establishedDate,
            employeeCount = employeeCount ?: existing.employeeCount,
            deletedAt = existing.deletedAt,
            createdAt = existing.createdAt,
            updatedAt = existing.updatedAt
        )
        
        return companyRepository.update(updated)
    }
    
    /**
     * 企業を物理削除
     * ビジネスルール: 関連データの整合性チェック（実際の実装では必要）
     */
    fun deleteCompany(id: Long): Boolean {
        val existing = companyRepository.findById(id)
            ?: throw IllegalArgumentException(
                messageService.getBusinessErrorMessage("company.not.found", id)
            )
        
        // TODO: 実際の実装では関連するチームやユーザーの存在チェックが必要
        // if (hasRelatedTeams(id)) {
        //     throw IllegalStateException("関連するチームが存在するため削除できません")
        // }
        
        return companyRepository.delete(id)
    }
    
    /**
     * 企業を論理削除
     */
    fun softDeleteCompany(id: Long): Boolean {
        val existing = companyRepository.findById(id)
            ?: throw IllegalArgumentException(
                messageService.getBusinessErrorMessage("company.not.found", id)
            )
        
        if (existing.deletedAt != null) {
            throw IllegalStateException(
                messageService.getBusinessErrorMessage("company.already.deleted")
            )
        }
        
        return companyRepository.softDelete(id)
    }
    
    /**
     * 論理削除された企業を復活
     */
    fun restoreCompany(id: Long): Boolean {
        val existing = companyRepository.findById(id)
            ?: throw IllegalArgumentException(
                messageService.getBusinessErrorMessage("company.not.found", id)
            )
        
        if (existing.deletedAt == null) {
            throw IllegalStateException(
                messageService.getBusinessErrorMessage("company.not.deleted")
            )
        }
        
        return companyRepository.restore(id)
    }
}