# Getnet Payment Tech

<div align="center">
  <img src="assets/getnet_payment_tech.png" alt="Getnet Payment Tech" height="300" />
</div>

---

## 🚨 Aviso

> **Este plugin é _não oficial_** e foi desenvolvido de forma independente para facilitar a integração de pagamentos via SDK da Getnet em dispositivos Android Smart POS.

---

Plugin Flutter para integrar sua aplicação com o SDK Android da Getnet para processamento de pagamentos em terminais POS (Point of Sale).

## 📋 Índice

- [Sobre o Plugin](#sobre-o-plugin)
- [Requisitos](#requisitos)
- [Instalação](#instalação)
- [Configuração](#configuração)
- [Sistema de Licença](#sistema-de-licença)
- [Como Usar](#como-usar)
- [Métodos Disponíveis](#métodos-disponíveis)
- [Handlers e Callbacks](#handlers-e-callbacks)
- [Modelos de Dados](#modelos-de-dados)
- [Exemplos de Código](#exemplos-de-código)
- [Tratamento de Erros](#tratamento-de-erros)
- [Testes](#testes)
- [Suporte](#suporte)

## 🎯 Sobre o Plugin

O **Getnet Payment Tech** é um plugin Flutter que permite integrar aplicações Android com o SDK da Getnet para processamento de pagamentos em terminais POS. O plugin oferece suporte para diversos métodos de pagamento, incluindo:

- 💳 **Cartão de Crédito** (à vista e parcelado)
- 💰 **Cartão de Débito**
- 🎁 **Voucher**
- 📱 **PIX**
- 🔄 **Estorno de Transações**
- 🔒 **Pré-Autorização**
- 📊 **Consulta de Status**
- 🖨️ **Impressão e Reimpressão**

### Características Principais

- ✅ Integração completa com SDK Getnet via Deeplinks
- ✅ Sistema de licença integrado para controle de uso
- ✅ Callbacks assíncronos para todas as operações
- ✅ Suporte a múltiplos métodos de pagamento
- ✅ Tratamento robusto de erros
- ✅ Modelos de dados tipados
- ✅ Armazenamento seguro de licenças (criptografado)

## 📦 Requisitos

### Requisitos do Sistema

- **Flutter**: >= 3.3.0
- **Dart**: >= 3.9.2
- **Android**: minSdk 22 (Android 5.1+)
- **CompileSdk**: 36

### Requisitos do Ambiente

- Dispositivo Android com SDK Getnet instalado
- Terminal POS compatível com Getnet
- Chaves de licença válidas (obrigatório)

## 🚀 Instalação

### 1. Adicione a dependência

No arquivo `pubspec.yaml` do seu projeto:

```yaml
dependencies:
  getnet_payment_tech: ^1.2.0
```

### 2. Instale as dependências

```bash
flutter pub get
```

### 3. Configure o Android

Garanta que seu aplicativo disponha das chaves de licença adquiridas no portal da JY Labtech ([https://portal.jylabtech.com.br](https://portal.jylabtech.com.br)) e que elas sejam disponibilizadas em tempo de execução (por exemplo, via variáveis de ambiente, cofre de segredos ou serviço seguro). Consulte [Configuração](#configuração) para ver um exemplo de uso em código.

## ⚙️ Configuração

### Configuração de Licença

O plugin requer que a chave `licenceKey` seja informada pelo aplicativo em tempo de execução. Recomendamos armazená-la com segurança (por exemplo, em um backend, cofre de segredos, variáveis de ambiente criptografadas ou serviços de configuração). O `licenceInternalKey` é **opcional** desde a versão 1.2.0.

```dart
final licenceKey = const String.fromEnvironment('GETNET_LICENCE_KEY');

await GetnetTech.I.initPayment(
  handler: handler,
  licenceKey: licenceKey,
);
```

> **Dica**: você pode sobrescrever os valores em tempo de build com `--dart-define`, utilizar serviços como Firebase Remote Config ou qualquer outro mecanismo seguro de provisionamento.

#### Configuração do `licenceInternalKey`

O plugin **sempre** valida a licença no servidor oficial da JY Labtech (`https://portal-api.jylabtech.com.br/pos-payments/licence/check`). O `licenceInternalKey` é opcional e, quando omitido, usa a constante `licenceCheck` exportada pelo plugin.

Se informado, ele pode ser uma string Base64 ou a URL completa, mas só é aceito se apontar para o servidor oficial (`https`, host `portal-api.jylabtech.com.br` e caminho `/pos-payments/licence/check...`). **Qualquer outro valor é ignorado** (inclusive o valor antigo do Cloud Run, URLs `http`, outros hosts ou valores inválidos) e o plugin usa o servidor oficial.

Valor padrão (`licenceCheck`, Base64 de `https://portal-api.jylabtech.com.br/pos-payments/licence/check`):

```
aHR0cHM6Ly9wb3J0YWwtYXBpLmp5bGFidGVjaC5jb20uYnIvcG9zLXBheW1lbnRzL2xpY2VuY2UvY2hlY2s=
```

Ou, em texto puro:

```
https://portal-api.jylabtech.com.br/pos-payments/licence/check
```

> **Migração:** o valor antigo do `licenceInternalKey` (iniciado por `aHR0cHM6Ly9wb3MtcGF5bWVudHMtYXBp...`) não quebra o app, mas é ignorado: a validação vai para o servidor oficial. Você pode simplesmente remover o parâmetro. As suas chaves de licença (`licenceKey`) **não mudam**: as chaves existentes continuam valendo.

## 🔐 Sistema de Licença

O uso do plugin exige uma licença válida da JY Labtech. O plugin:

- ✅ Valida a licença sempre no servidor oficial da JY Labtech, na inicialização (`initPayment`) e antes de **cada** operação (pagamentos, estorno, status, pré-autorização, impressão, reimpressão, `getInfo` e `getDeviceInfo`)
- ✅ Armazena a licença localmente de forma criptografada (`EncryptedSharedPreferences`)
- ✅ Funciona offline com a licença armazenada até a data de validade, por no máximo **7 dias** sem nova consulta ao servidor
- ✅ Bloqueia toda operação com erro `LICENCE_ERROR`, sem chamar o SDK da Getnet, quando a licença é inválida, vencida, suspensa ou cancelada

### Onde adquirir a licença

Acesse o portal **[https://portal.jylabtech.com.br](https://portal.jylabtech.com.br)** (o mesmo e-mail e senha do portal antigo continuam funcionando). No portal você pode:

- Adquirir a **Licença plugin de pagamento**, que vale para qualquer plugin de pagamento da JY Labtech;
- Copiar a sua chave de licença (`licenceKey`). Chaves existentes continuam válidas e chaves novas têm o formato `JYL-...`;
- Gerenciar e desativar os aparelhos vinculados à licença.

**Planos:**

| Plano | Descrição |
|---|---|
| `debug` | Cortesia concedida automaticamente no cadastro: 2 aparelhos, válida por 365 dias. Ideal para desenvolvimento e homologação. |
| `mensal` | Cobrado por dispositivo, para uso em produção. |

Cada aparelho que valida a licença ocupa uma vaga do limite de dispositivos da licença. O aparelho é identificado pelo `ANDROID_ID` (e pelo número de série, quando disponível).

### Como Funciona

1. **Validação local**: ao inicializar ou executar uma operação, o plugin verifica se existe uma licença válida armazenada no aparelho para a mesma `licenceKey` (sem acesso à rede). A licença local só vale se estiver ativa, dentro da validade, não cancelada, salva há no máximo 7 dias, com o relógio do aparelho coerente e validada no servidor oficial. Uma licença local salva por versões anteriores com o `licenceInternalKey` antigo (Cloud Run) é descartada e revalidada online no servidor oficial
2. **Validação com o servidor**: se não houver licença local válida, o plugin consulta o servidor oficial (`POST https://portal-api.jylabtech.com.br/pos-payments/licence/check`, com o header `Authorization: Bearer licence:<licenceKey>`), em background e com timeout de 15 s
3. **Armazenamento seguro**: somente uma licença ativa e dentro da validade é armazenada localmente, de forma criptografada. Uma resposta do servidor com licença vencida, inativa ou cancelada é tratada como erro
4. **Renovação**: a licença local deixa de valer na data de vencimento ou após 7 dias sem nova consulta; nesse momento o plugin consulta o servidor novamente (é necessário ter internet)

### Erros de Licença

Os erros de licença são retornados pelo nativo como `PlatformException` com código `LICENCE_ERROR`. O `initPayment` lança essa exceção; os demais métodos (pagamentos, impressão etc.) retornam `false` e a operação não é executada. A mensagem tem o formato `Sua licença não foi ativada. <motivo>`.

```dart
try {
  await GetnetTech.I.initPayment(
    handler: handler,
    licenceKey: licenceKey,
  );
} on PlatformException catch (e) {
  if (e.code == 'LICENCE_ERROR') {
    // Tratar erro de licença (e.message contém o motivo)
  }
}
```

| HTTP | Motivo | O que fazer |
|---|---|---|
| 404 | Licença não encontrada | Confira a `licenceKey` copiada do portal |
| 412 | Limite de aparelhos atingido ou aparelho desativado | Libere/reative o aparelho no portal ou amplie a licença |
| 428 | Licença vencida, suspensa ou cancelada | Renove ou regularize o pagamento no portal |
| 403 | Token de licença não enviado | Informe uma `licenceKey` não vazia |
| 400 | Aparelho sem identificador estável | Verifique se o aparelho fornece `ANDROID_ID` |
| 429 | Muitas requisições | Aguarde alguns instantes e tente novamente |

Erros de rede (sem internet, DNS, timeout) e respostas inesperadas do servidor também resultam em `LICENCE_ERROR` quando não há licença válida armazenada. Sem mensagem do servidor, o motivo é `Erro ao validar licença (HTTP <código>)`.

## 📖 Como Usar

### 1. Importe o Plugin

```dart
import 'package:getnet_payment_tech/getnet_payment_tech.dart';
```

### 2. Implemente o Handler

Crie uma classe que implementa `IGetnetHandler`:

```dart
class MyPaymentHandler implements IGetnetHandler {
  @override
  Future<void> onTransactionSuccess() async {
    print('Transação realizada com sucesso!');
  }

  @override
  Future<void> onError(String message) async {
    print('Erro: $message');
  }

  @override
  Future<void> onFinishedResponse(String message) async {
    print('Resposta final: $message');
    // Processar resposta da transação
  }

  @override
  Future<void> onLoading(bool show) async {
    print('Carregando: $show');
  }

  @override
  Future<void> onMessage(String message) async {
    print('Mensagem: $message');
  }

  @override
  Future<void> onChanged(String message) async {
    print('Mudança: $message');
  }
}
```

### 3. Inicialize o Plugin

```dart
Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  runApp(MyApp());

  await GetnetTech.I.initPayment(
    handler: MyPaymentHandler(),
    licenceKey: const String.fromEnvironment('GETNET_LICENCE_KEY', defaultValue: 'SUA_CHAVE_DE_LICENCA'),
  );
}
```

### 4. Execute Operações de Pagamento

```dart
// Pagamento no crédito
await GetnetTech.I.payment.creditPayment(
  amount: 100.50,
  callerId: '123456789',
  orderId: 'ORDER123',
);

// Pagamento no débito
await GetnetTech.I.payment.debitPayment(
  amount: 50.00,
  callerId: '123456789',
  orderId: 'ORDER124',
);
```

## 🔧 Métodos Disponíveis

### Métodos de Pagamento

#### `creditPayment`

Processa pagamento no cartão de crédito à vista.

```dart
Future<bool> creditPayment({
  required double amount,
  required String callerId,
  required String orderId,
  bool allowPrintCurrentTransaction = false,
})
```

**Parâmetros:**

- `amount`: Valor da transação em reais (ex: 100.50)
- `callerId`: ID único do chamador (identificador da aplicação)
- `orderId`: ID único do pedido
- `allowPrintCurrentTransaction`: Se deve imprimir comprovante automaticamente

**Retorno:** `true` se a operação foi iniciada com sucesso, `false` caso contrário.

#### `creditPaymentParc`

Processa pagamento no cartão de crédito parcelado.

```dart
Future<bool> creditPaymentParc({
  required double amount,
  required String callerId,
  required String orderId,
  required int installments,
  bool allowPrintCurrentTransaction = false,
})
```

**Parâmetros:**

- `amount`: Valor da transação em reais
- `callerId`: ID único do chamador
- `orderId`: ID único do pedido
- `installments`: Número de parcelas (deve ser > 1)
- `allowPrintCurrentTransaction`: Se deve imprimir comprovante automaticamente

#### `debitPayment`

Processa pagamento no cartão de débito.

```dart
Future<bool> debitPayment({
  required double amount,
  required String callerId,
  required String orderId,
  bool allowPrintCurrentTransaction = false,
})
```

#### `voucherPayment`

Processa pagamento via voucher.

```dart
Future<bool> voucherPayment({
  required double amount,
  required String callerId,
  required String orderId,
  bool allowPrintCurrentTransaction = false,
})
```

#### `pixPayment`

Processa pagamento via PIX.

```dart
Future<bool> pixPayment({
  required double amount,
  required String callerId,
  required String orderId,
  bool allowPrintCurrentTransaction = false,
})
```

### Outras Operações

#### `refundPayment`

Estorna uma transação anterior.

```dart
Future<bool> refundPayment({
  required double amount,
  required String callerId,
  required String transactionDate,
  required String cvNumber,
  String? originTerminal,
  bool allowPrintCurrentTransaction = false,
})
```

**Parâmetros:**

- `amount`: Valor a ser estornado
- `callerId`: ID do chamador da transação original
- `transactionDate`: Data da transação no formato DDMMYYYY
- `cvNumber`: Número CV da transação original
- `originTerminal`: Terminal de origem (opcional)

#### `preAuthorization`

Realiza pré-autorização de pagamento.

```dart
Future<bool> preAuthorization({
  required double amount,
  required String callerId,
  required String orderId,
  bool allowPrintCurrentTransaction = false,
})
```

#### `paymentStatus`

Consulta o status de uma transação.

```dart
Future<bool> paymentStatus({
  required String callerId,
  bool allowPrintCurrentTransaction = false,
})
```

#### `reprint`

Reimprime o último comprovante.

```dart
Future<bool> reprint()
```

#### `getDeviceInfo`

Obtém informações do dispositivo POS.

```dart
Future<bool> getDeviceInfo()
```

#### `getInfo`

Obtém informações do SDK Getnet.

```dart
Future<bool> getInfo()
```

#### `print`

Imprime conteúdo personalizado.

```dart
Future<bool> print({
  required GetnetPrinterParams printerParams,
})
```

## 📡 Handlers e Callbacks

O plugin utiliza um sistema de callbacks através da interface `IGetnetHandler` para notificar sobre eventos durante o processamento de pagamentos.

### Métodos do Handler

#### `onTransactionSuccess()`

Chamado quando uma transação é concluída com sucesso.

```dart
@override
Future<void> onTransactionSuccess() async {
  // Transação aprovada
}
```

#### `onFinishedResponse(String message)`

Chamado quando a resposta final da transação é recebida. Este é o callback mais importante para processar os dados da transação.

```dart
@override
Future<void> onFinishedResponse(String message) async {
  // message contém JSON com os dados da transação
  final jsonData = jsonDecode(message);
  // Processar dados...
}
```

#### `onError(String message)`

Chamado quando ocorre um erro durante o processamento.

```dart
@override
Future<void> onError(String message) async {
  // Tratar erro
  print('Erro: $message');
}
```

#### `onLoading(bool show)`

Chamado para indicar estado de carregamento.

```dart
@override
Future<void> onLoading(bool show) async {
  if (show) {
    // Mostrar indicador de carregamento
  } else {
    // Ocultar indicador de carregamento
  }
}
```

#### `onMessage(String message)`

Chamado quando há mensagens informativas durante o processamento.

```dart
@override
Future<void> onMessage(String message) async {
  // Exibir mensagem para o usuário
}
```

#### `onChanged(String message)`

Chamado quando há mudanças de estado durante o processamento.

```dart
@override
Future<void> onChanged(String message) async {
  // Atualizar UI conforme necessário
}
```

## 📊 Modelos de Dados

### GetnetTransactionModel

Modelo que representa os dados de uma transação.

```dart
class GetnetTransactionModel {
  final String? result;              // Código de resultado ('0' ou '00' = aprovado)
  final String? resultDetails;       // Detalhes do resultado
  final double? amount;              // Valor da transação
  final String? callerId;            // ID do chamador
  final String? nsu;                 // NSU da transação
  final String? cvNumber;            // Número CV
  final String? type;                // Tipo de pagamento
  final String? brand;                // Bandeira do cartão
  final String? authorizationCode;  // Código de autorização
  final String? cardLastDigits;      // Últimos 4 dígitos do cartão
  final String? cardholderName;      // Nome do portador
  final String? orderId;             // ID do pedido
  final String? errorMessage;        // Mensagem de erro (se houver)

  bool get isApproved => result == '0' || result == '00';
}
```

### Exemplo de Uso do Modelo

```dart
@override
Future<void> onFinishedResponse(String message) async {
  try {
    final jsonData = jsonDecode(message) as Map<String, dynamic>;
    final dataMap = jsonData['data'] as Map<String, dynamic>? ?? jsonData;

    final transaction = GetnetTransactionModel.fromMap(dataMap);

    if (transaction.isApproved) {
      print('Transação aprovada!');
      print('NSU: ${transaction.nsu}');
      print('Valor: R\$ ${transaction.amount}');
      print('Código de Autorização: ${transaction.authorizationCode}');
    } else {
      print('Transação não aprovada');
      print('Erro: ${transaction.errorMessage}');
    }
  } catch (e) {
    print('Erro ao processar transação: $e');
  }
}
```

## 💻 Exemplos de Código

### Exemplo Completo: Pagamento com Crédito

```dart
import 'package:flutter/material.dart';
import 'package:getnet_payment_tech/getnet_payment_tech.dart';
import 'dart:convert';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  await GetnetTech.I.initPayment(
    handler: MyPaymentHandler(),
    licenceKey: const String.fromEnvironment('GETNET_LICENCE_KEY', defaultValue: 'SUA_CHAVE_DE_LICENCA'),
  );

  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: PaymentScreen(),
    );
  }
}

class PaymentScreen extends StatefulWidget {
  @override
  _PaymentScreenState createState() => _PaymentScreenState();
}

class _PaymentScreenState extends State<PaymentScreen> implements IGetnetHandler {
  bool isLoading = false;
  String? statusMessage;
  GetnetTransactionModel? lastTransaction;

  @override
  void initState() {
    super.initState();
    _initPlugin();
  }

  Future<void> _initPlugin() async {
    await GetnetTech.I.initPayment(
      handler: this,
      licenceKey: const String.fromEnvironment('GETNET_LICENCE_KEY', defaultValue: 'SUA_CHAVE_DE_LICENCA'),
    );
  }

  // Implementação dos handlers
  @override
  Future<void> onTransactionSuccess() async {
    setState(() {
      statusMessage = 'Transação realizada com sucesso!';
      isLoading = false;
    });
  }

  @override
  Future<void> onError(String message) async {
    setState(() {
      statusMessage = 'Erro: $message';
      isLoading = false;
    });
  }

  @override
  Future<void> onFinishedResponse(String message) async {
    try {
      final jsonData = jsonDecode(message) as Map<String, dynamic>;
      final dataMap = jsonData['data'] as Map<String, dynamic>? ?? jsonData;

      setState(() {
        lastTransaction = GetnetTransactionModel.fromMap(dataMap);
        isLoading = false;
      });
    } catch (e) {
      setState(() {
        statusMessage = 'Erro ao processar resposta: $e';
        isLoading = false;
      });
    }
  }

  @override
  Future<void> onLoading(bool show) async {
    setState(() {
      isLoading = show;
    });
  }

  @override
  Future<void> onMessage(String message) async {
    setState(() {
      statusMessage = message;
    });
  }

  @override
  Future<void> onChanged(String message) async {
    // Atualizar conforme necessário
  }

  Future<void> processPayment() async {
    setState(() {
      isLoading = true;
      statusMessage = 'Processando pagamento...';
    });

    final callerId = DateTime.now().millisecondsSinceEpoch.toString();
    final orderId = DateTime.now().millisecondsSinceEpoch.toString();

    await GetnetTech.I.payment.creditPayment(
      amount: 100.50,
      callerId: callerId,
      orderId: orderId,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Pagamento Getnet')),
      body: Padding(
        padding: EdgeInsets.all(16.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            if (isLoading)
              CircularProgressIndicator()
            else
              ElevatedButton(
                onPressed: processPayment,
                child: Text('Pagar R\$ 100,50'),
              ),
            if (statusMessage != null)
              Padding(
                padding: EdgeInsets.all(16.0),
                child: Text(statusMessage!),
              ),
            if (lastTransaction != null)
              Card(
                child: Padding(
                  padding: EdgeInsets.all(16.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('Status: ${lastTransaction!.isApproved ? "Aprovado" : "Não Aprovado"}'),
                      if (lastTransaction!.nsu != null)
                        Text('NSU: ${lastTransaction!.nsu}'),
                      if (lastTransaction!.amount != null)
                        Text('Valor: R\$ ${lastTransaction!.amount!.toStringAsFixed(2)}'),
                    ],
                  ),
                ),
              ),
          ],
        ),
      ),
    );
  }
}
```

### Exemplo: Pagamento com Débito

```dart
Future<void> processDebitPayment() async {
  final callerId = DateTime.now().millisecondsSinceEpoch.toString();
  final orderId = DateTime.now().millisecondsSinceEpoch.toString();

  await GetnetTech.I.payment.debitPayment(
    amount: 50.00,
    callerId: callerId,
    orderId: orderId,
    allowPrintCurrentTransaction: true,
  );
}
```

### Exemplo: Pagamento Parcelado

```dart
Future<void> processInstallmentPayment() async {
  final callerId = DateTime.now().millisecondsSinceEpoch.toString();
  final orderId = DateTime.now().millisecondsSinceEpoch.toString();

  await GetnetTech.I.payment.creditPaymentParc(
    amount: 200.00,
    callerId: callerId,
    orderId: orderId,
    installments: 3, // 3 parcelas
    allowPrintCurrentTransaction: true,
  );
}
```

### Exemplo: Pagamento PIX

```dart
Future<void> processPixPayment() async {
  final callerId = DateTime.now().millisecondsSinceEpoch.toString();
  final orderId = DateTime.now().millisecondsSinceEpoch.toString();

  await GetnetTech.I.payment.pixPayment(
    amount: 75.50,
    callerId: callerId,
    orderId: orderId,
  );
}
```

### Exemplo: Estorno

```dart
Future<void> refundTransaction() async {
  await GetnetTech.I.payment.refundPayment(
    amount: 100.50,
    callerId: '123456789',
    transactionDate: '25122024', // DDMMYYYY
    cvNumber: '123456',
  );
}
```

### Exemplo: Consulta de Status

```dart
Future<void> checkPaymentStatus() async {
  await GetnetTech.I.payment.paymentStatus(
    callerId: '123456789',
  );
}
```

### Exemplo: Reimpressão

```dart
Future<void> reprintReceipt() async {
  await GetnetTech.I.payment.reprint();
}
```

### Exemplo: Informações do Dispositivo

```dart
Future<void> getDeviceInformation() async {
  await GetnetTech.I.payment.getDeviceInfo();

  // A resposta será recebida no handler onFinishedResponse
  // com informações do dispositivo como serial number, modelo, etc.
}
```

## ⚠️ Tratamento de Erros

### Erros Comuns

#### Erro de Licença

```dart
try {
  await GetnetTech.I.initPayment(
    handler: handler,
    licenceKey: licenceKey,
  );
} on PlatformException catch (e) {
  if (e.code == 'LICENCE_ERROR') {
    // Licença inválida, vencida, sem vaga de aparelho ou não configurada
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('Erro de Licença'),
        content: Text(e.message ?? 'Sua licença não foi ativada.'),
      ),
    );
  }
}
```

Veja a tabela de erros em [Sistema de Licença](#sistema-de-licença).

#### Erro de Validação

```dart
@override
Future<void> onError(String message) async {
  // Tratar diferentes tipos de erro
  if (message.contains('Invalid payment details')) {
    // Dados inválidos
  } else if (message.contains('Network')) {
    // Erro de rede
  } else {
    // Outro erro
  }
}
```

#### Erro de Transação Não Aprovada

```dart
@override
Future<void> onFinishedResponse(String message) async {
  final transaction = GetnetTransactionModel.fromMap(...);

  if (!transaction.isApproved) {
    // Transação não aprovada
    String errorMsg = transaction.errorMessage ?? 'Transação não aprovada';
    // Exibir mensagem ao usuário
  }
}
```

## 🔍 Estrutura de Resposta

### Resposta de Sucesso

Quando uma transação é bem-sucedida, o `onFinishedResponse` recebe um JSON com a seguinte estrutura:

```json
{
  "code": "SUCCESS",
  "data": {
    "result": "0",
    "amount": "0000000010050",
    "nsu": "123456",
    "cvNumber": "789012",
    "authorizationCode": "123456",
    "cardLastDigits": "1234",
    "cardholderName": "JOAO DA SILVA",
    "brand": "VISA",
    "type": "CREDIT",
    "orderId": "ORDER123",
    "callerId": "123456789"
  }
}
```

### Resposta de Erro

Quando ocorre um erro, o `onError` recebe uma mensagem de erro:

```json
{
  "code": "ERROR",
  "message": "Descrição do erro"
}
```

## 📝 Notas Importantes

### Geração de IDs

- **callerId**: Deve ser um identificador único da aplicação. Recomenda-se usar timestamp ou UUID.
- **orderId**: Deve ser um identificador único do pedido. Cada transação deve ter um orderId diferente.

### Formato de Valores

- Os valores são passados em **reais** (ex: 100.50)
- Internamente, o plugin converte para centavos (ex: 10050)
- O formato usado no SDK é uma string de 12 dígitos preenchida com zeros à esquerda

### Formato de Data

- Para estornos, use o formato **DDMMYYYY** (ex: "25122024" para 25/12/2024)

### Threading

- Todas as operações são assíncronas
- Os callbacks são executados na thread principal do Flutter
- Use `setState()` ou gerenciadores de estado para atualizar a UI
- Cada chamada recebe exatamente uma resposta no seu próprio `Future` (desde a 1.2.0). A resposta de uma operação que abre o app da Getnet (pagamentos, estorno, status, pré-autorização, reimpressão, `getInfo`) é entregue ao `Future` dessa operação, mesmo que outro método (ex.: `getDeviceInfo`) seja chamado enquanto ela está em andamento. Antes, a resposta podia ir para o `Future` de outra chamada (causando `Reply already submitted`) e o `Future` da operação original nunca completava
- Se o app da Getnet não puder ser aberto, o `Future` daquela chamada completa com `PlatformException` (antes, a partir da segunda chamada, o erro ia para o `Future` da primeira e o da chamada atual nunca completava)
- `print` continua funcionando após uma falha de impressão (antes, a impressão seguinte a um erro derrubava o app com `NullPointerException`)
- Se o usuário cancelar (ou fechar/voltar) no app da Getnet, a operação completa com erro `CANCELED` ("Operação cancelada ou não concluída no app da Getnet"): o método retorna `false` e o handler recebe `onError` com essa mensagem. Antes o `Future` ficava pendente para sempre
- O plugin só trata os resultados de Activity dos seus próprios deeplinks; resultados de outras Activities/plugins não são mais consumidos
- Se a implementação nativa ou o SDK da Getnet (`libposdigital`) não estiverem no app, o plugin não derruba mais o app na inicialização: toda chamada responde `PlatformException(code: 'NATIVE_UNAVAILABLE')` com a causa na mensagem

## 🛠️ Troubleshooting

### Problema: Plugin não encontra a classe principal

**Solução**: Verifique se o `pubspec.yaml` está configurado corretamente:

```yaml
plugin:
  platforms:
    android:
      package: br.com.jylabtech.getnet_payment_tech
      pluginClass: GetnetPaymentTechPlugin
```

### Problema: Erro de licença (`LICENCE_ERROR`)

**Solução**:

1. Confirme que a `licenceKey` foi informada (não vazia) no `initPayment`
2. O `licenceInternalKey` é opcional e valores fora do servidor oficial são ignorados (veja [Configuração do `licenceInternalKey`](#configuração-do-licenceinternalkey))
3. Confira no portal [https://portal.jylabtech.com.br](https://portal.jylabtech.com.br) se a licença está ativa, dentro da validade e com vaga de aparelho (ou se o aparelho não foi desativado)
4. Garanta que o aparelho tem acesso à internet na primeira validação e pelo menos a cada 7 dias
5. Ao trocar de chave, a licença armazenada localmente é descartada automaticamente e uma nova validação é feita

### Problema: Callbacks não são chamados

**Solução**:

1. Certifique-se de que `initPayment()` foi chamado antes de usar os métodos
2. Verifique se o handler está implementado corretamente
3. Confirme que o dispositivo tem o SDK Getnet instalado

### Problema: Erro `NATIVE_UNAVAILABLE`

**Solução**: a implementação nativa do plugin ou o SDK da Getnet não foi carregado. Confira se o app inclui o AAR `libposdigital` (veja [Configure o Android](#3-configure-o-android)) e leia a mensagem do erro, que traz a causa.

### Problema: Transação não inicia

**Solução**:

1. Verifique se o terminal POS está ligado e conectado
2. Confirme que o SDK Getnet está instalado e atualizado
3. Verifique os logs do Android para mais detalhes

## 🧪 Testes

Testes Dart (contrato do MethodChannel, constante `licenceCheck` e propagação de `LICENCE_ERROR`):

```bash
flutter test
```

Testes JVM do nativo (URL travada no servidor oficial, regras de validade da licença, cache offline, tratamento das respostas HTTP e resposta única no `Result` de cada chamada dos usecases), executados com o Gradle wrapper do example:

```bash
bash scripts/build_android_aar.sh   # gera android/native/local.properties e os binários em android/libs
cd example/android
./gradlew -p ../../android/native testDebugUnitTest
```

Teste da bridge Java (`android/src`), que carrega a implementação pré-compilada em `android/libs`:

```bash
cd example/android
./gradlew :getnet_payment_tech:testDebugUnitTest
```

## 📚 Recursos Adicionais

- [Documentação Getnet SDK](https://developers.getnet.com.br/)
- [Exemplo Completo](./example/)
- [Issues e Suporte](https://github.com/JY-Labtech/getnet_payment_tech/issues)

## 📄 Licença

Este software é proprietário e protegido por direitos autorais. Todos os direitos reservados.

**IMPORTANTE**: Este código é fornecido sob uma licença proprietária. O uso deste software é permitido APENAS mediante a compra de uma licença válida.

### Como Adquirir uma Licença

Para adquirir uma licença e obter acesso completo ao plugin, acesse nosso portal de licenciamento:

🔗 **[https://portal.jylabtech.com.br](https://portal.jylabtech.com.br)**

No portal você poderá:

- ✅ Comprar licenças para uso comercial (plano `mensal`, cobrado por dispositivo)
- ✅ Usar a licença `debug` de cortesia (2 aparelhos, 365 dias) para desenvolvimento
- ✅ Gerenciar suas licenças e aparelhos existentes
- ✅ Obter suporte técnico

### Restrições

Você NÃO está autorizado a:

- ❌ Copiar, reproduzir ou duplicar este código
- ❌ Modificar, adaptar ou criar trabalhos derivados
- ❌ Distribuir, publicar, sublicenciar, vender ou transferir o código
- ❌ Criar forks ou branches deste repositório
- ❌ Fazer engenharia reversa ou descompilar o código
- ❌ Remover avisos de propriedade ou direitos autorais
- ❌ Compartilhar o código com terceiros sem autorização escrita
- ❌ Usar o código para criar produtos ou serviços concorrentes

### Uso Autorizado

O uso deste software é permitido APENAS mediante:

1. Compra de uma licença válida através do portal de licenciamento
2. Conformidade com todos os termos e condições da licença
3. Uso exclusivo para os fins para os quais a licença foi adquirida

Para informações sobre licenciamento, entre em contato através dos canais indicados na seção [Suporte](#suporte).

Consulte o arquivo [LICENSE](./LICENSE) para os termos completos da licença.

## 👥 Suporte

Para suporte técnico ou dúvidas sobre o plugin:

- Abra uma issue no repositório do projeto
- Entre em contato com a equipe de desenvolvimento
- Consulte a documentação do SDK Getnet

---

---

<div align="left">
  <table>
  <tr>
    <td align="center"><a href="https://github.com/JY-Labtech"><img src="https://avatars.githubusercontent.com/u/185066406?s=200&v=4" width="100px;" alt=""/><br /><sub><b>JY Labtech</b></sub></a><br /> <a href="https://github.com/JY-Labtech" title="Autor"></a></td>
  </tr>

</table>

</div>

---

**Desenvolvido com ❤️ para facilitar integrações com Getnet**
