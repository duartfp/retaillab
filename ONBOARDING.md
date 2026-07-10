# Onboarding, RetailLab local environment

Guide to set up the development environment from scratch. Written based on a real setup on a corporate Windows PC without administrator rights, so it covers the most common blockers in that scenario.

## Requirements

- JDK 21 (LTS). Not a newer version, see the note in section 2 about why 21 specifically.
- Maven 3.9+
- Node 22 LTS or higher
- Git
- Docker (via WSL2, see section 6)

## 1. Git

Check if it's already installed:

```
git --version
```

If not, download it from git-scm.com.

## 2. Java (JDK)

Use **JDK 21 LTS specifically**, not a newer version like 24 or 25. This project uses Lombok to generate getters, setters and constructors, and at the time of writing, Lombok's annotation processing silently fails on very recent JDKs (25, in our case), causing confusing "cannot find symbol" compile errors for methods that should have been generated. JDK 21 is stable and fully supported.

### If you have administrator rights

Download the .msi installer from adoptium.net, version 21 LTS, and check the "Add to PATH" and "Set JAVA_HOME variable" options during installation.

### If you do NOT have administrator rights (corporate PC)

The .msi installer usually asks for an admin password. In that case:

1. Go to adoptium.net
2. Use the version selector to pick **21 LTS** (not the latest version shown by default)
3. Download and run the installer anyway. On many corporate PCs it installs without asking for admin rights, landing in `C:\Users\YOUR_USER\AppData\Local\Programs\Eclipse Adoptium\`, even without elevated privileges
4. Once installed, check where it ended up:

```
where.exe /r C:\ java.exe
```

Note: in PowerShell, `where` is an alias for `Where-Object`, a different command. Use `where.exe` explicitly.

5. Note the full path of the folder containing `bin\java.exe` (without the `bin` part), e.g. `C:\Users\YOUR_USER\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.11.10-hotspot`

## 3. Maven

Download the "Binary zip archive" from maven.apache.org/download.cgi and extract it into a folder of your own. Watch out, if your Documents folder is redirected to corporate OneDrive (common in enterprise environments), the final path will include `OneDrive - YourCompanyName`.

## 4. Node

If the nvm-windows installer asks for an admin password and you don't have one, use the zip version instead:

1. Go to nodejs.org/en/download
2. Download the "Windows Binary (.zip)" of the LTS version
3. Extract it into a folder of your own

## 5. Configuring PATH without administrator rights

On corporate PCs, both system and user environment variables can be locked from editing through the graphical interface. The workaround is to configure PATH through the PowerShell profile, which only runs in your session and requires no special permission.

### Step by step

1. Find your profile path:

```
$PROFILE
```

If your company redirects Documents to OneDrive, the path will look something like:
```
C:\Users\YOUR_USER\OneDrive - YourCompanyName\Documents\WindowsPowerShell\Microsoft.PowerShell_profile.ps1
```

2. Check if the file already exists:

```
Test-Path $PROFILE
```

3. If it returns `False`, create the file and the necessary folders:

```
New-Item -Path $PROFILE -Type File -Force
```

4. Open the file:

```
notepad $PROFILE
```

5. Add the lines below, adjusting the paths to the ones you noted in the previous steps:

```powershell
$env:JAVA_HOME = "PATH_TO_YOUR_JDK_21"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$env:Path = "PATH_TO_YOUR_MAVEN\bin;$env:Path"
$env:Path = "PATH_TO_YOUR_NODE;$env:Path"
```

6. Save and close Notepad

### Execution policy error (Restricted)

If, after opening a new terminal, the profile doesn't load (commands like `mvn` still say "not recognized"), PowerShell is likely blocking script execution by default. Check with:

```
Get-ExecutionPolicy
```

If it shows `Restricted`, allow it just for your user, no admin needed:

```
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

Confirm by typing `Y` if prompted. Then close the terminal and open a new one.

## 6. Docker, via WSL2 (no admin rights needed)

Docker Desktop normally requires administrator rights on Windows, since it installs a system service and configures virtualization. If you already have WSL2 available, you can skip Docker Desktop entirely and install the Docker Engine directly inside your Linux distro, where you are typically already a sudoer regardless of your Windows permissions.

1. Confirm you have a WSL distro and that it's WSL2:

```
wsl -l -v
```

2. Enter the distro and confirm you have sudo access:

```
wsl
sudo whoami
```

Should prompt for your Linux password (set when you configured WSL, not your Windows password) and return `root`.

3. Install Docker Engine inside the distro:

```bash
sudo apt update
sudo apt install -y ca-certificates curl gnupg

sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
```

4. Add yourself to the docker group, so you don't need sudo every time:

```bash
sudo usermod -aG docker $USER
```

Close the WSL terminal completely and reopen it for this to take effect.

5. Validate:

```bash
docker --version
docker ps
```

`docker ps` should run without permission errors, returning an empty table.

Note: containers and volumes created inside WSL2 automatically have their ports forwarded to `localhost` on the Windows side, so an application running natively on Windows (like our Spring Boot backend) can connect to `localhost:5432` without any extra configuration.

## 7. Validating the base installation

Close all open terminals, open a new PowerShell window (this is essential, terminals already open won't pick up the updated profile), and run:

```
git --version
java -version
javac -version
mvn -version
node -v
npm.cmd -v
```

### About npm on Windows

If `npm -v` throws a "not digitally signed" or "cannot be loaded" error, it's because the `npm.ps1` file was downloaded from the internet and Windows flags it as blocked, even with execution policy allowed.

Two options:

- Use `npm.cmd` instead of `npm` in every command (simpler, doesn't change anything on the system)
- Or permanently unblock the file:

```
Unblock-File -Path "PATH_TO_NODE_FOLDER\npm.ps1"
```

## 8. Running the project for the first time

Once the tools above are installed, here is the full sequence to get RetailLab running locally after cloning the repository.

1. Clone the repository and check out `develop`:

```
git clone https://github.com/duartfp/retaillab.git
cd retaillab
git checkout develop
```

2. Start the database (run this inside WSL, in the project folder):

```bash
cd "/mnt/c/Users/YOUR_USER/Documents/retaillab"
docker compose up -d
```

Confirm it's running:

```bash
docker ps
```

You should see a `retaillab-postgres` container with status "Up".

3. Run the backend (in PowerShell, on the Windows side):

```
cd backend
mvn spring-boot:run
```

On first run against an empty database, the application automatically seeds fake retail data (categories, products and prices), you don't need to do anything manually. Look for this in the logs:

```
Seeding catalog with fake retail data...
Catalog seeded: 5 categories, 16 products
```

On subsequent runs, it detects existing data and skips seeding.

4. Validate the API is working, open a new terminal and run:

```
curl http://localhost:8080/api/products
```

You should get back a paginated JSON list with 16 products.

### Inspecting the database directly

If you want to run SQL queries against the local database, use psql inside the container (from WSL):

```bash
docker exec -it retaillab-postgres psql -U retaillab -d retaillab
```

Once inside, useful commands:

```
\dt              -- list tables
\d product        -- describe the product table
SELECT * FROM product;
```

Type `\q` to exit.

## 9. Branching

Before making changes, create a feature branch from `develop`, don't commit directly to it. See CONTRIBUTING.md for the full branching model and commit message convention:

```
git checkout -b feature/short-description
```

## Common questions

**"It worked in my old terminal but not in the new one"**
PATH configured directly via `$env:Path` in the terminal (without going through the profile) only lasts for that session. Always double check the lines are actually saved in `$PROFILE`.

**"I need to redo all of this on another laptop"**
Yes, this guide should be redone on every new machine, since we're configuring variables per user session, not globally. That's the trade-off of not depending on administrator permission.

**"Do I need to import a database dump to get the same data as everyone else?"**
No. Nobody shares an actual database. Everyone runs their own local Postgres container (created from the same `docker-compose.yml`, versioned in Git) and the `CatalogSeeder` populates it with the same fake data automatically on first run. The database itself is disposable and never committed to Git, only the code that generates it is.