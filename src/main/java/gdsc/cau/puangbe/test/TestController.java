package gdsc.cau.puangbe.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    // TODO: Swagger 테스트용 API이며 추후 삭제 예정
    @GetMapping("/test")
    public String auth() {
        return "auth";
    }
}
