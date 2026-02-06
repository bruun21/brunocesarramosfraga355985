#!/bin/bash
HOST="http://localhost:8080"
API="$HOST/api/v1"

echo "--- 1. Login ---"
RES=$(curl -s -X POST $API/auth/login -H "Content-Type: application/json" -d '{"email":"admin@email.com","senha":"admin123"}')
TOKEN=$(echo "$RES" | grep -oP '(?<="token":")[^"]*')
if [ -z "$TOKEN" ]; then echo "Falha no Login: $RES"; exit 1; fi
AUTH="Authorization: Bearer $TOKEN"

echo -e "\n--- 4. Filtro Tipo (CANTOR) ---"
curl -s -H "$AUTH" "$API/artistas?tipo=CANTOR" | grep -oP '"nome":"[^"]*"'

echo -e "\n--- 5. Criar Artista Novo (Teste N:N) ---"
ART_NAME="Artista_NN_$(date +%s)"
ART_RES=$(curl -s -X POST $API/artistas -H "$AUTH" -H "Content-Type: application/json" -d "{\"nome\":\"$ART_NAME\",\"tipo\":\"CANTOR\"}")
ART_ID=$(echo "$ART_RES" | grep -oP '(?<="id":")[^"]*')
echo "Artista Criado: $ART_NAME ($ART_ID)"

echo -e "\n--- 6. Criar Album associado (N:N) ---"
ALB_RES=$(curl -s -X POST $API/albuns -H "$AUTH" -H "Content-Type: application/json" -d "{\"titulo\":\"Album NN Teste\",\"anoLancamento\":2024,\"artistaIds\":[\"$ART_ID\"]}")
ALB_ID=$(echo "$ALB_RES" | grep -oP '(?<="id":")[^"]*')
echo "Album Criado: $ALB_ID"

echo -e "\n--- 7. Verificar Relacionamento no Album ---"
curl -s -H "$AUTH" "$API/albuns/$ALB_ID" | grep -oP '"artistaIds":\["[^"]*"\]'

echo -e "\n--- 8. Filtro Album por Tipo Artista (CANTOR) ---"
curl -s -H "$AUTH" "$API/albuns?tipoArtista=CANTOR" | grep -oP '"titulo":"[^"]*"'

echo -e "\n--- 9. Presigned Upload URL ---"
curl -s -X POST "$API/albuns/$ALB_ID/presigned-upload?extensao=jpg" -H "$AUTH"

echo -e "\n--- 10. Sincronizar Regionais ---"
curl -s -X POST "$API/regionais/sincronizar"
echo -e "\n--- Listar Regionais (3 primeiras) ---"
curl -s "$API/regionais" | grep -oP '"nome":"[^"]*"' | head -n 3
