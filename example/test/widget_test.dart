import 'package:flutter_test/flutter_test.dart';

import 'package:getnet_payment_tech_example/main.dart';

void main() {
  testWidgets('Renders home page', (WidgetTester tester) async {
    await tester.pumpWidget(const MyApp());

    expect(find.text('Bem-vindo ao exemplo do Getnet Payment Tech'), findsOneWidget);
    expect(find.text('Iniciar Pagamentos'), findsOneWidget);
  });
}
