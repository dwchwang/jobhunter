package vn.dwchwang.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.dwchwang.jobhunter.domain.Resume;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long>, JpaSpecificationExecutor<Resume> {
  List<Resume> findByUserId(long userId);
}
