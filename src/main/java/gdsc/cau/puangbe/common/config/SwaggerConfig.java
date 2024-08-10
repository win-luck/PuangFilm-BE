package gdsc.cau.puangbe.common.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Components components = new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));
        // 인증 헤더가 필요한 API 설정
        Paths paths = new Paths()
                .addPathItem("/api/photo-request", new PathItem().post(new Operation().addSecurityItem(new SecurityRequirement().addList("bearerAuth"))))
                .addPathItem("/api/photo-request/status", new PathItem().get(new Operation().addSecurityItem(new SecurityRequirement().addList("bearerAuth"))))
                .addPathItem("/api/photo-request/list", new PathItem().get(new Operation().addSecurityItem(new SecurityRequirement().addList("bearerAuth"))));
        return new OpenAPI()
                .components(components)
                .paths(paths)
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Puang BE")
                .description("푸앙이사진관 백엔드 REST API")
                .version("1.0.0");
    }
}
