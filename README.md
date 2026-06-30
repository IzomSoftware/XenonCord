# This project has released the new version with lots of changes. please, if there's any problem/error/anything, contact us on github issues.
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

## Why fork Waterfall?
Waterfall has reached end of life long time ago, because they try to push community support into Velocity because it's "faster". PaperMC could've made changes to necessary parts of BungeeCord to make it as performant as Velocity is. for some reason they've decided to reinvent the wheel.
BungeeCord has a huge ton of design flaws (e.g. Entity rewrite) which causes it to be less performant compared to Velocity.
The goal of XenonCord is to instead of reinventing the wheel, fix these design flaws, improve the existing base, and make it even better than it was ever before.
At first, XenonCord's goal was to implement features alongside with optimizations, however after a while we've decided to change that behaviour & instead ship a general purpose Proxy.
Our key goal is to keep it simple & modular, we hate bloatware & therefore we'll not contribute to it.
We only implement features into mainstream which **really** require us to do so (e.g. domain whitelisting).

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
- Remove entity mapping entirely ✅ (Testing required)
- Replace thread.sleeps ✅ (Testing required)
- Replace reflections based stuff ✅ (Testing required)
- remove r/w lock (replace with ConcurrentHashmap ✅ (Testing required)
- use libdeflate  ✅
- remove system.nanotime ✅

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
