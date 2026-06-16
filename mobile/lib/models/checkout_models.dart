class ShippingAddressOption {
  final String? id;
  final String name;
  final String addressLine1;
  final String addressLine2;
  final String city;
  final String region;
  final String postalCode;
  final String country;
  final String phoneNumber;
  final String dialCode;

  const ShippingAddressOption({
    this.id,
    required this.name,
    required this.addressLine1,
    this.addressLine2 = '',
    required this.city,
    this.region = '',
    required this.postalCode,
    required this.country,
    this.phoneNumber = '',
    this.dialCode = '',
  });

  factory ShippingAddressOption.fromJson(
    Map<String, dynamic> json, {
    required String fallbackName,
  }) {
    return ShippingAddressOption(
      id: json['id']?.toString(),
      name: fallbackName,
      addressLine1: json['addressLine1']?.toString() ?? '',
      addressLine2: json['addressLine2']?.toString() ?? '',
      city: json['city']?.toString() ?? '',
      postalCode: json['postalCode']?.toString() ?? '',
      country: json['country']?.toString() ?? '',
      phoneNumber: json['phoneNumber']?.toString() ?? '',
      dialCode: json['dialCode']?.toString() ?? '',
    );
  }

  String get cityLine {
    final place =
        [city, region].where((part) => part.trim().isNotEmpty).join(', ');
    final postal = postalCode.trim().isEmpty ? '' : ' $postalCode';
    final countryPart = country.trim().isEmpty ? '' : ', $country';
    return '$place$postal$countryPart'.trim();
  }

  String get checkoutText {
    final lines = <String>[
      addressLine1,
      if (addressLine2.trim().isNotEmpty) addressLine2,
      cityLine,
    ].where((line) => line.trim().isNotEmpty).toList();
    return lines.join('\n');
  }

  static List<ShippingAddressOption> defaults() {
    return const [
      ShippingAddressOption(
        id: 'default-1',
        name: 'Jane Doe',
        addressLine1: '3 Newbridge Court',
        city: 'Chino Hills',
        region: 'CA',
        postalCode: '91709',
        country: 'United States',
      ),
      ShippingAddressOption(
        id: 'default-2',
        name: 'John Doe',
        addressLine1: '3 Newbridge Court',
        city: 'Chino Hills',
        region: 'CA',
        postalCode: '91709',
        country: 'United States',
      ),
      ShippingAddressOption(
        id: 'default-3',
        name: 'John Doe',
        addressLine1: '51 Riverside',
        city: 'Chino Hills',
        region: 'CA',
        postalCode: '91709',
        country: 'United States',
      ),
    ];
  }
}

class PaymentCardOption {
  final String? id;
  final String cardType;
  final String lastFour;
  final String holderName;
  final String expiryDate;

  const PaymentCardOption({
    this.id,
    required this.cardType,
    required this.lastFour,
    this.holderName = 'Jennifer Doe',
    this.expiryDate = '05/23',
  });

  factory PaymentCardOption.fromJson(Map<String, dynamic> json) {
    return PaymentCardOption(
      id: json['id']?.toString(),
      cardType: json['cardType']?.toString() ?? 'MasterCard',
      lastFour: json['lastFour']?.toString() ?? '3947',
    );
  }

  static List<PaymentCardOption> defaults() {
    return const [
      PaymentCardOption(
        id: 'default-card-1',
        cardType: 'MasterCard',
        lastFour: '3947',
      ),
      PaymentCardOption(
        id: 'default-card-2',
        cardType: 'Visa',
        lastFour: '4546',
        expiryDate: '11/22',
      ),
    ];
  }
}
