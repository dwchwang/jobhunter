package vn.dwchwang.jobhunter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.dwchwang.jobhunter.domain.Permission;
import vn.dwchwang.jobhunter.domain.Role;
import vn.dwchwang.jobhunter.domain.response.ResultPaginationDTO;
import vn.dwchwang.jobhunter.repository.PermissionRepository;
import vn.dwchwang.jobhunter.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean existByName(String name){
        return roleRepository.existsByName(name);
    }

    public Role fetchById(Long id){
        Optional<Role> role = roleRepository.findById(id);
        return role.orElse(null);
    }

    public Role create(Role role){
        // check permissions
        if(role.getPermissions() != null) {
            List<Long> permissions = role.getPermissions().stream().map(Permission::getId).toList();
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(permissions);
            role.setPermissions(dbPermissions);
        }
        return this.roleRepository.save(role);
    }

    public Role update(Role role) {
        Role dbRole = this.fetchById(role.getId());
        //check permissions
        if(role.getPermissions() != null) {
            List<Long> permissions = role.getPermissions().stream().map(Permission::getId).toList();
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(permissions);
            dbRole.setPermissions(dbPermissions);
        }

        dbRole.setName(role.getName());
        dbRole.setDescription(role.getDescription());
        dbRole.setActive(role.isActive());
        dbRole.setPermissions(role.getPermissions());

        dbRole = this.roleRepository.save(dbRole);
        return dbRole;

    }

    public void delete(long id) {
        this.roleRepository.deleteById(id);
    }

    public ResultPaginationDTO hanldeGetAllRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO dto = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageRole.getTotalPages());
        meta.setTotal(pageRole.getTotalElements());
        dto.setMeta(meta);
        dto.setResult(pageRole.getContent());
        return dto;
    }
}
