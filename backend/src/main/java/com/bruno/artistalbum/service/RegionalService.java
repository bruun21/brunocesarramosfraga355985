package com.bruno.artistalbum.service;

import com.bruno.artistalbum.model.Regional;
import com.bruno.artistalbum.repository.RegionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RegionalService {

    @Autowired
    private RegionalRepository regionalRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_URL = "https://integrador-argus-api.geia.vip/v1/regionais";

    @Transactional
    @Scheduled(fixedRate = 3600000) // Sincroniza a cada 1 hora
    public void sincronizarRegionais() {
        try {
            // 0. Busca regionais do endpoint
            Regional[] response = restTemplate.getForObject(API_URL, Regional[].class);
            if (response == null)
                return;
            List<Regional> externalRegionais = Arrays.asList(response);

            // 1. Busca regionais ativas atuais no banco
            List<Regional> currentActive = regionalRepository.findByAtivoTrue();
            Map<Integer, Regional> currentMap = currentActive.stream()
                    .collect(Collectors.toMap(Regional::getId, r -> r));

            Set<Integer> externalIds = externalRegionais.stream()
                    .map(Regional::getId)
                    .collect(Collectors.toSet());

            // Regra 1 e 3: Novos ou Alterados
            for (Regional ext : externalRegionais) {
                Regional local = currentMap.get(ext.getId());

                if (local == null) {
                    // Novo no endpoint -> inserir
                    ext.setInternalId(null);
                    ext.setAtivo(true);
                    regionalRepository.save(ext);
                } else if (!local.getNome().equals(ext.getNome())) {
                    // Atributo alterado -> inativar antigo e criar novo registro
                    local.setAtivo(false);
                    regionalRepository.save(local);

                    ext.setInternalId(null);
                    ext.setAtivo(true);
                    regionalRepository.save(ext);
                }
            }

            // Regra 2: Ausente no endpoint -> inativar
            for (Regional local : currentActive) {
                if (!externalIds.contains(local.getId())) {
                    local.setAtivo(false);
                    regionalRepository.save(local);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao sincronizar regionais: " + e.getMessage());
        }
    }

    public List<Regional> listarTodasAtivas() {
        return regionalRepository.findByAtivoTrue();
    }
}
