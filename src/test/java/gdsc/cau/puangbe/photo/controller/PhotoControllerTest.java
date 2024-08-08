package gdsc.cau.puangbe.photo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gdsc.cau.puangbe.common.exception.BaseException;
import gdsc.cau.puangbe.common.util.APIResponse;
import gdsc.cau.puangbe.common.util.ResponseCode;
import gdsc.cau.puangbe.photo.dto.request.UploadImageDto;
import gdsc.cau.puangbe.photo.service.PhotoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@WebMvcTest(controllers = PhotoController.class)
class PhotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PhotoService photoService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    ObjectMapper mapper = new ObjectMapper();
    String baseUrl = "/photo";

    @DisplayName("uploadImage: Python으로부터 처리 완료된 요청에 대한 정보를 업데이트한다.")
    @ParameterizedTest(name = "photoResultId={0}, imageUrl={1}")
    @CsvSource({
            "1, 'https://test-bucket.s3.amazonaws.com/test-image1.jpg'",
            "2, 'https://test-bucket.s3.amazonaws.com/test-image2.jpg'",
            "3, 'https://test-bucket.s3.amazonaws.com/test-image3.jpg'",
            "45, 'https://test-bucket.s3.amazonaws.com/test-image45.jpg'",
            "82, 'https://test-bucket.s3.amazonaws.com/test-image82.jpg'",
            "123, 'https://test-bucket.s3.amazonaws.com/test-image123.jpg'"
    })
    void uploadImageTest(Long photoResultId, String imageUrl) throws Exception {
        // given
        UploadImageDto uploadImageDto = new UploadImageDto(photoResultId, imageUrl);
        doNothing().when(photoService).uploadPhoto(uploadImageDto.getPhotoResultId(), uploadImageDto.getImageUrl());
        String responseBody = mapper.writeValueAsString(APIResponse.success(null, ResponseCode.PHOTO_RESULT_URL_UPLOADED.getMessage()));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(uploadImageDto)))
                .andExpect(content().json(responseBody));
    }

    @DisplayName("uploadImage: 이미지를 업로드하려는 photoResultId가 유효하지 않으면 404 예외가 발생하며, 실패 객체를 반환한다.")
    @Test
    void uploadImage404Test() throws Exception {
        // given
        UploadImageDto uploadImageDto = new UploadImageDto(1L, "imageUrl");
        doThrow(new BaseException(ResponseCode.PHOTO_RESULT_NOT_FOUND)).when(photoService).uploadPhoto(uploadImageDto.getPhotoResultId(), uploadImageDto.getImageUrl());
        String responseBody = mapper.writeValueAsString(APIResponse.fail(ResponseCode.PHOTO_RESULT_NOT_FOUND));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(uploadImageDto)))
                .andExpect(content().json(responseBody));
        assertThrows(BaseException.class, () -> photoService.uploadPhoto(uploadImageDto.getPhotoResultId(), uploadImageDto.getImageUrl()));
    }

    @DisplayName("uploadImage: 이미지를 업로드하려는 photoResult의 상태가 FINISHED이면 409 예외가 발생하며, 실패 객체를 반환한다.")
    @Test
    void uploadImage409Test() throws Exception {
        // given
        UploadImageDto uploadImageDto = new UploadImageDto(1L, "imageUrl");
        doThrow(new BaseException(ResponseCode.URL_ALREADY_UPLOADED)).when(photoService).uploadPhoto(uploadImageDto.getPhotoResultId(), uploadImageDto.getImageUrl());
        String responseBody = mapper.writeValueAsString(APIResponse.fail(ResponseCode.URL_ALREADY_UPLOADED));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(uploadImageDto)))
                .andExpect(content().json(responseBody));
        assertThrows(BaseException.class, () -> photoService.uploadPhoto(uploadImageDto.getPhotoResultId(), uploadImageDto.getImageUrl()));
    }

    @DisplayName("getImage: 유저의 특정 요청의 결과로 만들어진 이미지 URL을 조회한다.")
    @ParameterizedTest(name = "photoRequestId={0}, imageUrl={1}")
    @CsvSource({
            "1, 'https://test-bucket.s3.amazonaws.com/test-image1.jpg'",
            "2, 'https://test-bucket.s3.amazonaws.com/test-image2.jpg'",
            "3, 'https://test-bucket.s3.amazonaws.com/test-image3.jpg'",
            "45, 'https://test-bucket.s3.amazonaws.com/test-image45.jpg'",
            "82, 'https://test-bucket.s3.amazonaws.com/test-image82.jpg'",
            "123, 'https://test-bucket.s3.amazonaws.com/test-image123.jpg'"
    })
    void getImageTest(Long photoRequestId, String imageUrl) throws Exception {
        // given
        when(photoService.getPhotoUrl(photoRequestId)).thenReturn(imageUrl);
        String responseBody = mapper.writeValueAsString(APIResponse.success(imageUrl, ResponseCode.PHOTO_RESULT_URL_FOUND.getMessage()));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/" + photoRequestId))
                .andExpect(content().json(responseBody));
    }

    @DisplayName("getImage: 유저의 photoRequestId가 유효하지 않으면 404 예외가 발생하며, 실패 객체를 반환한다.")
    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 45L, 82L, 123L})
    void getImage404Test(Long photoRequestId) throws Exception {
        // given
        when(photoService.getPhotoUrl(photoRequestId)).thenThrow(new BaseException(ResponseCode.PHOTO_RESULT_NOT_FOUND));
        String responseBody = mapper.writeValueAsString(APIResponse.fail(ResponseCode.PHOTO_RESULT_NOT_FOUND));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/" + photoRequestId))
                .andExpect(content().json(responseBody));
        assertThrows(BaseException.class, () -> photoService.getPhotoUrl(photoRequestId));
    }
}
