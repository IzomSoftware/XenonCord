# The project is currently under heavy maintenance. a stable release would be published soon.
# 🌌 XenonCord  

*A lightweight, easy-to-use, and scalable BungeeCord proxy.*  

---

## 🚀 Overview  
XenonCord is a **fork of Waterfall**, which itself is a fork of **BungeeCord**. This project introduces various **fixes, patches, and new features**, including:  
- ✔ **BungeeGuard native forwarding**  
- ✔ **Optimizations for performance & scalability**  
- ✔ **Additional features to enhance BungeeCord**  

🔹 **Note:** All "addons" have been migrated into a **BungeeCord plugin** now.  

---

## ✨ Key Features  

✅ **Lightweight & Optimized**  
- Focused on **clean code** and **high performance** for seamless Minecraft server proxying.  
✅ **Security Suite**
- **Captcha System**: Advanced map-based captcha to block automated bots.
- **AntiProxy & VPN**: Real-time detection and blocking of known proxy and VPN services.
- **Global Whitelist**: Efficiently bypass all security checks for trusted IPs and usernames.
- **Account Limit**: Prevent alt-spam by limiting accounts per IP.
- **IP Whitelist**: Restrict access to specific IP ranges or domains.
✅ **Customizable**
- Fully modular design—enable or disable any feature via `XenonCord.yml`.
✅ **Developer-Friendly**  
- Fork, modify, and extend as needed—**open-source and easy to integrate**.  

---

## 🛠 Getting Started  

### 📌 As a Proxy  
1. **Download** the latest `.jar` file from [Releases](#).  
2. **Place** it in your **Minecraft server folder**.  
3. **Run** it using your server's start script.  

### 💻 As a Development Workspace  
1. **Clone the repository**:  
   ```bash
   git clone https://github.com/SyNdicateFoundation/XenonCord/
   ```
2. **Open in IntelliJ IDEA**

---

## 🚧 TODO  
📌 **Optimizations** – Improve performance & reduce overhead.
- Optimization plans:
- Replace thread.sleeps
- Replace reflections based stuff
- use libdeflate
- control buf copies
- remove r/w lock (replace with ConcurrentHashmap
- control buf release
- remove system.nanotime

📌 **Feature Additions** – Expand functionality with new capabilities.  

---

## 🎖 Credits  

Special thanks to:  
- **[BungeeCord](https://www.spigotmc.org/wiki/bungeecord/) by SpigotMC** – for laying the foundation.  
- **[Waterfall](https://papermc.io/) by PaperMC** – for making proxy development easier.  
- **Contributors & Community** – for helping improve XenonCord.  

---

## We welcome contributions! 🚀
  
💡 **Fork the repo, submit a PR, and we’ll review it.**  
Need help or want to contribute? **Join our community on Discord!**  

[![Discord](https://img.shields.io/discord/1189580010957324298?color=5865F2&logo=discord&logoColor=white&style=for-the-badge)](https://discord.gg/vTF2W5UKxr)  

--- 

## 📊 Live Statistics  

Track XenonCord’s usage and performance via **bStats**:  
[View XenonCord Stats on bStats](https://bstats.org/plugin/server-implementation/XenonCord/25130)  
