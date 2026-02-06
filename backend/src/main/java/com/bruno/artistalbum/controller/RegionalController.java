package com.bruno.artistalbum.controller;

import com.bruno.artistalbum.model.Regional;
import com.bruno.artistalbum.service.RegionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/regionais")
public class RegionalController {

    @Autowired
    private RegionalService regionalService;

    @GetMapping
    public ResponseEntity<List<Regional>> listarTodas() {
        return ResponseEntity.ok(regionalService.listarTodasAtivas());
    }

    @PostMapping("/sincronizar")
    public ResponseEntity<Void> sincronizarManual() {
        regionalService.sincronizarRegionais();
        return ResponseEntity.ok().build();
    }
}
