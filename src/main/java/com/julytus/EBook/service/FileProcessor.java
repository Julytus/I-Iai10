package com.julytus.EBook.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileProcessor {
    String uploadFile(MultipartFile file);
}
