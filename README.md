# Trivia Webapp

Trivia-app waarbij gebruikers meerkeuzevragen beantwoorden. Een tussenlaag-backend verbergt het juiste antwoord (Open Trivia API levert die direct aan).

**Stack:** Spring Boot (Java 21), React, Vite, TypeScript.

## Vereisten

- Java 21, Maven  
- Node.js (LTS), npm of pnpm  

## Starten

**Backend:**
```bash
cd backend && mvn spring-boot:run
```

**Frontend:**
```bash
cd frontend && npm i && npm run dev
```

Frontend verwacht de API op `http://localhost:8080`.

## Opbouw

- **Backend:** `GET /questions` (vragen zonder juiste antwoord), `POST /checkanswers` (antwoord controleren).
- **Frontend:** praat alleen met deze API; de Open Trivia API wordt alleen door de backend aangeroepen.
