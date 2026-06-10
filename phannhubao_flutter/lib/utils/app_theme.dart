import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class AppTheme {
  // ─── Color palette (mirrors auth.css :root vars) ───────────────────────
  static const Color primary = Color(0xFFD9472B); // --primary
  static const Color primaryDark = Color(0xFFB83822); // --primary-dark
  static const Color primaryLight = Color(0xFFF05A3A); // --primary-light
  static const Color secondary =
      Color(0xFF00BFA5); // accent teal (kept for home_screen)
  static const Color background = Color(0xFFF5F5F5); // --bg
  static const Color surface = Color(0xFFFFFFFF); // --surface
  static const Color surfaceLight = Color(0xFFF5F5F5); // same as bg
  static const Color border = Color(0xFFE0E0E0); // --border
  static const Color textPrimary = Color(0xFF1A1A1A); // --text-primary
  static const Color textSecondary = Color(0xFF9E9E9E); // --text-secondary
  static const Color textLabel = Color(0xFFBDBDBD); // --text-label
  static const Color error = Color(0xFFD9472B); // same as primary (red)
  static const Color success = Color(0xFF4CAF50); // --success

  // ─── Theme ─────────────────────────────────────────────────────────────
  static ThemeData get lightTheme {
    return ThemeData(
      useMaterial3: true,
      brightness: Brightness.light,
      scaffoldBackgroundColor: background,
      colorScheme: const ColorScheme.light(
        primary: primary,
        secondary: primaryLight,
        surface: surface,
        error: error,
      ),
      textTheme: GoogleFonts.interTextTheme(
        const TextTheme(
          displayLarge:
              TextStyle(color: textPrimary, fontWeight: FontWeight.w800),
          displayMedium:
              TextStyle(color: textPrimary, fontWeight: FontWeight.w800),
          headlineLarge:
              TextStyle(color: textPrimary, fontWeight: FontWeight.w800),
          headlineMedium:
              TextStyle(color: textPrimary, fontWeight: FontWeight.w700),
          bodyLarge: TextStyle(color: textPrimary),
          bodyMedium: TextStyle(color: textSecondary),
          labelLarge:
              TextStyle(color: textPrimary, fontWeight: FontWeight.w600),
        ),
      ),
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: surface,
        floatingLabelBehavior: FloatingLabelBehavior.auto,
        contentPadding: const EdgeInsets.fromLTRB(16, 22, 48, 10),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: border, width: 1.5),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: border, width: 1.5),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: primary, width: 1.5),
        ),
        errorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: error, width: 1.5),
        ),
        focusedErrorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: error, width: 1.5),
        ),
        labelStyle: const TextStyle(
            color: textLabel, fontSize: 14, fontWeight: FontWeight.w400),
        floatingLabelStyle: const TextStyle(
            color: textSecondary, fontSize: 11, fontWeight: FontWeight.w500),
        hintStyle: const TextStyle(color: textLabel),
        errorStyle: const TextStyle(color: error, fontSize: 12),
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: primary,
          foregroundColor: Colors.white,
          minimumSize: const Size(double.infinity, 56),
          shape:
              const StadiumBorder(), // pill shape – matches --radius-btn: 50px
          elevation: 0,
          textStyle: GoogleFonts.inter(
            fontSize: 13,
            fontWeight: FontWeight.w700,
            letterSpacing: 1.5,
          ),
        ),
      ),
    );
  }

  // Keep a darkTheme alias so existing references don't break.
  static ThemeData get darkTheme => lightTheme;
}
