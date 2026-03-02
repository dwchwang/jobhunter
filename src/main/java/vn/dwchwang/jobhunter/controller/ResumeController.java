package vn.dwchwang.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.dwchwang.jobhunter.domain.Company;
import vn.dwchwang.jobhunter.domain.Resume;
import vn.dwchwang.jobhunter.domain.response.ResultPaginationDTO;
import vn.dwchwang.jobhunter.domain.response.Resume.ResCreateResumeDTO;
import vn.dwchwang.jobhunter.domain.response.Resume.ResGetResumeDTO;
import vn.dwchwang.jobhunter.domain.response.Resume.ResUpdateResumeDTO;
import vn.dwchwang.jobhunter.service.ResumeService;
import vn.dwchwang.jobhunter.util.annotation.ApiMessage;
import vn.dwchwang.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    public final ResumeService resumeService;
    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }


    @PostMapping("/resumes")
    @ApiMessage("Create new resume")
    public ResponseEntity<ResCreateResumeDTO> createResume(@Valid @RequestBody Resume resume) throws IdInvalidException {
        //check id user va job co ton tai ko
        boolean isExitedId = this.resumeService.checkResumeByUserAndJob(resume);
        if (!isExitedId) {
            throw new IdInvalidException("User id/ Job id khong ton tai");
        }
        // create
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.createResume(resume));
    }

    @PutMapping("/resumes")
    @ApiMessage("Update resume")
    public ResponseEntity<ResUpdateResumeDTO>  updateResume(@RequestBody Resume resume) throws IdInvalidException {
        Optional<Resume> reqResume = this.resumeService.fineResumeById(resume.getId());
        if(reqResume.isEmpty()) {
            throw new IdInvalidException("Id resume " + resume.getId() + " khong ton tai");
        }

        Resume updateResume = reqResume.get();
        updateResume.setStatus(resume.getStatus());
        return ResponseEntity.ok().body(this.resumeService.updateResume(updateResume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete resume")
    public ResponseEntity<Void> deleteResume(@PathVariable Long id) throws IdInvalidException {
        Optional<Resume> reqResume = this.resumeService.fineResumeById(id);
        if(reqResume.isEmpty()) {
            throw new IdInvalidException("Id resume " + id + " khong ton tai");
        }
        this.resumeService.deleteResume(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("resumes/{id}")
    @ApiMessage("Get Resume by Id")
    public ResponseEntity<ResGetResumeDTO> getResumes(@PathVariable Long id) throws IdInvalidException {
        Optional<Resume> reqResume = this.resumeService.fineResumeById(id);
        if(reqResume.isEmpty()) {
            throw new IdInvalidException("Id resume " + id + " khong ton tai");
        }
        return ResponseEntity.ok().body(this.resumeService.getResumeById(reqResume.get()));
    }

    @GetMapping("/resumes")
    @ApiMessage("fetch all resume")
    public ResponseEntity<ResultPaginationDTO> getAllResumes
            (@Filter Specification<Resume> specification, Pageable pageable) {

        return ResponseEntity.ok(this.resumeService.hanldeGetAllComapnies(specification,pageable));
    }
}
