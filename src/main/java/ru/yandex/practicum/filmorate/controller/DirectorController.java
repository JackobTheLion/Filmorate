package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public List<Director> getAll() {
        log.info("GET request");
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable Long id) {
        log.info("GET request /{}", id);
        return directorService.getById(id);
    }

    @PostMapping
    public Director create(@RequestBody @Valid Director director) {
        log.info("POST request {}",director);
        return directorService.createDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable Long id) {
        log.info("DELETE request /{}",id);
        directorService.deleteDirector(id);
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director) {
        log.info("PUT request {}", director);
        return directorService.updateDirector(director);
    }

}
