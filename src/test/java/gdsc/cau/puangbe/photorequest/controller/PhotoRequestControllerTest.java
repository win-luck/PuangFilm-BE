package gdsc.cau.puangbe.photorequest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gdsc.cau.puangbe.common.enums.RequestStatus;
import gdsc.cau.puangbe.common.exception.BaseException;
import gdsc.cau.puangbe.common.util.APIResponse;
import gdsc.cau.puangbe.common.util.ResponseCode;
import gdsc.cau.puangbe.photorequest.dto.CreateImageDto;
import gdsc.cau.puangbe.photorequest.service.PhotoRequestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PhotoRequestController.class)
class PhotoRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PhotoRequestService photoRequestService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    ObjectMapper mapper = new ObjectMapper();
    String baseUrl = "/photo-request";
    CreateImageDto createImageDto = new CreateImageDto(
            List.of("url1", "url2", "url3", "url4", "url5", "url6"), 0, "abc@naver.com");

    @DisplayName("createImage: 이미지 처리 요청을 처리하며, 성공 객체를 반환한다.")
    @Test
    void createImageTest() throws Exception {
        // given
        doNothing().when(photoRequestService).createImage(createImageDto, 1L);
        String requestBody = mapper.writeValueAsString(createImageDto);

        // when & then
        mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .param("userId", "1")) // FIXME: 추후 JWT 시 개편 필요
                .andExpect(status().isOk());
    }

    @DisplayName("createImage: 유저 정보가 유효하지 않으면 404 예외가 발생해야 하며, 실패 객체를 반환한다.")
    @Test
    void createImage404Test() throws Exception {
        // given
        doThrow(new BaseException(ResponseCode.USER_NOT_FOUND)).when(photoRequestService).createImage(any(), anyLong());
        String requestBody = mapper.writeValueAsString(createImageDto);
        String responseBody = mapper.writeValueAsString(APIResponse.fail(ResponseCode.USER_NOT_FOUND));

        // when & then
        mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .param("userId", "1")) // FIXME: 추후 JWT 시 개편 필요
                .andExpect(content().json(responseBody));
        assertThrows(BaseException.class, () -> photoRequestService.createImage(createImageDto, 1L));
    }

    @DisplayName("getRequestImages: 유저의 전체 사진 리스트를 조회하며, 성공 객체를 반환한다.")
    @Test
    void getRequestImagesTest() throws Exception {
        // given
        List<String> urls = List.of("url1", "url2", "url3", "url4", "url5", "url6");
        when(photoRequestService.getRequestImages(1L)).thenReturn(urls);
        String responseBody = mapper.writeValueAsString(APIResponse.success(urls, ResponseCode.PHOTO_LIST_FOUND.getMessage()));

        // when & then
        mockMvc.perform(get(baseUrl + "/list")
                .param("userId", "1")) // FIXME: 추후 JWT 시 개편 필요
                .andExpect(content().json(responseBody));
    }

    @DisplayName("getRequestImages: 유저 정보가 유효하지 않으면 404 예외가 발생해야 하며, 실패 객체를 반환한다.")
    @Test
    void getRequestImages404Test() throws Exception {
        // given
        when(photoRequestService.getRequestImages(1L)).thenThrow(new BaseException(ResponseCode.USER_NOT_FOUND));
        String responseBody = mapper.writeValueAsString(APIResponse.fail(ResponseCode.USER_NOT_FOUND));

        // when & then
        mockMvc.perform(get(baseUrl + "/list")
                .param("userId", "1")) // FIXME: 추후 JWT 시 개편 필요
                .andExpect(content().json(responseBody));
        assertThrows(BaseException.class, () -> photoRequestService.getRequestImages(1L));
    }

    @DisplayName("getRequestStatus: 유저의 최근 요청 상태를 조회하며, 성공 객체를 반환한다.")
    @Test
    void getRequestStatusTest() throws Exception {
        // given
        when(photoRequestService.getRequestStatus(1L)).thenReturn(RequestStatus.FINISHED.name());
        String responseBody = mapper.writeValueAsString(APIResponse.success(RequestStatus.FINISHED.name(), ResponseCode.PHOTO_STATUS_FOUND.getMessage()));

        // when & then
        mockMvc.perform(get(baseUrl + "/check")
                .param("userId", "1")) // FIXME: 추후 JWT 시 개편 필요
                .andExpect(content().json(responseBody));
    }

    @DisplayName("getRequestStatus: 유저 정보가 유효하지 않으면 404 예외가 발생해야 하며, 실패 객체를 반환한다.")
    @Test
    void getRequestStatus404Test() throws Exception {
        // given
        when(photoRequestService.getRequestStatus(1L)).thenThrow(new BaseException(ResponseCode.USER_NOT_FOUND));
        String responseBody = mapper.writeValueAsString(APIResponse.fail(ResponseCode.USER_NOT_FOUND));

        // when & then
        mockMvc.perform(get(baseUrl + "/check")
                .param("userId", "1")) // FIXME: 추후 JWT 시 개편 필요
                .andExpect(content().json(responseBody));
        assertThrows(BaseException.class, () -> photoRequestService.getRequestStatus(1L));
    }
}
