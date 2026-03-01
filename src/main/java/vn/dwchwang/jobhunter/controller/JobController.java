package vn.dwchwang.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.dwchwang.jobhunter.domain.Job;
import vn.dwchwang.jobhunter.domain.Skill;
import vn.dwchwang.jobhunter.domain.User;
import vn.dwchwang.jobhunter.domain.response.ResUserDTO;
import vn.dwchwang.jobhunter.domain.response.ResultPaginationDTO;
import vn.dwchwang.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.dwchwang.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.dwchwang.jobhunter.service.JobService;
import vn.dwchwang.jobhunter.util.annotation.ApiMessage;
import vn.dwchwang.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create new job")

    public ResponseEntity<ResCreateJobDTO> createJob(@Valid @RequestBody Job job) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.handleCreate(job));
    }

    @PutMapping("/jobs")
    @ApiMessage("Update job")
    public ResponseEntity<ResUpdateJobDTO> updateSkill(@Valid @RequestBody Job job) throws IdInvalidException {
        Job existingJob = this.jobService.update(job);
        if(existingJob == null) {
            throw new IdInvalidException("Job id not found");
        }
        return ResponseEntity.ok(this.jobService.convertToRestUpdateJob(existingJob));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete job by id")
    public ResponseEntity<Void> deleteJob(@PathVariable long id) throws IdInvalidException {
        Optional<Job> existingJob = this.jobService.fetchJobById(id);
        if(existingJob.isEmpty()) {
            throw new IdInvalidException("job id not found");
        }
        this.jobService.handleRemoveJob(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/jobs")
    @ApiMessage("Fetch all jobs")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<Job> spec,
            Pageable pageable
    ) {
        return ResponseEntity.ok(this.jobService.handleGetAllJobs(spec, pageable));
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Get job by id")
    public ResponseEntity<Job> getUserById(@PathVariable long id) throws IdInvalidException {
        Optional<Job> job = this.jobService.fetchJobById(id);
        if(job.isEmpty()) {
            throw new IdInvalidException("Job id not found");
        }
        return ResponseEntity.ok(job.get());
    }
}
