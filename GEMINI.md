# MTier Pro - Project Instructions

## 🏗 Java Build Process
This project consists of two Minecraft plugins managed by Gradle. Always build `mtier-plugin` first as `mtier-pvp` depends on it.

### Commands:
1. **Build Core Plugin:**
   ```bash
   cd mtier-plugin && ./gradlew jar
   ```
2. **Build PvP Plugin:**
   ```bash
   cd mtier-pvp && ./gradlew jar
   ```

### Output Paths:
- `mtier-plugin/build/libs/mtier-plugin-1.0-SNAPSHOT.jar`
- `mtier-pvp/build/libs/mtier-pvp-1.0-SNAPSHOT.jar`

---

## 🎨 UI/UX Standards
- **Theme:** Orange OLED / Glassmorphism.
- **Icons:** Use `mc-icon` class for pixelated rendering.
- **Components:** Bento-style cards with `bento-card` class.

---

## 🛠 Features
- **Matchmaking:** MMR-based pairing.
- **Arena Management:** Advanced SlimeWorldManager (ASWM) for dynamic world cloning (`pvp_temp` template).
- **Item Ban System:** Turn-based banning phase after teleportation.
