# Getnet Payment Tech - Exemplo

Este é um exemplo completo de como usar o plugin `getnet_payment_tech` para integrar pagamentos com o SDK da Getnet em aplicações Flutter.

## Funcionalidades Demonstradas

### Pagamentos
- **Crédito**: Pagamento no crédito à vista
- **Crédito Parcelado**: Pagamento no crédito com parcelas (exemplo: 2x)
- **Débito**: Pagamento no débito

### Outras Operações
- **Pré-Autorização**: Realizar pré-autorização de valores
- **Status**: Consultar status de uma transação
- **Reimprimir**: Reimprimir último comprovante
- **Info**: Obter informações do terminal
- **Device Info**: Obter informações do dispositivo

### Estorno
- **Estornar Transação**: Estornar uma transação usando CV Number e data

## Como Usar

1. **Inicializar o Plugin**
   ```dart
   await GetnetTech.I.initPayment(
     handler: seuHandler,
     licenceKey: 'SUA_CHAVE_DE_LICENCA',
     licenceInternalKey: 'SUA_CHAVE_INTERNA',
   );
   ```

2. **Realizar um Pagamento**
   ```dart
   await GetnetTech.I.payment.creditPayment(
     amount: 100.0,
     callerId: "123456",
     orderId: "789012",
   );
   ```

3. **Implementar o Handler**
   ```dart
   class PaymentController extends IGetnetHandler {
     @override
     Future<void> onTransactionSuccess() async {
       // Transação aprovada
     }
     
     @override
     Future<void> onError(String message) async {
       // Erro na transação
     }
     
     // ... outros métodos
   }
   ```

## Estrutura do Projeto

```
example/
├── lib/
│   ├── main.dart                    # Ponto de entrada da aplicação
│   └── payment/
│       ├── payment_controller.dart   # Controller com lógica de pagamento
│       └── payment_page.dart         # Interface de pagamentos
└── pubspec.yaml
```

## Requisitos

- Flutter SDK >= 3.3.0
- Android SDK >= 22
- Dispositivo com SDK Getnet instalado

## Notas Importantes

- O `callerId` e `orderId` são gerados automaticamente, mas podem ser customizados
- O valor deve ser inserido em reais (ex: 100.00 para R$ 100,00)
- Para estornar, é necessário o CV Number e a data da transação original
- O plugin utiliza deeplinks para comunicação com o SDK Getnet
- As chaves `licenceKey` e `licenceInternalKey` devem ser fornecidas via código (por variáveis de ambiente ou constantes)

## Suporte

Para mais informações, consulte a documentação do plugin principal.
