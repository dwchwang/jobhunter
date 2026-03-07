package vn.dwchwang.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.dwchwang.jobhunter.domain.Company;
import vn.dwchwang.jobhunter.domain.response.ResultPaginationDTO;
import vn.dwchwang.jobhunter.service.CompanyService;
import vn.dwchwang.jobhunter.util.annotation.ApiMessage;
import vn.dwchwang.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
  private final CompanyService companyService;

  public CompanyController(CompanyService companyService) {
    this.companyService = companyService;
  }

  @PostMapping("/companies")
  @ApiMessage("Create a company")
  public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(this.companyService.hanldeCreateCompany(company));
  }

  @GetMapping("/companies")
  @ApiMessage("Fetch all companies")
  public ResponseEntity<ResultPaginationDTO> getAllCompanies(
      @Filter Specification<Company> specification, Pageable pageable) {
    return ResponseEntity.ok(this.companyService.hanldeGetAllComapnies(specification, pageable));
  }

  @GetMapping("/companies/{id}")
  @ApiMessage("Fetch company by id")
  public ResponseEntity<Company> getCompanyById(@PathVariable Long id) throws IdInvalidException {
    Optional<Company> company = this.companyService.findById(id);
    if (company.isEmpty()) {
      throw new IdInvalidException("Company id " + id + " không tồn tại");
    }
    return ResponseEntity.ok(company.get());
  }

  @PutMapping("/companies")
  @ApiMessage("Update a company")
  public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company company) {
    return ResponseEntity.ok(this.companyService.handleUpdateCompany(company));
  }

  @DeleteMapping("/companies/{id}")
  @ApiMessage("Delete a company")
  public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
    this.companyService.handleDeleteCompanyById(id);
    return ResponseEntity.ok(null);
  }
}
