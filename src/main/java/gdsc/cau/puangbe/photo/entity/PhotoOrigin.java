package gdsc.cau.puangbe.photo.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class PhotoOrigin {

    @Id
    @Column(name = "origin_id")
    private String id; //사용자에게 받은 사진의 s3 url 주소이므로 string

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private PhotoRequest request;
}
