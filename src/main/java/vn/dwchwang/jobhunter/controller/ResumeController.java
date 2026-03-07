package vn.dwchwang.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.dwchwang.jobhunter.domain.Resume;
import vn.dwchwang.jobhunter.domain.User;
import vn.dwchwang.jobhunter.domain.response.ResultPaginationDTO;
import vn.dwchwang.jobhunter.domain.response.Resume.ResCreateResumeDTO;
import vn.dwchwang.jobhunter.domain.response.Resume.ResGetResumeDTO;
import vn.dwchwang.jobhunter.domain.response.Resume.ResUpdateResumeDTO;
import vn.dwchwang.jobhunter.service.ResumeService;
import vn.dwchwang.jobhunter.service.UserService;
import vn.dwchwang.jobhunter.util.SecurityUtil;
import vn.dwchwang.jobhunter.util.annotation.ApiMessage;
import vn.dwchwang.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
  public final ResumeService resumeService;
  public final UserService userService;

  public ResumeController(ResumeService resumeService, UserService userService) {
    this.resumeService = resumeService;
    this.userService = userService;
  }

  @PostMapping("/resumes")
  @ApiMessage("Create new resume")
  public ResponseEntity<ResCreateResumeDTO> createResume(@Valid @RequestBody Resume resume) throws IdInvalidException {
    boolean isExitedId = this.resumeService.checkResumeByUserAndJob(resume);
    if (!isExitedId) {
      throw new IdInvalidException("User id/ Job id không tồn tại");
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.createResume(resume));
  }

  @PutMapping("/resumes")
  @ApiMessage("Update resume")
  public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume resume) throws IdInvalidException {
    Optional<Resume> reqResume = this.resumeService.fineResumeById(resume.getId());
    if (reqResume.isEmpty()) {
      throw new IdInvalidException("Id resume " + resume.getId() + " không tồn tại");
    }
    Resume updateResume = reqResume.get();
    updateResume.setStatus(resume.getStatus());
    return ResponseEntity.ok().body(this.resumeService.updateResume(updateResume));
  }

  @DeleteMapping("/resumes/{id}")
  @ApiMessage("Delete resume")
  public ResponseEntity<Void> deleteResume(@PathVariable Long id) throws IdInvalidException {
    Optional<Resume> reqResume = this.resumeService.fineResumeById(id);
    if (reqResume.isEmpty()) {
      throw new IdInvalidException("Id resume " + id + " không tồn tại");
    }
    this.resumeService.deleteResume(id);
    return ResponseEntity.ok().body(null);
  }

  @GetMapping("/resumes/{id}")
  @ApiMessage("Get Resume by Id")
  public ResponseEntity<ResGetResumeDTO> getResumes(@PathVariable Long id) throws IdInvalidException {
    Optional<Resume> reqResume = this.resumeService.fineResumeById(id);
    if (reqResume.isEmpty()) {
      throw new IdInvalidException("Id resume " + id + " không tồn tại");
    }
    return ResponseEntity.ok().body(this.resumeService.getResumeById(reqResume.get()));
  }

  @GetMapping("/resumes")
  @ApiMessage("fetch all resume")
  public ResponseEntity<ResultPaginationDTO> getAllResumes(
      @Filter Specification<Resume> specification, Pageable pageable) {
    return ResponseEntity.ok(this.resumeService.hanldeGetAllComapnies(specification, pageable));
  }

  @PostMapping("/resumes/by-user")
  @ApiMessage("Get resumes by user")
  public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable) {
    String email = SecurityUtil.getCurrentUserLogin().orElse("");
    User user = this.userService.handleGetUserByUserName(email);
    if (user == null) {
      return ResponseEntity.ok(new ResultPaginationDTO());
    }
    return ResponseEntity.ok(this.resumeService.fetchResumesByUser(user, pageable));
  }
}
