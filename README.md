# 🚀 Deep Space Intelligence System (DSIS)

> **INTELLIGENCE BEYOND EARTH.**

A complete aerospace mission-control web application built with React + Spring Boot.

[![Deploy to Render](https://render.com/images/deploy-to-render-button.svg)](https://render.com/deploy?repo=https://github.com/Mahadev-2007-dev/deep-space-intelligence-system)

## 🌐 Live Demo
**[https://dsis-deep-space-intelligence-system.onrender.com](https://dsis-deep-space-intelligence-system.onrender.com)**

## 🔑 Demo Credentials
| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Operator | `operator` | `operator123` |

## 📡 Modules
1. **User Management** — JWT authentication, admin & operator roles
2. **Mission Control Dashboard** — Live telemetry, fleet status, alerts
3. **Spacecraft Monitoring** — 4 spacecraft with simulated sensor feeds
4. **AI Anomaly Detection** — Rule-based anomaly engine with severity levels
5. **Space Object Scanner** — OpenCV image processing
6. **Emergency Simulator** — Educational emergency scenario training
7. **Planet & Moon Explorer** — Mars, Europa, Titan, Venus, Enceladus, Moon
8. **ORBIT AI** — Rule-based space assistant
9. **Mission Management** — Full CRUD mission tracking
10. **Reports & Analytics** — CSV/PDF export, charts
11. **3D Satellite Tracker** — CelesTrak TLE + SGP4 propagation
12. **3D Solar System** — Interactive Three.js visualization

## 🛠 Tech Stack
- **Frontend:** React 18, Vite, Tailwind CSS, Three.js, React Three Fiber, Recharts
- **Backend:** Java 17, Spring Boot 3.2.4, Spring Security, JWT
- **Database:** SQLite (file-based, persistent)
- **CV:** OpenCV for Java (org.openpnp)
- **Satellite:** CelesTrak TLE + satellite.js SGP4

## 🏗 Architecture
```
Single Docker Container (Render.com)
├── Spring Boot serves React SPA from /static/
├── /api/v1/** → REST API endpoints
├── /* → React SPA (SpaController forwards to index.html)
└── SQLite database on persistent /data disk
```

## 🚀 Deploy to Render
1. Fork this repository
2. Go to [render.com](https://render.com) → New → Web Service
3. Connect your GitHub repo
4. Render auto-detects the `render.yaml` configuration
5. Click Deploy

## ⚠️ Disclaimer
All spacecraft telemetry is **simulated data** for academic demonstration purposes.
This is a Computer Science Engineering academic project — not a real mission control system.
