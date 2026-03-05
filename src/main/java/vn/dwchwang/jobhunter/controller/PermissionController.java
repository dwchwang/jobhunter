package vn.dwchwang.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.dwchwang.jobhunter.domain.Company;
import vn.dwchwang.jobhunter.domain.Permission;
import vn.dwchwang.jobhunter.domain.response.ResultPaginationDTO;
import vn.dwchwang.jobhunter.service.PermissionService;
import vn.dwchwang.jobhunter.util.annotation.ApiMessage;
import vn.dwchwang.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission) throws IdInvalidException {
        //check exist
        if(this.permissionService.isPermissionExist(permission)){
            throw new IdInvalidException("Permission da ton tai");
        }

        // create new permission
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.create(permission));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission permission) throws IdInvalidException {
        //check exist by id
        if(this.permissionService.fetchById(permission.getId()) == null){
            throw new IdInvalidException("Permission voi id la: " + permission.getId() + " khong ton tai");
        }
        //check exist by module, method, apiPath
        if(this.permissionService.isPermissionExist(permission)){
            throw new IdInvalidException("Permission da ton tai");
        }
        // create new permission
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.update(permission));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) throws IdInvalidException {
        //check exist by id
        if(this.permissionService.fetchById(id) == null){
            throw new IdInvalidException("Permission voi id la: " + id + " khong ton tai");
        }
        this.permissionService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/permissions")
    @ApiMessage("Fetch permissions")
    public ResponseEntity<ResultPaginationDTO> getAllPermissions
            (@Filter Specification<Permission> specification, Pageable pageable) {

        return ResponseEntity.ok(this.permissionService.hanldeGetAllPermissions(specification,pageable));
    }
}
