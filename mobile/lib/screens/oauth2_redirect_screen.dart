import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/auth_provider.dart';
import 'main_page.dart';
import 'login_screen.dart';

class OAuth2RedirectScreen extends StatefulWidget {
  final String? token;
  final String? refreshToken;

  const OAuth2RedirectScreen({super.key, this.token, this.refreshToken});

  @override
  State<OAuth2RedirectScreen> createState() => _OAuth2RedirectScreenState();
}

class _OAuth2RedirectScreenState extends State<OAuth2RedirectScreen> {
  @override
  void initState() {
    super.initState();
    _handleRedirect();
  }

  Future<void> _handleRedirect() async {
    // Need a tiny delay for context to be fully ready
    await Future.delayed(Duration.zero);

    if (!mounted) return;

    if (widget.token != null && widget.token!.isNotEmpty) {
      // Save tokens using AuthProvider
      final authProvider = context.read<AuthProvider>();
      await authProvider.saveTokens(widget.token!, widget.refreshToken ?? '');
      await authProvider
          .checkAuthStatus(); // Reload user profile using new token

      if (mounted) {
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (_) => const MainPage()),
        );
      }
    } else {
      // Failed or no token provided, go back to login
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
              content: Text('Đăng nhập Social thất bại.'),
              backgroundColor: Colors.red),
        );
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (_) => const LoginScreen()),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      body: Center(
        child: CircularProgressIndicator(),
      ),
    );
  }
}
