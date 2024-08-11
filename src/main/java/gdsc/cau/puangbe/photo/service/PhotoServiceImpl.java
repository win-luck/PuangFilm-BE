package gdsc.cau.puangbe.photo.service;

import gdsc.cau.puangbe.common.enums.RequestStatus;
import gdsc.cau.puangbe.common.exception.BaseException;
import gdsc.cau.puangbe.common.util.ConstantUtil;
import gdsc.cau.puangbe.common.util.ResponseCode;
import gdsc.cau.puangbe.photo.dto.request.EmailInfo;
import gdsc.cau.puangbe.photo.entity.PhotoRequest;
import gdsc.cau.puangbe.photo.entity.PhotoResult;
import gdsc.cau.puangbe.photo.repository.PhotoResultRepository;
import gdsc.cau.puangbe.photo.repository.PhotoRequestRepository;
import gdsc.cau.puangbe.user.entity.User;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoServiceImpl implements PhotoService {

    private final PhotoResultRepository photoResultRepository;
    private final PhotoRequestRepository photoRequestRepository;
    private final RedisTemplate<String, Long> redisTemplate;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    // 완성된 요청 id 및 imageUrl을 받아 저장
    @Override
    @Transactional
    public void uploadPhoto(Long photoRequestId, String imageUrl) {
        PhotoRequest photoRequest = photoRequestRepository.findById(photoRequestId)
                .orElseThrow(() -> new BaseException(ResponseCode.PHOTO_REQUEST_NOT_FOUND));
        if (photoRequest.getStatus() == RequestStatus.FINISHED) {
            throw new BaseException(ResponseCode.URL_ALREADY_UPLOADED);
        }
        User user = photoRequest.getUser();
        PhotoResult photoResult = getPhotoResult(photoRequestId);

        photoRequest.finishStatus();
        photoRequestRepository.save(photoRequest);
        photoResult.update(imageUrl);
        photoResultRepository.save(photoResult);

        // Redis 대기열의 user 정보 삭제
        redisTemplate.opsForSet().remove(ConstantUtil.USER_ID_KEY, user.getId());
        redisTemplate.delete(user.getId().toString());

        // 이메일 발송
        EmailInfo emailInfo = EmailInfo.builder()
                .email(photoRequest.getEmail())
                .photoUrl(imageUrl)
                .name(user.getUserName())
                .framePageUrl("https://www.google.com/") // TODO : 프론트 분들 링크 관련 답변 오면 프레임 페이지 링크 관련 수정
                .build();

        sendEmail(emailInfo);
    }

    // 특정 요청의 imageUrl 조회
    @Override
    @Transactional(readOnly = true)
    public String getPhotoUrl(Long photoRequestId) {
        PhotoResult photoResult = getPhotoResult(photoRequestId);
        if(photoResult.getImageUrl() == null){
            throw new BaseException(ResponseCode.IMAGE_ON_PROCESS);
        }

        return photoResult.getImageUrl();
    }

    // 유저에게 이메일 전송
    private void sendEmail(EmailInfo emailInfo) {
        // 이메일 템플릿 내용 설정
        Context context = new Context();
        context.setVariable("userName", emailInfo.getName());
        context.setVariable("photoUrl", emailInfo.getPhotoUrl());
        context.setVariable("framePageUrl", emailInfo.getFramePageUrl());
        String body = templateEngine.process("email-template", context);

        try {
            // 메일 정보 설정
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
            messageHelper.setFrom(ConstantUtil.GDSC_CAU_EMAIL);
            messageHelper.setTo(emailInfo.getEmail());
            messageHelper.setSubject("[푸앙이 사진관] AI 프로필 사진 생성 완료");
            messageHelper.setText(body, true);

            // 메일 전송
            mailSender.send(mimeMessage);

        } catch (Exception e){
            e.printStackTrace();
            throw new BaseException(ResponseCode.EMAIL_SEND_ERROR);
        }

    }

    private PhotoResult getPhotoResult(Long photoRequestId){
        return photoResultRepository.findByPhotoRequestId(photoRequestId)
                .orElseThrow(() -> new BaseException(ResponseCode.PHOTO_RESULT_NOT_FOUND));
    }
}
