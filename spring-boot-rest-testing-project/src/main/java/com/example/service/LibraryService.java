package com.example.service;

import com.example.entity.Library;

import java.util.List;

public interface LibraryService {
    List<Library> findAllByAuthor(String authorName);

    List<Library> findAll();

    void delete(Long id);

    Library findLibraryById(Long id);

    Library updateLibrary(Library library);

    Library saveLibrary(Library library);
}