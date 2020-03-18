# CC

* Linguagem: Java 

* Ferramenta para trabalho remoto: GitHub

* Estrutura do sistema:

  Trabalho:

  - AnonGW aceita chamadas TCP na porta 80, cria uma thread para tratar dessa chamada, conecta-se a outro  por uma socket UDP AnonGW retransmite pedido a esse AnonGW e esse AnonGW faz o pedido ao server por uma socket TCP e o server responde a este AnonGW que lhe fez o pedido, este comunica a resposta para o AnonGW anteriror e esse por sua vez responde ao cliente.


1. Como resolvem o problema da multiplexagem aplicacional (vários clientes ao mesmo tempo) ?

2. O que acontece se a conexão com o cliente TCP  se fecha antes de enviar os dados para o servidor?

3. O que acontece se a conexão com o cliente TCP se fecha antes de receber uma resposta ao servidor?

4. Como é que o GW sabe que o servidor tem que responder alguma coisa e que o cliente tem de esperar?
