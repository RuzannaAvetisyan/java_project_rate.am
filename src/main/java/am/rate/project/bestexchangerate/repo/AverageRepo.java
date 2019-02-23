package am.rate.project.bestexchangerate.repo;

import am.rate.project.bestexchangerate.dom.Average;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDate;
import java.util.List;

@EnableJpaRepositories
public interface AverageRepo extends JpaRepository<Average, LocalDate> {

    Average findByDate(LocalDate date);

    List<Average> findByDateAfter(LocalDate date);
}
