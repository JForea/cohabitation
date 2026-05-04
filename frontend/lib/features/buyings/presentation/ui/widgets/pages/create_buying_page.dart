import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_svg/svg.dart';
import 'package:frontend/features/buyings/data/models/buying_redacted.dart';
import 'package:frontend/features/buyings/data/providers/buyings_provider.dart';
import 'package:frontend/features/buyings/presentation/ui/widgets/list_tiles/redact_buying_list_tile.dart';
import 'package:frontend/shared/data/models/profile.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';
import 'package:frontend/shared/data/providers/neighbours_provider.dart';
import 'package:frontend/shared/data/types/buying_category.dart';
import 'package:frontend/shared/presentation/theme/app_styles.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_back_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_text_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/chips/custom_choice_chip.dart';
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

  Future<bool> create() {
    final userProfile = ref.read(authProvider).value!.user!.profile!;

    Future<bool> created;
    if (multipleCreate) {
      created = ref
          .read(buyingsProvider.notifier)
          .createMany(
            userProfile: userProfile,
            buyingsRedacted: buyings,
            assignedTo: assignedTo,
            isPublic: true,
          );
    } else {
      created = ref
          .read(buyingsProvider.notifier)
          .create(
            userProfile: userProfile,
            buyingRedacted: buyings[0],
            assignedTo: assignedTo,
            isPublic: true,
          );
    }

    return created;
  }

  @override
  Widget build(BuildContext context) {
    final profiles = [
      ref.read(authProvider).value!.user!.profile!,
      ...ref.read(neighboursProvider).value!,
    ];

    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.surface,
      body: PageWrapper(
        children: [
          Row(
            spacing: 15,
            children: [
              CustomBackButton(mainColor: false),
              Text(
                'Добавить товар',
                style: TextStyle(
                  color: Theme.of(context).colorScheme.onSurface,
                  fontSize: 20,
                  fontWeight: .w500,
                ),
              ),
            ],
          ),
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
              password: false,
              require: true,
            ),
            ControlledNamedTextField(
              text: buyings[0].quantity,
              title: "Количество товара",
              hintText: "1 шт",
              onChange: (s) => changeQuantity(buyings[0], s),
              secondaryColor: false,
              password: false,
              require: true,
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
                ...buyings.map(
                  (b) => RedactBuyingListTile(
                    buying: b,
                    onRemove: () => removeBuying(b),
                    changeCategory: (c) => changeCategory(b, c),
                  ),
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
          Spacer(),
          CustomTextButton(
            onPressed: () async {
              final created = await create();

              if (created && context.mounted) {
                context.go("/");
              } else {
                print("Couldn't create buying.");
              }
            },
            text: "Создать",
          ),
        ],
      ),
    );
  }
}
