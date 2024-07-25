package gdsc.cau.puangbe.user.entity;

import gdsc.cau.puangbe.common.enums.Gender;
import gdsc.cau.puangbe.photo.entity.PhotoRequest;
import gdsc.cau.puangbe.photo.entity.PhotoResult;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String userName;

    private LocalDateTime createDate;

    private LocalDateTime requestDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<PhotoResult> photoResult = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<PhotoRequest> photoRequest = new ArrayList<>();
}
