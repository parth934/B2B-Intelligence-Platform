package com.parth.leadengine.repository;

import com.parth.leadengine.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    // Spring Boot will automatically create all CRUD methods (Save, Delete, Find)
}