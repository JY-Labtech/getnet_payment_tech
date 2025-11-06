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

  GetnetTransactionModel({
    this.result,
    this.resultDetails,
    this.amount,
    this.callerId,
    this.nsu,
    this.cvNumber,
    this.type,
    this.brand,
    this.inputType,
    this.installments,
    this.authorizationCode,
    this.cardBin,
    this.cardLastDigits,
    this.cardholderName,
    this.orderId,
    this.errorMessage,
    this.method,
    this.message,
  });

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'result': result,
      'resultDetails': resultDetails,
      'amount': amount,
      'callerId': callerId,
      'nsu': nsu,
      'cvNumber': cvNumber,
      'type': type,
      'brand': brand,
      'inputType': inputType,
      'installments': installments,
      'authorizationCode': authorizationCode,
      'cardBin': cardBin,
      'cardLastDigits': cardLastDigits,
      'cardholderName': cardholderName,
      'orderId': orderId,
      'errorMessage': errorMessage,
      'method': method,
      'message': message,
    };
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
    
    return GetnetTransactionModel(
      result: map['result'] != null ? map['result'].toString() : null,
      resultDetails: map['resultDetails'] != null ? map['resultDetails'].toString() : null,
      amount: amountValue,
      callerId: map['callerId'] != null ? map['callerId'].toString() : null,
      nsu: map['nsu'] != null ? map['nsu'].toString() : null,
      cvNumber: map['cvNumber'] != null ? map['cvNumber'].toString() : null,
      type: map['type'] != null ? map['type'].toString() : null,
      brand: map['brand'] != null ? map['brand'].toString() : null,
      inputType: map['inputType'] != null ? map['inputType'].toString() : null,
      installments: map['installments'] != null ? map['installments'].toString() : null,
      authorizationCode: map['authorizationCode'] != null ? map['authorizationCode'].toString() : null,
      cardBin: map['cardBin'] != null ? map['cardBin'].toString() : null,
      cardLastDigits: map['cardLastDigits'] != null ? map['cardLastDigits'].toString() : null,
      cardholderName: map['cardholderName'] != null ? map['cardholderName'].toString() : null,
      orderId: map['orderId'] != null ? map['orderId'].toString() : null,
      errorMessage: map['errorMessage'] != null ? map['errorMessage'].toString() : null,
      method: map['method'] != null ? map['method'].toString() : null,
      message: map['message'] != null ? map['message'].toString() : null,
    );
  }

  String toJson() => json.encode(toMap());

  factory GetnetTransactionModel.fromJson(String source) =>
      GetnetTransactionModel.fromMap(json.decode(source) as Map<String, dynamic>);

  factory GetnetTransactionModel.toError(String message) {
    return GetnetTransactionModel(
      result: '999999',
      errorMessage: message,
      method: 'error',
      message: message,
    );
  }

  @override
  bool operator ==(covariant GetnetTransactionModel other) {
    if (identical(this, other)) return true;

    return other.result == result &&
        other.resultDetails == resultDetails &&
        other.amount == amount &&
        other.callerId == callerId &&
        other.nsu == nsu &&
        other.cvNumber == cvNumber &&
        other.type == type &&
        other.brand == brand &&
        other.inputType == inputType &&
        other.installments == installments &&
        other.authorizationCode == authorizationCode &&
        other.cardBin == cardBin &&
        other.cardLastDigits == cardLastDigits &&
        other.cardholderName == cardholderName &&
        other.orderId == orderId &&
        other.errorMessage == errorMessage &&
        other.method == method &&
        other.message == message;
  }

  @override
  int get hashCode {
    return result.hashCode ^
        resultDetails.hashCode ^
        amount.hashCode ^
        callerId.hashCode ^
        nsu.hashCode ^
        cvNumber.hashCode ^
        type.hashCode ^
        brand.hashCode ^
        inputType.hashCode ^
        installments.hashCode ^
        authorizationCode.hashCode ^
        cardBin.hashCode ^
        cardLastDigits.hashCode ^
        cardholderName.hashCode ^
        orderId.hashCode ^
        errorMessage.hashCode ^
        method.hashCode ^
        message.hashCode;
  }
}

