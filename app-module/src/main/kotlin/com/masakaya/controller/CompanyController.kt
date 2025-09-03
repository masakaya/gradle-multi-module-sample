package com.masakaya.controller

import com.masakaya.service.CompanyService
import com.masakaya.jooq.generated.tables.pojos.Companies
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/companies")
@ConditionalOnProperty(name = ["app.demo-controller.enabled"], havingValue = "true")
class CompanyController(private val companyService: CompanyService) {
    
    @GetMapping
    fun getAllCompanies(): ResponseEntity<List<Companies>> {
        return ResponseEntity.ok(companyService.getAllCompanies())
    }
    
    @GetMapping("/active")
    fun getActiveCompanies(): ResponseEntity<List<Companies>> {
        return ResponseEntity.ok(companyService.getActiveCompanies())
    }
    
    @GetMapping("/{id}")
    fun getCompanyById(@PathVariable id: Long): ResponseEntity<Companies> {
        val company = companyService.getCompanyById(id)
        return if (company != null) {
            ResponseEntity.ok(company)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @GetMapping("/code/{code}")
    fun getCompanyByCode(@PathVariable code: String): ResponseEntity<Companies> {
        val company = companyService.getCompanyByCode(code)
        return if (company != null) {
            ResponseEntity.ok(company)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @PostMapping
    fun createCompany(@RequestBody request: CreateCompanyRequest): ResponseEntity<Companies> {
        return try {
            val company = companyService.createCompany(
                name = request.name,
                code = request.code,
                industry = request.industry,
                website = request.website,
                phone = request.phone,
                address = request.address,
                establishedDate = request.establishedDate,
                employeeCount = request.employeeCount
            )
            ResponseEntity.status(HttpStatus.CREATED).body(company)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }
    
    @PutMapping("/{id}")
    fun updateCompany(
        @PathVariable id: Long,
        @RequestBody request: UpdateCompanyRequest
    ): ResponseEntity<Companies> {
        return try {
            val company = companyService.updateCompany(
                id = id,
                name = request.name,
                industry = request.industry,
                website = request.website,
                phone = request.phone,
                address = request.address,
                establishedDate = request.establishedDate,
                employeeCount = request.employeeCount
            )
            ResponseEntity.ok(company)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        } catch (e: IllegalStateException) {
            ResponseEntity.badRequest().build()
        }
    }
    
    @DeleteMapping("/{id}")
    fun deleteCompany(@PathVariable id: Long): ResponseEntity<Void> {
        return try {
            val deleted = companyService.deleteCompany(id)
            if (deleted) {
                ResponseEntity.noContent().build()
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }
    
    @PatchMapping("/{id}/soft-delete")
    fun softDeleteCompany(@PathVariable id: Long): ResponseEntity<Void> {
        return try {
            val deleted = companyService.softDeleteCompany(id)
            if (deleted) {
                ResponseEntity.noContent().build()
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        } catch (e: IllegalStateException) {
            ResponseEntity.badRequest().build()
        }
    }
    
    @PatchMapping("/{id}/restore")
    fun restoreCompany(@PathVariable id: Long): ResponseEntity<Void> {
        return try {
            val restored = companyService.restoreCompany(id)
            if (restored) {
                ResponseEntity.noContent().build()
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        } catch (e: IllegalStateException) {
            ResponseEntity.badRequest().build()
        }
    }
}

data class CreateCompanyRequest(
    val name: String,
    val code: String,
    val industry: String? = null,
    val website: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val establishedDate: LocalDate? = null,
    val employeeCount: Int? = null
)

data class UpdateCompanyRequest(
    val name: String? = null,
    val industry: String? = null,
    val website: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val establishedDate: LocalDate? = null,
    val employeeCount: Int? = null
)