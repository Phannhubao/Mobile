import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import '../models/checkout_models.dart';
import '../services/auth_service.dart';

class PaymentMethodsScreen extends StatefulWidget {
  final List<PaymentCardOption> cards;
  final PaymentCardOption selectedCard;

  const PaymentMethodsScreen({
    super.key,
    required this.cards,
    required this.selectedCard,
  });

  @override
  State<PaymentMethodsScreen> createState() => _PaymentMethodsScreenState();
}

class _PaymentMethodsScreenState extends State<PaymentMethodsScreen> {
  late List<PaymentCardOption> _cards;
  late int _selectedIndex;

  @override
  void initState() {
    super.initState();
    _cards = List.of(widget.cards);
    _selectedIndex = _cards.indexWhere(
      (card) =>
          card.id == widget.selectedCard.id ||
          (card.cardType == widget.selectedCard.cardType &&
              card.lastFour == widget.selectedCard.lastFour),
    );
    if (_selectedIndex < 0) _selectedIndex = 0;
  }

  PaymentCardOption get _selectedCard => _cards[_selectedIndex];

  void _closeWithSelection() {
    Navigator.pop(context, _selectedCard);
  }

  Future<void> _showAddCardSheet() async {
    final added = await showModalBottomSheet<PaymentCardOption>(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (_) => const _AddCardSheet(),
    );
    if (added == null || !mounted) return;
    setState(() {
      _cards.add(added);
      _selectedIndex = _cards.length - 1;
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
          'Payment methods',
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
        onPressed: _showAddCardSheet,
        child: const Icon(Icons.add),
      ),
      body: SafeArea(
        child: ListView(
          padding: EdgeInsets.fromLTRB(16 * scale, 24 * scale, 16 * scale, 96),
          children: [
            Text(
              'Your payment cards',
              style: GoogleFonts.inter(
                fontSize: 14 * scale,
                fontWeight: FontWeight.w600,
                color: const Color(0xFF222222),
              ),
            ),
            SizedBox(height: 18 * scale),
            for (int i = 0; i < _cards.length; i++) ...[
              _PaymentCardArt(
                card: _cards[i],
                dark: i == 0,
                scale: scale,
                onTap: () => setState(() => _selectedIndex = i),
              ),
              SizedBox(height: 14 * scale),
              _DefaultCardRow(
                selected: i == _selectedIndex,
                scale: scale,
                onChanged: () => setState(() => _selectedIndex = i),
              ),
              SizedBox(height: 28 * scale),
            ],
          ],
        ),
      ),
    );
  }
}

class _DefaultCardRow extends StatelessWidget {
  final bool selected;
  final double scale;
  final VoidCallback onChanged;

  const _DefaultCardRow({
    required this.selected,
    required this.scale,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        SizedBox(
          width: 22 * scale,
          height: 22 * scale,
          child: Checkbox(
            value: selected,
            onChanged: (_) => onChanged(),
            activeColor: const Color(0xFF222222),
            checkColor: Colors.white,
            side: const BorderSide(color: Color(0xFF9B9B9B), width: 1.4),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(3 * scale),
            ),
          ),
        ),
        SizedBox(width: 12 * scale),
        Expanded(
          child: Text(
            'Use as default payment method',
            style: GoogleFonts.inter(
              fontSize: 12.5 * scale,
              color: const Color(0xFF222222),
            ),
          ),
        ),
      ],
    );
  }
}

class _PaymentCardArt extends StatelessWidget {
  final PaymentCardOption card;
  final bool dark;
  final double scale;
  final VoidCallback? onTap;

  const _PaymentCardArt({
    required this.card,
    required this.dark,
    required this.scale,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final isVisa = card.cardType.toLowerCase().contains('visa');

    return InkWell(
      borderRadius: BorderRadius.circular(8 * scale),
      onTap: onTap,
      child: Container(
        height: 188 * scale,
        padding: EdgeInsets.all(24 * scale),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(8 * scale),
          color: dark ? const Color(0xFF202020) : const Color(0xFFA9A9A9),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.18),
              blurRadius: 18 * scale,
              offset: Offset(0, 8 * scale),
            ),
          ],
        ),
        child: Stack(
          children: [
            Positioned(
              right: -42 * scale,
              top: -64 * scale,
              child: _CardGlow(size: 168 * scale, light: !dark),
            ),
            Positioned(
              left: -54 * scale,
              bottom: -64 * scale,
              child: _CardGlow(size: 180 * scale, light: !dark),
            ),
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                _Chip(scale: scale),
                const Spacer(),
                Row(
                  children: [
                    Text(
                      '* * * *  * * * *  * * *',
                      style: GoogleFonts.inter(
                        color: Colors.white,
                        fontSize: 18 * scale,
                        letterSpacing: 2,
                      ),
                    ),
                    SizedBox(width: 8 * scale),
                    Text(
                      card.lastFour,
                      style: GoogleFonts.inter(
                        color: Colors.white,
                        fontSize: 24 * scale,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                  ],
                ),
                SizedBox(height: 26 * scale),
                Row(
                  crossAxisAlignment: CrossAxisAlignment.end,
                  children: [
                    Expanded(
                      child: _CardLabel(
                        title: 'Card Holder Name',
                        value: card.holderName,
                        scale: scale,
                      ),
                    ),
                    Expanded(
                      child: _CardLabel(
                        title: 'Expiry Date',
                        value: card.expiryDate,
                        scale: scale,
                      ),
                    ),
                    isVisa
                        ? Text(
                            'VISA',
                            style: GoogleFonts.inter(
                              color: Colors.white,
                              fontSize: 22 * scale,
                              fontWeight: FontWeight.w900,
                              fontStyle: FontStyle.italic,
                            ),
                          )
                        : _MasterCardLogo(scale: scale),
                  ],
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class _CardLabel extends StatelessWidget {
  final String title;
  final String value;
  final double scale;

  const _CardLabel({
    required this.title,
    required this.value,
    required this.scale,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          title,
          style: GoogleFonts.inter(
            color: Colors.white,
            fontSize: 8 * scale,
          ),
        ),
        SizedBox(height: 4 * scale),
        Text(
          value,
          style: GoogleFonts.inter(
            color: Colors.white,
            fontSize: 11 * scale,
            fontWeight: FontWeight.w700,
          ),
        ),
      ],
    );
  }
}

class _CardGlow extends StatelessWidget {
  final double size;
  final bool light;

  const _CardGlow({required this.size, required this.light});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: size,
      height: size,
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        color: (light ? Colors.white : Colors.black).withOpacity(0.06),
      ),
    );
  }
}

class _Chip extends StatelessWidget {
  final double scale;

  const _Chip({required this.scale});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 30 * scale,
      height: 22 * scale,
      decoration: BoxDecoration(
        color: const Color(0xFFFFC24D),
        borderRadius: BorderRadius.circular(6 * scale),
      ),
      child: Center(
        child: Container(
          width: 24 * scale,
          height: 1,
          color: const Color(0xFFE4A72F),
        ),
      ),
    );
  }
}

class _MasterCardLogo extends StatelessWidget {
  final double scale;

  const _MasterCardLogo({required this.scale});

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: 34 * scale,
      height: 22 * scale,
      child: Stack(
        children: [
          Positioned(
            left: 0,
            child: _LogoCircle(color: const Color(0xFFEA001B), scale: scale),
          ),
          Positioned(
            right: 0,
            child: _LogoCircle(color: const Color(0xFFFFA200), scale: scale),
          ),
        ],
      ),
    );
  }
}

class _LogoCircle extends StatelessWidget {
  final Color color;
  final double scale;

  const _LogoCircle({required this.color, required this.scale});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 22 * scale,
      height: 22 * scale,
      decoration: BoxDecoration(color: color, shape: BoxShape.circle),
    );
  }
}

class _AddCardSheet extends StatefulWidget {
  const _AddCardSheet();

  @override
  State<_AddCardSheet> createState() => _AddCardSheetState();
}

class _AddCardSheetState extends State<_AddCardSheet> {
  final _nameController = TextEditingController();
  final _numberController = TextEditingController(text: '5546 8205 3693 3947');
  final _expiryController = TextEditingController(text: '05/23');
  final _cvvController = TextEditingController(text: '567');
  bool _defaultMethod = true;
  bool _saving = false;

  @override
  void dispose() {
    _nameController.dispose();
    _numberController.dispose();
    _expiryController.dispose();
    _cvvController.dispose();
    super.dispose();
  }

  Future<void> _addCard() async {
    final digits = _numberController.text.replaceAll(RegExp(r'\D'), '');
    if (digits.length < 4) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Enter a valid card number'),
          backgroundColor: Color(0xFFDB3022),
        ),
      );
      return;
    }

    final type = digits.startsWith('4') ? 'Visa' : 'MasterCard';
    final lastFour = digits.substring(digits.length - 4);
    setState(() => _saving = true);

    try {
      await AuthService().addUserCard(type, lastFour);
      if (!mounted) return;
      Navigator.pop(
        context,
        PaymentCardOption(
          id: DateTime.now().microsecondsSinceEpoch.toString(),
          cardType: type,
          lastFour: lastFour,
          holderName: _nameController.text.trim().isEmpty
              ? 'Jennifer Doe'
              : _nameController.text.trim(),
          expiryDate: _expiryController.text.trim().isEmpty
              ? '05/23'
              : _expiryController.text.trim(),
        ),
      );
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Could not add card: $e'),
          backgroundColor: const Color(0xFFDB3022),
        ),
      );
    } finally {
      if (mounted) setState(() => _saving = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final media = MediaQuery.of(context);
    final scale = (media.size.width / 375).clamp(0.82, 1.25);

    return Container(
      decoration: const BoxDecoration(
        color: Color(0xFFF9F9F9),
        borderRadius: BorderRadius.vertical(top: Radius.circular(28)),
      ),
      padding: EdgeInsets.fromLTRB(
        16 * scale,
        12 * scale,
        16 * scale,
        media.viewInsets.bottom + 26 * scale,
      ),
      child: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              width: 60 * scale,
              height: 5 * scale,
              decoration: BoxDecoration(
                color: const Color(0xFF9B9B9B),
                borderRadius: BorderRadius.circular(3 * scale),
              ),
            ),
            SizedBox(height: 22 * scale),
            Text(
              'Add new card',
              style: GoogleFonts.outfit(
                fontSize: 18 * scale,
                fontWeight: FontWeight.w700,
                color: const Color(0xFF222222),
              ),
            ),
            SizedBox(height: 18 * scale),
            _CardInput(
              controller: _nameController,
              hint: 'Name on card',
              scale: scale,
            ),
            _CardInput(
              controller: _numberController,
              hint: 'Card number',
              scale: scale,
              keyboardType: TextInputType.number,
              suffix: _MasterCardLogo(scale: scale * 0.8),
            ),
            _CardInput(
              controller: _expiryController,
              hint: 'Expire Date',
              scale: scale,
            ),
            _CardInput(
              controller: _cvvController,
              hint: 'CVV',
              scale: scale,
              keyboardType: TextInputType.number,
              suffix: Icon(Icons.help_outline,
                  color: const Color(0xFFBDBDBD), size: 20 * scale),
            ),
            SizedBox(height: 2 * scale),
            Row(
              children: [
                SizedBox(
                  width: 22 * scale,
                  height: 22 * scale,
                  child: Checkbox(
                    value: _defaultMethod,
                    onChanged: (value) =>
                        setState(() => _defaultMethod = value ?? true),
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
                    'Set as default payment method',
                    style: GoogleFonts.inter(
                      fontSize: 12.5 * scale,
                      color: const Color(0xFF222222),
                    ),
                  ),
                ),
              ],
            ),
            SizedBox(height: 22 * scale),
            SizedBox(
              width: double.infinity,
              height: 48 * scale,
              child: ElevatedButton(
                onPressed: _saving ? null : _addCard,
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
                  _saving ? 'ADDING...' : 'ADD CARD',
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

class _CardInput extends StatelessWidget {
  final TextEditingController controller;
  final String hint;
  final double scale;
  final TextInputType? keyboardType;
  final Widget? suffix;

  const _CardInput({
    required this.controller,
    required this.hint,
    required this.scale,
    this.keyboardType,
    this.suffix,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 58 * scale,
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
          hintText: hint,
          hintStyle: GoogleFonts.inter(
            fontSize: 13 * scale,
            color: const Color(0xFF9B9B9B),
          ),
          suffixIcon: suffix == null
              ? null
              : Padding(
                  padding: EdgeInsets.only(right: 12 * scale),
                  child: Center(widthFactor: 1, child: suffix),
                ),
          border: InputBorder.none,
          contentPadding: EdgeInsets.symmetric(
              horizontal: 18 * scale, vertical: 18 * scale),
        ),
      ),
    );
  }
}
