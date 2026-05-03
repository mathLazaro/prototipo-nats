# Protótipo NATS - Sistema Distribuído com Fila de Mensagens

Um sistema distribuído prototype demonstrando comunicação assíncrona usando **NATS** (messaging system) e padrões de fila de mensagens. O projeto implementa múltiplos servidores (workers) que se comunicam através de topics e filas, com suporte a operações de arquivo, cálculo de distância entre flores e busca de piadas.

## 📋 Visão Geral

Este projeto implementa uma arquitetura distribuída com três componentes principais:

- **Client**: Aplicação cliente com interface para execução de operações
- **Main-Server**: Servidor principal responsável por orquestração e processamento de mensagens
- **File-Server**: Servidor especializado em operações de arquivo (CRUD)

Todos os componentes se comunicam através do **NATS**, um sistema de mensageria de alto desempenho.

---

## 🔧 Configuração Inicial

### 1. Configurar Credenciais GitHub (Maven Settings)

As bibliotecas externas são hospedadas em repositórios privados do GitHub. Você precisa autenticar para fazer o download das dependências.

#### Passo 1: Gerar Personal Access Token (PAT)

1. Acesse [GitHub Settings → Developer settings → Personal access tokens](https://github.com/settings/tokens)
2. Clique em **Generate new token (classic)**
3. Dê um nome descritivo: `maven-packages`
4. Selecione o escopo `read:packages`
5. Clique em **Generate token**
6. **Copie o token** (você não poderá vê-lo novamente)

#### Passo 2: Criar/Atualizar o arquivo `settings.xml` em `~/.m2/`

O Maven procura por configurações no diretório pessoal do usuário. Crie ou edite o arquivo `settings.xml` em `~/.m2/`:

```bash
# Criar diretório .m2 se não existir
mkdir -p ~/.m2

# Criar/editar o arquivo settings.xml
nano ~/.m2/settings.xml
```

Adicione suas credenciais ao arquivo `~/.m2/settings.xml`:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <server>
      <id>github</id>
      <username>seuUsuarioGitHub</username>
      <password>seu_personal_access_token</password>
    </server>
  </servers>
</settings>
```

**⚠️ Importante**: Este arquivo (`~/.m2/settings.xml`) contém suas credenciais de acesso. **Nunca** o comita no repositório. O arquivo `settings.xml` fornecido no projeto é apenas um exemplo (`settings.example.xml`).

---

## 📦 Dependências e Versão do Java

### Requisitos do Sistema

- **Java**: 25 (OpenJDK 25 ou superior)
- **Maven**: 3.8.1 ou superior
- **NATS Server**: 2.10.0 ou superior (para executar localmente)

### Dependências Principais

| Dependência | Versão | Propósito |
|---|---|---|
| **jnats** | 2.16.0 | Cliente NATS para Java |
| **nats-conn-manager** | 1.0.1 | Gerenciador de conexão NATS (lib customizada) |
| **atividade-rmi-shared** | 1.0.0 | Modelos compartilhados (DTOs, utilitários) |
| **jackson-databind** | 2.17.1 | Serialização/Desserialização JSON |
| **log4j-core** | 2.23.1 | Logging estruturado |
| **lombok** | 1.18.38 | Redução de boilerplate (anotações) |

#### Repositórios de Bibliotecas Externas

- **nats-conn-manager**: https://github.com/mathLazaro/nats-conn-manager
- **atividade-rmi-shared**: https://github.com/mathLazaro/atividade-rmi-shared

---

## 🚀 Como Executar o Projeto

### Pré-requisitos

1. **Instalar e rodar NATS Server**

   ```bash
   # Via Docker (recomendado)
   docker run -d -p 4222:4222 -p 8222:8222 nats:latest
   
   # Ou via apt-get (Ubuntu/Debian)
   sudo apt-get install nats-server
   nats-server
   ```

   O servidor NATS estará disponível em `nats://localhost:4222`.

2. **Variável de Ambiente (Opcional)**

   Se usar um NATS em URL diferente:

   ```bash
   export NATS_URL=nats://seu-host:porta
   ```

   Se não definir, o padrão é `nats://localhost:4222`.

### Build do Projeto

Na raiz do projeto (`prototipo-nats/`), execute:

```bash
mvn clean package
```

Maven automaticamente usará as credenciais configuradas em `~/.m2/settings.xml`. Isso compilará todos os módulos (client, main-server, file-server) e gerará JARs em `target/`.

### Executar Cada Worker

#### 1. **Main-Server** (Orquestrador e Processador de Piadas)

```bash
cd servers/main-server/
mvn exec:java
```

Ou com URL customizada do NATS:

```bash
NATS_URL=nats://seu-host:4222 mvn exec:java
```

#### 2. **File-Server** (Servidor de Arquivos)

```bash
cd servers/file-server/
mvn exec:java
```

#### 3. **Client** (Application Cliente)

```bash
cd client/
mvn exec:java
```

A interface do cliente abrirá um menu interativo no terminal.

---

## 🏗️ Arquitetura e Componentes

### Fluxo de Comunicação

```
┌─────────────┐                    ┌──────────────┐
│   CLIENT    │                    │  NATS SERVER │
│             │◄──────subjects─────►              │
│  (Menu)     │                    │  (broker)    │
└─────────────┘                    └──────────────┘
                                           ▲
                                           │
                                           │
                                           │
                                           │
                                           │
                ┌──────────────────────────┤
                │                          │
         ┌──────▼──────┐          ┌────────▼────────┐
         │ MAIN-SERVER │          │  FILE-SERVER(S) │
         │             │          │                 │
         │ • Piadas    │          │ • Save File     │
         │ • Cálculos  │          │ • Read File     │
         │ • Processos │          │ • Append File   │
         │             │          │ • Delete File   │
         └─────────────┘          └─────────────────┘
         (1 instância)             (N instâncias)
```

### Cada Worker

#### **Client** 🖥️

**O que faz**: Interface interativa para o usuário executar operações no sistema.

**Responsabilidades**:
- Apresentar menu com opções de operação (Piadas, Flores, Arquivos)
- Serializar requisições e publicar em topics NATS
- Receber e exibir respostas dos servidores
- Gerir interação com usuário

**Subjects (Topics) NATS**:
- **Publicar para**: `joke.fetch`, `flower.distance`, `file.save`, `file.read`, `file.append`, `file.delete`
- **Subscribir em**: `*` (todos os retornos — via request-reply inbox)

**Padrões de Comunicação**:
- **Piadas e Flores**: Request-Reply (aguarda resposta)
- **Arquivos**: Publish (não aguarda resposta)

---

#### **Main-Server** 🎯

**O que faz**: Orquestrador central e processador de piadas e cálculos de distância.

**Responsabilidades**:
- Processar requisições de piadas (integração com API externa)
- Executar cálculos de distância entre coordenadas (fórmulas geométricas)
- Usar padrão Request-Reply para retornar respostas
- Gerenciar lógica de negócio complexa

**Subjects NATS**:
- **Subscribe em**: `joke.fetch` (default queue: `piada-queue`)
- **Subscribe em**: `flower.distance` (default queue: `flower-queue`)
- **Publish para**: inbox de resposta (reply_to)

**Exemplo de Fluxo**:
```
Client publica: { type: "programming", replyTo: "inbox_123" }
  ↓
Main-Server recebe em "joke.fetch"
  ↓
Main-Server integra com API externa
  ↓
Main-Server publica resposta em "inbox_123"
  ↓
Client recebe resposta
```

---

#### **File-Server** 📁

**O que faz**: Servidor especializado em persistência de arquivos.

**Responsabilidades**:
- Salvar arquivos em disco (pasta `TEMP/`)
- Ler conteúdo de arquivos
- Adicionar dados ao final de arquivos (append)
- Deletar arquivos
- Suportar múltiplas instâncias para escalabilidade

**Subjects NATS**:
- **Subscribe em**: `file.save`, `file.read`, `file.append`, `file.delete` (todos em queue: `file-queue`)
- **Não publica resposta** (padrão fire-and-forget)

**Padrão de Armazenamento**:
- Todos os arquivos armazenados em: `servers/file-server/TEMP/`
- Pode ser replicado (múltiplas instâncias do file-server leem/escrevem no mesmo TEMP via NFS ou cloud storage)

**Exemplo de Fluxo**:
```
Client publica: { operation: "SAVE", fileName: "dados.txt", data: byte[] }
  ↓
File-Server 1 recebe em "file.save" (queue "file-queue")
  ↓
File-Server 1 salva em TEMP/dados.txt
  ↓
(Se há File-Server 2 e 3, apenas um processa — NATS balancea a carga)
```

---

## 📊 Padrão de Dados - EventDTO

As mensagens padronizadas de resposta/erro entre workers usam **EventDTO** da lib `nats-conn-manager`:

```java
public record EventDTO<T>(String status, T payload, String error) {}
```

- `status`: resultado da operação (`SUCCESS` ou `ERROR`)
- `payload`: conteúdo de sucesso (tipo genérico `T`)
- `error`: mensagem de erro quando aplicável

Helpers disponíveis na própria classe:

```java
EventDTO.success(payload)
EventDTO.error("mensagem de erro")
```

### Serialização/Desserialização

- **Framework**: Jackson (com ObjectMapper)
- **Formato**: JSON byte array
- **Encoding**: UTF-8
- **Tratamento**: Logger estruturado com Log4j2

### Exemplo de Publicação (Server → Client)

```java
EventDTO<JokeResponseDTO> sucesso = EventDTO.success(jokeDTO);
EventDTO<Void> erro = EventDTO.error("Tipo de piada inválido");

connection.publish(replyTo, objectMapper.writeValueAsBytes(sucesso));
```

### Exemplo de Recebimento (Server)

```java
EventDTO<?> event = objectMapper.readValue(msg.getData(), EventDTO.class);

if ("SUCCESS".equals(event.status())) {
    log.info("Payload recebido com sucesso: {}", event.payload());
} else {
    log.error("Erro recebido: {}", event.error());
}
```

---

## 📂 Estrutura de Diretórios

```
prototipo-nats/
├── README.md                    # Este arquivo
├── settings.example.xml         # Template de credenciais (exemplo)
│
├── client/                      # Módulo cliente
│   ├── pom.xml
│   ├── src/main/java/com/github/mathlazaro/
│   │   ├── MainClient.java      # Entry point
│   │   ├── controller/          # Controllers (lógica)
│   │   │   ├── FileController.java
│   │   │   ├── FlowerController.java
│   │   │   └── MessageController.java
│   │   ├── model/               # DTOs e enums
│   │   │   ├── file/
│   │   │   ├── flower/
│   │   │   ├── main/
│   │   │   └── message/
│   │   └── view/                # Interface (View)
│   │       ├── FileView.java
│   │       ├── FlowerView.java
│   │       ├── MainView.java
│   │       ├── MessageView.java
│   │       └── View.java
│   └── target/
│
└── servers/                     # Servidores workers
    ├── main-server/             # Módulo main-server
    │   ├── pom.xml
    │   ├── src/main/java/com/github/mathlazaro/
    │   │   ├── MainServer.java  # Entry point
    │   │   ├── messaging/       # Message handlers
    │   │   ├── service/         # Lógica de negócio
    │   │   └── model/           # DTOs
    │   ├── resources/
    │   │   └── log4j2.xml       # Config logging
    │   └── target/
    │
    └── file-server/             # Módulo file-server
        ├── pom.xml
        ├── src/main/java/com/github/mathlazaro/
        │   ├── MainFileServer.java # Entry point
        │   ├── messaging/       # File handlers
        │   ├── service/         # FileService (CRUD)
        │   ├── model/           # RequestFileDTO
        │   └── resources/
        │       └── log4j2.xml
        ├── TEMP/                # Pasta compartilhada (arquivos)
        └── target/
```

---

## 🔍 Logging

Todos os componentes usam **Log4j2** com nível `INFO`:

```
[2024-05-03 14:25:30] [INFO] client: Publicando piada de tipo: programming
[2024-05-03 14:25:31] [INFO] main-server: Buscando piada da API...
[2024-05-03 14:25:32] [INFO] main-server: Piada recebida. Retornando ao client.
[2024-05-03 14:25:32] [INFO] client: Resposta recebida: "Why do programmers..."
```

Logs são em **português** para melhor compreensão em contexto educacional.

---

## 🎯 Casos de Uso

### 1. Piadas (Request-Reply)
```
Client → Main-Server: "Buscar piada de programação"
Client ← Main-Server: "A piada é..."
```

### 2. Cálculo de Distância (Request-Reply)
```
Client → Main-Server: "Calcular distância entre flores"
Client ← Main-Server: "Distância = X metros"
```

### 3. Salvar Arquivo (Publish)
```
Client → File-Server (fila): "Salvar arquivo.txt"
(Sem resposta - apenas persistência)
```

### 4. Replicação de Workers
```
Client → (NATS Fila)
         ├→ File-Server Instância 1 ✓ (processa)
         ├→ File-Server Instância 2 (aguarda próxima)
         └→ File-Server Instância 3 (aguarda próxima)
```

---

## 🐛 Troubleshooting

### "Connection refused: NATS_URL"
**Solução**: Certifique-se de que o NATS Server está rodando:
```bash
docker ps | grep nats
# ou
nats-server
```

### "Failed to download dependency from github"
**Solução**: Verifique o `settings.xml` com credenciais corretas:
```bash
mvn dependency:tree -s settings.xml
```

### "Main-Server não recebe mensagens"
**Solução**: Verifique se a fila está configurada corretamente:
```bash
# Verificar via NATS CLI
nats sub -s nats://localhost:4222 "joke.fetch"
```

### "Arquivo não persiste em File-Server"
**Solução**: Verifique permissões da pasta `TEMP/`:
```bash
ls -la servers/file-server/TEMP/
# Se não existir, criar manualmente
mkdir -p servers/file-server/TEMP/
```

---

## 📚 Referências

- **NATS.io Documentation**: https://docs.nats.io/
- **NATS Java Client**: https://github.com/nats-io/nats.java
- **Jackson JSON Processor**: https://github.com/FasterXML/jackson
- **Apache Log4j 2**: https://logging.apache.org/log4j/2.x/

---

## 📝 Notas de Desenvolvimento

- Todos os componentes implementam padrão de singleton para gerenciadores
- Configuração centralizada via `NatsConnectionManager` (lib customizada)
- Suporte a múltiplas instâncias de File-Server para escalabilidade horizontal
- Logging estruturado facilita debug distribuído

---

**Criado em**: 3 de Maio de 2024  
**Versão**: 1.0-SNAPSHOT  
**Linguagem**: Java 25  






