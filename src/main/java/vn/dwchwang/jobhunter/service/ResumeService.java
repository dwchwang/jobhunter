package vn.dwchwang.jobhunter.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.dwchwang.jobhunter.domain.Company;
import vn.dwchwang.jobhunter.domain.Job;
import vn.dwchwang.jobhunter.domain.Resume;
import vn.dwchwang.jobhunter.domain.User;
import vn.dwchwang.jobhunter.domain.response.ResultPaginationDTO;
import vn.dwchwang.jobhunter.domain.response.Resume.ResCreateResumeDTO;
import vn.dwchwang.jobhunter.domain.response.Resume.ResGetResumeDTO;
import vn.dwchwang.jobhunter.domain.response.Resume.ResUpdateResumeDTO;
import vn.dwchwang.jobhunter.repository.JobRepository;
import vn.dwchwang.jobhunter.repository.ResumeRepository;
import vn.dwchwang.jobhunter.repository.UserRepository;
import vn.dwchwang.jobhunter.util.SecurityUtil;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResumeService {
  private final ResumeRepository resumeRepository;
  private final UserRepository userRepository;
  private final JobRepository jobRepository;

  public ResumeService(ResumeRepository resumeRepository, UserService userService, UserRepository userRepository,
      JobRepository jobRepository) {
    this.resumeRepository = resumeRepository;
    this.userRepository = userRepository;
    this.jobRepository = jobRepository;
  }

  public boolean checkResumeByUserAndJob(Resume resume) {
    // check user id
    if (resume.getUser() == null)
      return false;
    Optional<User> userOptional = this.userRepository.findById(resume.getUser().getId());
    if (userOptional.isEmpty())
      return false;

    // check job id
    if (resume.getJob() == null)
      return false;
    Optional<Job> jobOptional = this.jobRepository.findById(resume.getJob().getId());
    if (jobOptional.isEmpty())
      return false;

    return true;
  }

  public ResCreateResumeDTO createResume(Resume resume) {
    resume = this.resumeRepository.saveAndFlush(resume);
    ResCreateResumeDTO resCreateResumeDTO = new ResCreateResumeDTO();
    resCreateResumeDTO.setId(resume.getId());
    resCreateResumeDTO.setCreatedAt(resume.getCreatedAt());
    resCreateResumeDTO.setCreatedBy(resume.getCreatedBy());
    return resCreateResumeDTO;
  }

  public Optional<Resume> fineResumeById(Long id) {
    return resumeRepository.findById(id);
  }

  public ResUpdateResumeDTO updateResume(Resume updateResume) {
    // updateResume.setUpdatedAt(Instant.now());
    // updateResume.setUpdatedBy(SecurityUtil.getCurrentUserLogin().orElse(""));
    updateResume = this.resumeRepository.save(updateResume);
    ResUpdateResumeDTO resUpdateResumeDTO = new ResUpdateResumeDTO();
    resUpdateResumeDTO.setId(updateResume.getId());
    resUpdateResumeDTO.setUpdatedAt(updateResume.getUpdatedAt());
    resUpdateResumeDTO.setUpdatedBy(updateResume.getUpdatedBy());
    return resUpdateResumeDTO;
  }

  public void deleteResume(Long id) {
    this.resumeRepository.deleteById(id);
  }

  public ResGetResumeDTO getResumeById(Resume resume) {
    ResGetResumeDTO res = new ResGetResumeDTO();
    res.setId(resume.getId());
    res.setEmail(resume.getEmail());
    res.setUrl(resume.getUrl());
    res.setStatus(resume.getStatus());
    res.setCreatedAt(resume.getCreatedAt());
    res.setCreatedBy(resume.getCreatedBy());
    res.setUpdatedAt(resume.getUpdatedAt());
    res.setUpdatedBy(resume.getUpdatedBy());

    res.setUser(new ResGetResumeDTO.UserResume(resume.getUser().getId(), resume.getUser().getName()));
    res.setJob(new ResGetResumeDTO.JobResume(resume.getJob().getId(), resume.getJob().getName()));

    return res;
  }

  public ResultPaginationDTO hanldeGetAllComapnies(Specification<Resume> spec, Pageable pageable) {
    Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
    ResultPaginationDTO dto = new ResultPaginationDTO();
    ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
    meta.setPage(pageable.getPageNumber() + 1);
    meta.setPageSize(pageable.getPageSize());

    meta.setPages(pageResume.getTotalPages());
    meta.setTotal(pageResume.getTotalElements());
    dto.setMeta(meta);

    List<ResGetResumeDTO> resGetResumeDTO = pageResume.getContent()
        .stream()
        .map(item -> this.getResumeById(item))
        .toList();
    dto.setResult(resGetResumeDTO);
    return dto;
  }

  public ResultPaginationDTO fetchResumesByUser(User user, Pageable pageable) {
    List<Resume> resumes = this.resumeRepository.findByUserId(user.getId());
    ResultPaginationDTO dto = new ResultPaginationDTO();
    ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
    meta.setPage(1);
    meta.setPageSize(resumes.size());
    meta.setPages(1);
    meta.setTotal(resumes.size());
    dto.setMeta(meta);
    List<ResGetResumeDTO> result = resumes.stream()
        .map(this::getResumeById)
        .collect(Collectors.toList());
    dto.setResult(result);
    return dto;
  }
}
