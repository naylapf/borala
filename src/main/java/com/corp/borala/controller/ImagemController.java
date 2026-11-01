package com.corp.borala.controller;

import com.corp.borala.repository.ImagemRepository;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Duration;

/** Entrega as imagens guardadas no banco: {@code /fotos/{id}}. Público, como as fotos das páginas de restaurante. */
@Controller
public class ImagemController {

    private final ImagemRepository imagemRepository;

    public ImagemController(ImagemRepository imagemRepository) {
        this.imagemRepository = imagemRepository;
    }

    @GetMapping("/fotos/{id}")
    public ResponseEntity<byte[]> foto(@PathVariable Long id) {
        return imagemRepository.findById(id)
                .map(imagem -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(imagem.getTipoConteudo()))
                        .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
                        .body(imagem.getDados()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
