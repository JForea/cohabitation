import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_svg/svg.dart';
import 'package:frontend/features/expenses/presentation/ui/widgets/fields/image_uploader_field.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/data/providers/async_apartment_provider.dart';
import 'package:frontend/shared/data/providers/expenses_provider.dart';
import 'package:frontend/shared/data/providers/user_provider.dart';
import 'package:frontend/shared/data/types/expense_category.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_text_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/chips/custom_choice_chip.dart';
import 'package:frontend/shared/presentation/ui/widgets/inputs/controlled_named_text_field.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/choice_wrapper.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/page_wrapper.dart';
import 'package:frontend/shared/utils/util_functions.dart';
import 'package:go_router/go_router.dart';
import 'package:image_picker/image_picker.dart';

class CreateExpensePage extends ConsumerStatefulWidget {
  const CreateExpensePage({super.key});

  @override
  ConsumerState<ConsumerStatefulWidget> createState() =>
      _CreateApartmentPageState();
}

class _CreateApartmentPageState extends ConsumerState<CreateExpensePage> {
  late String name;
  late String sum;
  late ExpenseCategory category;
  XFile? image;

  @override
  void initState() {
    name = "";
    sum = "";
    category = ExpenseCategory.products;
    super.initState();
  }

  void setName(String s) {
    name = s;
  }

  void setSum(String s) {
    sum = s;
  }

  void setCategory(ExpenseCategory c) {
    setState(() {
      category = c;
    });
  }

  void setImage(XFile? image) {
    setState(() {
      this.image = image;
    });
  }

  Future<bool> create() async {
    final profile = ref.read(userProvider.select((u) => u?.profile));

    if (profile == null) {
      return false;
    }

    int price = UtilFunctions.parsePrice(sum);

    ref.read(asyncApartmentProvider.notifier).addExpenseAmount(price);
    bool created = await ref
        .read(expensesProvider.notifier)
        .create(
          name: name,
          amount: price,
          category: category,
          createdBy: profile,
          image: image,
        );
    if (!created) {
      ref.read(asyncApartmentProvider.notifier).addExpenseAmount(-price);
    }

    return created;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: PageWrapper(
        backButton: true,
        pathIfCantPop: "/",
        pageName: "Добавить запись",
        bottomFloatingButtonExists: false,
        children: [
          ControlledNamedTextField(
            text: name,
            title: "Название",
            hintText: "Название расхода",
            onChange: setName,
            secondaryColor: false,
            type: .text,
            require: true,
          ),
          ControlledNamedTextField(
            text: sum,
            title: "Сумма",
            hintText: "1 000 ₽",
            onChange: setSum,
            secondaryColor: false,
            type: .price,
            require: true,
          ),
          ChoiceWrapper(
            name: "Категория",
            children: ExpenseCategory.values
                .map(
                  (c) => CustomChoiceChip(
                    icon: SvgPicture.asset(
                      "assets/icons/expense_categories/${UtilFunctions.tValueToStringRequest(c).toLowerCase()}.svg",
                      width: 20,
                      height: 20,
                      colorFilter: ColorFilter.mode(
                        c == category ? AppColors.blue : Colors.black,
                        .srcIn,
                      ),
                    ),
                    name: UtilFunctions.getDisplayNameFromT(c),
                    selected: c == category,
                    checkMark: false,
                    onSelect: () => setCategory(c),
                  ),
                )
                .toList(),
          ),
          image == null
              ? ImageUploaderField(
                  onImageSelect: setImage,
                  fieldName: "Чек",
                  innerText: "Добавить фото чека",
                  require: true,
                )
              : Center(
                  child: ConstrainedBox(
                    constraints: BoxConstraints(maxWidth: .infinity),
                    child: (kIsWeb
                        ? Image.network(image!.path)
                        : Image.file(File(image!.path), fit: .cover)),
                  ),
                ),
          Spacer(),
          CustomTextButton(
            onPressed: () async {
              final created = await create();

              if (created && context.mounted) {
                context.go("/");
              } else if (!created) {
                print("Couldn't create expense.");
              }
            },
            text: "Добавить",
          ),
        ],
      ),
    );
  }
}
