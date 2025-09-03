package com.masakaya.infrastructure.persistence

import com.masakaya.service.repository.CompanyRepository
import com.masakaya.jooq.generated.tables.Companies.Companion.COMPANIES
import com.masakaya.jooq.generated.tables.pojos.Companies
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 企業リポジトリ実装
 * Persistence Layer - jOOQを使用したデータアクセス実装
 */
@Repository
class CompanyRepositoryImpl(private val dsl: DSLContext) : CompanyRepository {
    
    override fun findAll(): List<Companies> {
        return dsl.selectFrom(COMPANIES)
            .orderBy(COMPANIES.CREATED_AT.desc())
            .fetchInto(Companies::class.java)
    }
    
    override fun findById(id: Long): Companies? {
        return dsl.selectFrom(COMPANIES)
            .where(COMPANIES.ID.eq(id))
            .fetchOneInto(Companies::class.java)
    }
    
    override fun findByCode(code: String): Companies? {
        return dsl.selectFrom(COMPANIES)
            .where(COMPANIES.CODE.eq(code))
            .fetchOneInto(Companies::class.java)
    }
    
    override fun findActiveCompanies(): List<Companies> {
        return dsl.selectFrom(COMPANIES)
            .where(COMPANIES.DELETED_AT.isNull)
            .orderBy(COMPANIES.CREATED_AT.desc())
            .fetchInto(Companies::class.java)
    }
    
    override fun create(
        name: String,
        code: String,
        industry: String?,
        website: String?,
        phone: String?,
        address: String?,
        establishedDate: LocalDate?,
        employeeCount: Int?
    ): Companies {
        return dsl.insertInto(COMPANIES)
            .set(COMPANIES.NAME, name)
            .set(COMPANIES.CODE, code)
            .set(COMPANIES.INDUSTRY, industry)
            .set(COMPANIES.WEBSITE, website)
            .set(COMPANIES.PHONE, phone)
            .set(COMPANIES.ADDRESS, address)
            .set(COMPANIES.ESTABLISHED_DATE, establishedDate)
            .set(COMPANIES.EMPLOYEE_COUNT, employeeCount)
            .returning()
            .fetchOneInto(Companies::class.java)!!
    }
    
    override fun update(company: Companies): Companies {
        dsl.update(COMPANIES)
            .set(COMPANIES.NAME, company.name)
            .set(COMPANIES.INDUSTRY, company.industry)
            .set(COMPANIES.WEBSITE, company.website)
            .set(COMPANIES.PHONE, company.phone)
            .set(COMPANIES.ADDRESS, company.address)
            .set(COMPANIES.ESTABLISHED_DATE, company.establishedDate)
            .set(COMPANIES.EMPLOYEE_COUNT, company.employeeCount)
            .where(COMPANIES.ID.eq(company.id))
            .execute()
        
        return findById(company.id!!)!!
    }
    
    override fun delete(id: Long): Boolean {
        return dsl.deleteFrom(COMPANIES)
            .where(COMPANIES.ID.eq(id))
            .execute() > 0
    }
    
    override fun softDelete(id: Long): Boolean {
        return dsl.update(COMPANIES)
            .set(COMPANIES.DELETED_AT, LocalDateTime.now())
            .where(COMPANIES.ID.eq(id))
            .execute() > 0
    }
    
    override fun restore(id: Long): Boolean {
        return dsl.update(COMPANIES)
            .set(COMPANIES.DELETED_AT, null as LocalDateTime?)
            .where(COMPANIES.ID.eq(id))
            .and(COMPANIES.DELETED_AT.isNotNull)
            .execute() > 0
    }
}