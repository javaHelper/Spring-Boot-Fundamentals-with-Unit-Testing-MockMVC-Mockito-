package com.example.service;

import com.example.entity.Library;
import com.example.repository.LibraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibraryServiceImpl implements LibraryService{
    @Autowired
    private LibraryRepository repository;

    @Override
    public List<Library> findAllByAuthor(String authorName) {
        return repository.findByAuthor(authorName);
    }

    @Override
    public List<Library> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(Long id) {
        Optional<Library> optionalLibrary = repository.findById(id);
        optionalLibrary.ifPresent(library -> repository.delete(library));
    }

    @Override
    public Library findLibraryById(Long id) {
        Optional<Library> optionalLibrary = repository.findById(id);
        return optionalLibrary.orElseGet(Library::new);
    }

    @Override
    public Library updateLibrary(Library library) {
        Optional<Library> optionalLibrary = repository.findById(library.getId());
        if(optionalLibrary.isPresent()){
            Library libraryToUpdate = optionalLibrary.get();
            libraryToUpdate.setBookName(library.getBookName());
            libraryToUpdate.setIsbn(library.getIsbn());
            libraryToUpdate.setAisle(library.getAisle());
            libraryToUpdate.setAuthor(library.getAuthor());
            return libraryToUpdate;
        }
        return null;
    }

    @Override
    public Library saveLibrary(Library library) {
         Library savedLibrary = repository.save(library);
         return savedLibrary;
    }
}