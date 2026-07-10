# Onboarding, RetailLab local environment

Guide to set up the development environment from scratch. Written based on a real setup on a corporate Windows PC without administrator rights, so it covers the most common blockers in that scenario.

## Requirements

- JDK 21 or higher (latest LTS, e.g. 25)
- Maven 3.9+
- Node 22 LTS or higher
- Git

## 1. Git

Check if it's already installed:

```
git --version
```

If not, download it from git-scm.com.

## 2. Java (JDK)

### If you have administrator rights

Download the .msi installer from adoptium.net, latest LTS version, and check the "Add to PATH" and "Set JAVA_HOME variable" options during installation.

### If you do NOT have administrator rights (corporate PC)

The .msi installer usually asks for an admin password. In that case:

1. Go to adoptium.net
2. Download the **.msi** version anyway, or run the standard installer. On many corporate PCs it installs without asking for admin rights, landing in `C:\Users\YOUR_USER\AppData\Local\Programs\Eclipse Adoptium\`, even without elevated privileges
3. Once installed, check where it ended up:

```
where /r C:\ java.exe
```

4. Note the full path of the folder containing `bin\java.exe` (without the `bin` part)

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
$env:JAVA_HOME = "PATH_TO_YOUR_JDK"
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

## 6. Validating the installation

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

## 7. Expected result

At the end, every command in step 6 should return its respective version with no error. Expected output example:

```
git version 2.x.x
openjdk version "21.x.x" or higher
Apache Maven 3.9.x
v22.x.x (or higher)
11.x.x
```

## Common questions

**"It worked in my old terminal but not in the new one"**
PATH configured directly via `$env:Path` in the terminal (without going through the profile) only lasts for that session. Always double check the lines are actually saved in `$PROFILE`.

**"I need to redo all of this on another laptop"**
Yes, this guide should be redone on every new machine, since we're configuring variables per user session, not globally. That's the trade-off of not depending on administrator permission.
