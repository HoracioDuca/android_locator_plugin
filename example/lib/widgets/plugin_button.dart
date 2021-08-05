import '../utils/example_constants.dart';
import 'package:flutter/material.dart';

class PluginButton extends StatelessWidget {
  final String text;
  final void Function() onPressed;

  const PluginButton({
    Key? key,
    required this.text,
    required this.onPressed,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(
        top: ExampleConstants.edgeInsetsTop,
      ),
      child: ElevatedButton(
        onPressed: onPressed,
        child: Text(
          text,
        ),
      ),
    );
  }
}
