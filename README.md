# Exemplo de como implementar testes unitários

## Sobre 

Este projeto tenta mostrar na pratica como adicionar testes unitários efetivos em projetos utilizando mokito, junit5  e spring boot.

> Este projeto não segue padrões de arquiteturas mais avançadas como clean archtecture, Hexagonal archtecture, ou princípios do SOLID, pois o intuito é ser simples e focar na atuação efetiva dos testes

### Swagger
Após iniciar a aplicaçao rest que por padrão se encontra na porta 8081 basta acessar o link http://localhost:8081/swagger-ui/index.html para acessar a documentação rest.



### Cobertura de código
Este projeto usa jacoco para análise de cobertura de testes para executar os testes e o relatório de cobertura basta rodar o cammando abaixo:

```bash
gradle jacocoTestReport
```

> Após rodar as tasks do jacoco acesso o relatório em HTML gerado neste próprio projeto em  `./build/reports/jacoco/test/html/index.html`

