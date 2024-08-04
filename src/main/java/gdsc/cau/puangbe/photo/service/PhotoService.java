package gdsc.cau.puangbe.photo.service;

public interface PhotoService {
    Long createPhoto(Long photoRequestId);
    void uploadPhoto(Long photoResultId, String imageUrl);
    String getPhotoUrl(Long photoRequestId);
}
