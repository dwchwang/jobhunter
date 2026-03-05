package vn.dwchwang.jobhunter.service;


import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.dwchwang.jobhunter.domain.Company;
import vn.dwchwang.jobhunter.domain.Permission;
import vn.dwchwang.jobhunter.domain.response.ResultPaginationDTO;
import vn.dwchwang.jobhunter.repository.PermissionRepository;

import java.util.Optional;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission permission) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(
                permission.getModule(),
                permission.getApiPath(),
                permission.getMethod()
        );
    }

    public Permission create(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public Permission fetchById(Long id) {
        Optional<Permission> permission = this.permissionRepository.findById(id);
        return permission.orElse(null);
    }


    public Permission update(@Valid Permission permission) {
        Permission existingPermission = this.permissionRepository.findById(permission.getId()).orElse(null);
        if (existingPermission != null) {
            existingPermission.setName(permission.getName());
            existingPermission.setApiPath(permission.getApiPath());
            existingPermission.setMethod(permission.getMethod());
            existingPermission.setModule(permission.getModule());
            //update
            existingPermission = this.permissionRepository.save(existingPermission);
            return existingPermission;
        }
        return null;
    }

    public void delete(Long id) {
        //delete permission_role
        Optional<Permission> permission = this.permissionRepository.findById(id);
        Permission currPermission = permission.orElse(null);
        currPermission.getRoles().forEach(role -> role.getPermissions().remove(currPermission));

        // delete permission
        this.permissionRepository.delete(currPermission);
    }

    public ResultPaginationDTO hanldeGetAllPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePermission = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO dto = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pagePermission.getTotalPages());
        meta.setTotal(pagePermission.getTotalElements());
        dto.setMeta(meta);
        dto.setResult(pagePermission.getContent());
        return dto;
    }
}
