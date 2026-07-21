# Install the beginner toolchain

[← Application selector](../README.md) · [Foundation](java-spring-foundation.md) · [First project tutorial](first-project-tutorial.md) · [Troubleshooting](troubleshooting.md)

Use this guide if you have never prepared a computer for Java development. The repository uses Java 17 and Maven Wrapper projects, so you need a JDK, Git, and an editor or IDE. You do not need to install Maven separately.

## 1. Understand what you are installing

| Tool | Why you need it | Successful check |
|---|---|---|
| JDK 17 or newer | Compiles and runs Java | `java -version` and `javac -version` report the same major version |
| Git | Downloads projects and records changes | `git --version` prints a version |
| IDE/editor | Edits Java and runs/refactors code | It opens the folder containing `pom.xml` |
| Maven wrapper | Downloads the project-specific Maven version | `./mvnw -version` or `mvnw.cmd -version` works inside a project |

Install a **JDK**, not only a JRE/runtime. The `java` command runs code; `javac` compiles it.

## 2. Install on Windows

> 📍 Use Windows Terminal or PowerShell. Reopen it after an installer changes the environment.

1. Install an Eclipse Temurin JDK 17+ from [Adoptium](https://adoptium.net/temurin/releases/). Select the JDK package and enable the installer options for `PATH` and `JAVA_HOME` when offered.
2. Install [Git for Windows](https://git-scm.com/download/win). The default options are suitable for this guide.
3. Install one editor:
   - [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) Community or Ultimate; or
   - [Visual Studio Code](https://code.visualstudio.com/download/) with Microsoft's **Extension Pack for Java**.
4. Open a new PowerShell window and verify:

```powershell
java -version
javac -version
git --version
```

Inside a generated or starter project use:

```powershell
.\mvnw.cmd -version
.\mvnw.cmd clean verify
```

Before continuing, check: `java` and `javac` are found, their major versions match, and Maven reports that same Java installation.

## 3. Install on macOS

> 📍 Use Terminal. Reopen it after installation.

Install an Eclipse Temurin JDK 17+ from [Adoptium](https://adoptium.net/temurin/releases/) or with Homebrew:

```bash
brew install --cask temurin@17
```

Install Git if `git --version` prompts you to install Apple's command-line tools, or use:

```bash
brew install git
```

Install [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) or [Visual Studio Code](https://code.visualstudio.com/download/) with the **Extension Pack for Java**. Then verify:

```bash
java -version
javac -version
git --version
```

Before continuing, check: Both Java commands report version 17 or newer and your editor can select that JDK for the project.

## 4. Install on Ubuntu or Debian Linux

> 📍 Use a terminal with a user allowed to install packages.

```bash
sudo apt update
sudo apt install openjdk-17-jdk git
```

Install [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) or [Visual Studio Code](https://code.visualstudio.com/download/) with the **Extension Pack for Java**. Then verify:

```bash
java -version
javac -version
git --version
```

For Fedora use `sudo dnf install java-17-openjdk-devel git`. For another distribution, install its complete OpenJDK development package rather than a runtime-only package.

Before continuing, check: `javac` exists and reports the same major version as `java`.

## 5. Configure the editor correctly

> 📍 Open the project root—the directory containing `pom.xml`—not only its `src` folder.

### IntelliJ IDEA

1. Choose **Open** and select the folder containing `pom.xml`.
2. Trust the project and allow Maven import.
3. Open **Project Structure → Project SDK** and select the installed JDK.
4. Wait until dependency indexing finishes before treating red imports as code errors.

### Visual Studio Code

1. Install Microsoft's **Extension Pack for Java**.
2. Choose **File → Open Folder** and select the folder containing `pom.xml`.
3. Accept the Java/Maven project import prompt.
4. Run **Java: Configure Java Runtime** from the Command Palette and select the installed JDK when needed.

Before continuing, check: The generated `*Application.java` opens without unresolved Spring imports after Maven import completes.

## 6. Verify a complete project toolchain

> 📍 Run these commands inside one starter folder, such as `taskboard-api/`.

Linux/macOS:

```bash
java -version
javac -version
./mvnw -version
./mvnw clean verify
```

Windows PowerShell:

```powershell
java -version
javac -version
.\mvnw.cmd -version
.\mvnw.cmd clean verify
```

The first Maven run needs internet access and may take several minutes while dependencies download. Later builds reuse the local cache.

Before continuing, check: Maven ends with `BUILD SUCCESS`. You are ready for the [first project tutorial](first-project-tutorial.md).

## Fix the most common setup failures

| Symptom | Likely cause | Fix |
|---|---|---|
| `java` works but `javac` is not found | Only a runtime/JRE is installed | Install a complete JDK |
| Java versions do not match | Multiple Java installations are on `PATH` | Select one JDK in the OS and IDE; reopen the terminal |
| `JAVA_HOME is not defined correctly` | `JAVA_HOME` points outside the JDK | Point it to the JDK directory, not its `bin` child |
| `mvnw: Permission denied` | Execute permission was lost | Run `chmod +x mvnw` |
| Spring imports stay red | Maven import/indexing is incomplete | Reload the Maven project and confirm network access |
| Port 8080 is already in use | Another application is running | Stop it with `Ctrl+C` or configure another port |

When asking for help, include the operating system, the four version-command outputs, the command you ran, and the first meaningful error. Never post tokens, passwords, or private connection strings.
