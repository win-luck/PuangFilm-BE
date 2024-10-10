package gdsc.cau.puangbe.photo.service;

import gdsc.cau.puangbe.common.exception.BaseException;
import gdsc.cau.puangbe.common.util.ConstantUtil;
import gdsc.cau.puangbe.common.util.ResponseCode;
import gdsc.cau.puangbe.photo.dto.request.EmailInfoDto;
import gdsc.cau.puangbe.photo.entity.PhotoResult;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoServiceFacadeImpl implements PhotoService{
    private final PhotoServiceImpl photoServiceImpl;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    private final String EMAIL_LINK = "https://www.google.com/"; // TODO : 프론트 분들 링크 관련 답변 오면 프레임 페이지 링크 관련 수정

    // 완성된 요청 id 및 imageUrl을 받아 저장
    @Override
    public void uploadPhoto(Long photoRequestId, String imageUrl) {
        sendEmail(photoServiceImpl.uploadPhoto(photoRequestId, imageUrl));
    }

    // 특정 요청의 imageUrl 조회
    @Override
    @Transactional(readOnly = true)
    public String getPhotoUrl(Long photoRequestId) {
        PhotoResult photoResult = photoServiceImpl.getPhotoResult(photoRequestId);
        if(photoResult.getImageUrl() == null){
            throw new BaseException(ResponseCode.IMAGE_ON_PROCESS);
        }

        log.info("결과 이미지 URL 조회 완료: {}", photoResult.getImageUrl());
        return photoResult.getImageUrl();
    }

    // 유저에게 이메일 전송
    private void sendEmail(EmailInfoDto emailInfoDto) {
        // 이메일 템플릿 내용 설정
        Context context = new Context();
        context.setVariable("userName", emailInfoDto.getName());
        context.setVariable("photoUrl", emailInfoDto.getPhotoUrl());
        context.setVariable("framePageUrl", EMAIL_LINK);
        String body = templateEngine.process("email-template", context);

        try {
            // 메일 정보 설정
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
            messageHelper.setFrom(ConstantUtil.GDSC_CAU_EMAIL);
            messageHelper.setTo(emailInfoDto.getEmail());
            messageHelper.setSubject("[푸앙이 사진관] AI 프로필 사진 생성 완료");
            messageHelper.setText(body, true);

            // 메일 전송
            mailSender.send(mimeMessage);
            log.info("이메일 전송 완료: {}", emailInfoDto.getEmail());
        } catch (Exception e){
            e.printStackTrace();
            throw new BaseException(ResponseCode.EMAIL_SEND_ERROR);
        }
    }
}
