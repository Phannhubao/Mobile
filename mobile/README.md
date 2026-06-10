# phannhubao

Flutter client for the Phan Nhu Bao Spring Boot backend.

## API URL

The app supports both front doors to the same backend:

- `API_MODE=ngrok` uses the ngrok URL and is the default.
- `API_MODE=local` uses `http://localhost:8080`.
- `API_BASE_URL` overrides both modes with any explicit URL.

Run through local port `8080`:

```powershell
adb reverse tcp:8080 tcp:8080
flutter run --dart-define=API_MODE=local
```

For an Android emulator without `adb reverse`, override the local URL:

```powershell
flutter run --dart-define=API_BASE_URL=http://10.0.2.2:8080
```

Run through the current ngrok tunnel:

```powershell
flutter run --dart-define=API_MODE=ngrok --dart-define=NGROK_BASE_URL=https://your-id.ngrok-free.app
```

Both URLs reach the backend on port `8080`, so they use the same PostgreSQL
database and the same `wishlist` table.

Production web builds served by Spring Boot automatically use the backend URL
configured at build time.

```powershell
flutter build web --dart-define=API_BASE_URL=https://your-backend.example.com
```

Firebase Hosting must always be built with `API_BASE_URL`; otherwise Firebase's
SPA rewrite returns `index.html` for API requests:

```powershell
flutter build web --dart-define=API_BASE_URL=https://your-id.ngrok-free.app
firebase deploy --only hosting
```

## Run through ngrok

Start the backend and ngrok:

```powershell
cd ..\backend
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=postgresql"
ngrok http 8080
```

Run Flutter with the HTTPS forwarding URL shown by ngrok:

```powershell
flutter run --dart-define=API_MODE=ngrok --dart-define=NGROK_BASE_URL=https://your-id.ngrok-free.app
```

Free ngrok URLs normally change whenever ngrok restarts, so pass the current
URL with `API_BASE_URL` instead of storing it in source code.

For social login, also start the backend with the current callback URL:

```powershell
$env:AUTHORIZED_REDIRECT_URIS="http://localhost:8080/oauth2/redirect.html,authapp://oauth2/redirect,https://your-id.ngrok-free.app/oauth2/redirect.html"
```

Add `https://your-id.ngrok-free.app/login/oauth2/code/google` and the
corresponding Facebook callback to those providers' authorized redirect URI
settings.
