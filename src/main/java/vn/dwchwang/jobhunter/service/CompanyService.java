package vn.dwchwang.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.dwchwang.jobhunter.domain.Company;
import vn.dwchwang.jobhunter.domain.User;
import vn.dwchwang.jobhunter.domain.response.ResultPaginationDTO;
import vn.dwchwang.jobhunter.repository.CompanyRepository;
import vn.dwchwang.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company hanldeCreateCompany(Company company) {
        this.companyRepository.save(company);
        return company;
    }

    public ResultPaginationDTO hanldeGetAllComapnies(Specification<Company> spec,Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(spec,pageable);
        ResultPaginationDTO dto = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageCompany.getTotalPages());
        meta.setTotal(pageCompany.getTotalElements());
        dto.setMeta(meta);
        dto.setResult(pageCompany.getContent());
        return dto;
    }

    public void handleDeleteCompanyById(Long id) {
        Optional<Company> company = this.companyRepository.findById(id);
        if (company.isPresent()) {
            Company com =  company.get();
            List<User> users = this.userRepository.findByCompany(com);
            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }

    public Company handleUpdateCompany(Company company) {
        Optional<Company> existingCompany = this.companyRepository.findById(company.getId());
        Company updatedCompany = existingCompany.orElse(null);
        if(updatedCompany != null) {
            updatedCompany.setName(company.getName());
            updatedCompany.setAddress(company.getAddress());
            updatedCompany.setLogo(company.getLogo());
            updatedCompany.setDescription(company.getDescription());

            return this.companyRepository.save(updatedCompany);
        }

        return null;
    }

    public Optional<Company> findById(long id) {
        return this.companyRepository.findById(id);
    }
}
