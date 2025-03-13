package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Library;
import com.example.service.LibraryService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/library")
@Slf4j
public class LibraryController {
    @Autowired
    private LibraryService libraryService;

    @GetMapping("/{id}")
    public ResponseEntity<Library> findBookById(@PathVariable(value = "id") Long id) {
        Library library = libraryService.findLibraryById(id);
        return new ResponseEntity<>(library, HttpStatus.OK);
    }

    @GetMapping("authorName/{authorName}")
    public ResponseEntity<List<Library>> getBookByAuthorName(@PathVariable String authorName) {
        List<Library> libraries = libraryService.findAllByAuthor(authorName);
        return new ResponseEntity<>(libraries, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<Library>> getBooks() {
        List<Library> libraries = libraryService.findAll();
        return new ResponseEntity<>(libraries, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteBookById(@PathVariable Long id) {
        libraryService.delete(id);

        log.info("Book  is deleted ");
        return new ResponseEntity<>("Book is deleted", HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<Library> updateLibrary(@RequestBody Library library){
        Library updateLibrary = libraryService.updateLibrary(library);
        return new ResponseEntity<>(updateLibrary, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Library> createLibrary(@RequestBody Library library){
        Library createdLibrary =  libraryService.saveLibrary(library);
        return new ResponseEntity<>(createdLibrary, HttpStatus.CREATED);
    }
}