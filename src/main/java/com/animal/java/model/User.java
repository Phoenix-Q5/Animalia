package com.animal.java.model;

import com.animal.java.model.enumConstants.Role;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String id;
    private String name;
    private String email;
    private String username;
    private String password;
    private List<Role> roles;
}
