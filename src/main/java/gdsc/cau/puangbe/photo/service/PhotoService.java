package gdsc.cau.puangbe.photo.service;

public interface PhotoService {
    void uploadPhoto(Long photoResultId, String imageUrl);
    String getPhotoUrl(Long photoRequestId);
}
