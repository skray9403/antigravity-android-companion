# 🚀 Antigravity Companion for Android

An ultra-optimized, native Android companion application built for **Google Antigravity** ([antigravity.google.com](https://antigravity.google.com/)). This app empowers you to monitor, control, and interact with your desktop Antigravity AI agents and cloud sessions directly from your Android phone or tablet.

---

## ✨ Features

### 🌐 1. High-Performance Web Portal (`https://antigravity.google.com/`)
- **Dynamic Mobile Optimization Engine**: Injects tailored responsive CSS and viewport scaling directly into `antigravity.google.com`, eliminating desktop scrollbars and auto-adjusting code diff containers for touch gestures.
- **Hardware-Accelerated WebView**: Smooth 60/120fps scrolling with full HTML5, WebSocket, and DOM storage support for seamless Google authentication and live agent streaming.
- **Gesture Navigation & Swipe-to-Refresh**: Native system back gestures navigate web history seamlessly.
- **Camera & File Chooser Integration**: Upload device screenshots, whiteboard sketches, or system logs directly into agent context.

### 🕹️ 2. Desktop Session Remote Controller
- **Live Agent Status & Metrics**: Monitor active desktop sessions in real-time — track agent state (`RUNNING`, `WAITING_APPROVAL`, `IDLE`), current subagent workflows, token usage, and latency.
- **Urgent Tool Execution Approvals**: Never leave your desk blocking an agent. When an agent requests approval for critical terminal commands or file changes, receive an immediate alert and approve or deny with a single tap.
- **Quick Slash Command Matrix**: One-tap trigger buttons for essential Antigravity slash commands:
  - `/goal` — Autonomous long-running execution until completion
  - `/plan` — Multi-step planning mode
  - `/compact` — Context window summarization
  - `/status` — Live agent state & background task dump
  - `/browser` — Headless web automation
  - `/boost` — Deep multi-perspective reasoning
  - `/teamwork` — Cooperative multi-agent swarm deployment
  - `/kill` — Emergency task termination
- **Remote Prompt Dispatcher**: Send prompts, tasks, or follow-ups to your desktop session with voice-to-text dictation or keyboard input.
- **Live Terminal & Transcript Console**: Tail background task stdout/stderr, subagent messages, and tool invocations in real time.

### 🎨 3. Sleek Material Design 3 (Material You)
- **Antigravity Dark Aesthetic**: Cyberpunk obsidian dark theme (`#090A0F`), subtle slate cards, neon violet (`#8B5CF6`), cyan glow (`#06B6D4`), and status-aware indicator pulses.
- **Edge-to-Edge Experience**: Fully compliant with Android 15/16/17 edge-to-edge guidelines, adapting smoothly to status bars, display cutouts, and gesture navigation bars.
- **Tactile Haptic Feedback**: Tactile responses on tool approvals and command dispatches.

---

## 🛠️ Tech Stack & Architecture

- **UI Framework**: Modern declarative [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3.
- **Target SDK**: Android 17 (API 37) | **Min SDK**: Android 7.0 (API 24).
- **Architecture**: MVVM with Kotlin Coroutines & `StateFlow`.
- **Web Layer**: AndroidX WebKit with custom JavaScript Bridge & ChromeClient.
- **CI/CD Pipeline**: GitHub Actions automated matrix building Debug and Release APKs on every commit.

---

## 📦 Cloud Builds & APK Download

You do not need to build on device. Every push to this repository triggers the GitHub Actions workflow [`.github/workflows/build-apk.yml`](.github/workflows/build-apk.yml) which automatically:
1. Runs all Kotlin & Compose unit tests.
2. Compiles and packages both **Debug** and **Release** APKs.
3. Publishes downloadable APK artifacts under the workflow run's **Artifacts** section.

### Download Steps:
1. Navigate to the **Actions** tab in this GitHub repository.
2. Click on the latest workflow run: **Build Antigravity Companion APK**.
3. Scroll to the **Artifacts** section at the bottom.
4. Download `Antigravity-Companion-Debug-APK.zip` (pre-signed, ready to sideload on any Android device).
5. Unzip and tap the `.apk` file to install on your phone!

---

## 🔗 Desktop Pairing Setup

To connect the companion app with your desktop Antigravity instance:
1. Open Antigravity Desktop or CLI daemon on your computer.
2. Ensure your phone is on the same local Wi-Fi network (or using a VPN/tunnel like Tailscale).
3. In the Companion app, go to **Settings** -> **Desktop Pairing Configuration**.
4. Enter your computer's local IP and port (e.g., `http://192.168.1.105:8080`).
5. Tap **Update Active Host** to start live controlling your desktop session!
