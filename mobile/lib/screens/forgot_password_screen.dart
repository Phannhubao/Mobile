import 'package:flutter/material.dart';
import 'login_screen.dart';

class ForgotPasswordScreen extends StatefulWidget {
  const ForgotPasswordScreen({super.key});

  @override
  State<ForgotPasswordScreen> createState() => _ForgotPasswordScreenState();
}

class _ForgotPasswordScreenState extends State<ForgotPasswordScreen> {
  final _formKey = GlobalKey<FormState>();
  final _emailCtrl = TextEditingController();
  bool _isPressed = false;

  @override
  void dispose() {
    _emailCtrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        final scale = ((constraints.maxWidth - 32) / 375).clamp(0.5, 1.5).toDouble();
        return Scaffold(
          backgroundColor: const Color(0xFFF9F9F9),
          body: SafeArea(
            child: SingleChildScrollView(
              child: Center(
                child: Container(
                  width: (375 * scale).clamp(200, 600),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      _header(scale),
                      Padding(
                        padding: EdgeInsets.symmetric(horizontal: 16 * scale),
                        child: Form(
                          key: _formKey,
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              SizedBox(height: 8 * scale),
                              Text(
                                'Enter your email address to receive a password reset link.',
                                style: TextStyle(
                                  fontFamily: 'Metropolis',
                                  fontSize: 14 * scale,
                                  color: const Color(0xFF9B9B9B),
                                  height: 1.43,
                                ),
                              ),
                              SizedBox(height: 24 * scale),
                              _buildField(
                                scale: scale,
                                label: 'Email',
                                controller: _emailCtrl,
                                keyboardType: TextInputType.emailAddress,
                                validator: (v) {
                                  if (v == null || v.isEmpty) return 'Vui lòng nhập email';
                                  if (!RegExp(r'^[^\s@]+@[^\s@]+\.[^\s@]+$').hasMatch(v.trim())) {
                                    return 'Email không hợp lệ';
                                  }
                                  return null;
                                },
                              ),
                              SizedBox(height: 32 * scale),
                              _sendButton(scale),
                              SizedBox(height: 24 * scale),
                              _backToLogin(scale),
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        );
      },
    );
  }

  Widget _header(double scale) {
    return Container(
      height: 219 * scale,
      child: Stack(
        children: [
          Positioned(
            left: 0,
            top: 44 * scale,
            child: Material(
              color: Colors.transparent,
              child: InkWell(
                borderRadius: BorderRadius.circular(24 * scale),
                onTap: () => Navigator.pop(context),
                child: Padding(
                  padding: EdgeInsets.all(8 * scale),
                  child: Icon(
                    Icons.chevron_left,
                    size: 24 * scale,
                    color: const Color(0xFF222222),
                  ),
                ),
              ),
            ),
          ),
          Positioned(
            left: 14 * scale,
            top: 106 * scale,
            child: Text(
              'Forgot password',
              style: TextStyle(
                fontFamily: 'Metropolis',
                fontSize: 34 * scale,
                fontWeight: FontWeight.w700,
                color: const Color(0xFF222222),
                height: 1.0,
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildField({
    required double scale,
    required String label,
    required TextEditingController controller,
    String hint = '',
    TextInputType? keyboardType,
    FormFieldValidator<String>? validator,
  }) {
    return Container(
      width: 343 * scale,
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(4 * scale),
        boxShadow: [
          BoxShadow(
            color: const Color(0x0D000000),
            blurRadius: 8 * scale,
            offset: Offset(0, 1 * scale),
          ),
        ],
      ),
      child: TextFormField(
        controller: controller,
        keyboardType: keyboardType,
        validator: validator,
        style: TextStyle(
          fontFamily: 'Metropolis',
          fontSize: 14 * scale,
          color: const Color(0xFF2D2D2D),
        ),
        decoration: InputDecoration(
          labelText: label,
          hintText: hint,
          labelStyle: TextStyle(
            fontFamily: 'Metropolis',
            fontSize: 14 * scale,
            color: const Color(0xFF9B9B9B),
          ),
          floatingLabelStyle: TextStyle(
            fontFamily: 'Metropolis',
            fontSize: 11 * scale,
            color: const Color(0xFF9B9B9B),
          ),
          hintStyle: TextStyle(
            fontFamily: 'Metropolis',
            fontSize: 14 * scale,
            color: const Color(0xFF2D2D2D),
          ),
          floatingLabelBehavior: FloatingLabelBehavior.auto,
          contentPadding: EdgeInsets.symmetric(horizontal: 20 * scale, vertical: 21 * scale),
          filled: true,
          fillColor: Colors.white,
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(4 * scale),
            borderSide: BorderSide.none,
          ),
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(4 * scale),
            borderSide: BorderSide(color: const Color(0xFFE0E0E0), width: 1),
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(4 * scale),
            borderSide: BorderSide(color: const Color(0xFFDB3022), width: 1.5),
          ),
        ),
      ),
    );
  }

  Widget _sendButton(double scale) {
    return SizedBox(
      width: 343 * scale,
      height: 48 * scale,
      child: GestureDetector(
        onTapDown: (_) => setState(() => _isPressed = true),
        onTapUp: (_) => setState(() => _isPressed = false),
        onTapCancel: () => setState(() => _isPressed = false),
        child: AnimatedScale(
          scale: _isPressed ? 0.97 : 1.0,
          duration: const Duration(milliseconds: 80),
          child: ElevatedButton(
            onPressed: () {},
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFFDB3022),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(25 * scale),
              ),
              elevation: 4,
              shadowColor: const Color(0xFFD32626).withValues(alpha: 0.25),
            ),
            child: Text(
              'SEND',
              style: TextStyle(
                fontFamily: 'Metropolis',
                fontSize: 14 * scale,
                fontWeight: FontWeight.w700,
                color: Colors.white,
                letterSpacing: 1.5,
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _backToLogin(double scale) {
    return Align(
      alignment: Alignment.centerRight,
      child: GestureDetector(
        onTap: () => Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (_) => const LoginScreen()),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              'Back to Login',
              style: TextStyle(
                fontFamily: 'Metropolis',
                fontSize: 14 * scale,
                color: const Color(0xFF222222),
                height: 1.43,
              ),
            ),
            SizedBox(width: 4 * scale),
            Icon(
              Icons.arrow_forward,
              size: 24 * scale,
              color: const Color(0xFFDB3022),
            ),
          ],
        ),
      ),
    );
  }
}
