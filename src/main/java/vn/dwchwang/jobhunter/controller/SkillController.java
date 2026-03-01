package vn.dwchwang.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.dwchwang.jobhunter.domain.Skill;
import vn.dwchwang.jobhunter.domain.User;
import vn.dwchwang.jobhunter.domain.response.ResultPaginationDTO;
import vn.dwchwang.jobhunter.service.SkillService;
import vn.dwchwang.jobhunter.util.annotation.ApiMessage;
import vn.dwchwang.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("Tao moi skill")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        if(skill.getName()!=null && this.skillService.existingByName(skill.getName())) {
            throw new IdInvalidException("Ten skill " + skill.getName() + " da ton tai, hay thu lai ten khac");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.handleCreateSkill(skill));
    }

    @PutMapping("/skills")
    @ApiMessage("Update skill")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        Skill currentSkill = this.skillService.findSkilById(skill.getId());
        if(currentSkill == null){
            throw new IdInvalidException("Skill id " + skill.getId() + " khong ton tai");
        }
        if(skill.getName()!=null && this.skillService.existingByName(skill.getName())) {
            throw new IdInvalidException("Ten skill " + skill.getName() + " da ton tai, hay thu lai ten khac");
        }
        currentSkill.setName(skill.getName());
        return ResponseEntity.ok(this.skillService.handleUpdateSkill(currentSkill));
    }

    @GetMapping("/skills")
    @ApiMessage("Fetch all skills")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<Skill> spec,
            Pageable pageable
    ) {
        return ResponseEntity.ok(this.skillService.handleGetAllSkills(spec, pageable));
    }

    @DeleteMapping("/skills")
    @ApiMessage("delete skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) throws IdInvalidException {
        //check id
        Skill currentSkill = this.skillService.findSkilById(id);
        if(currentSkill == null){
            throw new IdInvalidException("Skill id " + id + " khong ton tai");
        }
        this.skillService.handleDeleteSkill(id);
        return ResponseEntity.ok().body(null);
    }
}
