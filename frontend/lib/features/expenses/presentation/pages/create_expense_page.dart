import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_svg/svg.dart';
import 'package:frontend/features/expenses/data/providers/expenses_amount_by_category_provider.dart';
import 'package:frontend/features/expenses/presentation/widgets/fields/image_uploader_field.dart';
import 'package:frontend/features/expenses/utils/expense_validators.dart';
import 'package:frontend/core/failures/failures.dart';
import 'package:frontend/shared/state/providers/async_user_provider.dart';
import 'package:frontend/shared/state/providers/expenses_provider.dart';
import 'package:frontend/shared/state/providers/user_provider.dart';
import 'package:frontend/shared/domain/types/expense_category.dart';
import 'package:frontend/app/theme/app_colors.dart';
import 'package:frontend/shared/presentation/widgets/buttons/custom_text_button.dart';
import 'package:frontend/shared/presentation/widgets/chips/custom_choice_chip.dart';
import 'package:frontend/shared/presentation/widgets/dialogs/error_dialog.dart';
import 'package:frontend/shared/presentation/widgets/inputs/controlled_named_text_field.dart';
import 'package:frontend/shared/presentation/widgets/wrappers/choice_wrapper.dart';
import 'package:frontend/shared/presentation/widgets/wrappers/page_wrapper.dart';
import 'package:frontend/core/utils/util_functions.dart';
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

  late String nameErrorMessage;
  late String sumErrorMessage;

  late final DateTime month;

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

  Future<void> create(BuildContext context) async {
    final profile = ref.read(userProvider.select((u) => u?.profile));

    if (profile == null) return;

    int? price;
    bool ok = true;
    setState(() {
      final nameError = ExpenseValidators.validateName(name);
      if (nameError != null) {
        nameErrorMessage = nameError;
        ok = false;
      }
      price = ExpenseValidators.parsePrice(sum);
      if (price == null) {
        sumErrorMessage = "Неверное значение";
        ok = false;
      }
    });

    if (!ok) return;

    ref.read(asyncUserProvider.notifier).addExpenseAmount(price!);
    ref
        .read(expensesAmountByCategoryProvider(month).notifier)
        .addExpense(category, price!);

    try {
      await ref
          .read(expensesProvider.notifier)
          .create(
            name: name,
            amount: price!,
            category: category,
            createdBy: profile,
            image: image,
          );

      if (context.mounted) {
        context.go("/");
      }
    } on Failure catch (e) {
      if (context.mounted) {
        showErrorDialog(context, e.message);
      }
      ref.read(asyncUserProvider.notifier).addExpenseAmount(-price!);
      ref
          .read(expensesAmountByCategoryProvider(month).notifier)
          .addExpense(category, -price!);
    }
  }

  @override
  void initState() {
    name = "";
    sum = "";
    category = ExpenseCategory.products;

    nameErrorMessage = "";
    sumErrorMessage = "";

    DateTime now = .now();
    month = DateTime(now.year, now.month);

    super.initState();
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
            errorMessage: nameErrorMessage,
          ),
          ControlledNamedTextField(
            text: sum,
            title: "Сумма",
            hintText: "1 000 ₽",
            onChange: setSum,
            secondaryColor: false,
            type: .price,
            require: true,
            errorMessage: sumErrorMessage,
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
          CustomTextButton(
            onPressed: () async => await create(context),
            text: "Добавить",
          ),
        ],
      ),
    );
  }
}
