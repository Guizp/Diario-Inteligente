# ⏰ Smart-Reminder - Gerenciador Inteligente de Tarefas

## 📱 Descrição
O **Smart-Reminder** é um aplicativo Android nativo desenvolvido em **Kotlin** projetado para ajudar os usuários a organizar sua rotina de forma prática e eficiente. O app oferece um sistema completo de gerenciamento de tarefas (CRUD), permitindo criar, visualizar, editar e excluir lembretes com uma interface fluida e intuitiva.

O projeto foi construído seguindo boas práticas de arquitetura de software, garantindo uma separação clara de responsabilidades e facilitando a escalabilidade do código.

O app oferece uma interface moderna e adaptável, permitindo que o usuário:
- ✍️ **Crie novos lembretes** informando título, descrição e prazos;
- 📋 Visualize a **Listagem completa** de tarefas pendentes em uma interface limpa;
- 🔍 Acesse os **Detalhes do lembrete** para conferir informações completas;
- ✏️ **Edite dados existentes** através de uma tela dedicada com mapeamento dinâmico;
- 🗑️ **Remova tarefas** concluídas ou canceladas de forma simples;
- 🌗 Desfrute de suporte total ao **Modo Escuro (Dark Mode)** através do DayNight Theme;
- 🌎 Alterne entre os idiomas **Português e Inglês** (i18n) de forma nativa.

---

## 📸 Visualização (Screenshots)

### ☀️ Modo Claro (Português)
<p align="left">
  <img src="imagens/lista_modo-claro.png" width="150"/>
  <img src="imagens/criar_modo-claro.png" width="150"/>
  <img src="imagens/detalhes_modo-claro.png" width="150"/>
  <img src="imagens/editar_modo-claro.png" width="150"/>
</p>

### 🌙 Modo Escuro (Português)
<p align="left">
  <img src="imagens/lista_modo-escuro.png" width="150"/>
  <img src="imagens/criar_modo-escuro.png" width="150"/>
  <img src="imagens/detalhes_modo-escuro.png" width="150"/>
  <img src="imagens/editar_modo-escuro.png" width="150"/>
</p>

### English Mode (Light)
<p align="left">
  <img src="imagens/lista_ingles.png" width="150"/>
  <img src="imagens/criar_ingles.png" width="150"/>
  <img src="imagens/detalhes_ingles.png" width="150"/>
  <img src="imagens/editar_ingles.png" width="150"/>
</p>

---

## 🎥 Demonstração em Vídeo
📹 **[Clique Aqui - Demonstração do App](imagens/video_app.webm)**

---

## 🧩 Funcionalidades Técnicas & Arquitetura
- **Arquitetura Organizada por Pacotes:** Divisão limpa do projeto para melhor manutenção do código:
  - `model`: Definição das entidades e classes de dados dos lembretes;
  - `repository`: Camada responsável pelo gerenciamento, persistência e fluxo dos dados;
  - `ui`: Telas, Adapters e gerenciamento da interface com o usuário.
- **Gerenciamento de Estado (CRUD):** Implementação completa dos fluxos de criação, leitura, atualização e deleção de dados;
- **Navegação Segura:** Fluxo dinâmico entre a tela principal, tela de detalhes e telas de edição/cadastro, transportando os dados das tarefas com segurança;
- **Arquitetura Android:** Desenvolvimento estruturado utilizando **ViewBinding** para evitar erros de referência em layouts;
- **Listagem Otimizada:** Exibição dos lembretes de forma otimizada utilizando **RecyclerView** e *Custom Adapters*;
- **Suporte a Temas:** Layout adaptável para consistência visual em **DayNight Theme** (Modo Claro/Escuro);
- **Internacionalização (i18n):** Estrutura de strings mapeada minuciosamente para suporte nativo a múltiplos idiomas.

---

## 🛠️ Tecnologias Utilizadas
- **Linguagem:** [Kotlin](https://kotlinlang.org/)
- **IDE:** Android Studio
- **Interface:** XML + Material Design Components
- **Componentes:** ConstraintLayout, CardView, RecyclerView, ViewBinding, Navigation Component

---

## 🚀 Como executar o projeto
1. Clone este repositório:
   ```bash
   git clone [https://github.com/Guizp/Smart-Reminder.git](https://github.com/Guizp/Smart-Reminder.git)```
2. Abra o projeto no Android Studio.
3. Aguarde a sincronização do Gradle.
4. Execute o app em um emulador ou dispositivo físico.
