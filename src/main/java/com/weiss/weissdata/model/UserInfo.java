package com.weiss.weissdata.model;


import com.weiss.weissdata.model.forum.MyMark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserInfo {
    @Id
    private String id;
    private List<UserAutority> authorities;
    private String password;
    private String username;
    private String login;
    private String mail;
    private String photo;
    private List<MyMark> markList;

    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
}
