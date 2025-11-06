import 'dart:convert';

import 'package:flutter/services.dart';

import '../../interface/interface.dart';

abstract class IGetnetHelper {
  static GetnetResponse convertToGetnetResponse(String message) {
    if (message.contains('{')) {
      final map = json.decode(message);
      return GetnetResponse.fromMap(map);
    }
    return GetnetResponse(message: message);
  }

  static GetnetTransactionModel convertToGetnetTransactionModel(String message) {
    if (message.contains('{')) {
      final map = json.decode(message);
      return GetnetTransactionModel.fromMap(map);
    }
    return GetnetTransactionModel.toError(message);
  }

  static Future<dynamic> callHandler({
    required MethodCall call,
    required IGetnetHandler iGetnetHandler,
    required Function(GetnetTransactionModel) onTransaction,
  }) async {
    switch (call.method.handler) {
      case PaymentTypeHandler.onTransactionSuccess:
        {
          iGetnetHandler.onTransactionSuccess();
        }
        break;
      case PaymentTypeHandler.onError:
        {
          String errorMessage;
          if (call.arguments is String) {
            errorMessage = call.arguments as String;
          } else {
            errorMessage = call.arguments.toString();
          }
          iGetnetHandler.onError(errorMessage);
        }
        break;
      case PaymentTypeHandler.onMessage:
        {
          iGetnetHandler.onMessage(call.arguments);
        }
        break;
      case PaymentTypeHandler.onFinishedResponse:
        {
          iGetnetHandler.onFinishedResponse(call.arguments);
          final model = _getTransactionModel(call.arguments);
          if (model != null) {
            onTransaction(model);
          }
        }
        break;
      case PaymentTypeHandler.onChanged:
        {
          iGetnetHandler.onChanged(call.arguments);
        }
        break;
      case PaymentTypeHandler.onLoading:
        {
          iGetnetHandler.onLoading(call.arguments);
        }
        break;
      default:
        throw "METHOD NOT IMPLEMENTED";
    }
    return true;
  }

  static GetnetTransactionModel? _getTransactionModel(dynamic value) {
    try {
      String jsonString;
      if (value is String) {
        jsonString = value;
      } else if (value is Map) {
        jsonString = jsonEncode(value);
      } else {
        jsonString = value.toString();
      }
      
      final map = jsonDecode(jsonString) as Map<String, dynamic>;
      
      // Se o map tem uma chave "data", usa ela, senão usa o map inteiro
      final dataMap = map.containsKey('data') ? map['data'] as Map<String, dynamic> : map;
      
      final transactionModel = GetnetTransactionModel.fromMap(dataMap);
      return transactionModel;
    } catch (e) {
      return null;
    }
  }
}

