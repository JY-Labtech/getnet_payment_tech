// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'dart:convert';

class GetnetPrinterParams {
  final String? text;
  final String? base64Image;
  final int? lines;

  GetnetPrinterParams({
    this.text,
    this.base64Image,
    this.lines,
  });

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'text': text,
      'base64Image': base64Image,
      'lines': lines,
    };
  }

  factory GetnetPrinterParams.fromMap(Map<String, dynamic> map) {
    return GetnetPrinterParams(
      text: map['text'] != null ? map['text'] as String : null,
      base64Image: map['base64Image'] != null ? map['base64Image'] as String : null,
      lines: map['lines'] != null ? map['lines'] as int : null,
    );
  }

  String toJson() => json.encode(toMap());

  factory GetnetPrinterParams.fromJson(String source) =>
      GetnetPrinterParams.fromMap(json.decode(source) as Map<String, dynamic>);
}

