import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:getnet_payment_tech/getnet_payment_tech.dart';

class PaymentController extends ChangeNotifier implements IGetnetHandler {
  double saleValue = 0.0;
  bool enable = false;
  bool clickPayment = false;
  String? callerId;
  String? orderId;
  String? lastNsu;
  String? lastCvNumber;
  GetnetTransactionModel? lastTransaction;
  String? lastError;

  void setSaleValue(double value) {
    if (value > 0.0) {
      saleValue = value;
      clickPayment = false;
      enable = true;
    } else {
      clickPayment = false;
      enable = false;
    }
    notifyListeners();
  }

  void generateCallerId() {
    callerId = DateTime.now().millisecondsSinceEpoch.toString();
  }

  void generateOrderId() {
    orderId = DateTime.now().millisecondsSinceEpoch.toString();
  }

  void setClickPayment(bool value) {
    clickPayment = value;
    notifyListeners();
  }

  void clearResult() {
    lastTransaction = null;
    lastError = null;
    notifyListeners();
  }

  @override
  Future<void> onChanged(String message) async {
    print('Getnet Payment - Changed: $message');
  }

  @override
  Future<void> onError(String message) async {
    print('Getnet Payment - Error: $message');
    clickPayment = false;
    enable = true;
    lastError = message;
    lastTransaction = null;
    notifyListeners();
  }

  @override
  Future<void> onFinishedResponse(String message) async {
    print('Getnet Payment - Finished Response: $message');
    try {
      // Tenta processar o JSON
      final jsonData = jsonDecode(message) as Map<String, dynamic>;
      
      // Se tem uma chave "data", usa ela, senão usa o map inteiro
      final dataMap = jsonData.containsKey('data') && jsonData['data'] is Map 
          ? jsonData['data'] as Map<String, dynamic> 
          : jsonData;
      
      // Converte o amount de string para double se necessário
      if (dataMap.containsKey('amount') && dataMap['amount'] is String) {
        final amountStr = dataMap['amount'] as String;
        try {
          // Se tem 12 dígitos, está em centavos (formato Getnet)
          if (amountStr.length >= 10) {
            dataMap['amount'] = int.parse(amountStr) / 100.0;
          } else {
            dataMap['amount'] = double.parse(amountStr);
          }
        } catch (e) {
          print('Erro ao converter amount: $e');
        }
      }
      
      lastTransaction = GetnetTransactionModel.fromMap(dataMap);
      lastError = null;
      
      // Armazena informações úteis
      if (lastTransaction!.nsu != null) {
        lastNsu = lastTransaction!.nsu;
      }
      if (lastTransaction!.cvNumber != null) {
        lastCvNumber = lastTransaction!.cvNumber;
      }
      
      print('Transação processada: ${lastTransaction!.toJson()}');
    } catch (e) {
      print('Erro ao processar resposta: $e');
      print('Mensagem recebida: $message');
      lastError = 'Erro ao processar resposta: ${e.toString()}';
      lastTransaction = null;
    }
    
    clickPayment = false;
    enable = true;
    notifyListeners();
  }

  @override
  Future<void> onLoading(bool show) async {
    print('Getnet Payment - Loading: $show');
    clickPayment = show;
    notifyListeners();
  }

  @override
  Future<void> onMessage(String message) async {
    print('Getnet Payment - Message: $message');
  }

  @override
  Future<void> onTransactionSuccess() async {
    print('Getnet Payment - Transaction Success');
    clickPayment = false;
    enable = true;
    notifyListeners();
  }
}

