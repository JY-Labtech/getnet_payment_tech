// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'dart:convert';

/// Conteúdo a imprimir com `Payment.print`.
///
/// Imprime, nesta ordem: [text] (alinhado à esquerda, fonte média), a imagem
/// [base64Image] (centralizada) e [lines] linhas em branco.
class GetnetPrinterParams {
  /// Texto a imprimir. Use `\n` para quebrar linhas.
  final String? text;

  /// Imagem em Base64 (PNG/JPEG) a imprimir centralizada.
  final String? base64Image;

  /// Quantidade de linhas em branco ao final.
  final int? lines;

  GetnetPrinterParams({this.text, this.base64Image, this.lines});

  /// Itens no formato esperado pelo nativo (`printable_content`).
  List<Map<String, dynamic>> get printableContent {
    final content = <Map<String, dynamic>>[];
    if (text != null && text!.isNotEmpty) {
      content.add(<String, dynamic>{
        'type': 'text',
        'content': text,
        'align': 'left',
        'size': 'medium',
      });
    }
    if (base64Image != null && base64Image!.isNotEmpty) {
      content.add(<String, dynamic>{'type': 'image', 'imagePath': base64Image});
    }
    final blankLines = lines ?? 0;
    if (blankLines > 0) {
      content.add(<String, dynamic>{
        'type': 'text',
        'content': '\n' * blankLines,
        'align': 'left',
        'size': 'medium',
      });
    }
    return content;
  }

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'text': text,
      'base64Image': base64Image,
      'lines': lines,
      'printable_content': printableContent,
    };
  }

  factory GetnetPrinterParams.fromMap(Map<String, dynamic> map) {
    return GetnetPrinterParams(
      text: map['text'] != null ? map['text'] as String : null,
      base64Image: map['base64Image'] != null
          ? map['base64Image'] as String
          : null,
      lines: map['lines'] != null ? map['lines'] as int : null,
    );
  }

  String toJson() => json.encode(toMap());

  factory GetnetPrinterParams.fromJson(String source) =>
      GetnetPrinterParams.fromMap(json.decode(source) as Map<String, dynamic>);
}
