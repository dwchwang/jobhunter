package vn.dwchwang.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.dwchwang.jobhunter.domain.Skill;
import vn.dwchwang.jobhunter.domain.User;
import vn.dwchwang.jobhunter.domain.response.ResultPaginationDTO;
import vn.dwchwang.jobhunter.repository.SkillRepository;

import java.util.Optional;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Boolean existingByName(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skill handleCreateSkill(Skill skill) {
        this.skillRepository.save(skill);
        return skill;
    }

    public Skill findSkilById(Long id) {
        Optional<Skill> skill = this.skillRepository.findById(id);
        return skill.orElse(null);
    }

    public Skill handleUpdateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public ResultPaginationDTO handleGetAllSkills(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pageSkill = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO userDto = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageSkill.getTotalPages());
        meta.setTotal(pageSkill.getTotalElements());
        userDto.setMeta(meta);

        userDto.setResult(pageSkill.getContent());
        return userDto;
    }

    public void handleDeleteSkill(Long id) {
        //delete job
        Optional<Skill> skill = this.skillRepository.findSkillById(id);
        Skill currentSkill = skill.orElse(null);
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));

        // delete skill
        this.skillRepository.delete(currentSkill);
    }
}
