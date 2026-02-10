package com.example.backend.service;

import com.example.backend.dto.RegisterRequest;
import com.example.backend.exception.BaseException;
import com.example.backend.exception.FileException;
import com.example.backend.exception.UserException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class RegisterService {

    public String registers(RegisterRequest request) throws BaseException {
        if (request == null) {
            throw UserException.requestNull();
        }
        if (Objects.isNull(request.getName())) {
            throw UserException.nameNull();
        }
        return "login done " + request;
    }

    public String uploadProfilePicture(MultipartFile file) throws BaseException {
        if (file == null) {
            throw FileException.fileNull();
        }
        if (file.getSize() > 1048576 * 2) {
            throw FileException.fileMaxSize();
        }
        String contentType = file.getContentType();
        if (contentType == null) {
            throw FileException.unSupportedTypes();
        }

        List<String> supportedTypes = Arrays.asList("image/jpeg", "image/png");
        if (supportedTypes.contains(contentType)) {
            throw FileException.unSupportedTypes();
        }

        // TODO: upload file stroage (AWS, azure)
        try {
            byte[] bytes = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "";
    }
}
