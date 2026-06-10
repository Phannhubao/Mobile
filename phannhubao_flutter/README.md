# phannhubao

Flutter client for the Phan Nhu Bao Spring Boot backend.

## API URL

By default, the app connects to `http://localhost:8080`. For Android, expose
the host backend to the device before running the app:

```powershell
adb reverse tcp:8080 tcp:8080
```

Using `localhost` also keeps the HTTP callback valid for Google OAuth.
Production web builds served by Spring Boot automatically use the current web
backend URL configured at build time.

Override it for a physical device or a separately hosted backend:

```powershell
flutter run --dart-define=API_BASE_URL=http://10.0.2.2:8080
```

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
cd ..\phannhubao
.\mvnw.cmd spring-boot:run
ngrok http 8080
```

Run Flutter with the HTTPS forwarding URL shown by ngrok:

```powershell
flutter run --dart-define=API_BASE_URL=https://your-id.ngrok-free.app
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
