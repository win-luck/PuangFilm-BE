package gdsc.cau.puangbe.photo.repository;

import gdsc.cau.puangbe.photo.entity.PhotoResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoResultRepository extends JpaRepository<PhotoResult, Long> {
    Optional<PhotoResult> findById(Long photoResultId);

    Optional<PhotoResult> findByPhotoRequestId(Long photoRequestId);

    List<PhotoResult> findAllByUserId(Long userId);
}
