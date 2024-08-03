package gdsc.cau.puangbe.photo.repository;

import gdsc.cau.puangbe.photo.entity.PhotoResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<PhotoResult, Long> {
}
