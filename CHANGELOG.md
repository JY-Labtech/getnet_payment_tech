## 1.2.0

- Validação de licença migrada para o novo serviço da JY Labtech (`https://licence-server.invalid/pos-payments/licence/check`). Mesmo contrato da API anterior; as chaves de licença existentes continuam válidas.
- Atualizado o valor padrão do `licenceInternalKey` (constante `licenceCheck` e app de exemplo). O valor antigo deixa de ser usado (veja abaixo).
- README: novo portal de licenças (https://portal.jylabtech.com.br), planos `debug` (cortesia) e `mensal`, limite por dispositivo, cache offline de 7 dias, tabela de erros de licença e troubleshooting. Corrigidos o tratamento de `LICENCE_ERROR` (lançado pelo `initPayment`), o snippet de dependência e referências incorretas à Stone.
- **Validação de licença mais rígida (pode bloquear apps com licença irregular):**
  - A URL de validação fica travada no servidor oficial da JY Labtech. O `licenceInternalKey` passa a ser **opcional** (padrão: `licenceCheck`) e só é aceito se apontar para `https://licence-server.invalid/pos-payments/licence/check...`; qualquer outro valor (inclusive a URL antiga do Cloud Run, `http`, outros hosts ou Base64 inválido) é ignorado.
  - Uma resposta 200 com licença vencida, inativa ou cancelada (ou sem `expiredAt`) agora é erro; antes podia ser aceita. Nada é salvo no cache nesses casos.
  - O cache offline só vale se a licença estiver ativa e dentro da validade, tiver sido salva há no máximo 7 dias (e não no futuro), for da mesma `licenceKey` e tiver sido salvo com a URL do servidor oficial; caso contrário é apagado e a licença é revalidada online no core. Caches salvos com o `licenceInternalKey` antigo (Cloud Run) ou com qualquer valor fora do servidor oficial são descartados na primeira operação após a atualização (é preciso internet nesse momento).
  - Toda operação (pagamentos, estorno, status, pré-autorização, impressão, reimpressão, `getInfo`, `getDeviceInfo`) verifica a licença antes de chamar o SDK da Getnet e devolve `LICENCE_ERROR` se ela não for válida.
  - Novo campo `isCanceled` lido da resposta do servidor.
  - Mensagens de erro sempre preenchidas (`Erro ao validar licença (HTTP <código>)` quando o servidor não envia `message`).
- A consulta ao servidor de licença roda em background (fora da main thread) com timeouts de 15 s de conexão e leitura.
- `licenceCheck` exportado por `package:getnet_payment_tech/getnet_payment_tech.dart`.
- Testes: testes JVM do nativo (URL, regras de validade, cache, respostas HTTP e fluxo com rede/cache simulados) e testes Dart do contrato de licença no MethodChannel. README com a seção "Testes".
- `scripts/build_android_aar.sh` aceita flags extras do Gradle via `GRADLE_ARGS`.
- `.pubignore` para não publicar `local.properties` e outros arquivos locais.
- **Correção: cada chamada responde uma única vez no seu próprio `Future`.** Os usecases nativos guardavam o `Result` da primeira chamada e o reutilizavam nas seguintes, e a resposta do app da Getnet ia para o `Result` da última chamada recebida (qualquer método). Agora cada usecase usa o `Result` da chamada corrente e a resposta do deeplink é entregue ao `Result` da operação que o abriu, uma única vez. Mudança visível: `Future`s que antes podiam nunca completar (ex.: pagamento seguido de `getDeviceInfo` antes da resposta, ou falha ao abrir o deeplink a partir da segunda operação) agora completam, e não ocorre mais `Reply already submitted`.
- **Correção: `print` após uma falha de impressão** não gera mais `NullPointerException`; cada impressão responde no seu próprio `Future`.
- Teste da bridge Java (`GetnetPaymentTechPluginTest`) atualizado para o nativo pré-compilado em `android/libs` (antes esperava o AAR ausente).
- `android/native/local.properties` (caminhos da máquina, reescrito pelo script de build) adicionado ao `.gitignore`.
- App de exemplo não informa mais `licenceInternalKey` (usa o padrão `licenceCheck`).
- **Correção: cancelamento no app da Getnet completa a operação.** Quando o app da Getnet volta sem `RESULT_OK` (usuário cancela, volta ou fecha), a operação pendente completa com erro `CANCELED` (o método retorna `false` e o handler recebe `onError`). Mudança visível: antes o `Future` nunca completava.
- **Correção:** o listener de resultado de Activity só consome os requestCodes dos deeplinks do plugin; os demais seguem para outros plugins (antes consumia todos).
- **Correção:** sem a implementação nativa ou sem o SDK da Getnet (`NoClassDefFoundError`/`LinkageError`), a bridge não derruba mais o app: registra o canal e responde toda chamada com `PlatformException(code: 'NATIVE_UNAVAILABLE')` e a causa na mensagem.

## 1.1.0

- Fix transaction methods

## 1.0.0

- Initial release
