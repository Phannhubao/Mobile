import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import '../models/checkout_models.dart';
import '../services/auth_service.dart';

class ShippingAddressesScreen extends StatefulWidget {
  final List<ShippingAddressOption> addresses;
  final ShippingAddressOption selectedAddress;

  const ShippingAddressesScreen({
    super.key,
    required this.addresses,
    required this.selectedAddress,
  });

  @override
  State<ShippingAddressesScreen> createState() =>
      _ShippingAddressesScreenState();
}

class _ShippingAddressesScreenState extends State<ShippingAddressesScreen> {
  late List<ShippingAddressOption> _addresses;
  late int _selectedIndex;

  @override
  void initState() {
    super.initState();
    _addresses = List.of(widget.addresses);
    _selectedIndex = _addresses.indexWhere(
      (address) => address.id == widget.selectedAddress.id,
    );
    if (_selectedIndex < 0) _selectedIndex = 0;
  }

  ShippingAddressOption? get _selectedAddress =>
      _addresses.isEmpty ? null : _addresses[_selectedIndex];

  void _closeWithSelection() {
    Navigator.pop(context, _selectedAddress);
  }

  Future<void> _openAddAddress() async {
    final added = await Navigator.push<ShippingAddressOption>(
      context,
      MaterialPageRoute(builder: (_) => const AddShippingAddressScreen()),
    );
    if (added == null || !mounted) return;
    setState(() {
      _addresses.add(added);
      _selectedIndex = _addresses.length - 1;
    });
  }

  @override
  Widget build(BuildContext context) {
    final scale = (MediaQuery.of(context).size.width / 375).clamp(0.82, 1.25);

    return Scaffold(
      backgroundColor: const Color(0xFFF9F9F9),
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        leading: IconButton(
          icon: Icon(Icons.arrow_back_ios_new,
              color: const Color(0xFF222222), size: 20 * scale),
          onPressed: _closeWithSelection,
        ),
        title: Text(
          'Shipping Addresses',
          style: GoogleFonts.outfit(
            color: const Color(0xFF222222),
            fontWeight: FontWeight.w700,
            fontSize: 18 * scale,
          ),
        ),
        centerTitle: true,
      ),
      floatingActionButton: FloatingActionButton.small(
        backgroundColor: const Color(0xFF222222),
        foregroundColor: Colors.white,
        elevation: 6,
        onPressed: _openAddAddress,
        child: const Icon(Icons.add),
      ),
      body: SafeArea(
        child: ListView.separated(
          padding: EdgeInsets.fromLTRB(16 * scale, 22 * scale, 16 * scale, 96),
          itemBuilder: (context, index) {
            final address = _addresses[index];
            return _AddressCard(
              address: address,
              selected: index == _selectedIndex,
              scale: scale,
              onSelected: () => setState(() => _selectedIndex = index),
            );
          },
          separatorBuilder: (_, __) => SizedBox(height: 18 * scale),
          itemCount: _addresses.length,
        ),
      ),
    );
  }
}

class _AddressCard extends StatelessWidget {
  final ShippingAddressOption address;
  final bool selected;
  final double scale;
  final VoidCallback onSelected;

  const _AddressCard({
    required this.address,
    required this.selected,
    required this.scale,
    required this.onSelected,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      borderRadius: BorderRadius.circular(8 * scale),
      onTap: onSelected,
      child: Container(
        padding:
            EdgeInsets.fromLTRB(20 * scale, 14 * scale, 16 * scale, 14 * scale),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(8 * scale),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.05),
              blurRadius: 16 * scale,
              offset: Offset(0, 6 * scale),
            ),
          ],
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Expanded(
                  child: Text(
                    address.name,
                    style: GoogleFonts.inter(
                      fontSize: 14 * scale,
                      fontWeight: FontWeight.w500,
                      color: const Color(0xFF222222),
                    ),
                  ),
                ),
                Text(
                  'Edit',
                  style: GoogleFonts.inter(
                    fontSize: 13 * scale,
                    fontWeight: FontWeight.w500,
                    color: const Color(0xFFDB3022),
                  ),
                ),
              ],
            ),
            SizedBox(height: 10 * scale),
            Text(
              address.checkoutText,
              style: GoogleFonts.inter(
                fontSize: 13 * scale,
                height: 1.45,
                color: const Color(0xFF222222),
              ),
            ),
            SizedBox(height: 10 * scale),
            Row(
              children: [
                SizedBox(
                  width: 22 * scale,
                  height: 22 * scale,
                  child: Checkbox(
                    value: selected,
                    onChanged: (_) => onSelected(),
                    activeColor: const Color(0xFF222222),
                    checkColor: Colors.white,
                    side:
                        const BorderSide(color: Color(0xFF9B9B9B), width: 1.4),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(3 * scale),
                    ),
                  ),
                ),
                SizedBox(width: 12 * scale),
                Expanded(
                  child: Text(
                    'Use as the shipping address',
                    style: GoogleFonts.inter(
                      fontSize: 12.5 * scale,
                      color: const Color(0xFF222222),
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class AddShippingAddressScreen extends StatefulWidget {
  const AddShippingAddressScreen({super.key});

  @override
  State<AddShippingAddressScreen> createState() =>
      _AddShippingAddressScreenState();
}

class _AddShippingAddressScreenState extends State<AddShippingAddressScreen> {
  final _nameController = TextEditingController();
  final _addressController = TextEditingController(text: '3 Newbridge Court');
  final _cityController = TextEditingController(text: 'Chino Hills');
  final _regionController = TextEditingController(text: 'California');
  final _zipController = TextEditingController(text: '91709');
  final _countryController = TextEditingController(text: 'United States');
  bool _saving = false;

  @override
  void dispose() {
    _nameController.dispose();
    _addressController.dispose();
    _cityController.dispose();
    _regionController.dispose();
    _zipController.dispose();
    _countryController.dispose();
    super.dispose();
  }

  Future<void> _saveAddress() async {
    final name = _nameController.text.trim().isEmpty
        ? 'Jane Doe'
        : _nameController.text.trim();
    final address = _addressController.text.trim();
    final city = _cityController.text.trim();
    final region = _regionController.text.trim();
    final zip = _zipController.text.trim();
    final country = _countryController.text.trim();

    if (address.isEmpty || city.isEmpty || zip.isEmpty || country.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Please fill in the required address fields'),
          backgroundColor: Color(0xFFDB3022),
        ),
      );
      return;
    }

    setState(() => _saving = true);
    try {
      await AuthService().addUserAddress(
        addressLine1: address,
        city: city,
        country: country,
        postalCode: zip,
        phoneNumber: '000000000',
        dialCode: '+1',
      );
      if (!mounted) return;
      Navigator.pop(
        context,
        ShippingAddressOption(
          id: DateTime.now().microsecondsSinceEpoch.toString(),
          name: name,
          addressLine1: address,
          city: city,
          region: region,
          postalCode: zip,
          country: country,
          phoneNumber: '000000000',
          dialCode: '+1',
        ),
      );
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Could not save address: $e'),
          backgroundColor: const Color(0xFFDB3022),
        ),
      );
    } finally {
      if (mounted) setState(() => _saving = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final scale = (MediaQuery.of(context).size.width / 375).clamp(0.82, 1.25);

    return Scaffold(
      backgroundColor: const Color(0xFFF9F9F9),
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        leading: IconButton(
          icon: Icon(Icons.arrow_back_ios_new,
              color: const Color(0xFF222222), size: 20 * scale),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Adding Shipping Address',
          style: GoogleFonts.outfit(
            color: const Color(0xFF222222),
            fontWeight: FontWeight.w700,
            fontSize: 18 * scale,
          ),
        ),
        centerTitle: true,
      ),
      body: SafeArea(
        child: ListView(
          padding: EdgeInsets.fromLTRB(16 * scale, 26 * scale, 16 * scale, 24),
          children: [
            _AddressField(
              controller: _nameController,
              label: 'Full name',
              scale: scale,
            ),
            _AddressField(
              controller: _addressController,
              label: 'Address',
              scale: scale,
            ),
            _AddressField(
              controller: _cityController,
              label: 'City',
              scale: scale,
            ),
            _AddressField(
              controller: _regionController,
              label: 'State/Province/Region',
              scale: scale,
            ),
            _AddressField(
              controller: _zipController,
              label: 'Zip Code (Postal Code)',
              scale: scale,
              keyboardType: TextInputType.number,
            ),
            _AddressField(
              controller: _countryController,
              label: 'Country',
              scale: scale,
              suffix: Icon(Icons.chevron_right,
                  color: const Color(0xFF222222), size: 20 * scale),
            ),
            SizedBox(height: 24 * scale),
            SizedBox(
              height: 48 * scale,
              child: ElevatedButton(
                onPressed: _saving ? null : _saveAddress,
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFFDB3022),
                  disabledBackgroundColor:
                      const Color(0xFFDB3022).withOpacity(0.5),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(24 * scale),
                  ),
                  elevation: 6,
                  shadowColor: const Color(0xFFDB3022).withOpacity(0.25),
                ),
                child: Text(
                  _saving ? 'SAVING...' : 'SAVE ADDRESS',
                  style: GoogleFonts.inter(
                    fontSize: 13 * scale,
                    fontWeight: FontWeight.w700,
                    color: Colors.white,
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _AddressField extends StatelessWidget {
  final TextEditingController controller;
  final String label;
  final double scale;
  final TextInputType? keyboardType;
  final Widget? suffix;

  const _AddressField({
    required this.controller,
    required this.label,
    required this.scale,
    this.keyboardType,
    this.suffix,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 64 * scale,
      margin: EdgeInsets.only(bottom: 14 * scale),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(4 * scale),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.04),
            blurRadius: 10 * scale,
            offset: Offset(0, 4 * scale),
          ),
        ],
      ),
      child: TextField(
        controller: controller,
        keyboardType: keyboardType,
        style: GoogleFonts.inter(
          fontSize: 14 * scale,
          color: const Color(0xFF222222),
          fontWeight: FontWeight.w500,
        ),
        decoration: InputDecoration(
          labelText: label,
          labelStyle: GoogleFonts.inter(
            fontSize: 12 * scale,
            color: const Color(0xFF9B9B9B),
          ),
          suffixIcon: suffix,
          border: InputBorder.none,
          contentPadding:
              EdgeInsets.symmetric(horizontal: 18 * scale, vertical: 8 * scale),
        ),
      ),
    );
  }
}
