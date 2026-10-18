package com.fandza.app.Fandza.entity;

import com.fandza.app.Fandza.enums.UserType;
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
