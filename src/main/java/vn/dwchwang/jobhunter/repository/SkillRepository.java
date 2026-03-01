package vn.dwchwang.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.dwchwang.jobhunter.domain.Skill;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long>, JpaSpecificationExecutor<Skill> {
    Boolean existsByName(String name);
    Optional<Skill> findSkillById(Long id);
    List<Skill> findByIdIn(List<Long> reqSkills);
}
