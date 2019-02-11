package am.rate.project.bestexchangerate.repo;

import am.rate.project.bestexchangerate.dom.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepo extends JpaRepository<Request, Long> {
}
