# 🌍 BeLands – Lands & Economy for Java Servers

**Author:** [ElMand_o](https://github.com/ElMand-o)  
**Inspired by:** PocketMine-MP’s [**EconomyLand**](https://poggit.pmmp.io/p/EconomyLand) plugin.

BeLands brings a **PMMP-style Lands & Economy system** to **Java servers**, built with **Vault economy** and **GriefPrevention protection**.  
It was originally created for the [**EldenGames.com**](https://eldengames.com) Minecraft server, providing a familiar and user-friendly land claiming experience for its players.

---

## 🌟 Why I Made This
BeLands was designed for [**EldenGames.com**](https://eldengames.com), to recreate the simplicity of **PocketMine-MP’s EconomyLand** while adapting it for Java server performance and flexibility.  
This ensures both new and veteran players can easily buy, sell, and manage land with minimal friction.

---

## ✨ Features
- 🏠 **Land Claiming & Protection** via [GriefPrevention](https://www.spigotmc.org/resources/griefprevention.1884/).  
- 💰 **Economy Integration** through [Vault](https://www.spigotmc.org/resources/vault.34315/).  
- 🔑 **Trust & Access System** — grant or revoke player access to your land.  
- ⚡ **Lightweight, PMMP-inspired commands** for quick use.  

---

## 📜 Commands

| Command | Description |
|---------|-------------|
| `/land invite <claim_id> <player>` | Grant access to your land/claim |
| `/land kick <claim_id> <player>` | Revoke a player's access |
| `/land here` | Show land info at your current spot |
| `/land move <id>` | Teleport to a claim you own |
| `/startp` | Set the first corner of a claim |
| `/endp` | Set the second corner of a claim |
| `/land debug` | View your start/end positions |
| `/landbuy` | Purchase the currently selected land |
| `/landsell <land_id>` | Sell one of your lands |
| `/land transfer <claim_id> <player>` | Transfer claim ownership |
| `/land price <claim_id>` | Check buy/sell price of a claim |
| `/land whose` | List all your claim IDs |
| `/land list` | List all claim IDs |
| `/land help` | Display all available land commands |
| `/land reload` | Reload plugin configuration |

---

## 🔒 Permissions

Currently, only the reload command has a permission defined:

| Permission | Description |
|------------|-------------|
| `lands.reload` | Allows reloading the plugin configuration |

**TODO:**  
- Add permissions for claim creation, selling, trusting, transferring, etc.  
- Offer groups like `lands.user.*` and `lands.admin.*` for streamlined setup.

---

## ⚙️ Requirements
- **Paper/Spigot server** (1.18+, recommended)  
- [Vault](https://www.spigotmc.org/resources/vault.34315/)  
- [GriefPrevention](https://www.spigotmc.org/resources/griefprevention.1884/)  

---

## 📥 Installation
1. Download the latest release from [Releases](https://github.com/ElMand-o/BeLands/releases).  
2. Place the `.jar` file into your server’s `plugins/` folder.  
3. Install **Vault** and **GriefPrevention**.  
4. Restart your server.  
5. Configure settings like pricing and messages in `plugins/BeLands/config.yml`.  

---

## 🤝 Contributing & Support
- 📌 Found a bug? Open an [issue](https://github.com/ElMand-o/BeLands/issues).  
- 💡 Want a new feature? Submit a feature request or PR!  
- 📬 Contact:  
  - **Telegram:** [@Epic_Scammer](https://t.me/Epic_Scammer)  
  - **Rubika:** [@EpicScammer](https://rubika.ir/EpicScammer)  

---

## ❤️ Credits
- Inspired by [**EconomyLand**](https://poggit.pmmp.io/p/EconomyLand) on PocketMine-MP.  
- Thanks to the Minecraft plugin community for **Vault** & **GriefPrevention**.  
- Built originally for [**EldenGames.com**](https://eldengames.com).  

---

> ⚡ **BeLands** — simple & powerful land system for Java servers, powering [EldenGames.com](https://eldengames.com).
