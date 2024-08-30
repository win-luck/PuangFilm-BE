package gdsc.cau.puangbe.photorequest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gdsc.cau.puangbe.common.config.resolver.PuangUserArgumentResolver;
import gdsc.cau.puangbe.common.enums.RequestStatus;
import gdsc.cau.puangbe.common.exception.BaseException;
import gdsc.cau.puangbe.common.interceptor.RateLimiterInterceptor;
import gdsc.cau.puangbe.common.util.APIResponse;
import gdsc.cau.puangbe.common.util.ResponseCode;
import gdsc.cau.puangbe.photorequest.dto.CreateImageDto;
import gdsc.cau.puangbe.photorequest.service.PhotoRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PhotoRequestController.class)
class PhotoRequestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PhotoRequestService photoRequestService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    PuangUserArgumentResolver puangUserArgumentResolver;

    @MockBean
    RateLimiterInterceptor rateLimiterInterceptor;

    ObjectMapper mapper = new ObjectMapper();
    String baseUrl = "/api/photo-request";
    CreateImageDto createImageDto = new CreateImageDto(
            List.of("url1", "url2", "url3", "url4", "url5", "url6"), 0, "abc@naver.com");

    @BeforeEach
    void setUp() throws Exception {
        when(rateLimiterInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @DisplayName("createImage: 이미지 처리 요청을 처리하며, 성공 객체를 반환한다.")
    @Test
    void createImageTest() throws Exception {
        // given
        when(photoRequestService.createImage(any(CreateImageDto.class), any())).thenReturn(1L);
        String requestBody = mapper.writeValueAsString(createImageDto);
        String responseBody = mapper.writeValueAsString(APIResponse.success(1L, ResponseCode.PHOTO_REQUEST_CREATE_SUCCESS.getMessage()));

        // when & then
        mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));
    }

    @DisplayName("createImage: 유저 정보가 유효하지 않으면 404 예외가 발생해야 하며, 실패 객체를 반환한다.")
    @Test
    void createImage404Test() throws Exception {
        // given
        doThrow(new BaseException(ResponseCode.USER_NOT_FOUND)).when(photoRequestService).createImage(any(CreateImageDto.class), any());
        String requestBody = mapper.writeValueAsString(createImageDto);
        String responseBody = mapper.writeValueAsString(APIResponse.fail(ResponseCode.USER_NOT_FOUND));

        // when & then
        mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(content().json(responseBody));
        assertThatThrownBy(() -> photoRequestService.createImage(createImageDto, 1L))
                .isInstanceOf(BaseException.class)
                .hasMessage(ResponseCode.USER_NOT_FOUND.getMessage());
    }

    @DisplayName("getRequestImages: 유저의 전체 사진 리스트를 조회하며, 성공 객체를 반환한다.")
    @Test
    void getRequestImagesTest() throws Exception {
        // given
        List<String> urls = List.of("url1", "url2", "url3", "url4", "url5", "url6");
        when(photoRequestService.getRequestImages(any())).thenReturn(urls);
        String responseBody = mapper.writeValueAsString(APIResponse.success(urls, ResponseCode.PHOTO_LIST_FOUND.getMessage()));

        // when & then
        mockMvc.perform(get(baseUrl + "/list"))
                .andExpect(content().json(responseBody));
    }

    @DisplayName("getRequestImages: 유저 정보가 유효하지 않으면 404 예외가 발생해야 하며, 실패 객체를 반환한다.")
    @Test
    void getRequestImages404Test() throws Exception {
        // given
        when(photoRequestService.getRequestImages(any())).thenThrow(new BaseException(ResponseCode.USER_NOT_FOUND));
        String responseBody = mapper.writeValueAsString(APIResponse.fail(ResponseCode.USER_NOT_FOUND));

        // when & then
        mockMvc.perform(get(baseUrl + "/list"))
                .andExpect(content().json(responseBody));
        assertThatThrownBy(() -> photoRequestService.getRequestImages(1L))
                .isInstanceOf(BaseException.class)
                .hasMessage(ResponseCode.USER_NOT_FOUND.getMessage());
    }

    @DisplayName("getRequestStatus: 유저의 최근 요청 상태를 조회하며, 성공 객체를 반환한다.")
    @Test
    void getRequestStatusTest() throws Exception {
        // given
        when(photoRequestService.getRequestStatus(any())).thenReturn(RequestStatus.FINISHED.name());
        String responseBody = mapper.writeValueAsString(APIResponse.success(RequestStatus.FINISHED.name(), ResponseCode.PHOTO_STATUS_FOUND.getMessage()));

        // when & then
        mockMvc.perform(get(baseUrl + "/status"))
                .andExpect(content().json(responseBody));
    }

    @DisplayName("getRequestStatus: 유저 정보가 유효하지 않으면 404 예외가 발생해야 하며, 실패 객체를 반환한다.")
    @Test
    void getRequestStatus404Test() throws Exception {
        // given
        when(photoRequestService.getRequestStatus(any())).thenThrow(new BaseException(ResponseCode.USER_NOT_FOUND));
        String responseBody = mapper.writeValueAsString(APIResponse.fail(ResponseCode.USER_NOT_FOUND));

        // when & then
        mockMvc.perform(get(baseUrl + "/status"))
                .andExpect(content().json(responseBody));
        assertThatThrownBy(() -> photoRequestService.getRequestStatus(1L))
                .isInstanceOf(BaseException.class)
                .hasMessage(ResponseCode.USER_NOT_FOUND.getMessage());
    }
}
