import 'package:dotted_border/dotted_border.dart';
import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/ui/widgets/texts/field_name.dart';
import 'package:image_picker/image_picker.dart';

class ImageUploaderField extends StatelessWidget {
  const ImageUploaderField({
    super.key,
    required this.onImageSelect,
    this.fieldName,
    this.innerText,
    required this.require,
  });

  final void Function(XFile? image) onImageSelect;
  final String? fieldName;
  final String? innerText;
  final bool require;

  Future<void> selectImage() async {
    final picker = ImagePicker();

    final XFile? image = await picker.pickImage(source: .gallery);

    onImageSelect(image);
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: .start,
      spacing: 20,
      children: [
        if (fieldName != null) FieldName(title: fieldName!, require: require),
        GestureDetector(
          onTap: selectImage,
          child: DottedBorder(
            options: RoundedRectDottedBorderOptions(
              radius: .circular(30),
              dashPattern: [12, 15],
              padding: .symmetric(vertical: 30),
              strokeWidth: 1.2,
              color: Theme.of(context).colorScheme.onSurfaceVariant,
            ),
            child: Center(
              child: Column(
                spacing: 20,
                children: [
                  Icon(
                    Icons.camera_alt_rounded,
                    size: 48,
                    color: Color(0xFF9999B6),
                  ),
                  if (innerText != null)
                    Text(
                      innerText!,
                      style: TextStyle(
                        color: Theme.of(context).colorScheme.onSurfaceVariant,
                        fontSize: 16,
                        fontWeight: .w500,
                      ),
                    ),
                ],
              ),
            ),
          ),
        ),
      ],
    );
  }
}
