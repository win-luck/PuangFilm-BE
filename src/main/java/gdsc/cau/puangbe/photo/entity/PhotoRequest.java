package gdsc.cau.puangbe.photo.entity;

import gdsc.cau.puangbe.common.enums.Gender;
import gdsc.cau.puangbe.common.enums.RequestStatus;
import gdsc.cau.puangbe.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class PhotoRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id")
    private PhotoResult photoResult;

    private RequestStatus status = RequestStatus.WAITING;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private Gender gender;

    @OneToMany(mappedBy = "request", cascade = CascadeType.REMOVE)
    private List<PhotoOrigin> photoUrls = new ArrayList<>();
}
