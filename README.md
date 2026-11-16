# Cache de Conteúdo

Integrantes: Laura e Munir

## Como o cache funciona

O cache foi construído com ConcurrentHashMap, sem biblioteca
de cache.

Na primeira busca, o conteúdo é consultado no banco com um
atraso de dois segundos e armazenado no cache (MISS).
Nas próximas buscas, se ainda estiver válido, ele é retornado
diretamente do cache (HIT).

O limite é de cinco conteúdos e o TTL é de 60 segundos.
Esses valores podem ser alterados no application.properties.

Quando o cache está cheio, o conteúdo que está há mais tempo
sem ser acessado é removido, seguindo a política LRU.

Ao atualizar ou excluir um conteúdo, sua entrada é removida
do cache.

O GET /conteudos/{id} retorna o header X-Cache com HIT ou MISS.
As buscas também são registradas no log com ID e tempo.

As estatísticas podem ser consultadas em GET /cache/estatisticas.
Para limpar o cache, utilize DELETE /cache.