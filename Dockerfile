FROM gradle:7.6.1-jdk17

RUN apt-get update && apt-get install -qq -y --no-install-recommends

ENV INSTALL_PATH /reactive-flashcards

RUN mkdir $INSTALL_PATH

WORKDIR $INSTALL_PATH
# copiando tudo que está na minha pasta don projeto para dentro do container. No caso como é ambiente de dev não tem problema trabalhar dessa forma. Se fosse ambiente de produção ...
# Poderia usa ruma máquina que fosse só Java gerar o JAR fora do container  e copiar soment eo arquivo JAR que eu fosse usar.
COPY . .
