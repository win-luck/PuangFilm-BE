package gdsc.cau.puangbe.photo.entity;

import gdsc.cau.puangbe.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class PhotoResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private PhotoRequest photoRequest;

    private LocalDateTime createDate;

    private String imageUrl; //s3에 저장된 AI결과 이미지 url

    @Builder
    public PhotoResult(User user, PhotoRequest photoRequest, LocalDateTime createDate){
        this.user = user;
        this.photoRequest = photoRequest;
        this.createDate = createDate;

    }

    public void update(String imageUrl){
        this.imageUrl = imageUrl;
    }
}
