# Entrega-Aplicativo-02

Este repositório contém o código e os materiais referentes ao **Aplicativo 02 - MeuCine** da disciplina DESENVOLVIMENTO PARA DISPOSITIVOS MÓVEIS. O MeuCine é um aplicativo Android para organizar uma lista pessoal de filmes e séries. O usuário cadastra o título, o tipo (filme ou série) e uma imagem, e marca se já assistiu ou não. Na tela Minha Lista ele visualiza todos os itens com imagem e status, pode buscar por nome, filtrar por status e acompanhar quantos já assistiu. Os dados ficam salvos em um banco SQLite local e permanecem após fechar o app ou reiniciar o dispositivo.

Regras de Negócio:
- Status de visualização: Cada filme ou série possui um status, “Assistido” ou “Não assistido”, definido pelo usuário no cadastro e alterável na edição. O status é gravado no banco e exibido em cada item da lista.
- Ordenação por nome: Os itens são exibidos em ordem alfabética por nome. A ordenação é feita no SQL (ORDER BY nome) e vale também quando algum filtro está ativo.
- Filtro de listagem: permite filtrar todos, buscar por item ou filtro de status (assistido/não assistido).

## Integrantes do Grupo

- **João Alberto François**  
  GRR20211640
- **Lucas Labendzs**  
  GRR20245121
- **Renan Teles de Oliveira**  
  GRR20246258
- **Kauan Calegari**  
  GRR20243595
