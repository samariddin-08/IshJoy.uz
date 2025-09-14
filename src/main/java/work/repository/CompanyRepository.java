package work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import work.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company,Integer> {
}
