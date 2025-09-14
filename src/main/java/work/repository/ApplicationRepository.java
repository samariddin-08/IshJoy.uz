package work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import work.Application;
import work.entity.ApplicationForJob;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationForJob,Integer> {
}
