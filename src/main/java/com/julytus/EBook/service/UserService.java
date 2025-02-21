package com.julytus.EBook.service;

import com.julytus.EBook.model.User;

public interface UserService {
    User findByEmail(String email);
}
