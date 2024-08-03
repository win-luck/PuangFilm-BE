package gdsc.cau.puangbe.photo.service;

public interface PhotoService {
    Long createPhoto(Long photoRequestId);
    Void uploadPhoto(Long photoResultId, String imageUrl);
    String get(Long photoRequestId);
}
