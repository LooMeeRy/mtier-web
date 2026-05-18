# Agent Rules & Behavioral Directives

Whenever you are working on the **MTier Pro** project, you MUST adhere to the following workflow:

## 1. Context Initialization
- **READ FIRST**: Always read `ai-context/PROJECT_STATUS.md` at the start of every session.
- **UNDERSTAND STATE**: Synchronize your knowledge with the "Completed Tasks" and "Active Tasks" sections.

## 2. Mandatory Documentation Loop
Every time you perform a **Directive** (implementation, bug fix, or refactor):
- **UPDATE ACTIVITY LOG**: Add a new entry to the `## 🪵 Activity Log` section in `PROJECT_STATUS.md` with the current date (YYYY-MM-DD) and a brief description of what you did.
- **UPDATE TASK LIST**: If the work completes a pending task, mark it as `[x]`. If you started a new feature, add it to `## ✅ Completed Tasks` or `## 📋 Pending Tasks`.
- **NO REMINDERS NEEDED**: This is a core mandate. Do not wait for the user to ask you to update the status.

## 3. UI/UX Standards
- Theme: **Orange OLED / Glassmorphism**.
- Icons: Always use `mc-icon` class for pixelated Minecraft rendering.
- Typography: Use `font-display` (italic/bold) for headings.
- Animations: Prefer the custom `animate-float` and `animate-pulse-glow` for important badges/ranks.
