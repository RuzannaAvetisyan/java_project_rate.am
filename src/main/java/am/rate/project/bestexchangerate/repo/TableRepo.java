package am.rate.project.bestexchangerate.repo;

import am.rate.project.bestexchangerate.dom.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDate;

@EnableJpaRepositories
public interface TableRepo extends JpaRepository<Table, LocalDate> {
    Table findByDate(LocalDate date);
}
