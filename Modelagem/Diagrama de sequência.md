```mermaid

sequenceDiagram
  Usuário --> site: get() atividade
  site-->recomendador de atividade: procurar()atividade
  break when the booking process fails
      site-->Usuário: erro
  end
  

```
