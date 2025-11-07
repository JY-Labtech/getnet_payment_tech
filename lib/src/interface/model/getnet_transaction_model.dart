// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'dart:convert';

class GetnetTransactionModel {
  final String? result;
  final String? resultDetails;
  final double? amount;
  final String? callerId;
  final String? nsu;
  final String? cvNumber;
  final String? type;
  final String? brand;
  final String? inputType;
  final String? installments;
  final String? authorizationCode;
  final String? cardBin;
  final String? cardLastDigits;
  final String? cardholderName;
  final String? orderId;
  final String? errorMessage;
  final String? method;
  final String? message;

  bool get isApproved => result == '0' || result == '00';

  GetnetTransactionModel({this.result, this.resultDetails, this.amount, this.callerId, this.nsu, this.cvNumber, this.type, this.brand, this.inputType, this.installments, this.authorizationCode, this.cardBin, this.cardLastDigits, this.cardholderName, this.orderId, this.errorMessage, this.method, this.message});

  Map<String, dynamic> toMap() {
    return <String, dynamic>{'result': result, 'resultDetails': resultDetails, 'amount': amount, 'callerId': callerId, 'nsu': nsu, 'cvNumber': cvNumber, 'type': type, 'brand': brand, 'inputType': inputType, 'installments': installments, 'authorizationCode': authorizationCode, 'cardBin': cardBin, 'cardLastDigits': cardLastDigits, 'cardholderName': cardholderName, 'orderId': orderId, 'errorMessage': errorMessage, 'method': method, 'message': message};
  }

  factory GetnetTransactionModel.fromMap(Map<String, dynamic> map) {
    double? amountValue;
    if (map['amount'] != null) {
      if (map['amount'] is String) {
        // Se for string, tenta converter (pode estar em centavos)
        final amountStr = map['amount'] as String;
        try {
          // Se tem 12 dígitos, provavelmente está em centavos
          if (amountStr.length >= 10) {
            amountValue = int.parse(amountStr) / 100.0;
          } else {
            amountValue = double.parse(amountStr);
          }
        } catch (e) {
          amountValue = null;
        }
      } else if (map['amount'] is num) {
        amountValue = (map['amount'] as num).toDouble();
      }
    }

    return GetnetTransactionModel(result: map['result']?.toString(), resultDetails: map['resultDetails']?.toString(), amount: amountValue, callerId: map['callerId']?.toString(), nsu: map['nsu']?.toString(), cvNumber: map['cvNumber']?.toString(), type: map['type']?.toString(), brand: map['brand']?.toString(), inputType: map['inputType']?.toString(), installments: map['installments']?.toString(), authorizationCode: map['authorizationCode']?.toString(), cardBin: map['cardBin']?.toString(), cardLastDigits: map['cardLastDigits']?.toString(), cardholderName: map['cardholderName']?.toString(), orderId: map['orderId']?.toString(), errorMessage: map['errorMessage']?.toString(), method: map['method']?.toString(), message: map['message']?.toString());
  }

  String toJson() => json.encode(toMap());

  factory GetnetTransactionModel.fromJson(String source) => GetnetTransactionModel.fromMap(json.decode(source) as Map<String, dynamic>);

  factory GetnetTransactionModel.toError(String message) {
    return GetnetTransactionModel(result: '999999', errorMessage: message, method: 'error', message: message);
  }

  @override
  bool operator ==(covariant GetnetTransactionModel other) {
    if (identical(this, other)) return true;

    return other.result == result && other.resultDetails == resultDetails && other.amount == amount && other.callerId == callerId && other.nsu == nsu && other.cvNumber == cvNumber && other.type == type && other.brand == brand && other.inputType == inputType && other.installments == installments && other.authorizationCode == authorizationCode && other.cardBin == cardBin && other.cardLastDigits == cardLastDigits && other.cardholderName == cardholderName && other.orderId == orderId && other.errorMessage == errorMessage && other.method == method && other.message == message;
  }

  @override
  int get hashCode {
    return result.hashCode ^ resultDetails.hashCode ^ amount.hashCode ^ callerId.hashCode ^ nsu.hashCode ^ cvNumber.hashCode ^ type.hashCode ^ brand.hashCode ^ inputType.hashCode ^ installments.hashCode ^ authorizationCode.hashCode ^ cardBin.hashCode ^ cardLastDigits.hashCode ^ cardholderName.hashCode ^ orderId.hashCode ^ errorMessage.hashCode ^ method.hashCode ^ message.hashCode;
  }
}
