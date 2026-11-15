package com.fandza.ccs.Fandza.entity;

import com.fandza.ccs.Fandza.enums.UserType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_admin")
public class Admin extends AppUser {

    protected Admin() {
    }

    public Admin(String fullName, String email, String password) {
        super(fullName, email, password, UserType.ADMIN);
    }
}
