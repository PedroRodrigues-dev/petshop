#!/bin/bash

set -e

get_project_name() {
    grep "rootProject.name" settings.gradle | cut -d "'" -f2
}

get_project_version() {
    grep "^version" build.gradle | cut -d "'" -f2
}

APP_NAME=$(get_project_name)
VERSION=$(get_project_version)

BASE_DIR=$(pwd)

JAR_DIR="build/libs"

echo "=== Etapa 1: Preparando o projeto ==="
echo "Nome da aplicação: $APP_NAME"
echo "Versão da aplicação: $VERSION"
echo "Limpando diretórios antigos..."
./gradlew clean

echo "=== Etapa 2: Compilando o projeto ==="
echo "Realizando o build com Gradle..."
./gradlew build -x test

JAR_FILE=$(find "$JAR_DIR" -type f -name "${APP_NAME}-${VERSION}.jar" ! -name "*-plain.jar" | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo "Erro: Nenhum arquivo JAR válido encontrado (excluindo *-plain.jar)."
    exit 1
fi

echo "Arquivo JAR selecionado: $JAR_FILE"

cp "$JAR_FILE" app.jar

echo "=== Etapa 3: Construindo a imagem Docker ==="
echo "Criando a imagem Docker..."
docker build -t ${APP_NAME}:${VERSION} .

rm -f app.jar

echo "=== Etapa 4: Concluído ==="
echo "A imagem Docker ${APP_NAME}:${VERSION} foi criada com sucesso!"