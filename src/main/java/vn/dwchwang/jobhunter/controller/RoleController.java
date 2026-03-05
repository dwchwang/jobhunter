package vn.dwchwang.jobhunter.controller;


import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.dwchwang.jobhunter.domain.Permission;
import vn.dwchwang.jobhunter.domain.Role;
import vn.dwchwang.jobhunter.domain.response.ResultPaginationDTO;
import vn.dwchwang.jobhunter.repository.RoleRepository;
import vn.dwchwang.jobhunter.service.RoleService;
import vn.dwchwang.jobhunter.util.annotation.ApiMessage;
import vn.dwchwang.jobhunter.util.error.IdInvalidException;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> createRole(@RequestBody Role role) throws IdInvalidException {
        //check name
        if(this.roleService.existByName(role.getName())){
            throw new IdInvalidException("role voi name " + role.getName() + "da ton tai");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(role));
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> updateRole(@RequestBody Role role) throws IdInvalidException {
        //check id
        if(this.roleService.fetchById(role.getId()) == null){
            throw new IdInvalidException("role voi name " + role.getId() + "khong ton tai");
        }

        //check name
        if(this.roleService.existByName(role.getName())){
            throw new IdInvalidException("role voi name " + role.getName() + "da ton tai");
        }
        return ResponseEntity.ok().body(this.roleService.update(role));
    }

    @DeleteMapping("/roles")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> updateRole(@PathVariable long id) throws IdInvalidException {
        //check id
        if(this.roleService.fetchById(id) == null){
            throw new IdInvalidException("role voi name " + id + "khong ton tai");
        }

        this.roleService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/roles")
    @ApiMessage("Fetch roles")
    public ResponseEntity<ResultPaginationDTO> getAllPermissions
            (@Filter Specification<Role> specification, Pageable pageable) {

        return ResponseEntity.ok(this.roleService.hanldeGetAllRoles(specification,pageable));
    }
}
