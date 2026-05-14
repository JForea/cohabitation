import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_svg/svg.dart';
import 'package:frontend/features/buyings/data/models/buying_redacted.dart';
import 'package:frontend/shared/data/failures/failures.dart';
import 'package:frontend/shared/data/providers/buyings_provider.dart';
import 'package:frontend/features/buyings/presentation/ui/widgets/list_tiles/redact_buying_list_tile.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';
import 'package:frontend/shared/data/providers/async_user_provider.dart';
import 'package:frontend/shared/data/providers/neighbours_provider.dart';
import 'package:frontend/shared/data/types/buying_category.dart';
import 'package:frontend/shared/presentation/theme/app_styles.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_text_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/chips/custom_choice_chip.dart';
import 'package:frontend/shared/presentation/ui/widgets/dialogs/error_dialog.dart';
import 'package:frontend/shared/presentation/ui/widgets/inputs/controlled_named_text_field.dart';
import 'package:frontend/shared/presentation/ui/widgets/list_tiles/add_list_tile.dart';
import 'package:frontend/shared/presentation/ui/widgets/lists/custom_widget_list.dart';
import 'package:frontend/shared/presentation/ui/widgets/switches/custom_switch.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/choice_wrapper.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/page_wrapper.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/user_choice_wrapper.dart';
import 'package:frontend/shared/utils/util_functions.dart';
import 'package:go_router/go_router.dart';

class CreateBuyingPage extends ConsumerStatefulWidget {
  const CreateBuyingPage({super.key});

  @override
  ConsumerState<ConsumerStatefulWidget> createState() =>
      _CreateBuyingPageState();
}

class _CreateBuyingPageState extends ConsumerState<CreateBuyingPage> {
  late bool multipleCreate;
  late List<BuyingRedacted> buyings;
  late Profile? assignedTo;

  @override
  void initState() {
    multipleCreate = false;
    buyings = [BuyingRedacted()];
    assignedTo = null;
    super.initState();
  }

  void switchMultipleCreate() {
    setState(() {
      multipleCreate = !multipleCreate;

      if (!multipleCreate && buyings.isEmpty) {
        buyings = [BuyingRedacted()];
      }
    });
  }

  void changeName(BuyingRedacted buying, String s) {
    buying.name = s;
  }

  void changeQuantity(BuyingRedacted buying, String s) {
    buying.quantity = s;
  }

  void changeCategory(BuyingRedacted buying, BuyingCategory c) {
    setState(() {
      buying.category = c;
    });
  }

  void changeAssigned(Profile? p) {
    setState(() {
      assignedTo = p;
    });
  }

  void removeBuying(BuyingRedacted buying) {
    setState(() {
      if (buyings.length > 1) {
        buyings.remove(buying);
      }
    });
  }

  void addBuying() {
    setState(() {
      buyings.add(BuyingRedacted());
    });
  }

  bool validateName(String name) {
    return name.isNotEmpty && name.length <= 64;
  }

  bool validateQuantity(String quantity) {
    return quantity.isNotEmpty && quantity.length <= 16;
  }

  Future<void> create(BuildContext context) async {
    final userProfile = ref.read(asyncUserProvider).value!.profile!;

    try {
      if (multipleCreate) {
        bool ok = true;
        setState(() {
          for (int i = 0; i < buyings.length; i++) {
            if (!validateName(buyings[i].name)) {
              ok = false;
              buyings[i].isNameError = false;
            }
            if (!validateQuantity(buyings[i].quantity)) {
              ok = false;
              buyings[i].isQuantityError = false;
            }
          }
        });

        if (!ok) return;

        await ref
            .read(buyingsProvider.notifier)
            .createMany(
              userProfile: userProfile,
              buyingsRedacted: buyings,
              assignedTo: assignedTo,
              isPublic: true,
            );
      } else {
        bool ok = true;
        setState(() {
          if (!validateName(buyings[0].name)) {
            ok = false;
            buyings[0].isNameError = true;
          }
          if (!validateQuantity(buyings[0].quantity)) {
            ok = false;
            buyings[0].isQuantityError = true;
          }
        });

        if (!ok) return;

        await ref
            .read(buyingsProvider.notifier)
            .create(
              userProfile: userProfile,
              buyingRedacted: buyings[0],
              assignedTo: assignedTo,
              isPublic: true,
            );
      }

      if (context.mounted) {
        context.go("/");
      }
    } on Failure catch (e) {
      if (context.mounted) {
        showErrorDialog(context, e.message);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final profiles = [
      ref.read(asyncUserProvider).value!.profile!,
      ...ref.read(neighboursProvider).value!,
    ];

    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.surface,
      body: PageWrapper(
        backButton: true,
        pathIfCantPop: "/",
        pageName: 'Добавить товар',
        bottomFloatingButtonExists: false,
        children: [
          Row(
            spacing: 15,
            children: [
              CustomSwitch(
                turnedOn: multipleCreate,
                onSwitch: switchMultipleCreate,
              ),
              Text("МНОЖЕСТВЕННОЕ СОЗДАНИЕ", style: AppStyles.surfaceTitle()),
            ],
          ),
          if (!multipleCreate) ...[
            ControlledNamedTextField(
              text: buyings[0].name,
              title: "Название товара",
              hintText: "Хлеб",
              onChange: (s) => changeName(buyings[0], s),
              secondaryColor: false,
              type: .text,
              require: true,
              highlightError: buyings[0].isNameError,
            ),
            ControlledNamedTextField(
              text: buyings[0].quantity,
              title: "Количество товара",
              hintText: "1 шт",
              onChange: (s) => changeQuantity(buyings[0], s),
              secondaryColor: false,
              type: .text,
              require: true,
              highlightError: buyings[0].isQuantityError,
            ),
            ChoiceWrapper(
              name: "Категория",
              children: BuyingCategory.values
                  .map(
                    (c) => CustomChoiceChip(
                      icon: SvgPicture.asset(
                        "assets/icons/buying_categories/${c.name}.svg",
                      ),
                      name: UtilFunctions.getDisplayNameFromT(c),
                      selected: c == buyings[0].category,
                      checkMark: false,
                      onSelect: () => changeCategory(buyings[0], c),
                    ),
                  )
                  .toList(),
            ),
          ],
          if (multipleCreate) ...[
            CustomWidgetList(
              danger: false,
              children: [
                for (int i = 0; i < buyings.length; i++)
                  RedactBuyingListTile(
                    key: ValueKey(i),
                    buying: buyings[i],
                    onRemove: () => removeBuying(buyings[i]),
                    changeCategory: (c) => changeCategory(buyings[i], c),
                  ),
                AddListTile(onAdd: addBuying, text: "Добавить товар"),
              ],
            ),
          ],
          UserChoiceWrapper(
            name: "Назначить",
            profiles: profiles,
            selected: assignedTo?.id,
            select: changeAssigned,
          ),
          CustomTextButton(
            onPressed: () async => await create(context),
            text: "Создать",
          ),
        ],
      ),
    );
  }
}
