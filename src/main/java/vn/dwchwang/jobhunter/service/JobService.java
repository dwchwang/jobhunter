package vn.dwchwang.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.dwchwang.jobhunter.domain.Job;
import vn.dwchwang.jobhunter.domain.Skill;
import vn.dwchwang.jobhunter.domain.User;
import vn.dwchwang.jobhunter.domain.response.ResultPaginationDTO;
import vn.dwchwang.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.dwchwang.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.dwchwang.jobhunter.repository.JobRepository;
import vn.dwchwang.jobhunter.repository.SkillRepository;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    public JobService(JobRepository repository, JobRepository jobRepository, SkillRepository skillRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
    }

    public ResCreateJobDTO handleCreate(Job job) {
        // check skills
        if(job.getSkills() != null){
            List<Long> reqSkills = job.getSkills()
                    .stream().map(Skill::getId)
                    .toList();
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }
        //create job
        Job curJob = this.jobRepository.save(job);

        // convert response
        ResCreateJobDTO  resCreateJobDTO = new ResCreateJobDTO();
        resCreateJobDTO.setId(curJob.getId());
        resCreateJobDTO.setName(curJob.getName());
        resCreateJobDTO.setLocation(curJob.getLocation());
        resCreateJobDTO.setSalary(curJob.getSalary());
        resCreateJobDTO.setQuantity(curJob.getQuantity());
        resCreateJobDTO.setLevel(curJob.getLevel());
        resCreateJobDTO.setDescription(curJob.getDescription());
        resCreateJobDTO.setStartDate(curJob.getStartDate());
        resCreateJobDTO.setEndDate(curJob.getEndDate());
        resCreateJobDTO.setActive(curJob.isActive());
        resCreateJobDTO.setCreatedAt(curJob.getCreatedAt());
        resCreateJobDTO.setCreatedBy(curJob.getCreatedBy());
        if(curJob.getSkills() != null){
            List<String> dbSkills = curJob.getSkills()
                    .stream().map(Skill::getName)
                    .toList();
            resCreateJobDTO.setSkills(dbSkills);
        }

        return resCreateJobDTO;
    }

    public Optional<Job> fetchJobById(Long id) {
        return this.jobRepository.findById(id);
    }


    public Job update(Job job) {
        //update job
        Optional<Job> existingJob = this.jobRepository.findById(job.getId());
        Job updateJob = existingJob.orElse(null);
        if(updateJob != null) {
            updateJob.setName(job.getName());
            updateJob.setLocation(job.getLocation());
            updateJob.setSalary(job.getSalary());
            updateJob.setQuantity(job.getQuantity());
            updateJob.setLevel(job.getLevel());
            updateJob.setDescription(job.getDescription());
            updateJob.setStartDate(job.getStartDate());
            updateJob.setEndDate(job.getEndDate());
            updateJob.setActive(job.isActive());
            if(job.getSkills() != null){
                List<Long> reqSkills = job.getSkills()
                                          .stream().map(Skill::getId)
                                          .toList();
                List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
                job.setSkills(dbSkills);
                updateJob.setSkills(job.getSkills());
            }
            return this.jobRepository.save(updateJob);
        }
        return null;
    }

    public ResUpdateJobDTO convertToRestUpdateJob(Job job) {
        ResUpdateJobDTO resUpdateJobDTO = new ResUpdateJobDTO();
        resUpdateJobDTO.setId(job.getId());
        resUpdateJobDTO.setName(job.getName());
        resUpdateJobDTO.setLocation(job.getLocation());
        resUpdateJobDTO.setSalary(job.getSalary());
        resUpdateJobDTO.setQuantity(job.getQuantity());
        resUpdateJobDTO.setLevel(job.getLevel());
        resUpdateJobDTO.setDescription(job.getDescription());
        resUpdateJobDTO.setStartDate(job.getStartDate());
        resUpdateJobDTO.setEndDate(job.getEndDate());
        resUpdateJobDTO.setActive(job.isActive());
        if(job.getSkills() != null){
            List<String> dbSkills = job.getSkills()
                                          .stream().map(Skill::getName)
                                          .toList();
            resUpdateJobDTO.setSkills(dbSkills);
        }
        resUpdateJobDTO.setUpdateAt(job.getCreatedAt());
        resUpdateJobDTO.setUpdateBy(job.getCreatedBy());
        return  resUpdateJobDTO;
    }

    public void handleRemoveJob(long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDTO handleGetAllJobs(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDTO userDto = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageJob.getTotalPages());
        meta.setTotal(pageJob.getTotalElements());
        userDto.setMeta(meta);

        userDto.setResult(pageJob.getContent());
        return userDto;
    }
}
