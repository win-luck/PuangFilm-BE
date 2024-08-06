package gdsc.cau.puangbe.photorequest.service;

import gdsc.cau.puangbe.common.enums.Gender;
import gdsc.cau.puangbe.common.enums.RequestStatus;
import gdsc.cau.puangbe.common.exception.BaseException;
import gdsc.cau.puangbe.photo.entity.PhotoRequest;
import gdsc.cau.puangbe.photo.entity.PhotoResult;
import gdsc.cau.puangbe.photo.repository.PhotoRequestRepository;
import gdsc.cau.puangbe.photo.repository.PhotoResultRepository;
import gdsc.cau.puangbe.photorequest.dto.CreateImageDto;
import gdsc.cau.puangbe.user.entity.User;
import gdsc.cau.puangbe.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class PhotoRequestServiceImplTest {

    @InjectMocks
    private PhotoRequestServiceImpl photoRequestService;

    @Mock
    private PhotoRequestRepository photoRequestRepository;

    @Mock
    private PhotoResultRepository photoResultRepository;

    @Mock
    private UserRepository userRepository;

    String imageUrl = "https://example.com/image.jpg";
    List<String> photoUrls = List.of("https://example.com/image.jpg", "https://example.com/image2.jpg");
    User user = User.builder().userName("test").build();
    PhotoRequest photoRequest = PhotoRequest.builder()
            .user(user)
            .gender(Gender.MALE)
            .urls(List.of("https://example.com/image.jpg"))
            .build();
    CreateImageDto createImageDto = new CreateImageDto(photoUrls, Gender.MALE.getValue());

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("createImage: 유저의 이미지 URL 리스트를 받아 이미지 처리 요청을 생성한다.")
    @Test
    void createImageTest() {
        // given
        given(userRepository.findById(1L)).willReturn(java.util.Optional.of(user));
        given(photoRequestRepository.save(any(PhotoRequest.class))).willReturn(photoRequest);

        // when
        photoRequestService.createImage(createImageDto, 1L);

        // then
        verify(photoRequestRepository).save(any(PhotoRequest.class));
    }

    @DisplayName("createImage: 유저를 찾을 수 없는 경우 예외가 발생해야 한다.")
    @Test
    void createImageUserNotFoundTest() {
        // given
        given(userRepository.findById(1L)).willReturn(java.util.Optional.empty());

        // when & then
        assertThrows(BaseException.class, () -> photoRequestService.createImage(createImageDto, 1L));
    }

    @DisplayName("getRequestImages: 유저의 전체 사진 리스트를 조회한다.")
    @Test
    void getRequestImagesTest() {
        // given
        given(userRepository.existsById(1L)).willReturn(true);
        PhotoResult photoResult = PhotoResult.builder()
                .user(user)
                .photoRequest(photoRequest)
                .build();
        photoResult.update(imageUrl);
        given(photoResultRepository.findAllByUserId(1L)).willReturn(List.of(photoResult));

        // when
        List<String> images = photoRequestService.getRequestImages(1L);

        // then
        assertEquals(photoResult.getImageUrl(), images.get(0));
    }

    @DisplayName("getRequestImages: 유저를 찾을 수 없는 경우 예외가 발생해야 한다.")
    @Test
    void getRequestImagesUserNotFoundTest() {
        // given
        given(userRepository.findById(1L)).willReturn(java.util.Optional.empty());

        // when & then
        assertThrows(BaseException.class, () -> photoRequestService.getRequestImages(1L));
    }

    @DisplayName("getRequestStatus: 최근 생성 요청한 이미지의 상태를 조회한다.")
    @Test
    void getRequestStatusTest() {
        // given
        given(userRepository.existsById(1L)).willReturn(true);
        given(photoRequestRepository.findTopByUserIdOrderByCreateDateDesc(1L)).willReturn(java.util.Optional.of(photoRequest));

        // when
        String status = photoRequestService.getRequestStatus(1L);

        // then
        assertEquals(RequestStatus.WAITING.toString(), status);
    }

    @DisplayName("getRequestStatus: 유저를 찾을 수 없는 경우 예외가 발생해야 한다.")
    @Test
    void getRequestStatusUserNotFoundTest() {
        // given
        given(userRepository.existsById(1L)).willReturn(false);

        // when & then
        assertThrows(BaseException.class, () -> photoRequestService.getRequestStatus(1L));
    }

    @DisplayName("getRequestStatus: 요청된 사진이 없는 경우 예외가 발생해야 한다.")
    @Test
    void getRequestStatusNotFoundTest() {
        // given
        given(userRepository.existsById(1L)).willReturn(true);
        given(photoRequestRepository.findTopByUserIdOrderByCreateDateDesc(1L)).willReturn(java.util.Optional.empty());

        // when & then
        assertThrows(BaseException.class, () -> photoRequestService.getRequestStatus(1L));
    }
}
