import 'package:flutter/material.dart';
import 'package:getnet_payment_tech/getnet_payment_tech.dart';

import 'payment_controller.dart';

class PaymentPage extends StatefulWidget {
  const PaymentPage({super.key});

  @override
  State<PaymentPage> createState() => _PaymentPageState();
}

class _PaymentPageState extends State<PaymentPage> {
  static const _licenceKey = '';
  static const _licenceInternalKey = 'aHR0cHM6Ly9wb3MtcGF5bWVudHMtYXBpLTU3NzQ2NDIzNTQwOC5zb3V0aGFtZXJpY2EtZWFzdDEucnVuLmFwcC9wb3MtcGF5bWVudHMvbGljZW5jZS9jaGVjay9pbnZvaWNl';

  final PaymentController controller = PaymentController();
  final TextEditingController moneyController = TextEditingController();
  final TextEditingController callerIdController = TextEditingController();
  final TextEditingController orderIdController = TextEditingController();
  final TextEditingController cvNumberController = TextEditingController();
  final TextEditingController transactionDateController = TextEditingController();

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) => _initialisePlugin());
  }

  Future<void> _initialisePlugin() async {
    await GetnetTech.I.initPayment(handler: controller, licenceKey: _licenceKey, licenceInternalKey: _licenceInternalKey);
    controller.generateCallerId();
    controller.generateOrderId();
    callerIdController.text = controller.callerId ?? '';
    orderIdController.text = controller.orderId ?? '';
    setState(() {});
  }

  @override
  void dispose() {
    moneyController.dispose();
    callerIdController.dispose();
    orderIdController.dispose();
    cvNumberController.dispose();
    transactionDateController.dispose();
    super.dispose();
  }

  void _updateSaleValue() {
    final value = double.tryParse(moneyController.text) ?? 0.0;
    controller.setSaleValue(value);
    controller.callerId = callerIdController.text.isNotEmpty ? callerIdController.text : controller.callerId;
    controller.orderId = orderIdController.text.isNotEmpty ? orderIdController.text : controller.orderId;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(backgroundColor: Theme.of(context).colorScheme.inversePrimary, title: const Text('Pagamentos Getnet')),
      body: ListenableBuilder(
        listenable: controller,
        builder: (context, child) {
          return SingleChildScrollView(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: <Widget>[
                const SizedBox(height: 20),
                TextField(
                  onChanged: (_) => _updateSaleValue(),
                  keyboardType: const TextInputType.numberWithOptions(decimal: true),
                  decoration: const InputDecoration(labelText: 'Valor', hintText: 'Digite o valor', border: OutlineInputBorder(), prefixText: 'R\$ '),
                  controller: moneyController,
                ),
                const SizedBox(height: 16),
                TextField(
                  decoration: const InputDecoration(labelText: 'Caller ID', hintText: 'ID do chamador', border: OutlineInputBorder()),
                  controller: callerIdController,
                  onChanged: (_) => _updateSaleValue(),
                ),
                const SizedBox(height: 16),
                TextField(
                  decoration: const InputDecoration(labelText: 'Order ID', hintText: 'ID do pedido', border: OutlineInputBorder()),
                  controller: orderIdController,
                  onChanged: (_) => _updateSaleValue(),
                ),
                const SizedBox(height: 20),
                const Divider(),
                const SizedBox(height: 20),
                const Text('Métodos de Pagamento', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                const SizedBox(height: 16),
                Wrap(
                  spacing: 10.0,
                  runSpacing: 10.0,
                  children: <Widget>[
                    ElevatedButton.icon(
                      icon: const Icon(Icons.credit_card),
                      label: const Text('Crédito'),
                      onPressed: controller.enable && !controller.clickPayment
                          ? () {
                              FocusScope.of(context).unfocus();
                              controller.setClickPayment(true);
                              GetnetTech.I.payment.creditPayment(amount: controller.saleValue, callerId: controller.callerId ?? '', orderId: controller.orderId ?? '');
                            }
                          : null,
                    ),
                    ElevatedButton.icon(
                      icon: const Icon(Icons.credit_card),
                      label: const Text('Crédito Parc. 2x'),
                      onPressed: controller.enable && !controller.clickPayment
                          ? () {
                              FocusScope.of(context).unfocus();
                              controller.setClickPayment(true);
                              GetnetTech.I.payment.creditPaymentParc(amount: controller.saleValue, callerId: controller.callerId ?? '', orderId: controller.orderId ?? '', installments: 2);
                            }
                          : null,
                    ),
                    ElevatedButton.icon(
                      icon: const Icon(Icons.account_balance_wallet),
                      label: const Text('Débito'),
                      onPressed: controller.enable && !controller.clickPayment
                          ? () {
                              FocusScope.of(context).unfocus();
                              controller.setClickPayment(true);
                              GetnetTech.I.payment.debitPayment(amount: controller.saleValue, callerId: controller.callerId ?? '', orderId: controller.orderId ?? '');
                            }
                          : null,
                    ),
                    ElevatedButton.icon(
                      icon: const Icon(Icons.card_giftcard),
                      label: const Text('Voucher'),
                      onPressed: controller.enable && !controller.clickPayment
                          ? () {
                              FocusScope.of(context).unfocus();
                              controller.setClickPayment(true);
                              GetnetTech.I.payment.voucherPayment(amount: controller.saleValue, callerId: controller.callerId ?? '', orderId: controller.orderId ?? '');
                            }
                          : null,
                    ),
                    ElevatedButton.icon(
                      icon: const Icon(Icons.qr_code),
                      label: const Text('PIX'),
                      onPressed: controller.enable && !controller.clickPayment
                          ? () {
                              FocusScope.of(context).unfocus();
                              controller.setClickPayment(true);
                              GetnetTech.I.payment.pixPayment(amount: controller.saleValue, callerId: controller.callerId ?? '', orderId: controller.orderId ?? '');
                            }
                          : null,
                    ),
                  ],
                ),
                const SizedBox(height: 20),
                const Divider(),
                const SizedBox(height: 20),
                const Text('Outras Operações', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                const SizedBox(height: 16),
                Wrap(
                  spacing: 10.0,
                  runSpacing: 10.0,
                  children: <Widget>[
                    ElevatedButton.icon(
                      icon: const Icon(Icons.lock),
                      label: const Text('Pré-Autorização'),
                      onPressed: controller.enable && !controller.clickPayment
                          ? () {
                              FocusScope.of(context).unfocus();
                              controller.setClickPayment(true);
                              GetnetTech.I.payment.preAuthorization(amount: controller.saleValue, callerId: controller.callerId ?? '', orderId: controller.orderId ?? '');
                            }
                          : null,
                    ),
                    ElevatedButton.icon(
                      icon: const Icon(Icons.search),
                      label: const Text('Status'),
                      onPressed: !controller.clickPayment
                          ? () {
                              FocusScope.of(context).unfocus();
                              controller.setClickPayment(true);
                              GetnetTech.I.payment.paymentStatus(callerId: controller.callerId ?? '');
                            }
                          : null,
                    ),
                    ElevatedButton.icon(
                      icon: const Icon(Icons.refresh),
                      label: const Text('Reimprimir'),
                      onPressed: !controller.clickPayment
                          ? () {
                              FocusScope.of(context).unfocus();
                              GetnetTech.I.payment.reprint();
                            }
                          : null,
                    ),
                    ElevatedButton.icon(
                      icon: const Icon(Icons.info),
                      label: const Text('Info'),
                      onPressed: !controller.clickPayment
                          ? () {
                              FocusScope.of(context).unfocus();
                              controller.setClickPayment(true);
                              GetnetTech.I.payment.getInfo();
                            }
                          : null,
                    ),
                    ElevatedButton.icon(
                      icon: const Icon(Icons.phone_android),
                      label: const Text('Device Info'),
                      onPressed: !controller.clickPayment
                          ? () {
                              FocusScope.of(context).unfocus();
                              controller.setClickPayment(true);
                              GetnetTech.I.payment.getDeviceInfo();
                            }
                          : null,
                    ),
                  ],
                ),
                const SizedBox(height: 20),
                const Divider(),
                const SizedBox(height: 20),
                const Text('Estorno', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                const SizedBox(height: 16),
                TextField(
                  decoration: const InputDecoration(labelText: 'CV Number', hintText: 'Número CV da transação', border: OutlineInputBorder()),
                  controller: cvNumberController,
                ),
                const SizedBox(height: 16),
                TextField(
                  decoration: const InputDecoration(labelText: 'Data da Transação', hintText: 'DDMMYYYY', border: OutlineInputBorder()),
                  controller: transactionDateController,
                ),
                const SizedBox(height: 16),
                ElevatedButton.icon(
                  icon: const Icon(Icons.undo),
                  label: const Text('Estornar'),
                  style: ElevatedButton.styleFrom(backgroundColor: Colors.red, foregroundColor: Colors.white),
                  onPressed: !controller.clickPayment && cvNumberController.text.isNotEmpty && transactionDateController.text.isNotEmpty
                      ? () {
                          FocusScope.of(context).unfocus();
                          controller.setClickPayment(true);
                          GetnetTech.I.payment.refundPayment(amount: controller.saleValue, callerId: controller.callerId ?? '', transactionDate: transactionDateController.text, cvNumber: cvNumberController.text);
                        }
                      : null,
                ),
                const SizedBox(height: 20),
                ElevatedButton(
                  onPressed: controller.clickPayment
                      ? () {
                          controller.setSaleValue(0.0);
                          moneyController.clear();
                        }
                      : null,
                  style: ElevatedButton.styleFrom(backgroundColor: Colors.orange, foregroundColor: Colors.white),
                  child: const Text('Cancelar Operação'),
                ),
                const SizedBox(height: 20),
                if (controller.clickPayment)
                  const Card(
                    color: Colors.blue,
                    child: Padding(
                      padding: EdgeInsets.all(16.0),
                      child: Row(
                        children: [
                          CircularProgressIndicator(color: Colors.white),
                          SizedBox(width: 16),
                          Text('Processando pagamento...', style: TextStyle(color: Colors.white)),
                        ],
                      ),
                    ),
                  ),
                if (controller.lastTransaction != null)
                  Card(
                    color: controller.lastTransaction!.isApproved ? Colors.green : Colors.orange,
                    child: Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Row(
                            children: [
                              Icon(controller.lastTransaction!.isApproved ? Icons.check_circle : Icons.error, color: Colors.white, size: 32),
                              const SizedBox(width: 12),
                              Expanded(
                                child: Text(
                                  controller.lastTransaction!.isApproved ? 'Transação Aprovada' : 'Transação Não Aprovada',
                                  style: const TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold),
                                ),
                              ),
                              IconButton(
                                icon: const Icon(Icons.close, color: Colors.white),
                                onPressed: () => controller.clearResult(),
                              ),
                            ],
                          ),
                          const SizedBox(height: 12),
                          if (controller.lastTransaction!.amount != null) _buildInfoRow('Valor', 'R\$ ${controller.lastTransaction!.amount!.toStringAsFixed(2)}'),
                          if (controller.lastTransaction!.nsu != null) _buildInfoRow('NSU', controller.lastTransaction!.nsu!),
                          if (controller.lastTransaction!.authorizationCode != null) _buildInfoRow('Código de Autorização', controller.lastTransaction!.authorizationCode!),
                          if (controller.lastTransaction!.cardLastDigits != null) _buildInfoRow('Últimos 4 dígitos', controller.lastTransaction!.cardLastDigits!),
                          if (controller.lastTransaction!.cardholderName != null) _buildInfoRow('Portador', controller.lastTransaction!.cardholderName!),
                          if (controller.lastTransaction!.brand != null) _buildInfoRow('Bandeira', controller.lastTransaction!.brand!),
                          if (controller.lastTransaction!.type != null) _buildInfoRow('Tipo', controller.lastTransaction!.type!),
                          if (controller.lastTransaction!.resultDetails != null) _buildInfoRow('Detalhes', controller.lastTransaction!.resultDetails!),
                          if (controller.lastTransaction!.cvNumber != null) _buildInfoRow('CV Number', controller.lastTransaction!.cvNumber!),
                        ],
                      ),
                    ),
                  ),
                if (controller.lastError != null)
                  Card(
                    color: Colors.red,
                    child: Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Row(
                        children: [
                          const Icon(Icons.error_outline, color: Colors.white, size: 32),
                          const SizedBox(width: 12),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                const Text(
                                  'Erro',
                                  style: TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold),
                                ),
                                const SizedBox(height: 4),
                                Text(controller.lastError!, style: const TextStyle(color: Colors.white)),
                              ],
                            ),
                          ),
                          IconButton(
                            icon: const Icon(Icons.close, color: Colors.white),
                            onPressed: () => controller.clearResult(),
                          ),
                        ],
                      ),
                    ),
                  ),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _buildInfoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4.0),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 140,
            child: Text(
              '$label:',
              style: const TextStyle(color: Colors.white70, fontWeight: FontWeight.bold),
            ),
          ),
          Expanded(
            child: Text(value, style: const TextStyle(color: Colors.white)),
          ),
        ],
      ),
    );
  }
}
