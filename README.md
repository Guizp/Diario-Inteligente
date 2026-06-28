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
  <img src="https://github.com/user-attachments/assets/f6c67a37-1f31-4b8b-8577-3d0013f3cd65" width="150"/>
  <img src="https://github.com/user-attachments/assets/c5c4af31-5a66-4243-8e77-ae80512f6db7" width="150"/>
  <img src="https://github.com/user-attachments/assets/2f8380dd-468a-41d0-ac90-05984c20722b" width="150"/>
  <img src="https://github.com/user-attachments/assets/3c60af29-f96f-4ead-aa0d-de53476affb1" width="150"/>
</p>

### 🌙 Modo Escuro (Português)
<p align="left">
  <img src="https://github.com/user-attachments/assets/ac590f21-1213-48f2-af78-9168a57552c1" width="150"/>
  <img src="https://github.com/user-attachments/assets/cceb1190-2380-4a2d-85e3-18c8a21d2e29" width="150"/>
  <img src="https://github.com/user-attachments/assets/f724c77c-2bde-48f7-a99d-3d7a99d6a4df" width="150"/>
  <img src="https://github.com/user-attachments/assets/9cc4b1aa-3356-43b5-9b6c-b4cbb055b3c2" width="150"/>
</p>

---

## 🎥 Demonstração em Vídeo

<video src="https://github.com/user-attachments/assets/b4848846-979f-4138-a536-ffd203554e5b" width="100%" controls></video>
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
