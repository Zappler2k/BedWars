<div align="center">

# 🛏️ BedWars Plugin

<img src="https://via.placeholder.com/600x200/4f46e5/ffffff?text=BedWars+Plugin" alt="BedWars Plugin Banner" width="600"/>

<p>
  <img src="https://img.shields.io/badge/version-1.0.0-blue.svg" alt="Version"/>
  <img src="https://img.shields.io/badge/spigot-1.8--1.20-orange.svg" alt="Spigot"/>
  <img src="https://img.shields.io/badge/license-MIT-green.svg" alt="License"/>
  <img src="https://img.shields.io/badge/downloads-10K+-brightgreen.svg" alt="Downloads"/>
</p>

**Ein professionelles und funktionsreiches BedWars Plugin für Minecraft Server**

<p>
  <a href="#installation"><img src="https://img.shields.io/badge/📥-Download-4f46e5?style=for-the-badge" alt="Download"/></a>
  <a href="#documentation"><img src="https://img.shields.io/badge/📖-Wiki-7c3aed?style=for-the-badge" alt="Wiki"/></a>
  <a href="#support"><img src="https://img.shields.io/badge/💬-Discord-ec4899?style=for-the-badge" alt="Discord"/></a>
</p>

</div>

---

## ✨ Features

<table>
<tr>
<td width="33%" align="center">

### 🎮 Gameplay
- 🛏️ **Klassisches BedWars** - Schütze dein Bett!
- 👥 **Team-Modi** - 4v4v4v4, 2v2v2v2v2v2v2v2, Solo
- 🏪 **Shop-System** - Eisen, Gold, Diamanten
- ⚔️ **Team-Upgrades** - Bessere Ausrüstung
- 📊 **Statistiken** - Detaillierte Player-Stats

</td>
<td width="33%" align="center">

### 🛠️ Administration
- 🗺️ **Multi-Arena** - Unbegrenzte Arenen
- ⚙️ **Konfigurierbar** - Vollständig anpassbar
- 💾 **MySQL Support** - Datenbank-Integration
- 🌐 **Multi-Language** - Mehrsprachig
- ⚡ **Performance** - Server-optimiert

</td>
<td width="33%" align="center">

### 🎯 Besondere Features
- 🔥 **Custom Items** - TNT-Jump, Brücken-Ei
- 🛡️ **Anti-Cheat** - Schutz vor Cheats
- 🎪 **Events** - Spezielle Belohnungen
- 🔌 **API** - Für Entwickler
- 📈 **Rankings** - Leaderboards

</td>
</tr>
</table>

---

## 📋 Anforderungen

<div align="center">

<table>
<tr>
<td align="center" width="25%">
<img src="https://via.placeholder.com/80x80/4f46e5/ffffff?text=🎮" alt="Minecraft" width="80"/><br/>
<b>Minecraft</b><br/>
1.8.8 - 1.20.x
</td>
<td align="center" width="25%">
<img src="https://via.placeholder.com/80x80/7c3aed/ffffff?text=⚙️" alt="Server" width="80"/><br/>
<b>Server</b><br/>
Spigot, Paper, Bukkit
</td>
<td align="center" width="25%">
<img src="https://via.placeholder.com/80x80/ec4899/ffffff?text=☕" alt="Java" width="80"/><br/>
<b>Java</b><br/>
Version 8+
</td>
<td align="center" width="25%">
<img src="https://via.placeholder.com/80x80/10b981/ffffff?text=💾" alt="RAM" width="80"/><br/>
<b>RAM</b><br/>
Mindestens 2GB
</td>
</tr>
</table>

</div>

### 🔌 Abhängigkeiten
- **Vault** (Optional) - Economy Integration
- **PlaceholderAPI** (Optional) - Platzhalter Support
- **WorldEdit** (Optional) - Arena Setup

---

## 📥 Installation

<details>
<summary><b>🚀 Schnelle Installation (Klicken zum Erweitern)</b></summary>

<table>
<tr>
<td align="center" width="20%">
<img src="https://via.placeholder.com/60x60/4f46e5/ffffff?text=1" alt="Step 1" width="60"/><br/>
<b>Download</b><br/>
Plugin herunterladen
</td>
<td align="center" width="20%">
<img src="https://via.placeholder.com/60x60/7c3aed/ffffff?text=2" alt="Step 2" width="60"/><br/>
<b>Server Stop</b><br/>
Server stoppen
</td>
<td align="center" width="20%">
<img src="https://via.placeholder.com/60x60/ec4899/ffffff?text=3" alt="Step 3" width="60"/><br/>
<b>Install</b><br/>
In plugins/ kopieren
</td>
<td align="center" width="20%">
<img src="https://via.placeholder.com/60x60/10b981/ffffff?text=4" alt="Step 4" width="60"/><br/>
<b>Start</b><br/>
Server starten
</td>
<td align="center" width="20%">
<img src="https://via.placeholder.com/60x60/f59e0b/ffffff?text=5" alt="Step 5" width="60"/><br/>
<b>Config</b><br/>
Konfigurieren
</td>
</tr>
</table>

```bash
# Schnelle Installation
wget https://github.com/username/bedwars-plugin/releases/latest/download/BedWars.jar
mv BedWars.jar plugins/
```

</details>

---

## 🎮 Befehle & Permissions

<details>
<summary><b>👤 Spieler Befehle</b></summary>

| Befehl | Beschreibung | Permission |
|--------|--------------|------------|
| `/bw join <arena>` | Betrete eine Arena | `bedwars.play` |
| `/bw leave` | Verlasse das Spiel | `bedwars.play` |
| `/bw stats [player]` | Zeige Statistiken | `bedwars.stats` |
| `/bw shop` | Öffne den Shop | `bedwars.play` |

</details>

<details>
<summary><b>🛠️ Admin Befehle</b></summary>

| Befehl | Beschreibung | Permission |
|--------|--------------|------------|
| `/bw arena create <name>` | Erstelle Arena | `bedwars.admin` |
| `/bw arena delete <name>` | Lösche Arena | `bedwars.admin` |
| `/bw reload` | Plugin neu laden | `bedwars.admin` |
| `/bw forcestart <arena>` | Spiel force-starten | `bedwars.admin` |

</details>

---

## ⚙️ Konfiguration

<details>
<summary><b>🗺️ Arena Setup</b></summary>

```bash
# 1. Erstelle eine neue Arena
/bw arena create <arena-name>

# 2. Setze die Spawn-Punkte für Teams
/bw arena setspawn <arena> <team> <x> <y> <z>

# 3. Setze die Bett-Positionen
/bw arena setbed <arena> <team> <x> <y> <z>

# 4. Konfiguriere Spawner
/bw arena addspawner <arena> <type> <x> <y> <z>

# 5. Arena aktivieren
/bw arena enable <arena>
```

</details>

<details>
<summary><b>📁 Config-Dateien</b></summary>

```
plugins/BedWars/
├── config.yml          # Haupt-Konfiguration
├── messages.yml         # Nachrichten und Sprache
├── shop.yml            # Shop-Items und Preise
├── upgrades.yml        # Team-Upgrades
└── arenas/
    ├── arena1.yml      # Arena-spezifische Einstellungen
    └── arena2.yml
```

</details>

---

## 📊 Statistiken & Belohnungen

<div align="center">

### 🏆 Spieler-Stats
<table>
<tr>
<td align="center">📈<br/><b>Spiele</b></td>
<td align="center">🏆<br/><b>Siege</b></td>
<td align="center">⚔️<br/><b>K/D</b></td>
<td align="center">🛏️<br/><b>Betten</b></td>
<td align="center">📊<br/><b>Ratio</b></td>
</tr>
</table>

</div>

<details>
<summary><b>🎁 Belohnungssystem</b></summary>

```yaml
# Beispiel Konfiguration
rewards:
  win:
    money: 100
    exp: 50
    commands:
      - "give {player} diamond 1"
  kill:
    money: 10
    exp: 5
```

</details>

---

## 🌐 API für Entwickler

<details>
<summary><b>📦 Maven Integration</b></summary>

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>bedwars-api</artifactId>
    <version>1.0.0</version>
</dependency>
```

</details>

<details>
<summary><b>🔧 Event Beispiel</b></summary>

```java
@EventHandler
public void onGameStart(BedWarsGameStartEvent event) {
    Game game = event.getGame();
    // Deine Logik hier
}
```

</details>

---

## 🤝 Support

<div align="center">

<table>
<tr>
<td align="center" width="25%">
<a href="https://discord.gg/example">
<img src="https://via.placeholder.com/100x100/5865f2/ffffff?text=💬" alt="Discord" width="100"/><br/>
<b>Discord</b><br/>
Community Chat
</a>
</td>
<td align="center" width="25%">
<a href="https://github.com/username/bedwars-plugin/wiki">
<img src="https://via.placeholder.com/100x100/24292e/ffffff?text=📖" alt="Wiki" width="100"/><br/>
<b>Wiki</b><br/>
Dokumentation
</a>
</td>
<td align="center" width="25%">
<a href="https://github.com/username/bedwars-plugin/issues">
<img src="https://via.placeholder.com/100x100/d73a49/ffffff?text=🐛" alt="Issues" width="100"/><br/>
<b>Issues</b><br/>
Bug Reports
</a>
</td>
<td align="center" width="25%">
<a href="https://youtube.com/playlist">
<img src="https://via.placeholder.com/100x100/ff0000/ffffff?text=📺" alt="YouTube" width="100"/><br/>
<b>YouTube</b><br/>
Tutorials
</a>
</td>
</tr>
</table>

</div>

---

## 🏗️ Entwicklung

<details>
<summary><b>🔧 Build from Source</b></summary>

```bash
git clone https://github.com/username/bedwars-plugin.git
cd bedwars-plugin
mvn clean package
```

</details>

<details>
<summary><b>🤝 Contributing</b></summary>

1. **Fork** das Repository
2. **Branch** erstellen (`git checkout -b feature/AmazingFeature`)
3. **Commit** (`git commit -m 'Add AmazingFeature'`)
4. **Push** (`git push origin feature/AmazingFeature`)
5. **Pull Request** erstellen

</details>

---

## 📜 Changelog

<details>
<summary><b>Versionshistorie</b></summary>

### v1.0.0 (Latest)
- ✨ **Neu:** Initiales Release
- 🛏️ **Neu:** Vollständiges BedWars Gameplay
- 🏪 **Neu:** Shop-System implementiert
- 📊 **Neu:** Statistiken hinzugefügt

### v0.9.0-beta
- 🐛 **Fix:** Verschiedene Bug-Fixes
- ⚡ **Verbesserung:** Performance Optimierungen
- 🌐 **Neu:** Multi-Language Support

</details>

---

## 📸 Screenshots

<div align="center">

### 🎮 Gameplay
<img src="https://via.placeholder.com/800x400/4ECDC4/FFFFFF?text=BedWars+Gameplay+Screenshot" alt="Gameplay Screenshot" width="800"/>

### 🏪 Shop System
<img src="https://via.placeholder.com/800x400/45B7D1/FFFFFF?text=Shop+System+Screenshot" alt="Shop Screenshot" width="800"/>

### 📊 Statistics
<img src="https://via.placeholder.com/800x400/96CEB4/FFFFFF?text=Statistics+Screenshot" alt="Stats Screenshot" width="800"/>

</div>

---

## 📄 Lizenz

<div align="center">

Dieses Projekt steht unter der **MIT Lizenz**.

<details>
<summary><b>Lizenz anzeigen</b></summary>

```
MIT License

Copyright (c) 2025 BedWars Plugin

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

</details>

</div>

---

<div align="center">

**⭐ Gefällt dir das Plugin? Gib uns einen Stern!**

<img src="https://img.shields.io/github/stars/username/bedwars-plugin?style=social" alt="GitHub stars"/>

<br/><br/>

Made with ❤️ by [**Your Name**](https://github.com/username)

<img src="https://via.placeholder.com/600x100/1a202c/ffffff?text=Thank+you+for+using+BedWars+Plugin!" alt="Thank you banner" width="600"/>

</div>