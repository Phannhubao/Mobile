import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import '../models/category.dart';
import '../models/tag.dart';
import '../services/catalog_service.dart';

class AdminCatalogScreen extends StatefulWidget {
  final VoidCallback onBack;
  final double scale;

  const AdminCatalogScreen({
    super.key,
    required this.onBack,
    required this.scale,
  });

  @override
  State<AdminCatalogScreen> createState() => _AdminCatalogScreenState();
}

class _AdminCatalogScreenState extends State<AdminCatalogScreen> {
  final _catalogService = CatalogService();
  List<CategoryModel> _categories = [];
  List<Tag> _tags = [];
  bool _isLoading = true;
  int _selectedTab = 0;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _isLoading = true);
    try {
      final results = await Future.wait([
        _catalogService.getCategories(),
        _catalogService.getTags(),
      ]);
      if (!mounted) return;
      setState(() {
        _categories = results[0] as List<CategoryModel>;
        _tags = results[1] as List<Tag>;
        _isLoading = false;
      });
    } catch (error) {
      if (!mounted) return;
      setState(() => _isLoading = false);
      _showError(error);
    }
  }

  Future<void> _openCategoryForm([CategoryModel? category]) async {
    final nameController =
        TextEditingController(text: category?.categoryName ?? '');
    final descriptionController =
        TextEditingController(text: category?.categoryDescription ?? '');
    final iconController = TextEditingController(text: category?.icon ?? '');
    final imageController = TextEditingController(text: category?.image ?? '');
    final placeholderController =
        TextEditingController(text: category?.placeholder ?? '');
    String? parentId = category?.parentId;
    bool active = category?.active ?? true;

    final payload = await showDialog<Map<String, dynamic>>(
      context: context,
      builder: (dialogContext) => StatefulBuilder(
        builder: (context, setDialogState) => AlertDialog(
          title: Text(category == null ? 'Add Category' : 'Edit Category'),
          content: SizedBox(
            width: 430,
            child: SingleChildScrollView(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  _field(nameController, 'Category name', required: true),
                  _field(descriptionController, 'Description', maxLines: 2),
                  _field(iconController, 'Icon'),
                  _field(imageController, 'Image URL'),
                  _field(placeholderController, 'Placeholder'),
                  DropdownButtonFormField<String?>(
                    initialValue: parentId,
                    decoration: const InputDecoration(labelText: 'Parent'),
                    items: [
                      const DropdownMenuItem<String?>(
                        value: null,
                        child: Text('No parent'),
                      ),
                      ..._categories
                          .where((item) => item.id != category?.id)
                          .map(
                            (item) => DropdownMenuItem<String?>(
                              value: item.id,
                              child: Text(item.categoryName),
                            ),
                          ),
                    ],
                    onChanged: (value) =>
                        setDialogState(() => parentId = value),
                  ),
                  SwitchListTile(
                    contentPadding: EdgeInsets.zero,
                    title: const Text('Active'),
                    value: active,
                    onChanged: (value) =>
                        setDialogState(() => active = value),
                  ),
                ],
              ),
            ),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(dialogContext),
              child: const Text('Cancel'),
            ),
            FilledButton(
              onPressed: () {
                if (nameController.text.trim().isEmpty) return;
                Navigator.pop(dialogContext, {
                  'categoryName': nameController.text.trim(),
                  'parentId': parentId,
                  'categoryDescription': descriptionController.text.trim(),
                  'icon': iconController.text.trim(),
                  'image': imageController.text.trim(),
                  'placeholder': placeholderController.text.trim(),
                  'active': active,
                });
              },
              child: const Text('Save'),
            ),
          ],
        ),
      ),
    );

    nameController.dispose();
    descriptionController.dispose();
    iconController.dispose();
    imageController.dispose();
    placeholderController.dispose();
    if (payload == null) return;

    try {
      if (category == null) {
        await _catalogService.createCategory(payload);
      } else {
        await _catalogService.updateCategory(category.id, payload);
      }
      await _loadData();
      _showMessage('Category saved');
    } catch (error) {
      _showError(error);
    }
  }

  Future<void> _openTagForm([Tag? tag]) async {
    final nameController = TextEditingController(text: tag?.tagName ?? '');
    final iconController = TextEditingController(text: tag?.icon ?? '');

    final payload = await showDialog<Map<String, dynamic>>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: Text(tag == null ? 'Add Tag' : 'Edit Tag'),
        content: SizedBox(
          width: 430,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              _field(nameController, 'Tag name', required: true),
              _field(iconController, 'Icon'),
            ],
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(dialogContext),
            child: const Text('Cancel'),
          ),
          FilledButton(
            onPressed: () {
              if (nameController.text.trim().isEmpty) return;
              Navigator.pop(dialogContext, {
                'tagName': nameController.text.trim(),
                'icon': iconController.text.trim(),
              });
            },
            child: const Text('Save'),
          ),
        ],
      ),
    );

    nameController.dispose();
    iconController.dispose();
    if (payload == null) return;

    try {
      if (tag == null) {
        await _catalogService.createTag(payload);
      } else {
        await _catalogService.updateTag(tag.id, payload);
      }
      await _loadData();
      _showMessage('Tag saved');
    } catch (error) {
      _showError(error);
    }
  }

  Future<void> _deleteCategory(CategoryModel category) async {
    if (!await _confirmDelete(category.categoryName)) return;
    try {
      await _catalogService.deleteCategory(category.id);
      await _loadData();
      _showMessage('Category deleted');
    } catch (error) {
      _showError(error);
    }
  }

  Future<void> _deleteTag(Tag tag) async {
    if (!await _confirmDelete(tag.tagName)) return;
    try {
      await _catalogService.deleteTag(tag.id);
      await _loadData();
      _showMessage('Tag deleted');
    } catch (error) {
      _showError(error);
    }
  }

  Future<bool> _confirmDelete(String name) async {
    return await showDialog<bool>(
          context: context,
          builder: (dialogContext) => AlertDialog(
            title: const Text('Confirm delete'),
            content: Text('Delete "$name"?'),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(dialogContext, false),
                child: const Text('Cancel'),
              ),
              FilledButton(
                onPressed: () => Navigator.pop(dialogContext, true),
                style: FilledButton.styleFrom(
                  backgroundColor: const Color(0xFFDB3022),
                ),
                child: const Text('Delete'),
              ),
            ],
          ),
        ) ??
        false;
  }

  Widget _field(
    TextEditingController controller,
    String label, {
    bool required = false,
    int maxLines = 1,
  }) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: TextField(
        controller: controller,
        maxLines: maxLines,
        decoration: InputDecoration(
          labelText: required ? '$label *' : label,
          border: const OutlineInputBorder(),
        ),
      ),
    );
  }

  void _showMessage(String message) {
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message)),
    );
  }

  void _showError(Object error) {
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(error.toString().replaceFirst('Exception: ', '')),
        backgroundColor: const Color(0xFFDB3022),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final scale = widget.scale;
    return DefaultTabController(
      length: 2,
      child: Scaffold(
        backgroundColor: const Color(0xFFF9F9F9),
        appBar: AppBar(
          backgroundColor: Colors.white,
          leading: IconButton(
            onPressed: widget.onBack,
            icon: const Icon(Icons.arrow_back),
          ),
          title: Text(
            'Category & Tag Management',
            style: GoogleFonts.outfit(fontWeight: FontWeight.w700),
          ),
          actions: [
            IconButton(onPressed: _loadData, icon: const Icon(Icons.refresh)),
          ],
          bottom: TabBar(
            onTap: (index) => setState(() => _selectedTab = index),
            tabs: const [
              Tab(text: 'Categories'),
              Tab(text: 'Tags'),
            ],
          ),
        ),
        body: _isLoading
            ? const Center(child: CircularProgressIndicator())
            : IndexedStack(
                index: _selectedTab,
                children: [
                  _categoryList(scale),
                  _tagList(scale),
                ],
              ),
        floatingActionButton: FloatingActionButton.extended(
          onPressed:
              _selectedTab == 0 ? () => _openCategoryForm() : () => _openTagForm(),
          backgroundColor: const Color(0xFFDB3022),
          foregroundColor: Colors.white,
          icon: const Icon(Icons.add),
          label: Text(_selectedTab == 0 ? 'Add Category' : 'Add Tag'),
        ),
      ),
    );
  }

  Widget _categoryList(double scale) {
    if (_categories.isEmpty) {
      return const Center(child: Text('No categories found'));
    }
    return ListView.builder(
      padding: EdgeInsets.all(16 * scale),
      itemCount: _categories.length,
      itemBuilder: (context, index) {
        final category = _categories[index];
        final parent = _parentName(category.parentId);
        return _itemCard(
          title: category.categoryName,
          subtitle: [
            if (parent != null) 'Parent: $parent',
            category.active ? 'Active' : 'Inactive',
            if (category.categoryDescription?.isNotEmpty == true)
              category.categoryDescription!,
          ].join(' | '),
          icon: category.active ? Icons.folder : Icons.folder_off_outlined,
          onEdit: () => _openCategoryForm(category),
          onDelete: () => _deleteCategory(category),
        );
      },
    );
  }

  String? _parentName(String? parentId) {
    if (parentId == null) return null;
    for (final category in _categories) {
      if (category.id == parentId) return category.categoryName;
    }
    return null;
  }

  Widget _tagList(double scale) {
    if (_tags.isEmpty) {
      return const Center(child: Text('No tags found'));
    }
    return ListView.builder(
      padding: EdgeInsets.all(16 * scale),
      itemCount: _tags.length,
      itemBuilder: (context, index) {
        final tag = _tags[index];
        return _itemCard(
          title: tag.tagName,
          subtitle: tag.icon?.isNotEmpty == true ? tag.icon! : 'No icon',
          icon: Icons.sell_outlined,
          onEdit: () => _openTagForm(tag),
          onDelete: () => _deleteTag(tag),
        );
      },
    );
  }

  Widget _itemCard({
    required String title,
    required String subtitle,
    required IconData icon,
    required VoidCallback onEdit,
    required VoidCallback onDelete,
  }) {
    return Card(
      color: Colors.white,
      margin: const EdgeInsets.only(bottom: 10),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: const Color(0xFFFFE9E7),
          foregroundColor: const Color(0xFFDB3022),
          child: Icon(icon),
        ),
        title: Text(title, style: const TextStyle(fontWeight: FontWeight.w700)),
        subtitle: Text(subtitle, maxLines: 2, overflow: TextOverflow.ellipsis),
        trailing: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            IconButton(onPressed: onEdit, icon: const Icon(Icons.edit_outlined)),
            IconButton(
              onPressed: onDelete,
              color: const Color(0xFFDB3022),
              icon: const Icon(Icons.delete_outline),
            ),
          ],
        ),
      ),
    );
  }
}
