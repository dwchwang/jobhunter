package vn.dwchwang.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.dwchwang.jobhunter.domain.Skill;
import vn.dwchwang.jobhunter.domain.Subscriber;
import vn.dwchwang.jobhunter.domain.response.ResultPaginationDTO;
import vn.dwchwang.jobhunter.repository.SkillRepository;
import vn.dwchwang.jobhunter.repository.SubscriberRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriberService {

  private final SubscriberRepository subscriberRepository;
  private final SkillRepository skillRepository;

  public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository) {
    this.subscriberRepository = subscriberRepository;
    this.skillRepository = skillRepository;
  }

  public boolean existsByEmail(String email) {
    return this.subscriberRepository.existsByEmail(email);
  }

  public Subscriber create(Subscriber subscriber) {
    // Resolve skills
    if (subscriber.getSkills() != null) {
      List<Long> skillIds = subscriber.getSkills().stream()
          .map(Skill::getId).collect(Collectors.toList());
      List<Skill> skills = this.skillRepository.findAllById(skillIds);
      subscriber.setSkills(skills);
    }
    return this.subscriberRepository.save(subscriber);
  }

  public Subscriber update(Subscriber subscriber) {
    Optional<Subscriber> existing = this.subscriberRepository.findById(subscriber.getId());
    if (existing.isEmpty())
      return null;
    Subscriber dbSubs = existing.get();
    // Update skills
    if (subscriber.getSkills() != null) {
      List<Long> skillIds = subscriber.getSkills().stream()
          .map(Skill::getId).collect(Collectors.toList());
      List<Skill> skills = this.skillRepository.findAllById(skillIds);
      dbSubs.setSkills(skills);
    }
    return this.subscriberRepository.save(dbSubs);
  }

  public Optional<Subscriber> findById(long id) {
    return this.subscriberRepository.findById(id);
  }

  public Subscriber findByEmail(String email) {
    return this.subscriberRepository.findByEmail(email);
  }

  public void delete(long id) {
    this.subscriberRepository.deleteById(id);
  }

  public ResultPaginationDTO fetchAll(Specification<Subscriber> spec, Pageable pageable) {
    Page<Subscriber> page = this.subscriberRepository.findAll(spec, pageable);
    ResultPaginationDTO dto = new ResultPaginationDTO();
    ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
    meta.setPage(pageable.getPageNumber() + 1);
    meta.setPageSize(pageable.getPageSize());
    meta.setPages(page.getTotalPages());
    meta.setTotal(page.getTotalElements());
    dto.setMeta(meta);
    dto.setResult(page.getContent());
    return dto;
  }
}
