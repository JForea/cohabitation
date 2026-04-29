import 'package:flutter/material.dart';
import 'package:frontend/features/home/presentation/ui/widgets/navigation/custom_bottom_nav_bar.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  late int activeIndex;

  void setIndex(int i) {
    setState(() {
      activeIndex = i;
    });
  }

  @override
  void initState() {
    super.initState();
    activeIndex = 0;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.surface,
      body: Container(height: .infinity),
      bottomNavigationBar: CustomBottomNavBar(
        currentlyActive: activeIndex,
        setIndex: setIndex,
      ),
    );
  }
}
