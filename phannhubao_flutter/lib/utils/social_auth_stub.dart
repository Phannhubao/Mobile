import 'dart:async';
import 'package:flutter_web_auth_2/flutter_web_auth_2.dart';

Future<String> authenticateWithPopup({
  required String url,
  required String callbackUrlScheme,
  int width = 500,
  int height = 700,
}) async {
  return FlutterWebAuth2.authenticate(
    url: url,
    callbackUrlScheme: callbackUrlScheme,
  );
}
