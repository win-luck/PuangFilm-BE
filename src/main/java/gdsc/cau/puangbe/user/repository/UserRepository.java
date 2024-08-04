package gdsc.cau.puangbe.user.repository;

import gdsc.cau.puangbe.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long userId);
}
