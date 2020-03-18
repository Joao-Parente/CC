# CC

* Linguagem: Java 

* Ferramenta para trabalho remoto: GitHub

* Estrutura do sistema:

  Trabalho:

  - AnonGW aceita chamadas TCP na porta 80, cria uma thread para tratar dessa chamada, conecta-se a outro  por uma socket UDP AnonGW retransmite pedido a esse AnonGW e esse AnonGW faz o pedido ao server por uma socket TCP e o server responde a este AnonGW que lhe fez o pedido, este comunica a resposta para o AnonGW anteriror e esse por sua vez responde ao cliente.

  1ºFase
     Implementar o sistema sendo que nesta fase a comunicação entre os AnonGW vai ser feita em sockets TCP.
     
     -> Criar Classe AnonGW :

                      a. A escutar na porta 80.
                      
     a. Quando receber chamada na porta 80, cria uma conexão TCP para o cliente e faz o pedido, recebe a resposta e envia-a para o cliente.

