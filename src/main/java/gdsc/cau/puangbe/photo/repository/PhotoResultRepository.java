package gdsc.cau.puangbe.photo.repository;

import gdsc.cau.puangbe.photo.entity.PhotoResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhotoResultRepository extends JpaRepository<PhotoResult, Long> {
    Optional<PhotoResult> findById(Long photoResultId);
}
