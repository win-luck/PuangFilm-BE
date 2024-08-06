package gdsc.cau.puangbe.photo.service;
import gdsc.cau.puangbe.common.enums.Gender;
import gdsc.cau.puangbe.common.exception.BaseException;
import gdsc.cau.puangbe.common.util.ResponseCode;
import gdsc.cau.puangbe.photo.entity.PhotoRequest;
import gdsc.cau.puangbe.photo.entity.PhotoResult;
import gdsc.cau.puangbe.photo.repository.PhotoRequestRepository;
import gdsc.cau.puangbe.photo.repository.PhotoResultRepository;
import gdsc.cau.puangbe.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class PhotoServiceImplTest {

    @InjectMocks
    private PhotoServiceImpl photoService;

    @Mock
    private PhotoRequestRepository photoRequestRepository;

    @Mock
    private PhotoResultRepository photoResultRepository;

    private Long photoRequestId = 1L;
    private String imageUrl = "https://example.com/image.jpg";
    private User user = User.builder().userName("test").build();
    private PhotoRequest photoRequest = PhotoRequest.builder()
            .user(user)
            .gender(Gender.MALE)
            .urls(List.of("https://example.com/image.jpg"))
            .build();
    private PhotoResult photoResult = PhotoResult.builder()
            .user(user)
            .photoRequest(photoRequest)
            .createDate(LocalDateTime.now())
            .build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("createPhoto: 이미지 처리 요청을 생성한다.")
    @Test
    void createPhotoTest() {
        // given
        given(photoRequestRepository.findById(photoRequestId)).willReturn(Optional.of(photoRequest));
        given(photoResultRepository.save(any())).willReturn(photoResult);

        // when
        photoService.createPhoto(photoRequestId);

        // then
        verify(photoRequestRepository, times(1)).findById(photoRequestId);
        verify(photoResultRepository, times(1)).save(any());
    }

    @DisplayName("createPhoto: 이미지 처리 요청을 찾을 수 없는 경우 예외가 발생한다.")
    @Test
    void createPhotoNotFoundTest() {
        // given
        given(photoRequestRepository.findById(photoRequestId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> photoService.createPhoto(photoRequestId))
                .isInstanceOf(BaseException.class)
                .hasMessage(ResponseCode.PHOTO_REQUEST_NOT_FOUND.getMessage());
    }

    @DisplayName("uploadPhoto: 사진 URL을 업로드하고 상태를 업데이트한다.")
    @Test
    void uploadPhotoTest() {
        // given
        given(photoResultRepository.findByPhotoRequestId(photoRequestId)).willReturn(Optional.of(photoResult));
        given(photoRequestRepository.findById(any())).willReturn(Optional.of(photoRequest));

        // when
        photoService.uploadPhoto(photoRequestId, imageUrl);

        // then
        assertThat(photoResult.getImageUrl()).isEqualTo(imageUrl);
        verify(photoResultRepository, times(1)).save(photoResult);
    }

    @DisplayName("uploadPhoto: 사진 결과를 찾을 수 없는 경우 예외가 발생한다.")
    @Test
    void uploadPhotoNotFoundTest() {
        // given
        given(photoResultRepository.findByPhotoRequestId(photoRequestId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> photoService.uploadPhoto(photoRequestId, imageUrl))
                .isInstanceOf(BaseException.class)
                .hasMessage(ResponseCode.PHOTO_RESULT_NOT_FOUND.getMessage());
    }

    @DisplayName("uploadPhoto: 이미 URL이 업로드된 경우 예외가 발생한다.")
    @Test
    void uploadPhotoAlreadyUploadedTest() {
        // given
        photoRequest.finishStatus();
        given(photoResultRepository.findByPhotoRequestId(photoRequestId)).willReturn(Optional.of(photoResult));
        given(photoRequestRepository.findById(any())).willReturn(Optional.of(photoRequest));

        // when & then
        assertThatThrownBy(() -> photoService.uploadPhoto(photoRequestId, imageUrl))
                .isInstanceOf(BaseException.class)
                .hasMessage(ResponseCode.URL_ALREADY_UPLOADED.getMessage());
    }

    @DisplayName("getPhotoUrl: 요청된 사진의 URL을 반환한다.")
    @Test
    void getPhotoUrlTest() {
        // given
        given(photoResultRepository.findByPhotoRequestId(photoRequestId)).willReturn(Optional.of(photoResult));
        photoResult.update(imageUrl);

        // when
        String resultUrl = photoService.getPhotoUrl(photoRequestId);

        // then
        assertThat(resultUrl).isEqualTo(imageUrl);
        verify(photoResultRepository, times(1)).findByPhotoRequestId(photoRequestId);
    }

    @DisplayName("getPhotoUrl: 사진 결과를 찾을 수 없는 경우 예외가 발생한다.")
    @Test
    void getPhotoUrlNotFoundTest() {
        // given
        given(photoResultRepository.findByPhotoRequestId(photoRequestId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> photoService.getPhotoUrl(photoRequestId))
                .isInstanceOf(BaseException.class)
                .hasMessage(ResponseCode.PHOTO_RESULT_NOT_FOUND.getMessage());
    }
}
