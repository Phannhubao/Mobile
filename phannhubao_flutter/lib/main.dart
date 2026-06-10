import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'providers/auth_provider.dart';
import 'providers/favorites_provider.dart';
import 'providers/cart_provider.dart';
import 'screens/splash_screen.dart';
import 'screens/oauth2_redirect_screen.dart';
import 'services/auth_service.dart';
import 'utils/app_theme.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  SystemChrome.setSystemUIOverlayStyle(
    const SystemUiOverlayStyle(
      statusBarColor: Colors.transparent,
      statusBarIconBrightness: Brightness.dark,
      systemNavigationBarColor: AppTheme.background,
      systemNavigationBarIconBrightness: Brightness.dark,
    ),
  );
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        Provider(create: (_) => AuthService()),
        ChangeNotifierProvider(
            create: (ctx) => AuthProvider(authService: ctx.read())),
        ChangeNotifierProvider(create: (_) => FavoritesProvider()),
        ChangeNotifierProvider(create: (_) => CartProvider()),
      ],
      child: MaterialApp(
        title: 'AuthApp',
        debugShowCheckedModeBanner: false,
        theme: AppTheme.lightTheme,
        onGenerateRoute: (settings) {
          if (settings.name != null &&
              settings.name!.startsWith('/oauth2/redirect')) {
            final uri = Uri.parse(settings.name!);
            final token = uri.queryParameters['token'];
            final refreshToken = uri.queryParameters['refreshToken'];
            return MaterialPageRoute(
              builder: (context) => OAuth2RedirectScreen(
                  token: token, refreshToken: refreshToken),
            );
          }
          return null; // Let standard routing take over
        },
        home: const SplashScreen(),
      ),
    );
  }
}
