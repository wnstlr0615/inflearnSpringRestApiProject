package com.joon.demoinflearnrestapi.Account;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of="id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue
    private Integer id;
    private String email;
    private String password;
    @ElementCollection(fetch = FetchType.EAGER) //값을 여러 개 가질 수 있다
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;
}
